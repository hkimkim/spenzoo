package edu.neu.madcourse.spenzoo_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.neu.madcourse.spenzoo_finalproject.databinding.BudgetRecordBinding;
import edu.neu.madcourse.spenzoo_finalproject.databinding.ExpenseRecordBinding;

public class RewardsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDB;
    private ConstraintLayout budgetReward, expenseReward, adoptReward;
    private Button okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference("Users")
                .child(mAuth.getCurrentUser().getUid());

        budgetReward = findViewById(R.id.reward_plan_budget);
        budgetReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BudgetActivity.class));
            }
        });


        expenseReward = findViewById(R.id.reward_record_expense);
        expenseReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ExpenseActivity.class));
            }
        });

        okBtn = findViewById(R.id.reward_ok_btn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });

        adoptReward = findViewById(R.id.reward_animal_adopt);

        mDB.child("numOfAnimals").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Integer numOfAnimals = snapshot.getValue(Integer.class);

                    if (numOfAnimals >= 5) {

                        adoptReward.setClickable(false);
                        adoptReward.setBackgroundColor(Color.parseColor("#cfcfcf"));
                        TextView adoptTitle = findViewById(R.id.adopt_title);
                        TextView adoptText = findViewById(R.id.adopt_text);

                        adoptTitle.setTextColor(Color.GRAY);
                        adoptText.setTextColor(Color.GRAY);

                    } else {
                        adoptReward.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getApplicationContext(), AdoptAnimalActivity.class).putExtra("status", "additional").putExtra("newUser", "false"));
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



}