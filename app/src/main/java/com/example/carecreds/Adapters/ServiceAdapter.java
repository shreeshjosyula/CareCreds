package com.example.carecreds.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carecreds.R;
import com.example.carecreds.Model.Service;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<Service> serviceList;

    // Constructor to receive the service list
    public ServiceAdapter(List<Service> serviceList) {
        this.serviceList = serviceList;
    }

    // View Holder for each service item
    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceNameTextView, serviceDescriptionTextView, volunteersNeededTextView,
                pointsPerJobTextView, hoursRequiredTextView, extraNotesTextView;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.serviceNameTextView);
            serviceDescriptionTextView = itemView.findViewById(R.id.serviceDescriptionTextView);
            volunteersNeededTextView = itemView.findViewById(R.id.volunteersNeededTextView);
            pointsPerJobTextView = itemView.findViewById(R.id.pointsPerJobTextView);
            hoursRequiredTextView = itemView.findViewById(R.id.hoursRequiredTextView);
            extraNotesTextView = itemView.findViewById(R.id.extraNotesTextView);
        }
    }

    public void setServiceList(Service service, TextView noServicesFoundTextView) {
        noServicesFoundTextView.setVisibility(View.GONE);
        this.serviceList.add(service);
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_item, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);

        // Bind data to the ViewHolder
        holder.serviceNameTextView.setText("Service: "+ service.getTitle());
        holder.serviceDescriptionTextView.setText("Service description: "+ service.getDescription());
        holder.volunteersNeededTextView.setText("Volunteers needed: "+String.valueOf(service.getVolunteersNeeded()));
        holder.pointsPerJobTextView.setText("Points per job: "+String.valueOf(service.getPoints()));
        holder.hoursRequiredTextView.setText("Hours expected: "+String.valueOf(service.getHours()));
        holder.extraNotesTextView.setText("Notes: "+service.getExtraNotes());
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
