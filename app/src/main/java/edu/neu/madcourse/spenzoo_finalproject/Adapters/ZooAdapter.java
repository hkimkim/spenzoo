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

import edu.neu.madcourse.spenzoo_finalproject.Model.Animal;
import edu.neu.madcourse.spenzoo_finalproject.R;

public class ZooAdapter extends RecyclerView.Adapter<ZooAdapter.ViewHolder> {
    public ArrayList<Animal> animalList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public ZooAdapter(ArrayList<Animal> transactionList) {
        this.animalList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_animal, parent, false);
        return new ViewHolder(inflate, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.animalImage.setImageResource(animalList.get(position).getImageSource());

        holder.name.setText(animalList.get(position).getName());
        holder.dateOfAdoption.setText(new SimpleDateFormat("dd MMM, yyyy").format(animalList.get(position).getDateOfAdoption()));
        holder.category.setText(animalList.get(position).getCategory());
    }

    @Override
    public int getItemCount() {
        return animalList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView animalImage;
        TextView name, dateOfAdoption, category;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            animalImage = itemView.findViewById(R.id.animal_image);
            name = itemView.findViewById(R.id.name_animal);
            dateOfAdoption = itemView.findViewById(R.id.dateOfAdoption_animal);
            category = itemView.findViewById(R.id.animal_category);

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