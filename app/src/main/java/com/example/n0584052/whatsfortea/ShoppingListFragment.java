package com.example.n0584052.whatsfortea;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.io.Serializable;
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

        txtName = (EditText) getView().findViewById(R.id.txtShoppingListItem);
        txtQuantity = (EditText) getView().findViewById(R.id.txtShoppingListQuantity);

        listview.setAdapter(adapter);

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Delete "+shoppingList.get(position)+" from your shopping list?");
                alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "Deleted "+shoppingList.get(position)+" from list!", Toast.LENGTH_SHORT).show();
                        mDatabase.child("ShoppingList").child(shoppingList.get(position)).removeValue();
                        shoppingList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });
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
                        shoppingList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                }).setNeutralButton("Edit amount", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ShoppingListItem thisItem = new ShoppingListItem();
                        thisItem.setShoppingListName(shoppingList.get(position));
                        thisItem.setShoppingListQuantity(hiddenList.get(position));
                        Fragment newFragment = new EditShoppingListFragment();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("obj", thisItem);
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

        FloatingActionButton addButton = (FloatingActionButton) getView().findViewById(R.id.addToShoppingList);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Add an item to your Shopping List");
                txtName.setText("");
                txtQuantity.setText("");
                Context context = getContext();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                if(txtName.getParent()!=null)
                    ((ViewGroup)txtName.getParent()).removeView(txtName);
                if(txtQuantity.getParent()!=null)
                    ((ViewGroup)txtQuantity.getParent()).removeView(txtQuantity);
                layout.addView(txtName);
                layout.addView(txtQuantity);
                alertDialogBuilder.setView(layout);
                txtName.setVisibility(getView().VISIBLE);
                txtQuantity.setVisibility(getView().VISIBLE);
                alertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(txtName.getText().toString().equals("") && txtQuantity.getText().toString().equals("")) {
                            Toast.makeText(getContext(),"You need both a quantity and a name to add to your shopping list!",Toast.LENGTH_LONG).show();
                        }else{
                            mDatabase.child("ShoppingList").child(txtName.getText().toString()).setValue(txtQuantity.getText().toString());
                            Toast.makeText(getContext(), "Added " + txtName.getText() + " to your shopping list", Toast.LENGTH_SHORT).show();
                            //do things
                            adapter.notifyDataSetChanged();
                        }
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
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                adapter.notifyDataSetChanged();
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
