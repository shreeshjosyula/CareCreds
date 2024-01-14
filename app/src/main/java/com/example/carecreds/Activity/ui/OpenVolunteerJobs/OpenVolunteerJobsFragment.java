package com.example.carecreds.Activity.ui.OpenVolunteerJobs;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carecreds.Adapters.OpenJobsAdapter;
import com.example.carecreds.Model.Service;
import com.example.carecreds.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OpenVolunteerJobsFragment extends Fragment {

    private FirebaseFirestore db;
    private List<Service> serviceList;
    private RecyclerView recyclerView;
    private OpenJobsAdapter adapter;

    public OpenVolunteerJobsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        db = FirebaseFirestore.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_open_jobs, container, false);
        recyclerView = view.findViewById(R.id.open_jobs_recycler_view);
        // Set up RecyclerView with adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        serviceList = new ArrayList<>();
        adapter = new OpenJobsAdapter(serviceList);
        recyclerView.setAdapter(adapter);
        fetchJobsData();
        return view;
    }

    private void fetchJobsData() {
        db.collection("Service")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ProgressBar progressBar = getView().findViewById(R.id.open_jobs_progress_bar);
                        progressBar.setVisibility(View.GONE);
                        TextView noJobsFoundTextView = getView().findViewById(R.id.no_open_jobs_found_text_view);
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Service service = document.toObject(Service.class);
                            int availableSpaces = calculateAvailableSpaces(service);

                            if (!(Boolean) document.get("isCompleted")) {
                                if (availableSpaces > 0) {
                                    service.setId(document.getId());
                                    serviceList.add(service);
                                }
                            }
                        }
                        if (serviceList.size() == 0) {
                            noJobsFoundTextView.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        // Handle error fetching data
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }


    private int calculateAvailableSpaces(Service service) {
        int enrolledVolunteers = service.getEnrolledVolunteers().size();
        int neededVolunteers = service.getVolunteersNeeded();
        return neededVolunteers - enrolledVolunteers;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);

        // Find the MenuItem with the SearchView
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        // Access the SearchView from the MenuItem
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Job title or client email");
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
            if (service.getClientEmail().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(service);
            }
        }
        adapter.filterList(filteredList);
    }
}
