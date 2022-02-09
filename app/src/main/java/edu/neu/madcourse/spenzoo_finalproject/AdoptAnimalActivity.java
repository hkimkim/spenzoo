package edu.neu.madcourse.spenzoo_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.neu.madcourse.spenzoo_finalproject.Model.Animal;
import edu.neu.madcourse.spenzoo_finalproject.Model.User;

public class AdoptAnimalActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDB;
    private String mKey;
    private String status;
    private String newUser;

    private TextView welcomeUser;
    private TextView adoptionText;
    private ImageSwitcher imageSwitcher;
    private ImageButton prevBtn;
    private ImageButton nextBtn;
    private Button adoptBtn;
    private final Integer[] listOfAnimals = {R.drawable.penguin, R.drawable.elephant,
                                        R.drawable.red_panda, R.drawable.fox, R.drawable.koala};

    private final Integer numOfAnimals = listOfAnimals.length -1;
    private Integer counter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_animal);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mKey = extras.getString("key");
            status = extras.getString("status");
            newUser = extras.getString("newUser");
        }

        Log.i("newUser in Adopt", newUser);

        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference("Users")
                .child(mAuth.getCurrentUser().getUid());

        welcomeUser = findViewById(R.id.welcome_user);
        adoptionText = findViewById(R.id.explain_adopt_text);
        imageSwitcher = findViewById(R.id.image_switcher);
        prevBtn = findViewById(R.id.prev_animal);
        nextBtn = findViewById(R.id.next_animal);
        adoptBtn = findViewById(R.id.adopt_btn);

        setWelcomeText();

        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setImageResource(R.drawable.elephant);
                return imageView;
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("counter prev", String.valueOf(counter));

                Animation in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
                Animation out = AnimationUtils.loadAnimation(getApplicationContext(),  R.anim.slide_out_left);

                imageSwitcher.setOutAnimation(out);
                imageSwitcher.setInAnimation(in);
                counter--;

                if (counter == -1) {
                    imageSwitcher.setImageResource(listOfAnimals[numOfAnimals]);
                    counter = numOfAnimals;
                } else {
                    imageSwitcher.setImageResource(listOfAnimals[counter]);
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("counter next", String.valueOf(counter));
                Animation out = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_out_right);
                Animation in = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left);

                imageSwitcher.setOutAnimation(out);
                imageSwitcher.setInAnimation(in);

                counter++;

                if (counter == numOfAnimals + 1) {
                    imageSwitcher.setImageResource(listOfAnimals[0]);
                    counter = 0;
                } else {
                    imageSwitcher.setImageResource(listOfAnimals[counter]);
                }
            }
        });

        adoptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAnimalToDB();
                incrementNumOfAnimals();
                // grantRewards();
                Intent i = new Intent(getApplicationContext(), AnimalSettingActivity.class);
                i.putExtra("key", mKey);
                i.putExtra("status", status);
                i.putExtra("newUser", newUser);
                startActivity(i);

            }
        });
    }

    private void setWelcomeText() {

        mDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);

                    if (status.equals("initial")) {
                        welcomeUser.setText("Welcome @" + user.getUsername() + "👋");
                    } else {
                        welcomeUser.setVisibility(View.INVISIBLE);
                        adoptionText.setText("You can adopt up to 5 animals");
                    }
                } else {
                    Log.i("Setting Animal", "Animal not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void saveAnimalToDB() {
        Animal.AnimalType chosenAnimalType = chosenAnimal();
        Log.i("Chosen Animal", chosenAnimalType.toString());
        mKey = mDB.child("animal").push().getKey();
        mDB.child("animal").child(mKey).setValue(new Animal(chosenAnimalType));
        Log.i("mkey", mKey);
    }

    private void incrementNumOfAnimals() {
        mDB.child("numOfAnimals").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Integer count = snapshot.getValue(Integer.class);
                    Log.i("count", count.toString());
                    mDB.child("numOfAnimals").setValue(count + 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private Animal.AnimalType chosenAnimal() {
        Animal.AnimalType chosenAnimal;

        Log.i("Selected Animal id", String.valueOf(counter));

        if (counter == 0) {
            chosenAnimal = Animal.AnimalType.PENGUIN;
        } else if (counter == 1) {
            chosenAnimal = Animal.AnimalType.ELEPHANT;
        } else if (counter == 2) {
            chosenAnimal = Animal.AnimalType.RED_PANDA;
        } else if (counter == 3) {
            chosenAnimal = Animal.AnimalType.FOX;
        } else {
            chosenAnimal = Animal.AnimalType.KOALA;
        }

        Log.i("Selected Animal", chosenAnimal.toString());
        return chosenAnimal;
    }



}