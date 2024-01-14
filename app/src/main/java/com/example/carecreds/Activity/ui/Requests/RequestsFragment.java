package com.example.carecreds.Activity.ui.Requests;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carecreds.Adapters.RequestAdapter;
import com.example.carecreds.Model.Request;
import com.example.carecreds.R;
import com.example.carecreds.Utils.SharedPrefsUtil;
import com.example.carecreds.Utils.SnackBarUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RequestsFragment extends Fragment {

    List<Request> userRequestsList;
    TextView noRequestsFoundTextView;
    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userEmail = SharedPrefsUtil.getString(requireContext(), "email", "");
        CollectionReference requestsCollectionRef = db.collection("Request");
        RecyclerView recyclerView = view.findViewById(R.id.requests_recycler_view);
        noRequestsFoundTextView = view.findViewById(R.id.no_requests_found_text_view);
        Query userRequestsQuery = requestsCollectionRef.whereEqualTo("volunteerEmail", userEmail);
        userRequestsQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ProgressBar progressBar = view.findViewById(R.id.requests_progress_bar);
                progressBar.setVisibility(View.GONE);
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    userRequestsList = new ArrayList<>();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Request request = document.toObject(Request.class);
                        if (request != null) {
                            request.setRequestId(document.getId());
                            userRequestsList.add(request);
                        }
                    }

                    if (userRequestsList.size() == 0) {
                        noRequestsFoundTextView.setVisibility(View.VISIBLE);
                    }
                    RequestAdapter adapter = new RequestAdapter(requireContext(), userRequestsList, noRequestsFoundTextView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext())); // Or use GridLayoutManager if needed
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                        noRequestsFoundTextView.setVisibility(View.VISIBLE);
                }
            } else {
                SnackBarUtils.showCustomSnackbar(requireActivity().findViewById(android.R.id.content), "Error getting documents.");
            }
        });
        return view;
    }
}