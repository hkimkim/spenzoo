package edu.neu.madcourse.spenzoo_finalproject.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import edu.neu.madcourse.spenzoo_finalproject.Model.Expense;
import edu.neu.madcourse.spenzoo_finalproject.R;

public class RecentTransactionAdapter extends RecyclerView.Adapter<RecentTransactionAdapter.ViewHolder> {
    public ArrayList<Expense> transactionList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public RecentTransactionAdapter(ArrayList<Expense> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_transaction, parent, false);
        return new ViewHolder(inflate, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.dateOfTransaction.setText(dateFormatter(transactionList.get(position).getDate()));

        if (transactionList.get(position).getDescription().isEmpty()) {
            holder.description.setText(transactionList.get(position).getCategory());
        } else {
            holder.description.setText(transactionList.get(position).getDescription());
        }
        holder.amount.setText("$"+ transactionList.get(position).getAmount());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateOfTransaction;
        TextView amount;
        TextView description;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            dateOfTransaction = itemView.findViewById(R.id.dateOfTransaction);
            amount = itemView.findViewById(R.id.amountOfTransaction);
            description = itemView.findViewById(R.id.name_item);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }



    }

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


}
