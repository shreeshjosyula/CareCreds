package com.example.carecreds.Activity.ui.AllVolunteers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carecreds.Adapters.AllVolunteersAdapter;
import com.example.carecreds.Model.AllVolunteers;
import com.example.carecreds.R;
import com.example.carecreds.databinding.FragmentAllVolunteersBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AllVolunteersFragment extends Fragment {

    private FragmentAllVolunteersBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentAllVolunteersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.allVolunteersRecyclerView;
        TextView textView = binding.noVolunteersFoundTextView;
        ProgressBar progressBar = binding.allVolunteersProgressBar;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // fetch all volunteers from firebase
        db.collection("Volunteer")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
progressBar.setVisibility(View.GONE);
                        // set up recycler view
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        //convert this query to a list of all volunteers
                        List<AllVolunteers> allVolunteers = task.getResult().toObjects(AllVolunteers.class);
                        if (allVolunteers.size() == 0) {
                            textView.setVisibility(View.VISIBLE);
                        }
                        allVolunteers.sort((o1, o2) -> o2.getRating() - o1.getRating());
                        AllVolunteersAdapter adapter = new AllVolunteersAdapter(allVolunteers);
                        recyclerView.setAdapter(adapter);
                    }
                });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}