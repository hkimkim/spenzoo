package edu.neu.madcourse.spenzoo_finalproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;

import edu.neu.madcourse.spenzoo_finalproject.ClickListener;
import edu.neu.madcourse.spenzoo_finalproject.Model.Quest;
import edu.neu.madcourse.spenzoo_finalproject.R;

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.ViewHolder> {

    public static final int COMPLETED = 0;
    public static final int INCOMPLETE = 1;
    public static final int SUBMITTED = 2;

    private Context context;
    private Quest[] quests;
    private ClickListener listener;

    public QuestAdapter(Context context, Quest[] quests, ClickListener listener) {
        this.context = context;
        this.quests = quests;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == COMPLETED) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quest_completed, parent, false);
            return new ViewHolder(view, listener);
        } else if (viewType == INCOMPLETE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quest_incomplete, parent, false);
            return new ViewHolder(view, listener);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quest_submitted, parent, false);
            return new ViewHolder(view, listener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Quest quest = quests[position];

        // TODO: why can't I get the to string of int?
//        holder.questReward.setText(quest.getReward().toString());
        holder.questDescription.setText(quest.getQuestDescription());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView questReward;
        public TextView questDescription;
        public ImageView button;
        public WeakReference<ClickListener> listenerRef;

        public ViewHolder(View itemView, ClickListener listener) {
            super(itemView);

            listenerRef = new WeakReference<>(listener);
            questReward = itemView.findViewById(R.id.quest_reward);
            questDescription = itemView.findViewById(R.id.quest_description);
            button = itemView.findViewById(R.id.check_button);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }

    @Override
    public int getItemCount() {
        return quests.length;
    }

    @Override
    public int getItemViewType(int position) {
        if (quests[position].getStatus() == 0) {
            return COMPLETED;
        } else if (quests[position].getStatus() == 1) {
            return INCOMPLETE;
        } else {
            return SUBMITTED;
        }
    }

}
