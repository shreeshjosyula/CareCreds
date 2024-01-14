package com.example.carecreds.Activity.ui.Services;// ServicesFragment.java

// ServicesFragment.java

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carecreds.Adapters.ServiceAdapter;
import com.example.carecreds.Model.Service;
import com.example.carecreds.R;
import com.example.carecreds.Utils.SharedPrefsUtil;
import com.example.carecreds.Utils.SnackBarUtils;
import com.example.carecreds.dialog.AddServiceDialogFragment;
import com.example.carecreds.dialog.VolunteerDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ServicesFragment extends Fragment implements AddServiceDialogFragment.AddServiceListener {

    private List<Service> serviceList = new ArrayList<>();
    private ServiceAdapter adapter;
    TextView noServicesFoundTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_services, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        Button addServiceButton = root.findViewById(R.id.addServiceButton);
        noServicesFoundTextView = root.findViewById(R.id.no_services_found_text_view);
        serviceList.clear();
        ProgressBar progressBar = root.findViewById(R.id.services_progress_bar);
        // Set up RecyclerView
        adapter = new ServiceAdapter(serviceList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userEmail = SharedPrefsUtil.getString(requireContext(), "email", "");
        db.collection("Service")
                .whereEqualTo("clientEmail", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        // check if there are any services
                        if (task.getResult().size() == 0) {
                            noServicesFoundTextView.setVisibility(View.VISIBLE);
                        }
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Service service = document.toObject(Service.class);
                            Boolean isCompleted = (Boolean) document.get("isCompleted");
                            if (!isCompleted) {
                                service.setId(document.getId());
                                adapter.setServiceList(service, noServicesFoundTextView);
                            }
                        }
                    } else {
                        SnackBarUtils.showCustomSnackbar(requireActivity().findViewById(android.R.id.content), "Error getting documents.");
                    }
                });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {


            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    db.collection("Service").document(serviceList.get(viewHolder.getAbsoluteAdapterPosition()).getId()).delete();
                    db.collection("CurrentlyEnrolled").whereEqualTo("serviceId", serviceList.get(viewHolder.getAbsoluteAdapterPosition()).getId()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("CurrentlyEnrolled").document(document.getId()).delete();
                            }
                        }
                    });
                    db.collection("Request").whereEqualTo("serviceID", serviceList.get(viewHolder.getAbsoluteAdapterPosition()).getId()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("Request").document(document.getId()).delete();
                            }
                        }
                    });
                    serviceList.remove(viewHolder.getAbsoluteAdapterPosition());
                    if (serviceList.size() == 0) {
                        noServicesFoundTextView.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                } else if (direction == ItemTouchHelper.RIGHT) {
                    // open VolunteerDialogFragment
                    SharedPrefsUtil.saveString(requireContext(), "serviceId", serviceList.get(viewHolder.getAbsoluteAdapterPosition()).getId());
                    SharedPrefsUtil.saveString(requireContext(), "serviceName", serviceList.get(viewHolder.getAbsoluteAdapterPosition()).getTitle());
                    SharedPrefsUtil.saveInt(requireContext(), "servicePoints", serviceList.get(viewHolder.getAbsoluteAdapterPosition()).getPoints());
                    SharedPrefsUtil.saveString(requireContext(), "serviceDescription", serviceList.get(viewHolder.getAbsoluteAdapterPosition()).getDescription());
                    SharedPrefsUtil.saveArrayListToSharedPreferences(requireContext(), serviceList.get(viewHolder.getAbsoluteAdapterPosition()).getEnrolledVolunteers());
                    FragmentManager fm = getParentFragmentManager();
                    DialogFragment dialog = new VolunteerDialogFragment();
                    dialog.setTargetFragment(ServicesFragment.this, 0);
                    dialog.show(fm, "VolunteerDialogFragment");
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                //setting icon on swipe
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .addSwipeLeftLabel("Delete")
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(requireContext(),  android.R.color.transparent))
                        .addSwipeRightActionIcon(R.drawable.ic_request)
                        .addSwipeRightLabel("Request Volunteer")
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);

        addServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddServiceDialog();
            }
        });

        return root;
    }

    private void openAddServiceDialog() {
        DialogFragment dialog = new AddServiceDialogFragment();
        dialog.setTargetFragment(this, 0);
        dialog.show(getParentFragmentManager(), "AddServiceDialogFragment");
    }

    @Override
    public void onServiceAdded(Service service) {
        adapter.setServiceList(service, noServicesFoundTextView);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);

        // Find the MenuItem with the SearchView
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        // Access the SearchView from the MenuItem
        SearchView searchView = (SearchView) searchItem.getActionView();
        // Set an OnQueryTextListener to the SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // Called when the user submits the query
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform the final search
                return true;
            }

            // Called when the query text is changed by the user
            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform a search whenever the user types a character
                searchJobs(newText);
                return true;
            }
        });


    }

    public void searchJobs(String query) {
        List<Service> filteredList = new ArrayList<>();
        for (Service service : serviceList) {
            if (service.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(service);
            }
        }
        adapter.filterList(filteredList);
    }
}

