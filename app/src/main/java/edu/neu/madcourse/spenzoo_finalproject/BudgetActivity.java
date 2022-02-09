package edu.neu.madcourse.spenzoo_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import edu.neu.madcourse.spenzoo_finalproject.Adapters.BudgetAdapter;
import edu.neu.madcourse.spenzoo_finalproject.Model.Budget;
import edu.neu.madcourse.spenzoo_finalproject.Model.Expense;

public class BudgetActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDB;
    private BottomNavigationView bottomNavigationView;
    private Button addBudgetBtn;
    private QuestTracker questTracker;

    // pop window for set budget --- start
    private AlertDialog.Builder budgetDialogBuilder;
    private AlertDialog budgeDialog;
    private TextView budgetMonth;
    private EditText budgetAmount, budgetDescription;
    private Button addBudget, cancelBudget;
    private String chosenMonth;
    private String chosenYear;
    // pop window for add new expense --- end

    // for  all budgets
    private List<Budget> mBudget;
    private RecyclerView recyclerView;
    private BudgetAdapter budgetAdapter;
    private RecyclerView.LayoutManager rLayoutManger;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        addBudgetBtn = findViewById(R.id.add_budget);
        addBudgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("BudgetActivity", "add budget");
                createNewBudgetDialog();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
        questTracker = QuestTracker.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        rLayoutManger = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(rLayoutManger);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        displayBottomNavigationView();
        readBudgetHistory();

        // Invoke add new expense dialog -- start
        dateOfExpense = Calendar.getInstance(TimeZone.getTimeZone("EST"));

        FloatingActionButton addExpense = findViewById(R.id.fab);
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("MainActivity:", "click add expense button");
                createNewExpenseDialog();
//                updateExpenseDashBoard();
            }
        });
    }

    private void readBudgetHistory() {
        mBudget = new ArrayList<>();

        mDB = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
        mDB.child("budget").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mBudget.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Budget budget = dataSnapshot.getValue(Budget.class);

                    mBudget.add(budget);

                    budgetAdapter = new BudgetAdapter(BudgetActivity.this, mBudget);
                    recyclerView.setAdapter(budgetAdapter);

                    new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                        @Override
                        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                            // this method is called
                            // when the item is moved.
                            Log.i("BudgetActivity", "swap");
                            return false;
                        }

                        @Override
                        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                            // this method is called when we swipe our item to right direction.
                            // on below line we are getting the item at a particular position.
                            Budget deletedBudget = mBudget.get(viewHolder.getAdapterPosition());
                            mBudget.remove(deletedBudget);
                            String deletedPeriod = deletedBudget.getYear() + "-" + deletedBudget.getMonth();
                            mDB.child("budget").child(deletedPeriod).removeValue();
                        }
                    }).attachToRecyclerView(recyclerView);
                }

                Collections.reverse(mBudget);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    // popup window for budge --- start
    public void createNewBudgetDialog() {
        budgetDialogBuilder = new AlertDialog.Builder(this);
        final View budgetDialogView = getLayoutInflater().inflate(R.layout.set_budget_popup, null);

        // TextView
        budgetAmount = budgetDialogView.findViewById(R.id.budget_amount);
        budgetDescription = budgetDialogView.findViewById(R.id.budget_description);

        // Month Picker
        budgetMonth = budgetDialogView.findViewById(R.id.budget_month);

        Calendar today = Calendar.getInstance();
        int curMonth = today.get(Calendar.MONTH) + 1;
        String date = curMonth + "/" + today.get(Calendar.YEAR);
        budgetMonth.setText(date);
        chosenMonth = String.valueOf(curMonth);
        chosenYear =  String.valueOf(today.get(Calendar.YEAR));

        budgetMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("EST"));
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog monthDatePickerDialog = new DatePickerDialog(BudgetActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
                monthDatePickerDialog.setTitle("Select Period");
                monthDatePickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
                monthDatePickerDialog.show();

            }});

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                Log.i("BudgetActivity", "set date");
                chosenMonth = String.valueOf(month);
                chosenYear = String.valueOf(year);
                String chosenDate = chosenMonth + "/" + chosenYear;

                budgetMonth.setText(chosenDate);
            }
        };

        // set view
        budgetDialogBuilder.setView(budgetDialogView);
        budgeDialog = budgetDialogBuilder.create();
        budgeDialog.show();

        // Buttons
        addBudget = budgetDialogView.findViewById(R.id.budget_add);
        addBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (budgetAmount.getText().toString().equals("")) {
                    Toast.makeText(BudgetActivity.this, "Please enter amount", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("Add Budget:", "click add");
                    Log.i("Add Budget:", "current user id: " + mAuth.getCurrentUser().getUid());

                    String chosenPeriod =  chosenYear + "-" + chosenMonth;

                    // Check if chosenPeriod budget exists
                    DatabaseReference thisMonthBudget = mDB.child("budget").child(chosenPeriod);

                    thisMonthBudget.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            Log.i("BudgetActivity Budget Amount", budgetAmount.getText().toString());
//                            Log.i("BudgetActivity Budget", chosenYear);
//                            Log.i("BudgetActivity Budget", chosenMonth);


                            Budget newBudget = new Budget(Double.valueOf(budgetAmount.getText().toString()), Integer.parseInt(chosenYear), Integer.parseInt(chosenMonth), budgetDescription.getText().toString());
                            Log.i("BudgetActivity Budget", "+++++++");
                            Log.i("BudgetActivity Budget", snapshot.toString());
                            if (snapshot.exists()) {
                                Log.i("find duplicate", snapshot.child("amount").getValue().toString() + '/' + snapshot.child("description").getValue().toString());


                                if (Integer.parseInt(snapshot.child("amount").getValue().toString()) > 0) {
                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    thisMonthBudget.setValue(newBudget);
                                                    budgeDialog.dismiss();

                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    break;
                                            }

                                        }
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(BudgetActivity.this);
                                    builder.setMessage("You have set budget for this month before. Do you want to overwrite it?").setPositiveButton("Yes", dialogClickListener)
                                            .setNegativeButton("No", dialogClickListener).show();
                                } else {
                                    // thisMonthBudget.setValue(newBudget);
                                    budgeDialog.dismiss();
                                }
                            } else {
                                mDB.child("budget").child(chosenPeriod).setValue(newBudget);
                                budgeDialog.dismiss();
                                grantBudgetRewards();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            budgeDialog.dismiss();
                        }
                    });


                }
            }
        });
        cancelBudget = budgetDialogView.findViewById(R.id.budget_cancel);
        cancelBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                budgeDialog.dismiss();
            }
        });
    }
    // popup window for budge --- end


    private void displayBottomNavigationView() {
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);

        bottomNavigationView.setSelectedItemId(R.id.mi_budget);

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

                    case R.id.mi_expense:
                        startActivity(new Intent(getApplicationContext(), ExpenseActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.mi_budget:
                        startActivity(new Intent(getApplicationContext(), BudgetActivity.class));
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
        expenseCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                chosenCategory = (String) expenseCategory.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

                DatePickerDialog dateDialog = new DatePickerDialog(BudgetActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
                dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                dateDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
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
                Toast.makeText(getApplicationContext(), "You're recording an expense", Toast.LENGTH_SHORT).show();
            } else {
                TextView expenseText = expenseDialogView.findViewById(R.id.expense_text);
                expenseText.setTextColor(getResources().getColor(R.color.gray));
                TextView incomeText = expenseDialogView.findViewById(R.id.expense_bool);
                incomeText.setTextColor(getResources().getColor(R.color.black));
                Toast.makeText(getApplicationContext(), "You're recording an income", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Please enter amount", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.i("Add Expense:", "click add");
                    Log.i("Add Expense:", "current user id: "+ mAuth.getCurrentUser().getUid());

                    Expense newExpense = new Expense(chosenCategory, Double.valueOf(expenseAmount.getText().toString()),dateOfExpense.getTime(), expenseDescription.getText().toString(), isExpense);

                    // add record to database
                    mDB.child("expense").push().setValue(newExpense);


                    // reset
                    dateOfExpense = Calendar.getInstance(TimeZone.getTimeZone("EST"));
                    isExpense = true;
                    chosenCategory = Expense.Category.DEFAULT.name();  // enum from Expense Model class
                    dialog.dismiss();

                    // grant user rewards
                    grantRewards();
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

    private void grantRewards() {
        mDB.child("rewards").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reward = snapshot.getValue(int.class);
                Log.i("Grant rewards", "current reward: " + String.valueOf(reward));
                mDB.child("rewards").setValue(reward + 5);
                Toast.makeText(BudgetActivity.this, "Expense Recorded! 💰Reward+5💰", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void grantBudgetRewards() {
        mDB.child("rewards").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reward = snapshot.getValue(int.class);
                Log.i("Grant rewards", "current reward: " + String.valueOf(reward));
                mDB.child("rewards").setValue(reward + 10);
                Toast.makeText(BudgetActivity.this, "Budget Set! 💰Reward+10💰", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}