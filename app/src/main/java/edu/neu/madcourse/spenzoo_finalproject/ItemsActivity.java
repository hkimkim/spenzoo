package edu.neu.madcourse.spenzoo_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import edu.neu.madcourse.spenzoo_finalproject.Adapters.ItemsAdapter;
import edu.neu.madcourse.spenzoo_finalproject.Adapters.ZooAdapter;
import edu.neu.madcourse.spenzoo_finalproject.Model.Animal;
import edu.neu.madcourse.spenzoo_finalproject.Model.Item;

public class ItemsActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private DatabaseReference mDB;
    private ItemsAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());

        setAdapter();
    }


    private void setAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,  false);
        recyclerView = (RecyclerView) findViewById(R.id.items_recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);


        mDB.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Item> itemList = new ArrayList<>();
                Iterator<DataSnapshot> items = snapshot.getChildren().iterator();
                ArrayList<String> mKeysList = new ArrayList<>();

                while (items.hasNext()) {
                    DataSnapshot item = items.next();
                    Item itemValue = item.getValue(Item.class);

                    itemList.add(itemValue);
                    mKeysList.add(item.getKey());
                }


                adapter = new ItemsAdapter(itemList);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new ItemsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        startActivity(new Intent(getApplicationContext(), PiecePlacingActivity.class).putExtra("key", mKeysList.get(position)).putExtra("status", "update").putExtra("type", "item").putExtra("newUser", "false"));;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}