package edu.neu.madcourse.spenzoo_finalproject.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.neu.madcourse.spenzoo_finalproject.Model.Item;
import edu.neu.madcourse.spenzoo_finalproject.R;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    public ArrayList<Item> itemsList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public ItemsAdapter(ArrayList<Item> transactionList) {
        this.itemsList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_item, parent, false);
        return new ViewHolder(inflate, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemImage.setImageResource(itemsList.get(position).getImageSource());

        holder.name.setText(itemsList.get(position).getType());
        holder.dateOfPurchase.setText(new SimpleDateFormat("dd MMM, yyyy").format(itemsList.get(position).getDateOfPurchase()));
        holder.cost.setText("-" + itemsList.get(position).getCost().toString() +"pts");
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView name, dateOfPurchase, cost;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.img_item);
            name = itemView.findViewById(R.id.name_item);
            dateOfPurchase = itemView.findViewById(R.id.item_date_of_purchase);
            cost = itemView.findViewById(R.id.item_cost);

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
}