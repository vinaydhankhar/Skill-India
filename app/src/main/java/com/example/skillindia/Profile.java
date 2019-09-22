package com.example.skillindia;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Profile extends AppCompatActivity {
    Button signOut;
    CircleImageView userPic;

    String username,type,interest,email,name,phone;
    TextView userName,Type,Interest,Email,Name,Phone;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ValueEventListener mProfileListener;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    private Bitmap bitmap;

    static int pReqCode=1;
    static int REQCode=1;
    Upload upload;
    Uri pickedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        signOut=findViewById(R.id.signout);
       // userPic=findViewById(R.id.userPhoto);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
       // storageReference= FirebaseStorage.getInstance().getReference("userPhotos");

        userName=findViewById(R.id.username);
        Type=findViewById(R.id.type);
        Interest=findViewById(R.id.interest);
        Email=findViewById(R.id.email);
        Name=findViewById(R.id.name);
        Phone=findViewById(R.id.phone);

        email=firebaseUser.getEmail();
        Email.setText(firebaseUser.getEmail());  //set Email


        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseAuth.signOut();
                startActivity(new Intent(v.getContext(),MainActivity.class));
            }
        });

        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=22)
                {
                    checkAndRequestForPermission();
                }
                else
                {
                    openGallery();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK && requestCode==REQCode && data!=null && data.getData()!=null)
        {
            //the user has successfully picked an image from gallery
            //we need to save its reference
            pickedImage=data.getData();
            userPic.setImageURI(pickedImage);

            //send to storage
            //change profile photo here
            // i have to code

        }
    }


    private String getFileExtension(Uri uri)  //to get extension of file
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return ((MimeTypeMap) mime).getExtensionFromMimeType(cr.getType(uri));
    }


    private void checkAndRequestForPermission() {

        if(ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(Profile.this,Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Toast.makeText(Profile.this,"Please Accept For Required Permission",Toast.LENGTH_SHORT).show();
            }
            else
            {
                ActivityCompat.requestPermissions(Profile.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},pReqCode);
            }
        }
        else
        {
            openGallery();
        }
    }
    private void openGallery()
    {
        //todo open gallery intent and wait for user to pick animager
        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQCode);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //initialize profile pic
              //  upload=dataSnapshot.child("photo").getValue(Upload.class);


             //   Picasso.get().load(upload.getImageURL()).into(userPic);  //picaso is 3rd party library for images with android

                name=dataSnapshot.child("name").getValue().toString();
                Name.setText(name);

                username=(dataSnapshot.child("username").getValue().toString());
                userName.setText(username);

                phone=(dataSnapshot.child("phone").getValue().toString());
                Phone.setText(phone);

                type=(dataSnapshot.child("userType").getValue().toString());
                Type.setText(type);


                interest=(dataSnapshot.child("interest").getValue().toString());
                Interest.setText(interest);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                int w = Log.w("Profile", "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(Profile.this, "Failed to load profile",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };


        databaseReference.addValueEventListener(profileListener);
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        mProfileListener = profileListener;


    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mProfileListener != null) {
            databaseReference.removeEventListener(mProfileListener);
        }

    }


}