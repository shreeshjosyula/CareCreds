package com.example.carecreds.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.carecreds.Model.CurrentlyEnrolled;
import com.example.carecreds.R;
import com.example.carecreds.Utils.SharedPrefsUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CurrentlyEnrolledAdapter extends RecyclerView.Adapter<CurrentlyEnrolledAdapter.CurrentlyEnrolledViewHolder> {

    private List<CurrentlyEnrolled> currentlyEnrolledList;
    private TextView textView;

    public CurrentlyEnrolledAdapter(List<CurrentlyEnrolled> currentlyEnrolledList, TextView textView) {
        this.currentlyEnrolledList = currentlyEnrolledList;
        this.textView = textView;
    }

    @NonNull
    @Override
    public CurrentlyEnrolledViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.enrolled_volunteer_item, parent, false);
        return new CurrentlyEnrolledViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentlyEnrolledViewHolder holder, int position) {
        CurrentlyEnrolled currentlyEnrolled = currentlyEnrolledList.get(position);

        holder.volunteerName.setText("Volunteer Name: "+currentlyEnrolled.getVolunteerName());
        holder.volunteerEmail.setText("Volunteer Email: "+currentlyEnrolled.getVolunteerEmail());
        holder.serviceEmail.setText("Client Email: " + currentlyEnrolled.getClientEmail());
        holder.points.setText("Points per job: "+String.valueOf(currentlyEnrolled.getPoints()));
        holder.rating.setText("Rating: "+String.valueOf(currentlyEnrolled.getRating()));
        holder.serviceDescription.setText("Service Description: "+currentlyEnrolled.getServiceDescription());
        holder.serviceTitle.setText("Service Title: "+currentlyEnrolled.getServiceTitle());

        SharedPrefsUtil.getString(holder.itemView.getContext(), "userType", "");
        if (SharedPrefsUtil.getString(holder.itemView.getContext(), "userType", "").equals("volunteer")) {
            holder.completeServiceButton.setVisibility(View.GONE);
            holder.rateVolunteerTv.setVisibility(View.GONE);
            holder.ratingBar.setVisibility(View.GONE);
        }

        // Handle button click or any other functionality related to the button
        holder.completeServiceButton.setOnClickListener(view -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // calculate avg rating
            try {
                float rating = holder.ratingBar.getRating();
                double doubleRate = Double.parseDouble(currentlyEnrolled.getRating());
                int ratingInt = (int) doubleRate;
                double newRating = (ratingInt + rating) / 2;
                if (ratingInt != 0 || rating != 0) {
                    updateRating(view, db, newRating, currentlyEnrolled);
                } else {
                    updateRating(view, db, rating, currentlyEnrolled);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            updatePoints(view, db, currentlyEnrolled.getPoints(), position, currentlyEnrolled);
        });
    }

    public void changeServiceStatusToCompleted(View view, FirebaseFirestore db, int position, CurrentlyEnrolled currentlyEnrolled) {
        db.collection("Service")
                .document(currentlyEnrolled.getServiceID())
                .update("isCompleted", true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        removeFromCurrentlyEnrolled(view, db, currentlyEnrolled.getId(), position);
                    }
                });
    }

    private void removeFromCurrentlyEnrolled(View view, FirebaseFirestore db, String id, int position) {
        db.collection("CurrentlyEnrolled")
                .document(id)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentlyEnrolledList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, currentlyEnrolledList.size());
                        if (currentlyEnrolledList.isEmpty()) {
                            textView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void updatePoints(View view, FirebaseFirestore db, int points, int position, CurrentlyEnrolled currentlyEnrolled) {
        db.collection("CareCred")
                .document(currentlyEnrolled.getVolunteerEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int currentPoints = Integer.parseInt(String.valueOf(task.getResult().get("points")));
                        db.collection("CareCred")
                                .document(currentlyEnrolled.getVolunteerEmail())
                                .update("points", currentPoints + points)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        changeServiceStatusToCompleted(view, db, position, currentlyEnrolled);
                                    }
                                });
                    }
                });
    }

    private void updateRating(View view, FirebaseFirestore db, double newRating, CurrentlyEnrolled currentlyEnrolled) {
        db.collection("Volunteer")
                .document(currentlyEnrolled.getVolunteerEmail())
                .update("rating", newRating);
    }

    @Override
    public int getItemCount() {
        return currentlyEnrolledList.size();
    }

    public static class CurrentlyEnrolledViewHolder extends RecyclerView.ViewHolder {
        TextView volunteerName, volunteerEmail, serviceEmail, points, rating, serviceDescription, serviceTitle, rateVolunteerTv;
        Button completeServiceButton;

        RatingBar ratingBar;

        public CurrentlyEnrolledViewHolder(@NonNull View itemView) {
            super(itemView);
            volunteerName = itemView.findViewById(R.id.volunteer_name);
            volunteerEmail = itemView.findViewById(R.id.volunteer_email);
            serviceEmail = itemView.findViewById(R.id.service_email);
            points = itemView.findViewById(R.id.points);
            rating = itemView.findViewById(R.id.rating);
            serviceDescription = itemView.findViewById(R.id.service_description);
            serviceTitle = itemView.findViewById(R.id.service_title);
            completeServiceButton = itemView.findViewById(R.id.rate_volunteer_button);
            rateVolunteerTv = itemView.findViewById(R.id.rate_volunteer_text);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
