package com.example.carecreds.Adapters;

import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carecreds.Model.AllVolunteers;
import com.example.carecreds.R;

import java.util.List;

public class AllVolunteersAdapter extends RecyclerView.Adapter<AllVolunteersAdapter.ViewHolder> {

    private List<AllVolunteers> volunteerList;

    public AllVolunteersAdapter(List<AllVolunteers> volunteerList) {
        this.volunteerList = volunteerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_volunteer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AllVolunteers volunteer = volunteerList.get(position);

        String textToUnderline = volunteer.getEmail();

        SpannableString content = new SpannableString("Email: " + textToUnderline);
        content.setSpan(new UnderlineSpan(), 7, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Underline from 7th index (after "Email: ")
        holder.emailTextView.setText(content);


        holder.nameTextView.setText("Name: " + volunteer.getFullName());
        holder.phoneTextView.setText("Phone: " + volunteer.getPhone());
        holder.genderTextView.setText("Gender: " + volunteer.getGender());
        holder.ratingTextView.setText("Rating: " + volunteer.getRating());

        holder.emailTextView.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + volunteer.getEmail()));
            v.getContext().startActivity(Intent.createChooser(emailIntent, "Send email"));
        });
    }

    @Override
    public int getItemCount() {
        return volunteerList.size();
    }

    public void sortByRating() {
        volunteerList.sort((o1, o2) -> o2.getRating() - o1.getRating());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView, phoneTextView, genderTextView, ratingTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.all_volunteer_name);
            emailTextView = itemView.findViewById(R.id.all_volunteer_email);
            phoneTextView = itemView.findViewById(R.id.all_volunteer_phone);
            genderTextView = itemView.findViewById(R.id.all_volunteer_gender);
            ratingTextView = itemView.findViewById(R.id.all_volunteer_rating);
        }
    }
}

