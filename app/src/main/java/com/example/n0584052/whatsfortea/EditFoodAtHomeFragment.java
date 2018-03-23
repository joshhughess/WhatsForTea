package com.example.n0584052.whatsfortea;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditFoodAtHomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditFoodAtHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditFoodAtHomeFragment extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users/"+mAuth.getCurrentUser().getUid());

    public EditFoodAtHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_edit_food_at_home, container, false);
        final TextView editFoodAtHomeItem = view.findViewById(R.id.txtEditFoodAtHomeTitle);
        final EditText editFoodAtHomeQuantity = view.findViewById(R.id.txtEditFoodAtHomeQuantity);
        Button saveBtn = view.findViewById(R.id.btnSaveFoodAtHome);
        Bundle bundle = getArguments();
        final FoodAtHomeItem thisItem = (FoodAtHomeItem) bundle.getSerializable("obj");
        Log.d("foodAtHome",editFoodAtHomeItem.getText().toString());
        editFoodAtHomeItem.setText(editFoodAtHomeItem.getText()+" "+thisItem.getFoodAtHomeName());
        editFoodAtHomeQuantity.setText(thisItem.getFoodAtHomeQuantity());
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("FoodAtHome").child(thisItem.getFoodAtHomeName()).setValue(editFoodAtHomeQuantity.getText().toString());
                Toast.makeText(getContext(), "Saved your new quantity", Toast.LENGTH_SHORT).show();
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
}
