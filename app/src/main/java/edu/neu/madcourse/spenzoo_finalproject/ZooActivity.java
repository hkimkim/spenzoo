package edu.neu.madcourse.spenzoo_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import edu.neu.madcourse.spenzoo_finalproject.Adapters.ZooAdapter;
import edu.neu.madcourse.spenzoo_finalproject.Model.Animal;

public class ZooActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private DatabaseReference mDB;
    private ZooAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoo);
        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());

        setAdapter();
    }


    private void setAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,  false);
        recyclerView = (RecyclerView) findViewById(R.id.animals_recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);


        mDB.child("animal").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Animal> animalsList = new ArrayList<>();
                Iterator<DataSnapshot> items = snapshot.getChildren().iterator();
                ArrayList<String> mKeysList = new ArrayList<>();

                while (items.hasNext()) {
                    DataSnapshot item = items.next();
                    Animal animalItem = item.getValue(Animal.class);

                    animalsList.add(animalItem);
                    mKeysList.add(item.getKey());
                }


                adapter = new ZooAdapter(animalsList);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new ZooAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent i = new Intent(getApplicationContext(), AnimalStatusActivity.class)
                                .putExtra("key", mKeysList.get(position))
                                .putExtra("status", "update")
                                .putExtra("category", animalsList.get(position).getCategory())
                                .putExtra("type", "animal").putExtra("newUser", "false");

                        startActivity(i);
                }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}