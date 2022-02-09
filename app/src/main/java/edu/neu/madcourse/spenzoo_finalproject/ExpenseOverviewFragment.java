package edu.neu.madcourse.spenzoo_finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import java.util.Locale;

import edu.neu.madcourse.spenzoo_finalproject.Adapters.ExpenseAdapter;
import edu.neu.madcourse.spenzoo_finalproject.Model.Expense;

public class ExpenseOverviewFragment extends Fragment {
    private FirebaseAuth mAuth;

    private DatabaseReference mDB;
    private Calendar calendar;

    private ProgressBar progressBar;

    private AnyChartView pieChart;
    float monthlyFood, monthlyHousing, monthlyTransport, monthlyEntertainment, monthlyTravel,
            monthlyHobby, monthlyApparel, monthlySaving, monthlyInsurance, monthlyMiscellaneous;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_overview, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());

        calendar = Calendar.getInstance();
        progressBar = view.findViewById(R.id.progress_bar);
        pieChart = view.findViewById(R.id.categoryPieChart);

        progressBar.bringToFront();

        getExpenseCatagories();
        showPieChart();

//        new java.util.Timer().schedule(
//                new java.util.TimerTask() {
//                    @Override
//                    public void run() {
//                        getExpenseCatagories();
//                        showPieChart();
//                    }
//                },
//                1000
//        );
//        view.findViewById(R.id.progress_bar).setVisibility(View.GONE);


        return view;
    }

    private void getExpenseCatagories() {
        getFoodCategory();
        getHousingCategory();
        getTransportCategory();
        getEntertainmentCategory();
        getTravelCategory();
        getHobbyCategory();
        getApparelCategory();
        getSavingCategory();
        getInsuranceCategory();
        getMiscellaneousCategory();
    }

    private void getFoodCategory() {
        mDB.child("expense").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                monthlyFood = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Expense expense = dataSnapshot.getValue(Expense.class);
                    if (expense.getIsExpense() && expense.getCategory().equals("Food") &&
                            expense.getDate().getMonth() == calendar.getTime().getMonth() &&
                            expense.getDate().getYear() == calendar.getTime().getYear()) {
                        monthlyFood += expense.getAmount();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getHousingCategory () {
        mDB.child("expense").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                monthlyHousing = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Expense expense = dataSnapshot.getValue(Expense.class);
                    if (expense.getIsExpense() && expense.getCategory().equals("Housing") &&
                            expense.getDate().getMonth() == calendar.getTime().getMonth() &&
                            expense.getDate().getYear() == calendar.getTime().getYear()) {
                        monthlyHousing += expense.getAmount();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTransportCategory() {
        mDB.child("expense").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                monthlyTransport = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Expense expense = dataSnapshot.getValue(Expense.class);
                    if (expense.getIsExpense() && expense.getCategory().equals("Transport") &&
                            expense.getDate().getMonth() == calendar.getTime().getMonth() &&
                            expense.getDate().getYear() == calendar.getTime().getYear()) {
                        monthlyTransport += expense.getAmount();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getEntertainmentCategory () {
        mDB.child("expense").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                monthlyEntertainment = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Expense expense = dataSnapshot.getValue(Expense.class);
                    if (expense.getIsExpense() && expense.getCategory().equals("Entertainment") &&
                            expense.getDate().getMonth() == calendar.getTime().getMonth() &&
                            expense.getDate().getYear() == calendar.getTime().getYear()) {
                        monthlyEntertainment += expense.getAmount();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTravelCategory() {
        mDB.child("expense").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                monthlyTravel = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Expense expense = dataSnapshot.getValue(Expense.class);
                    if (expense.getIsExpense() && expense.getCategory().equals("Travel") &&
                            expense.getDate().getMonth() == calendar.getTime().getMonth() &&
                            expense.getDate().getYear() == calendar.getTime().getYear()) {
                        monthlyTravel += expense.getAmount();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getHobbyCategory() {
        mDB.child("expense").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                monthlyHobby = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Expense expense = dataSnapshot.getValue(Expense.class);
                    if (expense.getIsExpense() && expense.getCategory().equals("Hobby") &&
                            expense.getDate().getMonth() == calendar.getTime().getMonth() &&
                            expense.getDate().getYear() == calendar.getTime().getYear()) {
                        monthlyHobby += expense.getAmount();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getApparelCategory() {
        mDB.child("expense").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                monthlyApparel = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Expense expense = dataSnapshot.getValue(Expense.class);
                    if (expense.getIsExpense() && expense.getCategory().equals("Apparel") &&
                            expense.getDate().getMonth() == calendar.getTime().getMonth() &&
                            expense.getDate().getYear() == calendar.getTime().getYear()) {
                        monthlyApparel += expense.getAmount();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSavingCategory() {
        mDB.child("expense").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                monthlySaving = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Expense expense = dataSnapshot.getValue(Expense.class);
                    if (expense.getIsExpense() && expense.getCategory().equals("Saving") &&
                            expense.getDate().getMonth() == calendar.getTime().getMonth() &&
                            expense.getDate().getYear() == calendar.getTime().getYear()) {
                        monthlySaving += expense.getAmount();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getInsuranceCategory () {
        mDB.child("expense").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                monthlyInsurance = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Expense expense = dataSnapshot.getValue(Expense.class);
                    if (expense.getIsExpense() && expense.getCategory().equals("Insurance") &&
                            expense.getDate().getMonth() == calendar.getTime().getMonth() &&
                            expense.getDate().getYear() == calendar.getTime().getYear()) {
                        monthlyInsurance += expense.getAmount();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMiscellaneousCategory () {
        mDB.child("expense").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                monthlyMiscellaneous = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Expense expense = dataSnapshot.getValue(Expense.class);
                    if (expense.getIsExpense() && expense.getCategory().equals("Miscellaneous") &&
                            expense.getDate().getMonth() == calendar.getTime().getMonth() &&
                            expense.getDate().getYear() == calendar.getTime().getYear()) {
                        monthlyMiscellaneous += expense.getAmount();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showPieChart() {
        mDB.child("expense").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Pie pie = AnyChart.pie();
                List<DataEntry> data = new ArrayList<>();
                data.add(new ValueDataEntry("Food", monthlyFood));
                data.add(new ValueDataEntry("Housing", monthlyHousing));
                data.add(new ValueDataEntry("Transport", monthlyTransport));
                data.add(new ValueDataEntry("Entertainment", monthlyEntertainment));
                data.add(new ValueDataEntry("Travel", monthlyTravel));
                data.add(new ValueDataEntry("Hobby", monthlyHobby));
                data.add(new ValueDataEntry("Apparel", monthlyApparel));
                data.add(new ValueDataEntry("Saving", monthlySaving));
                data.add(new ValueDataEntry("Insurance", monthlyInsurance));
                data.add(new ValueDataEntry("Miscellaneous", monthlyMiscellaneous));

                pie.data(data);
                pie.title(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " Expense Summary");
                pie.title().fontSize(20);
                pie.title().padding(10);
                pie.labels().position("outside");

                pie.legend().title().enabled(true);
                pie.legend().title().text("Expense Categories");
                pie.legend().title().padding(10);
                pie.legend().itemsLayout("horizontal-expandable");
                pie.legend().position("bottom");

                pieChart.setChart(pie);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
