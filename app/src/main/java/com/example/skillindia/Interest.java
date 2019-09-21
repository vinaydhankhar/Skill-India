package com.example.skillindia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Interest extends AppCompatActivity {
    GridLayout mainGrid;
    Button proceed;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ArrayList<String> interests, ans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        mainGrid = findViewById(R.id.mainGrid);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        interests = new ArrayList<String>(6);
        ans = new ArrayList<String>();
        interests.add("computer");
        interests.add("cricket");
        interests.add("maths");
        interests.add("physics");
        interests.add("chemistry");
        interests.add("biology");
        //set Event

        setToggleEvent(mainGrid);

    }

    private void setToggleEvent(GridLayout mainGrid) {
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            //you can see all child objects in cardview so just cast in cardview
            final CardView cardView = (CardView) mainGrid.getChildAt(i);
            final int f=i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cardView.getCardBackgroundColor().getDefaultColor() == -1) {
                        cardView.setCardBackgroundColor(Color.parseColor("#32cd32"));
                        databaseReference.child("interest").setValue(interests.get(f));

                        startActivity(new Intent(v.getContext(),HomePage.class));
                        finish();

                    } else {
                        cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                }
            });
        }

    }
}

