package edu.neu.madcourse.spenzoo_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import edu.neu.madcourse.spenzoo_finalproject.Adapters.RecentTransactionAdapter;
import edu.neu.madcourse.spenzoo_finalproject.Model.Animal;
import edu.neu.madcourse.spenzoo_finalproject.Model.Expense;
import edu.neu.madcourse.spenzoo_finalproject.Sensor.ShakeEventListener;

public class AnimalStatusActivity extends AppCompatActivity {
    private static final int MAX_HAPPY = 100;
    private FirebaseAuth mAuth;
    private DatabaseReference mDB;

    private String mKey;
    private String status;
    private String category;
    private String newUser;
    private String type;
    private boolean tryShake;


    private ImageView animalView;
    private ProgressBar happinessLevelProgressBar;
    private TextView animalName;
    private TextView animalInfo;
    private TextView happinessLevelPoints;
    private TextView noSpendingText;
    private TextView animalSupportCategory;

    private TextView supportTotalExpense;
    private TextView currentMonth;

    // Recent Transaction
    private RecentTransactionAdapter adapter;
    private RecyclerView recyclerViewCategoryTransaction;

    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;
    private boolean runThread;

    private ImageView closeBtn;
    private Button confirmBtn;
    private Button updateBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_status);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mKey = extras.getString("key");
            status = extras.getString("status");
            category = extras.getString("category");
            type = extras.getString("type");
            newUser = extras.getString("newUser");
        }

        Log.i("newUser in Status", newUser);

        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference("Users")
                .child(mAuth.getCurrentUser().getUid());

        animalView = findViewById(R.id.animal_image_view);
        happinessLevelProgressBar = findViewById(R.id.happiness_level);
        happinessLevelPoints = findViewById(R.id.happiness_points);
        animalName = findViewById(R.id.name);
        animalInfo = findViewById(R.id.animal_info);
        supportTotalExpense = findViewById(R.id.category_total_expnese);
        currentMonth = findViewById(R.id.current_date);
        animalSupportCategory = findViewById(R.id.animal_support_category);

        recyclerViewCategoryTransaction = findViewById(R.id.recent_transaction_recyclerView);

        displayAnimalInfo();
        checkHappiness();

        // Display recent Transaction
        setCategoryTransaction();

        closeBtn = findViewById(R.id.close_status);
        confirmBtn = findViewById(R.id.confirm_btn);
        updateBtn = findViewById(R.id.update_btn);

        if (newUser.equals("true")) {
            tryShake = true;
        } else {
            tryShake = false;
        }

        if (status.equals("update") && !newUser.equals("true")){
            confirmBtn.setText("Reposition");
            closeBtn.setVisibility(View.VISIBLE);
        } else {
            confirmBtn.setText("Done");
        }

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (newUser.equals("true") ) {
                    if (tryShake) {
                        AlertDialog alertDialog = new AlertDialog.Builder(AnimalStatusActivity.this).create();
                        alertDialog.setTitle("Congrats on Adopting Animal Supporter 🎉");
                        alertDialog.setMessage("\n You can shake your phone to increase happiness of your animal supporter! \n");

                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "I'll Try Now!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                tryShake = false;
                                alertDialog.dismiss();
                            }
                        });

                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Later", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                grantRewards();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class).putExtra("newUser", "true"));
                            }
                        });

                        alertDialog.show();
                    } else {
                        grantRewards();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class).putExtra("newUser", "true"));
                    }

                } else {
                    if (status.equals("update")) {
                        startActivity(new Intent(getApplicationContext(), PiecePlacingActivity.class).putExtra("type", type).putExtra("key", mKey).putExtra("status", status).putExtra("newUser", "false"));
                    } else {
                        grantRewards();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class).putExtra("newUser", "false"));
                    }
                }
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AnimalSettingActivity.class);
                i.putExtra("key", mKey);
                i.putExtra("status", "update");
                i.putExtra("newUser", newUser);
                startActivity(i);
            }
        });


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        runThread = true;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();

        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

            public void onShake() {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        increaseHappinessLevel();
                        Toast.makeText(AnimalStatusActivity.this, "Love Received 💚 HappinessLevel + 5", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

    }



    private void checkHappiness() {

        mDB.child("animal").child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {

           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {

               if (snapshot.exists()) {
                   Animal animal = snapshot.getValue(Animal.class);
                   if (animal.getHappinessLevel() <= 20) {
                       Toast.makeText(getApplicationContext(), "Your animal supporter is very unhappy! Shake your phone to increase happiness of your animal!", Toast.LENGTH_LONG).show();
                   }
               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }

        });
    }


    private void setCategoryTransaction() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,  false);
        recyclerViewCategoryTransaction = (RecyclerView) findViewById(R.id.animal_support_recyclerView);
        recyclerViewCategoryTransaction.setLayoutManager(linearLayoutManager);


        mDB.child("expense").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TextView noSpendingText = findViewById(R.id.no_spending_text2);
                ArrayList<Expense> recentCategoryTransactionList = new ArrayList<>();
                Iterator<DataSnapshot> items = snapshot.getChildren().iterator();

                Double sum = 0.00;
                while (items.hasNext()) {
                    DataSnapshot item = items.next();
                    Expense expenseItem = item.getValue(Expense.class);
                    Log.i("SpendingItem", expenseItem.getDate().toString());

                    if (expenseItem.getIsExpense()
                            && expenseItem.getDate().getMonth() == Calendar.getInstance().getTime().getMonth()
                            && expenseItem.getDate().getYear() == Calendar.getInstance().getTime().getYear()
                            && expenseItem.getCategory().equals(category)) {

                        Log.i("SpendingItem", expenseItem.getDescription());
                        recentCategoryTransactionList.add(expenseItem);
                        sum += expenseItem.getAmount();
                    }
                }

                supportTotalExpense.setText("$" + String.format("%.2f",sum));

                // recentTransactionList.stream().mapToDouble(Expense::getAmount).sum() == 0
                if (recentCategoryTransactionList.size() == 0) {
                    noSpendingText.setText("No Recent Spending in " + category);
                    noSpendingText.setVisibility(View.VISIBLE);
                } else {
                    noSpendingText.setVisibility(View.INVISIBLE);
                    Collections.sort(recentCategoryTransactionList, new Comparator<Expense>() {
                        @Override
                        public int compare(Expense o1, Expense o2) {
                            return o1.getDate().compareTo(o2.getDate());
                        }
                    });

                    Collections.reverse(recentCategoryTransactionList);

                    adapter = new RecentTransactionAdapter(recentCategoryTransactionList);
                    recyclerViewCategoryTransaction.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }




    private void displayAnimalInfo() {
        mDB.child("animal").child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Animal animalItem = snapshot.getValue(Animal.class);

                    happinessLevelProgressBar.setMax(MAX_HAPPY);
                    happinessLevelProgressBar.setProgress(animalItem.getHappinessLevel());
                    displayHappinessLevel(animalItem.getHappinessLevel());

                    animalView.setImageResource(animalItem.getImageSource());
                    animalName.setText(animalItem.getName());
                    happinessLevelPoints.setText(animalItem.getHappinessLevel().toString() + " / " + MAX_HAPPY);

                    String dateOfAdoption = new SimpleDateFormat("MMMM dd, yyyy").format(animalItem.getDateOfAdoption());
                    String animalType = animalItem.getType();
                    String animalSupport = animalItem.getCategory();

                    String animalInfoText = animalType + " | " + dateOfAdoption + " | " + animalSupport;

                    animalInfo.setText(animalInfoText);

                    animalSupportCategory.setText(animalItem.getCategory());
                    currentMonth.setText(new SimpleDateFormat("MMMM yyyy").format(Calendar.getInstance().getTime()));

                } else {
                    Log.i("Setting Animal", "Animal not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    private void displayHappinessLevel(Integer happinessLevel) {
        if (happinessLevel <= 20) {
            happinessLevelProgressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#b1006a")));
        } else if (happinessLevel > 80) {
            happinessLevelProgressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#4a26fd")));
        } else {
            happinessLevelProgressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#A788F2")));
        }
    }

    private void increaseHappinessLevel() {
        runThread = true;
        mDB.child("animal").child(mKey).child("happinessLevel").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    if (runThread) {
                        runThread = false;
                        Log.i("mKey from AnimalStatus", mKey);
                        Integer increasedHappinessLevel = snapshot.getValue(Integer.class) + 5;
                        happinessLevelPoints.setTypeface(Typeface.DEFAULT_BOLD);

                        if (increasedHappinessLevel >= 100) {
                            mDB.child("animal").child(mKey).child("happinessLevel").setValue(100);
                        } else {
                            mDB.child("animal").child(mKey).child("happinessLevel").setValue(increasedHappinessLevel);
                        }

                        runThread = true;
                    }

                }
                displayAnimalInfo();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void grantRewards() {
        mDB.child("rewards").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer reward = snapshot.getValue(int.class);
                Log.i("Grant rewards", "current reward: " + String.valueOf(reward));
                mDB.child("rewards").setValue(reward + 15);
                Toast.makeText(AnimalStatusActivity.this, "Glad you found a new supporter! 💰Reward+15💰", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}