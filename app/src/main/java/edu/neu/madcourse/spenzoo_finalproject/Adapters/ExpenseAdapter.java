package edu.neu.madcourse.spenzoo_finalproject.Adapters;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import edu.neu.madcourse.spenzoo_finalproject.ExpenseHistoryFragment;
import edu.neu.madcourse.spenzoo_finalproject.Model.Expense;
import edu.neu.madcourse.spenzoo_finalproject.R;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    public static final int EXPENSE = 0;
    public static final int INCOME = 1;

    private Context context;
    private List<Expense> mExpense;
    private OnItemClickListener elistener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        elistener = listener;
    }


    public ExpenseAdapter(Context context, List<Expense> mExpense) {
        this.context = context;
        this.mExpense = mExpense;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == EXPENSE) {
            View view = LayoutInflater.from(context).inflate(R.layout.expense_record, parent, false);
            return new ViewHolder(view, elistener);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.income_record, parent, false);
            return new ViewHolder(view, elistener);
        }

//        Objects.requireNonNull(viewHolder.itemView).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ViewHolder holder, int position) {
        Expense expense = mExpense.get(position);
        holder.expenseDate.setText(dateFormatter(expense.getDate()));
        holder.expenseName.setText(expense.getDescription());
        holder.expenseAmount.setText(expense.getAmount().toString());
    }

    @SuppressLint("SimpleDateFormat")
    private String dateFormatter(Date date) {
        Date today = Calendar.getInstance().getTime();
        if (date.getYear() == today.getYear() && date.getMonth() == today.getMonth() && date.getDate() == today.getDate()) {
            return "Today";
        } else if (date.getYear() == today.getYear() && date.getMonth() == today.getMonth() && date.getDate() == today.getDate()) {
            return "Yesterday";
        } else {
            return new SimpleDateFormat("EEEE, d MMM").format(date);
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView expenseDate;
        public TextView expenseName;
        public TextView expenseAmount;

        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);

            expenseDate = itemView.findViewById(R.id.expense_date);
            expenseName = itemView.findViewById(R.id.expense_description);
            expenseAmount = itemView.findViewById(R.id.expense_amount);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return mExpense.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mExpense.get(position).getIsExpense()) {
            return EXPENSE;
        } else {
            return INCOME;
        }
    }
}
