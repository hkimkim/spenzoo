package edu.neu.madcourse.spenzoo_finalproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
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

import java.util.Calendar;
import java.util.TimeZone;

import edu.neu.madcourse.spenzoo_finalproject.Model.Expense;
import edu.neu.madcourse.spenzoo_finalproject.ui.main.SectionsPagerAdapter;
import edu.neu.madcourse.spenzoo_finalproject.databinding.ActivityExpenseBinding;

public class ExpenseActivity extends AppCompatActivity {

    private ActivityExpenseBinding binding;
    private BottomNavigationView bottomNavigationView;

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
    private FirebaseAuth mAuth;
    private DatabaseReference mDB;
    private int reward;
    // pop window for add new expense --- end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        displayBottomNavigationView();

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

    public void displayBottomNavigationView() {
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);

        bottomNavigationView.setSelectedItemId(R.id.mi_expense);

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
        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());

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

                DatePickerDialog dateDialog = new DatePickerDialog(ExpenseActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
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

                    // Update dashboard
//                    updateExpenseDashBoard();

                    // Update Expense
//                    setRecentTransaction();

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
                Toast.makeText(ExpenseActivity.this, "Expense Recorded! 💰Reward+5💰", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}