package com.example.carecreds.dialog;

// AddServiceDialogFragment.java

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.carecreds.Model.Service;
import com.example.carecreds.R;
import com.example.carecreds.Utils.SharedPrefsUtil;
import com.example.carecreds.Utils.SnackBarUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddServiceDialogFragment extends DialogFragment {

    private EditText serviceTitleEditText, serviceDescriptionEditText, volunteersNeededEditText,
            hoursEditText, editTextPointsPerJob, extraNotesEditText;

    private AddServiceListener listener;
    private FirebaseFirestore db;

    @NonNull
    @Override
    public AlertDialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_service, null);
        builder.setView(dialogView)
                .setTitle("Add Service")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // This will be overridden below to prevent dialog from closing immediately
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });
        db = FirebaseFirestore.getInstance();
        listener = (AddServiceListener) getTargetFragment();
        serviceTitleEditText = dialogView.findViewById(R.id.editTextTitle);
        serviceDescriptionEditText = dialogView.findViewById(R.id.editTextDescription);
        volunteersNeededEditText = dialogView.findViewById(R.id.editTextVolunteersNeeded);
        hoursEditText = dialogView.findViewById(R.id.editTextHours);
        extraNotesEditText = dialogView.findViewById(R.id.editTextExtraNotes);
        editTextPointsPerJob = dialogView.findViewById(R.id.editTextPointsPerJob);

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addService();
                    }
                });
            }
        });

        return alertDialog;
    }

    private void addService() {
        String title, description, extraNotes;
        int volunteersNeeded, hours, points;
        try {
            title = serviceTitleEditText.getText().toString().trim();
            description = serviceDescriptionEditText.getText().toString().trim();
            volunteersNeeded = Integer.parseInt(volunteersNeededEditText.getText().toString().trim());
            hours = Integer.parseInt(hoursEditText.getText().toString().trim());
            points = Integer.parseInt(editTextPointsPerJob.getText().toString().trim());
            extraNotes = extraNotesEditText.getText().toString().trim();
            if (listener != null && getTargetFragment() instanceof AddServiceListener) {
                AddServiceListener fragmentListener = (AddServiceListener) getTargetFragment();
                fragmentListener.onServiceAdded(new Service("",title, description, volunteersNeeded, hours, points, extraNotes, new ArrayList<>(), false, SharedPrefsUtil.getString(requireContext(), "email","")));
            }
            CollectionReference servicesRef = db.collection("Service");
            Map<String, Object> service = new HashMap<>();
            service.put("title", title);
            service.put("description", description);
            service.put("volunteersNeeded", volunteersNeeded);
            service.put("hours", hours);
            service.put("enrolledVolunteers", new ArrayList<>());
            service.put("points", points);
            service.put("clientEmail", SharedPrefsUtil.getString(requireContext(), "email",""));
            service.put("extraNotes", extraNotes);
            service.put("isCompleted", false);

            servicesRef.add(service)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                dismiss();
                            } else {
                                SnackBarUtils.showCustomSnackbar(requireActivity().findViewById(android.R.id.content), "Failed to add service");
                            }
                        }
                    });
            dismiss();
        } catch (NumberFormatException e) {
            SnackBarUtils.showCustomSnackbar(requireActivity().findViewById(android.R.id.content), "Please enter a valid number");
            e.printStackTrace();
        }
    }

    public interface AddServiceListener {
        void onServiceAdded(Service service);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
