package edu.neu.madcourse.spenzoo_finalproject;

import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
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
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.neu.madcourse.spenzoo_finalproject.Adapters.RecentTransactionAdapter;
import edu.neu.madcourse.spenzoo_finalproject.Model.Animal;
import edu.neu.madcourse.spenzoo_finalproject.Model.Budget;
import edu.neu.madcourse.spenzoo_finalproject.Model.Expense;
import edu.neu.madcourse.spenzoo_finalproject.Model.Item;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDB;
    private BottomNavigationView bottomNavigationView;

    // Timer thread constants and variables
    private static final long FIVE_MINUTES_IN_MILLIS = 300000;
    private static final long MAX_HAPPINESS_LEVEL_HOURS_IN_MILLIS = 43200000;
    public static final int DELAY_MILLIS = 3000;
    private long mTimeLeftInMillis;
    private boolean runThread;


    // pop window for add new expense --- start
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Spinner expenseCategory;
    private String chosenCategory;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TextView expenseDate;
    private EditText expenseAmount, expenseDescription;
    private Switch expenseBool;
    private Boolean isExpense = true;
    private Button addExpense, cancelExpense;
    private Calendar dateOfExpense;
    private int reward;
    // pop window for add new expense --- end


    // Expense dashboard
    private TextView date;
    private TextView amountSpent;
    private TextView daysLeft;
    private TextView totalExpenseCalculation;
    private ProgressBar expenseProgressBar;
    private CircleImageView profile;
    private Double budget;
    private Calendar cal;
    private Double totalExpense;
    private TextView userPoints;

    // Zoo Display
    private ConstraintLayout zooFrame;

    // Recent Transaction
    private RecentTransactionAdapter adapter;
    private RecyclerView recyclerViewRecentTransaction;

    private String newUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             newUser = extras.getString("newUser");
        } else {
            newUser = "false";
        }

        if (newUser.equals("true")) {
            displayWelcomeMessage();
        }

        // Hide Title Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        updateHappinessLevel();


        // Set Expense Dashboard
        date = (TextView) findViewById(R.id.today_date);
        amountSpent = (TextView) findViewById(R.id.amount_spent);
        daysLeft = (TextView) findViewById(R.id.days_left);
        totalExpenseCalculation = (TextView) findViewById(R.id.total_expense_calculation);
        expenseProgressBar = (ProgressBar) findViewById(R.id.expenseProgressBar);
        cal = Calendar.getInstance(TimeZone.getTimeZone("EST"));
        userPoints = findViewById(R.id.user_points);

        updateExpenseDashBoard();

        // DisplayZoo
        zooFrame = findViewById(R.id.zooFrame);

        // Display Bottom Navigation Menu
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        displayBottomNavigationView();

        // Invoke Profile
        profile = (CircleImageView) findViewById(R.id.profile_image);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchProfile();
            }
        });


        // Invoke add new expense dialog -- start
        dateOfExpense = Calendar.getInstance(TimeZone.getTimeZone("EST"));

        FloatingActionButton addExpense = findViewById(R.id.fab);
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("MainActivity:", "click add expense button");
                createNewExpenseDialog();
                updateExpenseDashBoard();
            }
        });


        // Display recent Transaction
        setRecentTransaction();

        // displayZoo
        displayZoo();

        // display reward points
        displayUserPoints();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (runThread) {
                    new CountDownTimer(MAX_HAPPINESS_LEVEL_HOURS_IN_MILLIS, FIVE_MINUTES_IN_MILLIS) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            mTimeLeftInMillis = millisUntilFinished;
                            Log.i("timer", String.valueOf(mTimeLeftInMillis));
                            subtractHappinessLevel();
                        }

                        @Override
                        public void onFinish() {
                            start();
                        }

                    }.start();
                }
            }
        }, DELAY_MILLIS);


    }

    private void displayWelcomeMessage(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Congrats on Becoming a ZooKeeper! 🎉");
        alertDialog.setMessage("\nYou can always view or update your animal status by clicking on the animal on the Home screen! \n");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok, Great!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
        newUser = "false";
    }


    private void displayUserPoints() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDB.child("rewards").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            userPoints.setText(snapshot.getValue(Integer.class).toString());
                        } else {
                            userPoints.setText(0);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });            }
        }, 200);

    }

    private void setRecentTransaction() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,  false);
        recyclerViewRecentTransaction = (RecyclerView) findViewById(R.id.recent_transaction_recyclerView);
        recyclerViewRecentTransaction.setLayoutManager(linearLayoutManager);


        mDB.child("expense").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TextView noSpendingText = findViewById(R.id.no_spending_text);
                ArrayList<Expense> recentTransactionList = new ArrayList<>();
                Iterator<DataSnapshot> items = snapshot.getChildren().iterator();

                while (items.hasNext()) {
                    DataSnapshot item = items.next();
                    Expense expenseItem = item.getValue(Expense.class);
                    Log.i("SpendingItem", expenseItem.getDate().toString());

                    if (expenseItem.getIsExpense()) {
                        Log.i("SpendingItem", expenseItem.getDescription());
                        recentTransactionList.add(expenseItem);
                    }
                }

                // recentTransactionList.stream().mapToDouble(Expense::getAmount).sum() == 0
                if (recentTransactionList.size() == 0) {
                    noSpendingText.setVisibility(View.VISIBLE);
                } else {
                    noSpendingText.setVisibility(View.INVISIBLE);
                    Collections.sort(recentTransactionList, new Comparator<Expense>() {
                        @Override
                        public int compare(Expense o1, Expense o2) {
                            return o1.getDate().compareTo(o2.getDate());
                        }
                    });
                    Collections.reverse(recentTransactionList);


                    if (recentTransactionList.size() > 3) {
                        List<Expense>topThreeList = recentTransactionList.subList(0, 3);
                        ArrayList<Expense> topThree = new ArrayList();
                        topThree.addAll(topThreeList);
                        adapter = new RecentTransactionAdapter(topThree);
                    } else {
                        adapter = new RecentTransactionAdapter(recentTransactionList);
                    }
                    recyclerViewRecentTransaction.setAdapter(adapter);
                    adapter.setOnItemClickListener(new RecentTransactionAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            startActivity(new Intent(MainActivity.this, ExpenseActivity.class));
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void updateExpenseDashBoard() {

        // Display today's date
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        date.setText(sdf.format(cal.getTime()));

        // calculate total expense
        calculateTotalExpense();

        // Update budget expense ratio

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateBudgetExpenseRatio();
            }
        }, 400);

        // Set days left
        Integer monthLastDays = cal.getActualMaximum(Calendar.DATE);
        Integer todayDate = cal.get(Calendar.DAY_OF_MONTH);
        String stringDaysLeft = (monthLastDays - todayDate) + " days left";
        daysLeft.setText(stringDaysLeft);
    }

    private void calculateTotalExpense() {
        mDB.child("expense").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    Iterator<DataSnapshot> items = snapshot.getChildren().iterator();
                    Double sum = 0.00;
                    while (items.hasNext()) {
                        DataSnapshot item = items.next();
                        Expense expenseItem = item.getValue(Expense.class);
                        if (expenseItem.getIsExpense() && expenseItem.getDate().getMonth() == cal.getTime().getMonth() && expenseItem.getDate().getYear() == cal.getTime().getYear()) {
                            sum += expenseItem.getAmount();
                        }
                    }

                    String chosenPeriod =  Calendar.getInstance().get(Calendar.YEAR) + "-" + (Calendar.getInstance().get(Calendar.MONTH) +1);

                    String stringAmountSpent = String.format("$ %.2f", sum.doubleValue());
                    amountSpent.setText(stringAmountSpent);

                    mDB.child("budget").child(chosenPeriod).child("totalExpense").setValue(sum);
                } else {
                    amountSpent.setText("$ 0.00");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });



    }

    private void updateBudgetExpenseRatio() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("EST"));
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");

        // Get Budget from db
        mDB.child("budget").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                        Iterator<DataSnapshot> items = snapshot.getChildren().iterator();
                        while (items.hasNext()) {
                            DataSnapshot item = items.next();
                            String mKey = item.getKey();
                            Budget budgetItem = item.getValue(Budget.class);

                            // Need to subtract 1 because Date month index starts at 0 (jan = 0)
                            int budgetItemYear = budgetItem.getYear();
                            int budgetItemMonth = budgetItem.getMonth() - 1;

                            if (budgetItemMonth == cal.getTime().getMonth() && budgetItemYear == cal.get(Calendar.YEAR)) {
                                budget = budgetItem.getAmount();
                                totalExpense = budgetItem.getTotalExpense();

                                if (budget == 0) {
                                    resetProgressBarTextColor();
                                    totalExpenseCalculation.setText("");
                                    expenseProgressBar.setMax(totalExpense.intValue());
                                } else {
                                    String totalExpenseString = "$ " + totalExpense.intValue() + " / " + budget.intValue();
                                    // When user goes over budget change color
                                    if (budget > 0 && totalExpense > budget) {
                                        setWarningProgressBarTextColor();
                                    } else {
                                        resetProgressBarTextColor();
                                    }
                                    totalExpenseCalculation.setText(totalExpenseString);
                                    expenseProgressBar.setMax(budget.intValue());
                                }
                                expenseProgressBar.setProgress(totalExpense.intValue());
                            }


                            mDB.child("budget").child(mKey).child("totalExpense").removeValue();
                        }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    private void resetProgressBarTextColor() {
        amountSpent.setTextColor(Color.WHITE);
        expenseProgressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#247ed3")));
    }

    private void setWarningProgressBarTextColor() {
        //amountSpent.setTextColor(Color.parseColor("#AB47BC"));
        expenseProgressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#b1006a")));
    }

    private void launchProfile() {
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
    }


    public void displayBottomNavigationView() {
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);

        bottomNavigationView.setSelectedItemId(R.id.mi_home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mi_home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.mi_store:
                        startActivity(new Intent(getApplicationContext(), StoreActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.mi_budget:
                        startActivity(new Intent(getApplicationContext(), BudgetActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.mi_expense:
                        startActivity(new Intent(getApplicationContext(), ExpenseActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    // popup window for expense --- start
    public void createNewExpenseDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View expenseDialogView = getLayoutInflater().inflate(R.layout.add_expense_popup, null);

        // category spinner
        expenseCategory = expenseDialogView.findViewById(R.id.expense_category);
        String[] categories = getResources().getStringArray(R.array.category);
        ArrayAdapter categoryAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseCategory.setAdapter(categoryAdapter);
        expenseCategory.setOnItemSelectedListener(this);

        // Date Picker
        expenseDate = expenseDialogView.findViewById(R.id.expense_date);
        Calendar today = Calendar.getInstance(TimeZone.getTimeZone("EST"));
        int curMonth = today.get(Calendar.MONTH) + 1;
        String date = curMonth + "/" + today.get(Calendar.DAY_OF_MONTH) + "/" + today.get(Calendar.YEAR);
        expenseDate.setText(date);

        expenseDate.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("EST"));
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dateDialog = new DatePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
                dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                dateDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                dateDialog.show();
            }
        }));
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                Log.i("MainActivity", "set date");
                String date = month + "/" + day + "/" + year;

                Log.i("picked Date", String.valueOf(month));
                dateOfExpense.set(year, month - 1, day);
                expenseDate.setText(date);
            }
        };


        // TextView
        expenseAmount = expenseDialogView.findViewById(R.id.expense_amount);
        expenseDescription = expenseDialogView.findViewById(R.id.expense_description);

        // Expense switch
        expenseBool = expenseDialogView.findViewById(R.id.expense_bool);
        expenseBool.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            { if (expenseBool.isChecked()) {
                isExpense = true;
                TextView incomeText = expenseDialogView.findViewById(R.id.expense_bool);
                incomeText.setTextColor(getResources().getColor(R.color.gray));
                TextView expenseText = expenseDialogView.findViewById(R.id.expense_text);
                expenseText.setTextColor(getResources().getColor(R.color.black));
                Toast.makeText(MainActivity.this, "You're recording an expense", Toast.LENGTH_SHORT).show();
            } else {
                TextView expenseText = expenseDialogView.findViewById(R.id.expense_text);
                expenseText.setTextColor(getResources().getColor(R.color.gray));
                TextView incomeText = expenseDialogView.findViewById(R.id.expense_bool);
                incomeText.setTextColor(getResources().getColor(R.color.black));
                Toast.makeText(MainActivity.this, "You're recording an income", Toast.LENGTH_SHORT).show();
                isExpense = false;
            }
            }
        });

        // set view
        dialogBuilder.setView(expenseDialogView);
        dialog = dialogBuilder.create();
        dialog.show();

        // Buttons
        addExpense = expenseDialogView.findViewById(R.id.expense_add);
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expenseAmount.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter amount", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.i("Add Expense:", "click add");
                    Log.i("Add Expense:", "current user id: "+ mAuth.getCurrentUser().getUid());

                    Expense newExpense = new Expense(chosenCategory, Double.valueOf(expenseAmount.getText().toString()),dateOfExpense.getTime(), expenseDescription.getText().toString(), isExpense);

                    // add record to database
                    mDB.child("expense").push().setValue(newExpense);

                    // Update dashboard
                    updateExpenseDashBoard();

                    // Grant user rewards
                    grantRewards();

                    // update user points

                    displayUserPoints();

                    // Update Expense
                    setRecentTransaction();

                    // reset
                    dateOfExpense = Calendar.getInstance(TimeZone.getTimeZone("EST"));
                    isExpense = true;
                    chosenCategory = Expense.Category.DEFAULT.name();  // enum from Expense Model class
                    dialog.dismiss();

                }

            }
        });

        cancelExpense = expenseDialogView.findViewById(R.id.expense_cancel);
        cancelExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        chosenCategory = adapterView.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // default is Food
    }

    private void grantRewards() {
        mDB.child("rewards").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reward = snapshot.getValue(int.class);
                Log.i("Grant rewards", "current reward: " + String.valueOf(reward));
                mDB.child("rewards").setValue(reward + 5);
                Toast.makeText(MainActivity.this, "Expense Recorded! 💰Reward+5💰", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayZoo() {


        mDB.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Iterator<DataSnapshot> items = snapshot.getChildren().iterator();
                    while (items.hasNext()) {
                        DataSnapshot itemDS = items.next();
                        String mKey = itemDS.getKey();
                        Item item = itemDS.getValue(Item.class);
                        ImageView itemSticker = new ImageView(MainActivity.this);
                        itemSticker.setImageResource(item.getImageSource());
                        itemSticker.setX(item.getxPosition());
                        itemSticker.setY(item.getyPosition());

                        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(150, 150);
                        itemSticker.setLayoutParams(params);

                        itemSticker.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getApplicationContext(), PiecePlacingActivity.class)
                                        .putExtra("key", mKey )
                                        .putExtra("status", "update")
                                        .putExtra("type", "item")
                                        .putExtra("newUser", "false"));
                            }
                        });

                        zooFrame.addView(itemSticker);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDB.child("animal").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            Iterator<DataSnapshot> items = snapshot.getChildren().iterator();
                            for (int i = 0;  i < snapshot.getChildrenCount(); i++) {
                                DataSnapshot item = items.next();
                                String mKey = item.getKey();
                                Animal animalItem = item.getValue(Animal.class);

                                Float animalXPosition = animalItem.getxPosition();
                                Float animalYPosition = animalItem.getyPosition();

                                ImageView animalSticker = new ImageView(MainActivity.this);
                                animalSticker.bringToFront();
                                animalSticker.setImageResource(animalItem.getImageSource());
                                animalSticker.setX(animalXPosition);
                                animalSticker.setY(animalYPosition);

                                TextView statusEmoticon = new TextView(MainActivity.this);
                                statusEmoticon.bringToFront();
                                statusEmoticon.setX(animalItem.getxPosition() + 25);
                                statusEmoticon.setY(animalItem.getyPosition() - 30);
                                //statusEmoticon.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
                                statusEmoticon.setTextSize(14);

                                ConstraintLayout.LayoutParams animalParams = new ConstraintLayout.LayoutParams(150, 150);
                                animalSticker.setLayoutParams(animalParams);

                                //ConstraintLayout.LayoutParams speechBubbleParams = new ConstraintLayout.LayoutParams(200, 200);

                                ConstraintLayout.LayoutParams statusEmoticonParams = new ConstraintLayout.LayoutParams(150, 150);
                                statusEmoticon.setLayoutParams(statusEmoticonParams);


                                String statusText = "";
                                // Run thread to hide statusEmoticon after 3 seconds
                                if (animalItem.getHappinessLevel() >= 80) {
                                    statusText = showCategory(animalItem.getCategory()) + " ❤️";
                                    statusEmoticon.setText(statusText);
                                } else if (animalItem.getHappinessLevel() <= 20) {
                                    statusText = showCategory(animalItem.getCategory()) + " ☹️";
                                    statusEmoticon.setText(statusText);
                                } else {
                                    statusText = showCategory(animalItem.getCategory()) + " 🙂️";
                                    statusEmoticon.setText(statusText);
                                }


                                zooFrame.addView(animalSticker);
                                zooFrame.addView(statusEmoticon);


                                ObjectAnimator blinkAnimation = ObjectAnimator.ofInt(statusEmoticon, "textColor", Color.BLACK, Color.TRANSPARENT);
                                blinkAnimation.setDuration(3000);
                                blinkAnimation.setEvaluator(new ArgbEvaluator());
                                blinkAnimation.setRepeatCount(ValueAnimator.INFINITE);
                                blinkAnimation.setRepeatMode(ValueAnimator.REVERSE);
                                blinkAnimation.start();


                                animalSticker.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.i("MKey from main", mKey.toString());
                                        Intent i = new Intent(getApplicationContext(), AnimalStatusActivity.class);
                                        //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.putExtra("key", mKey)
                                                .putExtra("status", "update")
                                                .putExtra("type", "animal")
                                                .putExtra("category", animalItem.getCategory())
                                                .putExtra("newUser", "false");
                                        startActivity(i);
                                    }
                                });
                            }
                        }
                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

            }
        }, 100);



    }

    private void subtractHappinessLevel() {

        mDB.child("animal").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Iterator<DataSnapshot> animalItem = snapshot.getChildren().iterator();
                    while (animalItem.hasNext()) {
                        DataSnapshot item = animalItem.next();
                        String itemKey = item.getKey();
                        Animal animal = item.getValue(Animal.class);

                        Integer happinessLevel = animal.getHappinessLevel();

                        if (happinessLevel > 0) {
                            mDB.child("animal").child(itemKey).child("happinessLevel").setValue(happinessLevel - 1);
                        } else {
                            mDB.child("animal").child(itemKey).child("happinessLevel").setValue(0);
                        }
                        mDB.child("animal").child(itemKey).child("lastLogTime").setValue(System.currentTimeMillis());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    private void updateHappinessLevel() {

        runThread = false;
        mDB.child("animal").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Iterator<DataSnapshot> animalItem = snapshot.getChildren().iterator();

                    while (animalItem.hasNext()) {
                        DataSnapshot item = animalItem.next();
                        String itemKey = item.getKey();
                        Animal animal = item.getValue(Animal.class);

                        Integer happinessLevel = animal.getHappinessLevel();
                        Long lastLogTime = animal.getLastLogTime();
                        Integer pointsToDeduct = Math.toIntExact((System.currentTimeMillis() - lastLogTime) / FIVE_MINUTES_IN_MILLIS);

                        Log.i("current Time", String.valueOf(System.currentTimeMillis()));
                        Log.i("last Time", String.valueOf(lastLogTime));


                        if (happinessLevel > 0) {
                            mDB.child("animal").child(itemKey).child("happinessLevel").setValue(happinessLevel - pointsToDeduct);
                        } else {
                            mDB.child("animal").child(itemKey).child("happinessLevel").setValue(0);
                        }

                        Log.i("points to deduct", pointsToDeduct.toString());
                        Log.i("happinessLevel", animal.getHappinessLevel().toString());
                    }

                }

                runThread = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    private String showCategory(String category) {
        if (category.equals("Hobby")) {
            return "🏄";
        } else if (category.equals("Food")) {
            return "🍕";
        } else if (category.equals("Housing")) {
            return "🏠";
        } else if (category.equals("Transport")) {
            return "🚕";
        }else if (category.equals("Entertainment")) {
            return "🍿";
        }else if (category.equals("Travel")) {
            return "✈️";
        }else if (category.equals("Apparel")) {
            return "🛍";
        }else if (category.equals("Saving")) {
            return "🏦";
        }else if (category.equals("Insurance")) {
            return "👷";
        }else if (category.equals("Miscellaneous")) {
            return "🤡";
        }else {
            return "";
        }
    }



}