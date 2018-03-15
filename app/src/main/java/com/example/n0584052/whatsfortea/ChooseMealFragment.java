package com.example.n0584052.whatsfortea;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
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
import java.util.Locale;

import static android.R.attr.data;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChooseMealFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ChooseMealFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    //private OnFragmentInteractionListener mListener;
    ArrayAdapter<String> adapter;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    ListView listview;
    ArrayList<String> chooseMenuList=new ArrayList<>();

    ArrayList<Food> foodObjects = new ArrayList<>();

    public ChooseMealFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_meal, container, false);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final CheckBox vegetarian = (CheckBox) getView().findViewById(R.id.vegetarian);



        final Calendar myCalendar = Calendar.getInstance();
        final EditText dateSelect = (EditText) getView().findViewById(R.id.getDateSelected);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateSelect.setText(sdf.format(myCalendar.getTime()));
            }

        };

        listview = (ListView) getView().findViewById(R.id.chooseMenuListView);
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,chooseMenuList);

        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                String val =(String) adapterView.getItemAtPosition(position);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle(foodObjects.get(position).getFoodItemName());
                String msg = "";
                for(int i=0;i<foodObjects.get(position).getIngredientNames().size();i++){
                    msg += foodObjects.get(position).getIngredientQuantities().get(i)+" - "+foodObjects.get(position).getIngredientNames().get(i)+"\n";
                }
                alertDialogBuilder.setMessage(msg);
                if(dateSelect.getParent()!=null)
                    ((ViewGroup)dateSelect.getParent()).removeView(dateSelect);
                alertDialogBuilder.setView(dateSelect);
                dateSelect.setVisibility(getView().VISIBLE);
                dateSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new DatePickerDialog(getActivity(), date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
                alertDialogBuilder.setPositiveButton("Add to Meal Plan?", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things

                        mDatabase.child("/users/"+mAuth.getCurrentUser().getUid()+"/MealPlan").child(dateSelect.getText().toString().replaceAll("\\/","-")).child(foodObjects.get(position).getFoodItemName()).child("vegetarian").setValue(foodObjects.get(position).getVegetarian());
                        for(int i=0;i<foodObjects.get(position).getIngredientNames().size();i++) {
                            mDatabase.child("/users/"+mAuth.getCurrentUser().getUid()+"/MealPlan").child(dateSelect.getText().toString().replaceAll("\\/","-")).child(foodObjects.get(position).getFoodItemName()).child("ingredients").child(foodObjects.get(position).getIngredientNames().get(i)).setValue(foodObjects.get(position).getIngredientQuantities().get(i));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        mDatabase.child("/Food").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                final Food food = new Food();
                final String thisKey = dataSnapshot.getKey().toString();
                Log.d("hello",thisKey);
                String value = dataSnapshot.getValue().toString();
                food.setFoodItemName(thisKey);
                mDatabase.child("/Food/"+thisKey).addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String key = dataSnapshot.getKey().toString();
                        String value = dataSnapshot.getValue().toString();
                        String vegetarianYN = null;
                        if(dataSnapshot.getKey().equals("vegetarian")){
                            vegetarianYN = dataSnapshot.getValue().toString();
                        }else{
                            mDatabase.child("/Food/"+thisKey).child("ingredients").addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    String key = dataSnapshot.getKey().toString();
                                    String value = dataSnapshot.getValue().toString();
                                    food.addIngredientName(key);
                                    food.addIngredientQuantity(value);
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
                        }
                        food.setVegetarian(vegetarianYN);
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
                foodObjects.add(food);
                chooseMenuList.add(thisKey);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey().toString();
                String value = dataSnapshot.getValue().toString();
                for(int i=0;i<chooseMenuList.size();i++){
                    if(chooseMenuList.get(i).equals(key)){
                        chooseMenuList.remove(i);
                        chooseMenuList.add(key);
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

        vegetarian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vegetarian.isChecked()){
                    for(int i=0;i<chooseMenuList.size();i++){
                        if(foodObjects.get(i).getVegetarian().equals("N")){
                            Log.d("foodObject",foodObjects.get(i).getFoodItemName());
                            foodObjects.remove(i);
                        }
                    }
                }
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
        //mListener = null;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

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
