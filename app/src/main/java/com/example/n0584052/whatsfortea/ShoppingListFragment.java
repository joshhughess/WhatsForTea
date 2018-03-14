package com.example.n0584052.whatsfortea;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShoppingListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ShoppingListFragment extends Fragment  {

    //private OnFragmentInteractionListener mListener;
    ArrayAdapter<String> adapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users/"+mAuth.getCurrentUser().getUid());

    ListView listview;
    ArrayList<String> shoppingList=new ArrayList<>();
    ArrayList<String> hiddenList=new ArrayList<>();
    EditText txtName;
    EditText txtQuantity;

    public ShoppingListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listview = (ListView) getView().findViewById(R.id.listView2);
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,shoppingList);

        Button button = (Button) getView().findViewById(R.id.btnShoppingList);
        txtName = (EditText) getView().findViewById(R.id.txtShoppingListItem);
        txtQuantity = (EditText) getView().findViewById(R.id.txtShoppingListQuantity);

        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                String val =(String) adapterView.getItemAtPosition(position);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle(shoppingList.get(position));
                alertDialogBuilder.setMessage(hiddenList.get(position));
                alertDialogBuilder.setPositiveButton("Bought?", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        Log.d("bought",shoppingList.get(position));
                        mDatabase.child("FoodAtHome").child(shoppingList.get(position)).setValue(hiddenList.get(position));
                        mDatabase.child("ShoppingList").child(shoppingList.get(position)).removeValue();
                        adapter.notifyDataSetChanged();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        mDatabase.child("ShoppingList").addChildEventListener(new ChildEventListener() {
              @Override
              public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                  String key = dataSnapshot.getKey().toString();
                  String value = dataSnapshot.getValue().toString();
                  shoppingList.add(key);
                  hiddenList.add(value);
                  adapter.notifyDataSetChanged();
              }

              @Override
              public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                  String key = dataSnapshot.getKey().toString();
                  String value = dataSnapshot.getValue().toString();
                  for (int i = 0; i < shoppingList.size(); i++) {
                      if (shoppingList.get(i).equals(key)) {
                          shoppingList.remove(i);
                          hiddenList.remove(i);
                          shoppingList.add(key);
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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("ShoppingList").child(txtName.getText().toString()).setValue(txtQuantity.getText().toString());
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
       // mListener = null;
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
