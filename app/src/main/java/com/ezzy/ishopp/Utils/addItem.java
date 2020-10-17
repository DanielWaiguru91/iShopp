package com.ezzy.ishopp.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ezzy.ishopp.R;
import com.ezzy.ishopp.models.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class addItem extends DialogFragment {

    private ImageView openCamera, openGallery, mItemImage;
    private EditText mItemName, mItemDescription, mItemPrice;
    private Button mSaveButton;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private static final int picId = 1;
    private static final int SELECT_FILE_ID = 12;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_vendor,null);

        openCamera = view.findViewById(R.id.openCamera);
        openGallery = view.findViewById(R.id.openGallery);
        mItemName = view.findViewById(R.id.edittextitemName);
        mItemDescription = view.findViewById(R.id.edittextitemDes);
        mItemPrice = view.findViewById(R.id.edittextitemprice);
        mSaveButton = view.findViewById(R.id.saveButton);
        mItemImage = view.findViewById(R.id.itemImage);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem(
                        mItemName.getText().toString(),
                        mItemPrice.getText().toString(),
                        mItemDescription.getText().toString(),
                        ""
                );
            }
        });

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    private void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, picId);
    }

    private void openGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, SELECT_FILE_ID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == picId && resultCode == Activity.RESULT_OK){
            Bitmap image = (Bitmap)data.getExtras().get("data");
            mItemImage.setImageBitmap(image);
        }
        if (requestCode == SELECT_FILE_ID && resultCode == Activity.RESULT_OK){
            Uri selectedImageUri = data.getData();
            mItemImage.setImageBitmap(BitmapFactory.decodeFile(String.valueOf(selectedImageUri)));
        }

    }

    private void saveItem(String name, String price, String description, String image_url){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        Item mItem = new Item();
        mItem.setName(name);
        mItem .setPrice(price);
        mItem.setDescription(description);
        mItem.setImage_url(image_url);

        mDatabaseReference.child("store")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("items")
                .setValue(mItem)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        makeToast("Item saved");
                        getDialog().dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                makeToast("Error adding item");
            }
        });
    }

    private void makeToast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
