package com.example.n0584052.whatsfortea;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AccountFragment extends Fragment {

    //private OnFragmentInteractionListener mListener;

    public AccountFragment() {
        // Required empty public constructor
    }

    private ArrayList<String> superMarketStrings;
    private ArrayAdapter<String> adapter;
    private Spinner supermarketChoice;
    private String supermarketChoiceSelected;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users/"+mAuth.getCurrentUser().getUid());
    private String vegChoice = "N";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        final CheckBox vegetarian = (CheckBox) view.findViewById(R.id.vegetarianPref);
        Button updateAccount = (Button) view.findViewById(R.id.updateAccount);
        supermarketChoice = (Spinner) view.findViewById(R.id.supermarketChoice);
        superMarketStrings = new ArrayList<String>();
        String[] arrayString = getResources().getStringArray(R.array.supermarkets);
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,arrayString);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        supermarketChoice.setAdapter(adapter);
        supermarketChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            supermarketChoiceSelected=supermarketChoice.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        vegetarian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vegetarian.isChecked()){
                    vegChoice = "Y";
                }else{
                    vegChoice = "N";
                }
            }
        });

        updateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!supermarketChoiceSelected.equals("Select")) {
                    Log.d("supermarketChoiceSele",supermarketChoiceSelected);
                    mDatabase.child("preferences").child("supermarket").setValue(supermarketChoiceSelected);
                }
                mDatabase.child("preferences").child("vegetarian").setValue(vegChoice);
            }
        });
        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
