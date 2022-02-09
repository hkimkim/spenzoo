package edu.neu.madcourse.spenzoo_finalproject.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.List;
import java.util.Objects;
import edu.neu.madcourse.spenzoo_finalproject.Model.Budget;
import edu.neu.madcourse.spenzoo_finalproject.R;


public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.ViewHolder> {

    Context context;
    private List<Budget> mBudget;

    public BudgetAdapter(Context context, List<Budget> mBudget) {
        this.context = context;
        this.mBudget = mBudget;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.budget_record, parent, false);
        ViewHolder viewHolder = new ViewHolder(layout);

        Objects.requireNonNull(viewHolder.itemView).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetAdapter.ViewHolder holder, int position) {
        Log.i("onBindViewHolder", String.valueOf(position));
        Budget budget = mBudget.get(position);
        holder.budgetRecordDate.setText(budget.getYear().toString() + '/' + budget.getMonth().toString());
        Log.i("description", budget.getDescription());
        holder.budgetRecordDescription.setText(budget.getDescription());
        holder.budgetRecordAmount.setText(budget.getAmount().toString());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView budgetRecordDate;
        public TextView budgetRecordAmount;
        public TextView budgetRecordDescription;
        public ViewHolder(View itemView) {
            super(itemView);
            budgetRecordDate = itemView.findViewById(R.id.budget_record_date);
            budgetRecordAmount = itemView.findViewById(R.id.budget_record_amount);
            budgetRecordDescription = itemView.findViewById(R.id.budget_record_description);
        }
    }

    @Override
    public int getItemCount() {
        return mBudget.size();
    }
}