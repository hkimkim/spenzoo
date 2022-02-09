package edu.neu.madcourse.spenzoo_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.anychart.charts.Pie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import edu.neu.madcourse.spenzoo_finalproject.Model.Animal;

public class AnimalSettingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String mKey;
    private String status;
    private String newUser;
    private DatabaseReference mDB;

    private ImageView animalView;
    private EditText animalName;
    private TextView categoryQText;
    private TextView title;

    private Button submitBtn;
    private Spinner expenseCategory;
    private String chosenCategory;
    private Integer chosenCategoryIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_animal_info);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mKey = extras.getString("key");
            status = extras.getString("status");
            newUser = extras.getString("newUser");
        }

        Log.i("newUser in AnimalSetting", newUser);

        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference("Users")
                .child(mAuth.getCurrentUser().getUid())
                .child("animal").child(mKey);

        animalView = findViewById(R.id.chosen_animal);
        animalName = findViewById(R.id.animal_name);
        categoryQText = findViewById(R.id.animal_cat_choose);
        submitBtn = findViewById(R.id.submitBtn);
        expenseCategory = findViewById(R.id.expense_category_set_animal);
        title = findViewById(R.id.setting_title);


        if (status.equals("update")) {
            title.setText("What Should We Update?");
        } else if (status.equals("additional")) {
            title.setText("Great! Another supporter!");
        }

        displayAnimal();
        showCategoryMenu();

        // Save to db
        submitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Animal animalItem = snapshot.getValue(Animal.class);
                            if (checkNameField()) {
                                animalItem.setName(animalName.getText().toString().trim());
                                animalItem.setCategory(chosenCategory);
                                animalItem.setCategoryIndex(chosenCategoryIndex);
                                mDB.setValue(animalItem);

                                if (status.equals("update")) {
                                    Intent i = new Intent(getApplicationContext(), AnimalStatusActivity.class);
                                    i.putExtra("key", mKey);
                                    i.putExtra("status", status);
                                    i.putExtra("category", animalItem.getCategory());
                                    i.putExtra("newUser", newUser);
                                    i.putExtra("type", "animal");
                                    startActivity(i);
                                } else {
                                    Log.i("Activity", "Animal Settings Complete");
                                    Intent i = new Intent(getApplicationContext(), PiecePlacingActivity.class);
                                    i.putExtra("key", mKey);
                                    i.putExtra("status", status);
                                    i.putExtra("category", animalItem.getCategory());
                                    i.putExtra("newUser", newUser);
                                    i.putExtra("type", "animal");
                                    startActivity(i);
                                }

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

            }
        });
    }

    private void displayAnimal() {
        mDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Animal animalItem = snapshot.getValue(Animal.class);
                    animalView.setImageResource(animalItem.getImageSource());

                    if (!animalItem.getName().equals("default")) {
                        animalName.setText(animalItem.getName());
                    }
                    if (animalItem.getCategoryIndex() != -1) {
                        expenseCategory.setSelection(animalItem.getCategoryIndex());
                    }

                } else {
                    Log.i("Setting Animal", "Animal not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    private void showCategoryMenu() {
        String[] categories = getResources().getStringArray(R.array.category);
        ArrayAdapter categoryAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseCategory.setAdapter(categoryAdapter);
        expenseCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenCategory = parent.getItemAtPosition(position).toString();
                chosenCategoryIndex = position;
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


    }

    private boolean checkNameField(){
        // Check if name field is empty
        if (animalName.getText().toString().trim().isEmpty()) {
            animalName.setError("Give your supporter a name!");
            animalName.requestFocus();
            return false;
        }
        return true;
    }
}