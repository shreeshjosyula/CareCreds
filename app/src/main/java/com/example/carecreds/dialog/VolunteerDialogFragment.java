package com.example.carecreds.dialog;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carecreds.Adapters.VolunteerDialogAdapter;
import com.example.carecreds.Model.AllVolunteers;
import com.example.carecreds.R;
import com.example.carecreds.RecyclerViewDecoration.SimpleDividerItemDecoration;
import com.example.carecreds.Utils.SharedPrefsUtil;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class VolunteerDialogFragment extends DialogFragment {

    private RecyclerView recyclerView;
    private VolunteerDialogAdapter adapter;
    private List<AllVolunteers> volunteerList = new ArrayList<>();

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int screenWidthPixels = displayMetrics.widthPixels;

            int marginInPixels = 52;

            int width = screenWidthPixels - (2 * marginInPixels); // Apply margin from both sides
            int height = 1250;
            getDialog().getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.volunteer_dialog, container, false);

        recyclerView = view.findViewById(R.id.volunteer_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        requireDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int dividerColor = getResources().getColor(R.color.dividerColor);
        float dividerHeight = getResources().getDimensionPixelSize(R.dimen.dividerHeight);
        SimpleDividerItemDecoration itemDecoration = new SimpleDividerItemDecoration(dividerColor, dividerHeight, getContext());
        recyclerView.addItemDecoration(itemDecoration);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Volunteer")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        volunteerList = task.getResult().toObjects(AllVolunteers.class);
                        // check if volunteer has already been enrolled or not
                        for (int i = 0; i < volunteerList.size(); i++) {
                            List<String> volunteers = SharedPrefsUtil.getArrayListFromSharedPreferences(requireContext());
                            if (volunteers.contains(volunteerList.get(i).getEmail())) {
                                volunteerList.remove(i);
                            }
                        }
                        adapter = new VolunteerDialogAdapter(requireContext(), volunteerList, this);
                        recyclerView.setAdapter(adapter);
                    }
                });


        return view;
    }

}

