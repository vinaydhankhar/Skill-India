package com.example.skillindia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registeruser extends AppCompatActivity {
    GridLayout mainGrid;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeruser);

        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();


        TextView tv;
        tv=findViewById(R.id.Signin);

        mainGrid=findViewById(R.id.mainGrid);

        setSingleEvent(mainGrid);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent=new Intent(v.getContext(),Login.class);
                startActivity(intent);
            }
        });
    }
    private void setSingleEvent(GridLayout mainGrid) {
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            //you can see all child objects in cardview so just cast in cardview
            final CardView cardView = (CardView) mainGrid.getChildAt(i);
            final int f=i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    //Toast.makeText(RegisterUser.this,"clicked at index"+f,Toast.LENGTH_SHORT).show();
                    if(f==0)
                    {
                        //student
                        databaseReference.child("users").child(firebaseUser.getUid()).child("userType").setValue("student");

                    }
                    else {
                        //counsell

                        databaseReference.child("users").child(firebaseUser.getUid()).child("userType").setValue("counsellor");

                    }
                    startActivity(new Intent(v.getContext(),Interest.class));
                }
            });
        }
    }
}


