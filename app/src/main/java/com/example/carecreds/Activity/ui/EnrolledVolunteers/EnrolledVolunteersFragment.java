package com.example.carecreds.Activity.ui.EnrolledVolunteers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carecreds.Adapters.CurrentlyEnrolledAdapter;
import com.example.carecreds.Model.CurrentlyEnrolled;
import com.example.carecreds.R;
import com.example.carecreds.Utils.SharedPrefsUtil;
import com.example.carecreds.Utils.SnackBarUtils;
import com.example.carecreds.databinding.FragmentEnrolledVolunteersBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EnrolledVolunteersFragment extends Fragment {

    private FragmentEnrolledVolunteersBinding binding;
    CurrentlyEnrolledAdapter adapter;
    TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentEnrolledVolunteersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        RecyclerView recyclerView = root.findViewById(R.id.enrolled_volunteers_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        textView = binding.noEnrolledVolunteersFoundTextView;

// Create an instance of your adapter

// Set the adapter to your RecyclerView


        String userType = SharedPrefsUtil.getString(requireContext(), "userType", "");
        fetchDataAndSetAdapter(db, recyclerView, userType);
    return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchDataAndSetAdapter(FirebaseFirestore db, RecyclerView rv, String userType) {
        db.collection("CurrentlyEnrolled")
                .whereEqualTo(userType+"Email", SharedPrefsUtil.getString(requireContext(), "email", ""))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ProgressBar progressBar = binding.enrolledVolunteersProgressBar;
                        progressBar.setVisibility(View.GONE);
                        List<CurrentlyEnrolled> enrolledList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CurrentlyEnrolled enrolled = document.toObject(CurrentlyEnrolled.class);
                            enrolled.setId(document.getId());
                            enrolledList.add(enrolled);
                            adapter = new CurrentlyEnrolledAdapter(enrolledList,textView);
                        }
                        if (enrolledList.isEmpty()) {
                            textView.setVisibility(View.VISIBLE);
                        }
                        rv.setAdapter(adapter);
                    } else {
                        SnackBarUtils.showCustomSnackbar(requireActivity().findViewById(android.R.id.content), "Error getting documents");
                    }
                }).addOnFailureListener(e -> {
                    SnackBarUtils.showCustomSnackbar(requireActivity().findViewById(android.R.id.content), "Error getting documents.");
                });
    }
}