package edu.neu.madcourse.spenzoo_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

import edu.neu.madcourse.spenzoo_finalproject.Model.Item;
import edu.neu.madcourse.spenzoo_finalproject.Model.User;

public class ProfileActivity extends AppCompatActivity {

    public static final int MAX_ANIMAL = 5;
    private FirebaseAuth mAuth;
    private DatabaseReference mDB;
    int reward;

    RecyclerView recyclerView;

    TextView username, userInfo, join_date;
    Button adoptBtn, animalBtn, itemBtn, rewardsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = findViewById(R.id.username);
        userInfo = findViewById(R.id.userInfo);
        join_date = findViewById(R.id.join_date);

        adoptBtn = findViewById(R.id.adopt_animal);
        animalBtn = findViewById(R.id.animals_list);
        itemBtn = findViewById(R.id.items_list);
        rewardsBtn = findViewById(R.id.reward_info);

        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
        showProfile();

        checkAnimalNumber();

        adoptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, AdoptAnimalActivity.class)
                        .putExtra("status", "additional")
                        .putExtra("newUser", "false"));
            }
        });

        animalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ZooActivity.class));
            }
        });

        itemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ItemsActivity.class));
            }
        });

        rewardsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, RewardsActivity.class));
            }
        });
    }

    private void showProfile() {
        // Log.i("TimeStamp", String.valueOf(mAuth.getCurrentUser().getMetadata().getCreationTimestamp()));

        //join_date.setText(new SimpleDateFormat("MMMM dd, yyyy").format(mAuth.getCurrentUser().getMetadata().getLastSignInTimestamp()));

        mDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText("@"+user.getUsername());
                join_date.setText(new SimpleDateFormat("MMMM dd, yyyy").format(user.getCreationDate()));

                Integer rewards = user.getRewards();
                Integer numOfAnimals = user.getNumOfAnimals();

                userInfo.setText("💰" + rewards + " points" + "  |  " + "🦁 " + numOfAnimals + " Animals");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void checkAnimalNumber() {
        mDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user.getNumOfAnimals() >= MAX_ANIMAL) {
                        adoptBtn.setEnabled(false);
                        adoptBtn.setBackgroundColor(Color.parseColor("#cfcfcf"));
                        adoptBtn.setText("Can adopt to MAX 5 animals");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }



}