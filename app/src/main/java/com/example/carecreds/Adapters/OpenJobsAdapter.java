package com.example.carecreds.Adapters;

import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carecreds.Model.Service;
import com.example.carecreds.R;
import com.example.carecreds.Utils.SharedPrefsUtil;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenJobsAdapter extends RecyclerView.Adapter<OpenJobsAdapter.ViewHolder> {
    private List<Service> serviceList;

    // Constructor to initialize the service list
    public OpenJobsAdapter(List<Service> serviceList) {
        this.serviceList = serviceList;
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceNameTextView, serviceDescriptionTextView, textViewEnrolledVolunteers,
                pointsPerJobTextView, extraNotesTextView, clientEmailTextView;
        Button enrollButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.textViewServiceTitle);
            serviceDescriptionTextView = itemView.findViewById(R.id.textViewServiceDescription);
            textViewEnrolledVolunteers = itemView.findViewById(R.id.textViewEnrolledVolunteers);
            pointsPerJobTextView = itemView.findViewById(R.id.textViewPointsAwarded);
            extraNotesTextView = itemView.findViewById(R.id.textViewExtraNotes);
            enrollButton = itemView.findViewById(R.id.buttonEnroll);
            clientEmailTextView = itemView.findViewById(R.id.textViewClientEmail);
        }
    }

    @NonNull
    @Override
    public OpenJobsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.open_job_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpenJobsAdapter.ViewHolder holder, int position) {
        Service service = serviceList.get(position);

        // Bind data to the ViewHolder
        holder.serviceNameTextView.setText("Service Title: "+service.getTitle());
        holder.serviceDescriptionTextView.setText("Service Description: "+service.getDescription());
        holder.textViewEnrolledVolunteers.setText("Volunteers enrolled: "+service.getEnrolledVolunteers().size()+"/"+String.valueOf(service.getVolunteersNeeded()));
        holder.pointsPerJobTextView.setText("Points per job: "+String.valueOf(service.getPoints()));
        holder.extraNotesTextView.setText("Extra Notes: "+service.getExtraNotes());

        String textToUnderline = service.getClientEmail();

        SpannableString content = new SpannableString("Email: " + textToUnderline);
        content.setSpan(new UnderlineSpan(), 7, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Underline from 7th index (after "Email: ")
        holder.clientEmailTextView.setText(content);

        holder.clientEmailTextView.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + service.getClientEmail()));
            v.getContext().startActivity(Intent.createChooser(emailIntent, "Send email"));
        });

        // Set any additional click listeners or actions for the enrollButton if needed
        if (service.getEnrolledVolunteers().contains(SharedPrefsUtil.getString(holder.itemView.getContext(), "email", ""))) {
            // Disable the enroll button if already enrolled
            holder.enrollButton.setEnabled(false);
            holder.enrollButton.setText("Enrolled");
        } else {
            // Enable the enroll button if not enrolled
            holder.enrollButton.setEnabled(true);
            holder.enrollButton.setText("Enroll");
            String volunteerEmail = SharedPrefsUtil.getString(holder.itemView.getContext(), "email", "");
            String volunteerName = SharedPrefsUtil.getString(holder.itemView.getContext(), "fullName", "");
            String rating = SharedPrefsUtil.getString(holder.itemView.getContext(), "rating", "");
            String serviceTitle = service.getTitle();
            String serviceID = SharedPrefsUtil.getString(holder.itemView.getContext(), "serviceId", "");

            // Set a click listener for the enroll button
            holder.enrollButton.setOnClickListener(v -> {
                serviceList.get(position).getEnrolledVolunteers().add(SharedPrefsUtil.getString(holder.itemView.getContext(), "email", ""));
                holder.textViewEnrolledVolunteers.setText("Volunteers enrolled: "+service.getEnrolledVolunteers().size()+"/"+String.valueOf(service.getVolunteersNeeded()));
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Service")
                        .document(service.getId())
                        .update("enrolledVolunteers", service.getEnrolledVolunteers())
                        .addOnSuccessListener(aVoid -> {
                            service.getEnrolledVolunteers().add(SharedPrefsUtil.getString(holder.itemView.getContext(), "email", ""));
                            holder.enrollButton.setEnabled(false);
                            Map<String, Object> enrollmentData = new HashMap<>();
                            enrollmentData.put("volunteerName", volunteerName);
                            enrollmentData.put("volunteerEmail", volunteerEmail);
                            enrollmentData.put("serviceTitle", serviceTitle);
                            enrollmentData.put("clientEmail", service.getClientEmail());
                            enrollmentData.put("points", service.getPoints());
                            enrollmentData.put("serviceId", service.getId());
                            enrollmentData.put("serviceDescription", service.getDescription());
                            enrollmentData.put("rating", rating);

                            db.collection("CurrentlyEnrolled")
                                    .add(enrollmentData);
                        })
                        .addOnFailureListener(e -> {
                            // Show an error message if the update fails
                            holder.enrollButton.setEnabled(true);
                        });
            });
        }
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }


    public void filterList(List<Service> filteredList) {
        serviceList = filteredList;
        notifyDataSetChanged();
    }
}

