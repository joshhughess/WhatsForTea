package com.example.n0584052.whatsfortea;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FoodAtHomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FoodAtHomeFragment extends ListFragment implements AdapterView.OnItemClickListener {

    //private OnFragmentInteractionListener mListener;
    ArrayAdapter<String> adapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users/"+mAuth.getCurrentUser().getUid());

    private ListView lv;
    public FoodAtHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final ArrayList<String> items = new ArrayList<String>();
        final ArrayList<String> itemQuantities = new ArrayList<String>();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                items.add((String) snapshot.child("FoodAtHome").child("Item").child("ItemName").getValue());
                itemQuantities.add((String) snapshot.child("FoodAtHome").child("Item").child("ItemQuantity").getValue());
                System.out.println(snapshot.child("FoodAtHome").child("Item").getValue());  //prints "Do you have data? You'll love Firebase."
                for(int i = 0;i<items.size();i++){
                    Log.d("test",items.get(i).toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        lv = (ListView) container.findViewById(R.id);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1,items);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
        View view = inflater.inflate(R.layout.fragment_food_at_home, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
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
