package com.example.n0584052.whatsfortea;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
public class FoodAtHomeFragment extends Fragment {

    //private OnFragmentInteractionListener mListener;
    ArrayAdapter<String> adapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users/"+mAuth.getCurrentUser().getUid());

    ListView listview;
    ArrayList<String> foodAtHomeList=new ArrayList<>();
    ArrayList<String> hiddenList=new ArrayList<>();
    EditText txtName;
    EditText txtQuantity;

    public FoodAtHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food_at_home, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listview = (ListView) getView().findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,foodAtHomeList);

        final EditText txtName = getView().findViewById(R.id.txt_foodAtHomeName);
        final EditText txtQuantity = getView().findViewById(R.id.txt_FoodAtHomeQuantity);

        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                String val =(String) adapterView.getItemAtPosition(position);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle(foodAtHomeList.get(position));
                alertDialogBuilder.setMessage(hiddenList.get(position));
                alertDialogBuilder.setNeutralButton("Edit Item", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FoodAtHomeItem foodAtHomeItem = new FoodAtHomeItem();
                        foodAtHomeItem.setFoodAtHomeName(foodAtHomeList.get(position));
                        foodAtHomeItem.setFoodAtHomeQuantity(hiddenList.get(position));
                        Fragment newFragment = new EditFoodAtHomeFragment();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("obj", foodAtHomeItem);
                        newFragment.setArguments(bundle);
                        transaction.replace(R.id.mainContainer, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Delete "+foodAtHomeList.get(position)+" from Food at Home?");
                alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "Deleted "+foodAtHomeList.get(position)+" from list!", Toast.LENGTH_SHORT).show();
                        mDatabase.child("FoodAtHome").child(foodAtHomeList.get(position)).removeValue();
                        foodAtHomeList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });
        mDatabase.child("FoodAtHome").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey().toString();
                String value = dataSnapshot.getValue().toString();
                foodAtHomeList.add(key);
                hiddenList.add(value);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey().toString();
                String value = dataSnapshot.getValue().toString();
                for(int i=0;i<foodAtHomeList.size();i++){
                    if(foodAtHomeList.get(i).equals(key)){
                        foodAtHomeList.remove(i);
                        hiddenList.remove(i);
                        foodAtHomeList.add(key);
                        hiddenList.add(value);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FloatingActionButton fab = getView().findViewById(R.id.fab_foodAtHome);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Add a food at home item");
                txtName.setText("");
                txtQuantity.setText("");
                Context context = getContext();
                LinearLayout layout = new LinearLayout(context);
                if(txtName.getParent()!=null)
                    ((ViewGroup)txtName.getParent()).removeView(txtName);
                if(txtQuantity.getParent()!=null)
                    ((ViewGroup)txtQuantity.getParent()).removeView(txtQuantity);
                layout.addView(txtName);
                layout.addView(txtQuantity);
                alertDialogBuilder.setView(layout);
                txtName.setVisibility(getView().VISIBLE);
                txtQuantity.setVisibility(getView().VISIBLE);
                layout.setOrientation(LinearLayout.VERTICAL);
                alertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(txtName.getText().toString().equals("") && txtQuantity.getText().toString().equals("")) {
                            Toast.makeText(getContext(),"You need both a quantity and a name to add to your food at home!",Toast.LENGTH_LONG).show();
                        }else {
                            mDatabase.child("FoodAtHome").child(txtName.getText().toString()).setValue(txtQuantity.getText().toString());
                            Toast.makeText(getContext(), "Added " + txtName.getText().toString() + " to list!", Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


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
