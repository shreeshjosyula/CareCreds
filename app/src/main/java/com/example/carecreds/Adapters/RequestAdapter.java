package com.example.carecreds.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carecreds.Model.Request;
import com.example.carecreds.R;
import com.example.carecreds.Utils.SharedPrefsUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private Context context;
    private List<Request> requestList;
    private TextView noRequests;

    public RequestAdapter(Context context, List<Request> requestList, TextView noRequests) {
        this.context = context;
        this.requestList = requestList;
        this.noRequests = noRequests;
        if (requestList.size() == 0) {
            noRequests.setVisibility(View.VISIBLE);
        } else {
            noRequests.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.request_item, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Request request = requestList.get(position);

        holder.serviceTitle.setText("Title: " + request.getServiceName());
        holder.serviceDescription.setText("Description: " + request.getDescription());
        holder.clientEmail.setText("Client Email: " + request.getClientEmail());
        holder.points.setText("Points per job: " + String.valueOf(request.getPoints()));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> updates = new HashMap<>();

                if (request.getServiceId() == null) {
                    Toast.makeText(context, "Error occurred! Contact Us!", Toast.LENGTH_SHORT).show();
                    return;
                }
                db.collection("Service")
                        .document(request.getServiceId())
                        .update("enrolledVolunteers", FieldValue.arrayUnion(request.getVolunteerEmail()))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Map<String, Object> currentlyEnrolled = new HashMap<>();
                                currentlyEnrolled.put("clientEmail", request.getClientEmail());
                                currentlyEnrolled.put("serviceDescription", request.getDescription());
                                currentlyEnrolled.put("serviceTitle", request.getServiceName());
                                currentlyEnrolled.put("points", request.getPoints());
                                currentlyEnrolled.put("rating", SharedPrefsUtil.getString(context, "rating", ""));
                                currentlyEnrolled.put("volunteerEmail", request.getVolunteerEmail());
                                currentlyEnrolled.put("volunteerName", SharedPrefsUtil.getString(context, "fullName", ""));
                                currentlyEnrolled.put("serviceId", request.getServiceId());


                                db.collection("CurrentlyEnrolled")
                                        .add(currentlyEnrolled)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                db.collection("Request")
                                                        .document(request.getRequestId())
                                                        .delete()
                                                        .addOnSuccessListener(aVoid -> {
                                                            if (position >= 0 && position < requestList.size()) {
                                                                requestList.remove(position);
                                                                notifyItemRemoved(position);
                                                                notifyItemRangeChanged(position, requestList.size());
                                                                if (requestList.size() == 0) {
                                                                    noRequests.setVisibility(View.VISIBLE);
                                                                }
                                                            }
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            e.printStackTrace();
                                                        });
                                            } else {
                                                // Handle add request failure
                                                Exception exception = task.getException();
                                                if (exception != null) {
                                                    exception.printStackTrace();
                                                }
                                            }
                                        });
                            }
                        });
            }
        });

        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.collection("Request")
                        .document(request.getRequestId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            requestList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, requestList.size());
                            if (requestList.size() == 0) {
                                noRequests.setVisibility(View.VISIBLE);
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView serviceTitle, serviceDescription, clientEmail, points;
        Button accept, decline;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            serviceTitle = itemView.findViewById(R.id.service_title);
            serviceDescription = itemView.findViewById(R.id.service_description);
            clientEmail = itemView.findViewById(R.id.client_email);
            points = itemView.findViewById(R.id.points);
            accept = itemView.findViewById(R.id.accept);
            decline = itemView.findViewById(R.id.decline);
        }
    }
}
