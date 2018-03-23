package com.example.n0584052.whatsfortea;

import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class FeedbackFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public FeedbackFragment() {
        // Required empty public constructor
    }

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("feedback/"+mAuth.getCurrentUser().getUid());
    //ensure only one feedback per hour allowed
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
    Date todaysDate = new Date();
    String[] name = mAuth.getCurrentUser().getDisplayName().split(" ");
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        Button btn = view.findViewById(R.id.btn_feedback);
        final RatingBar rating = view.findViewById(R.id.appRating);
        final EditText comments = view.findViewById(R.id.feedbackMessage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(dateFormat.format(todaysDate)).child("feedbackComments").setValue(comments.getText().toString());
                mDatabase.child(dateFormat.format(todaysDate)).child("rating").setValue(rating.getRating());
                Toast.makeText(getContext(), "Thank you for your feedback "+name[0], Toast.LENGTH_SHORT).show();
            }

        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
