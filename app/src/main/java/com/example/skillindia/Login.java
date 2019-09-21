package com.example.skillindia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    EditText emailid,password;
    Button signin;
    TextView signup;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailid=findViewById(R.id.TextEmail);
        password=findViewById(R.id.TextPassword);
        signin=findViewById(R.id.Signin);
        signup=findViewById(R.id.Signup);
        firebaseAuth=FirebaseAuth.getInstance();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user!=null){
                    Toast.makeText(Login.this, "User logged in ", Toast.LENGTH_SHORT).show();
                    Intent I = new Intent(Login.this, Registeruser.class);
                    startActivity(I);
                }
                else{
                    Toast.makeText(Login.this, "Login to continue", Toast.LENGTH_SHORT).show();
                }
            }
        };
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(Login.this, Registeract.class);
                startActivity(I);
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailid.getText().toString();
                String pwd=password.getText().toString();
                if(email.isEmpty()){
                    emailid.setError("Provide your email first");
                    emailid.requestFocus();
                }
                else if(pwd.isEmpty()){
                    password.setError("set your password");
                    password.requestFocus();
                }
                else if(email.isEmpty()&&pwd.isEmpty()){
                    Toast.makeText(Login.this,"Fields Empty",Toast.LENGTH_SHORT).show();
                }
                else if(!(email.isEmpty()&&pwd.isEmpty())){
                    firebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(Login.this.getApplicationContext(),
                                        "Not successful",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else{
                                startActivity(new Intent(Login.this, Registeruser.class));
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(Login.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}