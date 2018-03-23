package com.example.n0584052.whatsfortea;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddChooseMealFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddChooseMealFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddChooseMealFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    String mCurrentPhotoPath;
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://whatsfortea-f6090.appspot.com");
    StorageReference storageRef = storage.getReference();
    int count = 1;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Food");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public AddChooseMealFragment() {
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
        View view = inflater.inflate(R.layout.fragment_add_choose_meal, container, false);
        final EditText mealName = view.findViewById(R.id.txtMealName);
        final EditText ingredientName = view.findViewById(R.id.txtIngredient);
        final EditText ingredientName1 = view.findViewById(R.id.txtIngredient1);
        final EditText ingredientName2 = view.findViewById(R.id.txtIngredient2);
        final EditText ingredientName3 = view.findViewById(R.id.txtIngredient3);
        final EditText ingredientName4 = view.findViewById(R.id.txtIngredient4);
        final EditText ingredientName5 = view.findViewById(R.id.txtIngredient5);
        final EditText ingredientName6 = view.findViewById(R.id.txtIngredient6);
        final EditText ingredientName7 = view.findViewById(R.id.txtIngredient7);
        final EditText ingredientName8 = view.findViewById(R.id.txtIngredient8);

        final EditText ingredientQuantity = view.findViewById(R.id.txtQuantity);
        final EditText ingredientQuantity1 = view.findViewById(R.id.txtQuantity1);
        final EditText ingredientQuantity2 = view.findViewById(R.id.txtQuantity2);
        final EditText ingredientQuantity3 = view.findViewById(R.id.txtQuantity3);
        final EditText ingredientQuantity4 = view.findViewById(R.id.txtQuantity4);
        final EditText ingredientQuantity5 = view.findViewById(R.id.txtQuantity5);
        final EditText ingredientQuantity6 = view.findViewById(R.id.txtQuantity6);
        final EditText ingredientQuantity7 = view.findViewById(R.id.txtQuantity7);
        final EditText ingredientQuantity8 = view.findViewById(R.id.txtQuantity8);

        final ArrayList<EditText> ingredientNames = new ArrayList<>();
        ingredientNames.add(ingredientName);
        ingredientNames.add(ingredientName2);
        ingredientNames.add(ingredientName3);
        ingredientNames.add(ingredientName4);
        ingredientNames.add(ingredientName5);
        ingredientNames.add(ingredientName6);
        ingredientNames.add(ingredientName7);
        ingredientNames.add(ingredientName8);

        final ArrayList<EditText> ingredientQuantities = new ArrayList<>();
        ingredientQuantities.add(ingredientQuantity);
        ingredientQuantities.add(ingredientQuantity2);
        ingredientQuantities.add(ingredientQuantity3);
        ingredientQuantities.add(ingredientQuantity4);
        ingredientQuantities.add(ingredientQuantity5);
        ingredientQuantities.add(ingredientQuantity6);
        ingredientQuantities.add(ingredientQuantity7);
        ingredientQuantities.add(ingredientQuantity8);


        Button btnAddMeal = view.findViewById(R.id.btnAddMeal);
        final CheckBox vegCheckBox = view.findViewById(R.id.vegetarianAddMeal);

        FloatingActionButton fab1 = view.findViewById(R.id.addIngredient);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count!=8){
                    ingredientNames.get(count).setVisibility(view.VISIBLE);
                    ingredientQuantities.get(count).setVisibility(view.VISIBLE);
                    count++;
                }else{
                    Toast.makeText(getContext(), "8 ingredients only!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAddMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mealName.getText().toString().matches("")){
                    boolean ingredientListClear = true;
                    Log.d("count","="+count);
                    for (int i = 0; i < count; i++) {
                        if (ingredientNames.get(i).getText().toString().matches("") || ingredientQuantities.get(i).getText().toString().matches("")) {
                            ingredientListClear = false;
                        }
                    }
                    if(!ingredientListClear){
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setTitle("Error");
                        alertDialogBuilder.setMessage("Missing ingredient name or quantity: "+(count-1));
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }else{
                        final int REQUEST_IMAGE_CAPTURE = 1;
                        final int REQUEST_TAKE_PHOTO = 1;

                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // Ensure that there's a camera activity to handle the intent
                        File photoFile = null;
                        String nameTest = mealName.getText().toString().toLowerCase().replace(" ","");
                        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                            // Create the File where the photo should go
                            try {
                                photoFile = createImageFile(nameTest);
                                Log.d("photoFile","-"+photoFile);
                            } catch (IOException ex) {
                                // Error occurred while creating the File
                                Log.d("exception", "onClick: "+ex);
                            }
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                Uri photoURI = FileProvider.getUriForFile(getContext(),
                                        "com.example.android.fileprovider",
                                        photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                File f = new File(mCurrentPhotoPath);
                                Uri contentUri = Uri.fromFile(f);
                                Log.d("contentURi","-"+contentUri);
                                mediaScanIntent.setData(contentUri);
                                getContext().sendBroadcast(mediaScanIntent);
                            }
                        }
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setTitle(mealName.getText().toString());
                        final File finalPhotoFile = photoFile;
                        alertDialogBuilder.setPositiveButton("Save Meal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Uri file = Uri.fromFile(new File(finalPhotoFile.getPath()));
                                StorageReference foodItemsRef = storageRef.child("FoodItems/"+file.getLastPathSegment());
                                UploadTask uploadTask = foodItemsRef.putFile(file);
// Register observers to listen for when the download is done or if it fails
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        Log.d("exception","-"+exception);
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                        String upperMealName = mealName.getText().toString().substring(0,1).toUpperCase() + mealName.getText().toString().substring(1);
                                        Log.d("upperMeaName",upperMealName);
                                        if(vegCheckBox.isChecked()){
                                            Log.d("Checked","!");
                                            mDatabase.child(upperMealName).child("vegetarian").setValue("Y");
                                        }else{
                                            Log.d("Not Checked","!");
                                            mDatabase.child(upperMealName).child("vegetarian").setValue("N");
                                        }
                                        for(int i=0;i<count;i++){
                                            Log.d("ingredientNames",ingredientNames.get(i).getText().toString());
                                            Log.d("ingredientQuantities",ingredientQuantities.get(i).getText().toString());
                                            mDatabase.child(upperMealName).child("ingredients").child(ingredientNames.get(i).getText().toString()).setValue(ingredientQuantities.get(i).getText().toString());
                                        }
                                    }
                                });
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }else{
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("Error");
                    alertDialogBuilder.setMessage("Please make sure you enter a title for the meal.");
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private File createImageFile(String name) throws IOException {
        // Create an image file name
        String imageFileName = name+".jpg";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir,imageFileName);
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
