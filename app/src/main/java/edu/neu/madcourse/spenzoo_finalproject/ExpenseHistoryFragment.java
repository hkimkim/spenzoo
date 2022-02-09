package edu.neu.madcourse.spenzoo_finalproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import edu.neu.madcourse.spenzoo_finalproject.Adapters.ExpenseAdapter;
import edu.neu.madcourse.spenzoo_finalproject.Adapters.RecentTransactionAdapter;
import edu.neu.madcourse.spenzoo_finalproject.Model.Expense;

public class ExpenseHistoryFragment extends Fragment {

    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;
    private List<Expense> mExpense;
    private ExpenseAdapter expenseAdapter;


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

    private DatabaseReference mDB;
    private Calendar calendar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_history, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());

        calendar = Calendar.getInstance();

        recyclerView = view.findViewById(R.id.expenseHistory);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.getStackFromEnd();
        recyclerView.setLayoutManager(linearLayoutManager);

        readExpenseHistory();

        return view;
    }

    private void readExpenseHistory() {
        mExpense = new ArrayList<>();

        mDB = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
        mDB.child("expense").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mExpense.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Expense expense = dataSnapshot.getValue(Expense.class);
                    if (expense.getDate().getMonth() == calendar.getTime().getMonth() &&
                            expense.getDate().getYear() == calendar.getTime().getYear()) {
                        mExpense.add(expense);
                    }

                    expenseAdapter = new ExpenseAdapter(getContext(), mExpense);
                    recyclerView.setAdapter(expenseAdapter);

                    recyclerView.setAdapter(expenseAdapter);

                }
                Collections.reverse(mExpense);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
