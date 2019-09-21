package com.example.skillindia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Registeract extends AppCompatActivity {
 private static final String TAG = "Registeract";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    String guid, fileURL;

    private EditText mEmailField;
    private EditText mPasswordField;

    private EditText mName;
    private EditText mPhone;
    private TextView mSignInTextView;
    private Button mSignUpButton;
    private CircleImageView userPic;
    private ProgressDialog progressDialog;
   // Upload upload;

    static int pReqCode = 1;
    static int REQCode = 1;
    Uri pickedImage;
    boolean result = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeract);

       mDatabase = FirebaseDatabase.getInstance().getReference();
       mAuth = FirebaseAuth.getInstance();
       storageReference = FirebaseStorage.getInstance().getReference("userPhotos"); //folder in firebase storage

       // Views
       mEmailField = findViewById(R.id.TextEmail);
       mPasswordField = findViewById(R.id.TextPassword);
       mSignInTextView = findViewById(R.id.Signin);
       mSignUpButton = findViewById(R.id.Signup);
       mName = findViewById(R.id.TextName);
       mPhone = findViewById(R.id.TextPhone);
       userPic = findViewById(R.id.userPhoto);

       progressDialog = new ProgressDialog(this);
       // Click listeners
       mSignInTextView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
             Intent intent = new Intent(Registeract.this,Login.class);
             startActivity(intent);
          }
       });
       mSignUpButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
             signUp();
          }
       });


       userPic.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
             if (Build.VERSION.SDK_INT >= 22) {
                checkAndRequestForPermission();
             } else {
                openGallery();
             }
          }
       });

    }

   //check if permissios are granterd or not
   private void checkAndRequestForPermission() {

      if (ContextCompat.checkSelfPermission(Registeract.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
         if (ActivityCompat.shouldShowRequestPermissionRationale(Registeract.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(Registeract.this, "Please Accept For Required Permission", Toast.LENGTH_SHORT).show();
         } else {
            ActivityCompat.requestPermissions(Registeract.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, pReqCode);
         }
      } else {
         openGallery();
      }
   }

   //open the gallery intent
   private void openGallery() {
      //todo open gallery intent and wait for user to pick animager
      Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
      galleryIntent.setType("image/*");
      startActivityForResult(galleryIntent, REQCode);
   }


   @Override
   public void onStart() {
      super.onStart();

      // Check auth on Activity start

   }

   //for sending email after verification
   private void sendEmailVerification() {
      // Send verification email
      // [START send_email_verification]
      final FirebaseUser user = mAuth.getCurrentUser();
      user.sendEmailVerification()
              .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                    // [START_EXCLUDE]
                    // Re-enable button

                    if (task.isSuccessful()) {
                       Toast.makeText(Registeract.this,
                               "Verification email sent to " + user.getEmail(),
                               Toast.LENGTH_SHORT).show();
                    } else {
                       Log.e(TAG, "sendEmailVerification", task.getException());
                       Toast.makeText(Registeract.this,
                               "Failed to send verification email.",
                               Toast.LENGTH_SHORT).show();
                    }
                    // [END_EXCLUDE]
                 }
              });
      // [END send_email_verification]
   }

   //function to create the user
   private void signUp() {
      Log.d(TAG, "signUp");
      if (!validateForm()) {
         return;
      }

      progressDialog.setMessage("Creating New USer..");
      progressDialog.show();

      String email = mEmailField.getText().toString();
      String password = mPasswordField.getText().toString();

      mAuth.createUserWithEmailAndPassword(email, password)
              .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());


                    if (task.isSuccessful()) {
                       onAuthSuccess(task.getResult().getUser());
                    } else {
                       Toast.makeText(Registeract.this, "Sign Up Failed",
                               Toast.LENGTH_SHORT).show();
                    }
                 }
              });
   }

   private void onAuthSuccess(FirebaseUser user) {

      String username = usernameFromEmail(user.getEmail());
      String name = mName.getText().toString().trim();
      String phone = mPhone.getText().toString().trim();
      // Write new user
      guid = user.getUid(); //class variable for user id

      writeNewUser(guid, username, name, phone);

      progressDialog.hide();
//start register_user activity
      // Go to MainActivity
      startActivity(new Intent(this, Registeruser.class));
      finish();


   }

   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {  //method when useer has picked an image or not
      super.onActivityResult(requestCode, resultCode, data);

      if (resultCode == RESULT_OK && requestCode == REQCode && data != null) {
         //the user has successfully picked an image from gallery
         //we need to save its reference
         result = true;
         pickedImage = data.getData();
         userPic.setImageURI(pickedImage);
      } else {
         //imagee  not picked
         result = false;
      }
   }

   private String usernameFromEmail(String email) {
      if (email.contains("@")) {
         return email.split("@")[0];
      } else {
         return email;
      }
   }

   private String getFileExtension(Uri uri)  //to get extension of file
   {
      ContentResolver cr = getContentResolver();
      MimeTypeMap mime = MimeTypeMap.getSingleton();
      return ((MimeTypeMap) mime).getExtensionFromMimeType(cr.getType(uri));
   }

   private void uploadFile()   //to upload file to storage
   {
      /*if (pickedImage != null) {
         final StorageReference fileReference = storageReference.child(mName.getText() + "." + getFileExtension(pickedImage));
         //tocreate name.jpg in uploads folder in storage
         fileReference.putFile(pickedImage).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
               if (!task.isSuccessful()) {
                  throw task.getException();
               }
               return fileReference.getDownloadUrl();
            }
         }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
               if (task.isSuccessful()) {
                  Uri downUri = task.getResult();
                  Log.d(TAG, "onComplete: Url: " + downUri.toString());
                  fileURL = downUri.toString();
                  sendmessage("upload success");
                  upload = new Upload(mName.getText().toString().trim(),
                          fileURL);
                  sendmessage(upload.getImageURL());

                  DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
                  databaseReference.child(guid).child("photo").setValue(upload);


               }
            }
         });
      } else {
         sendmessage("no file selected");
      }*/
   }

   private void sendmessage(String msg) {
      Toast.makeText(Registeract.this, msg, Toast.LENGTH_SHORT).show();
   }

   private boolean validateForm() {
      boolean res = true;
      if (TextUtils.isEmpty(mEmailField.getText().toString())) {
         mEmailField.setError("Required");
         res = false;
      } else {
         mEmailField.setError(null);
      }

      if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
         mPasswordField.setError("Required");
         res = false;
      } else {
         mPasswordField.setError(null);
      }
      if (TextUtils.isEmpty(mName.getText().toString())) {
         mName.setError("Required");
         res = false;
      } else {
         mName.setError(null);
      }
      if (TextUtils.isEmpty(mPhone.getText().toString())) {
         mPhone.setError("Required");
         res = false;
      } else {
         mPhone.setError(null);
      }

    /*  if (result == false) {
         sendmessage("Please  pick an image");
         res = false; //for image not picked
      }*/
      return res;
   }

   // [START basic_write]
   private void writeNewUser(String userId, String username, String name, String phone) {
      UserInformation user = new UserInformation(username, name, phone);
   Log.i(TAG,"here in writing new user"+user+username+phone);

      mDatabase.child("users").child(userId).setValue(user);
      //sendEmailVerification();
      //uploadto stporage
      uploadFile();
      //here send to database
   }
   // [END basic_write]


}