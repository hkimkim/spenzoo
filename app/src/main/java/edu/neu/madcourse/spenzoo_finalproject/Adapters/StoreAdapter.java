package edu.neu.madcourse.spenzoo_finalproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import edu.neu.madcourse.spenzoo_finalproject.Model.Item;
import edu.neu.madcourse.spenzoo_finalproject.R;

public class StoreAdapter extends BaseAdapter {

    Context context;
    private Item[] storeItems;
    private LayoutInflater inflater;

    public StoreAdapter(Context context, Item[] storeItems) {
        this.context = context;
        this.storeItems = storeItems;
    }

    @Override
    public int getCount() {
        return storeItems.length;
    }

    @Override
    public Object getItem(int i) {
        return storeItems[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View gridView = view;
        if (view == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.item_in_store, null);
        }

        ImageView itemImg = gridView.findViewById(R.id.item_img);
        TextView itemCost = gridView.findViewById(R.id.item_cost);
        itemImg.setImageResource(storeItems[i].getImg());
        itemCost.setText(String.valueOf(storeItems[i].getCost()));
        return gridView;
    }
}