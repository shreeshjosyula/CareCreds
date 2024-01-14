package com.example.carecreds.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carecreds.Model.AllVolunteers;
import com.example.carecreds.R;
import com.example.carecreds.Utils.SharedPrefsUtil;
import com.example.carecreds.Utils.SnackBarUtils;
import com.example.carecreds.dialog.VolunteerDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolunteerDialogAdapter extends RecyclerView.Adapter<VolunteerDialogAdapter.ViewHolder> {

    private final List<AllVolunteers> volunteerList;
    private final Context context;
    private final VolunteerDialogFragment volunteerDialogFragment;

    public VolunteerDialogAdapter(Context context, List<AllVolunteers> volunteerList, VolunteerDialogFragment volunteerDialogFragment) {
        this.context = context;
        this.volunteerList = volunteerList;
        this.volunteerDialogFragment = volunteerDialogFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.volunteer_dialog_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AllVolunteers volunteer = volunteerList.get(position);

        holder.nameTextView.setText("Name: " + volunteer.getFullName());
        holder.ratingTextView.setText("| Rating: " + volunteer.getRating());

        holder.itemView.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // send request to volunteer in Request collection with auto id with map of client email, volunteer email, service id, service name
            Map<String, Object> request = new HashMap<>();
            request.put("clientEmail", SharedPrefsUtil.getString(context, "email", ""));
            request.put("volunteerEmail", volunteer.getEmail());
            request.put("serviceID", SharedPrefsUtil.getString(context, "serviceId", ""));
            request.put("serviceName", SharedPrefsUtil.getString(context, "serviceName", ""));
            request.put("points", SharedPrefsUtil.getInt(context, "servicePoints", 0));
            request.put("description", SharedPrefsUtil.getString(context, "serviceDescription", ""));
            db.collection("Request")
                    .add(request)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            SnackBarUtils.showCustomSnackbar(volunteerDialogFragment.requireDialog().findViewById(android.R.id.content), "Request sent");
                            volunteerDialogFragment.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            SnackBarUtils.showCustomSnackbar(volunteerDialogFragment.requireDialog().findViewById(android.R.id.content), "Error sending request");
                            volunteerDialogFragment.dismiss();
                        }
                    });

        });
    }

    @Override
    public int getItemCount() {
        return volunteerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView ratingTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.volunteer_name);
            ratingTextView = itemView.findViewById(R.id.volunteer_rating);
        }
    }
}

