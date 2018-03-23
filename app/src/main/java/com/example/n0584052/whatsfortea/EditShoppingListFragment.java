package com.example.n0584052.whatsfortea;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import java.io.Serializable;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditShoppingListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditShoppingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditShoppingListFragment extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users/"+mAuth.getCurrentUser().getUid());
    public EditShoppingListFragment() {
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

        View view = inflater.inflate(R.layout.fragment_edit_shopping_list, container, false);
        final TextView editShoppingListItem = view.findViewById(R.id.editShoppingListTitle);
        final EditText editShoppingListQuantity = view.findViewById(R.id.editShoppingListQuantity);
        Button saveBtn = view.findViewById(R.id.editShoppingListItemButton);
        Bundle bundle = getArguments();
        final ShoppingListItem thisItem = (ShoppingListItem) bundle.getSerializable("obj");
        editShoppingListItem.setText(editShoppingListItem.getText()+" "+thisItem.getShoppingListName());
        editShoppingListQuantity.setText(thisItem.getShoppingListQuantity());
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_editShoppingList = editShoppingListQuantity.getText().toString();
                str_editShoppingList = str_editShoppingList.replaceAll("[^\\d.]", "");
                str_editShoppingList = str_editShoppingList.replaceAll("[.]", "");
                str_editShoppingList = str_editShoppingList.replaceAll("[,]", "");
                Log.d("str_editShoppingList", str_editShoppingList);
                String str_shoppingListQuantity = thisItem.getShoppingListQuantity();
                str_shoppingListQuantity = str_shoppingListQuantity.replaceAll("[^\\d.]", "");
                str_shoppingListQuantity = str_shoppingListQuantity.replaceAll("[.]", "");
                str_shoppingListQuantity = str_shoppingListQuantity.replaceAll("[,]", "");
                Log.d("item_shoppingListQuan", str_shoppingListQuantity);
                int int_editShoppingList = Integer.valueOf(str_editShoppingList);
                int int_shoppingListQuantity = Integer.valueOf(str_shoppingListQuantity);
                //if edited amount is less than original
                if(int_editShoppingList<int_shoppingListQuantity){
                    //new foodathomevalue
                    final int difference = int_shoppingListQuantity-int_editShoppingList;
                    Log.d("difference","-)"+difference);
                    final boolean[] found = {false};
                    final int[] value = {0};
                    mDatabase.child("FoodAtHome").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Log.d("datasnapsht",dataSnapshot.getKey().toString());
                            if(dataSnapshot.getKey().toString().matches(thisItem.getShoppingListName())) {
                                Log.d("datasnapshot",dataSnapshot.getValue().toString());
                                value[0] = Integer.valueOf(dataSnapshot.getValue().toString());
                                found[0] =true;
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
                    Log.d("found", String.valueOf(found[0]));
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!found[0]) {
                                //not found in food at home
                                mDatabase.child("FoodAtHome").child(thisItem.getShoppingListName()).setValue(difference);
                            }else{
                                mDatabase.child("FoodAtHome").child(thisItem.getShoppingListName()).setValue(difference + value[0]);
                            }
                        }},3000);
                }
                mDatabase.child("ShoppingList").child(thisItem.getShoppingListName()).setValue(editShoppingListQuantity.getText().toString());
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
