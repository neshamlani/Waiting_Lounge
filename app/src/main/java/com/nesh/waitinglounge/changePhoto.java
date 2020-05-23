package com.nesh.waitinglounge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class changePhoto extends AppCompatActivity {
    ImageView im;
    ProgressBar pb;
    Uri selectedImage;
    FirebaseAuth mAuth;
    FirebaseFirestore fs;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_photo);
        im=(ImageView)findViewById(R.id.change);
        pb=(ProgressBar)findViewById(R.id.changePhotoProgessBar);
        pb.setVisibility(View.GONE);
        Intent in=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(in, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        selectedImage = data.getData();
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Picasso.with(this).load(selectedImage).into(im);
        }
    }
    public String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(cr.getType(uri));
    }
    public void updatePhoto(View view){
        pb.setVisibility(View.VISIBLE);
        final Map<String, Object> data=new HashMap<>();
        mAuth=FirebaseAuth.getInstance();
        fs=FirebaseFirestore.getInstance();
        final String user=mAuth.getCurrentUser().getEmail();
        storageReference= FirebaseStorage.getInstance().getReference(user);
        final StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(selectedImage));
        fileRef.putFile(selectedImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String url=uri.toString();
                                data.clear();
                                data.put("Profile_Photo",url);
                                fs.collection("Users_Clients").document(user)
                                        .set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(),"Uploaded",Toast.LENGTH_LONG).show();
                                        pb.setVisibility(View.GONE);
                                        finish();
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                                pb.setVisibility(View.GONE);
                                            }
                                        });
                            }
                        });
                    }
                });
    }
}
