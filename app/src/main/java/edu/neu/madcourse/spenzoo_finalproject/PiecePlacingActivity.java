package edu.neu.madcourse.spenzoo_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import edu.neu.madcourse.spenzoo_finalproject.Model.Animal;
import edu.neu.madcourse.spenzoo_finalproject.Model.Item;

public class PiecePlacingActivity extends AppCompatActivity implements View.OnTouchListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDB;
    private String mKey;
    private String status;
    private String pieceType;
    private String category;
    private String newUser;
    private Boolean tryShaking;

    private TextView positionActivityTitle;
    private Button confirmBtn;
    private Button deleteBtn;
    private Button itemConfirmBtn;
    private ConstraintLayout zooFrame;
    private ImageView sticker;
    private float x, y;
    private float selectedX, selectedY;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piece_placing);
        mAuth = FirebaseAuth.getInstance();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mKey = extras.getString("key");
            status = extras.getString("status");
            pieceType = extras.getString("type");
            category = extras.getString("category");
            newUser = extras.getString("newUser");
        }

        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference("Users")
                .child(mAuth.getCurrentUser().getUid());

        displayZoo();

        deleteBtn = findViewById(R.id.position_delete_item);
        itemConfirmBtn = findViewById(R.id.position_confirm_item_btn);
        confirmBtn = (Button) findViewById(R.id.position_confirm_btn);


        positionActivityTitle = (TextView) findViewById(R.id.place_selection_title);
        zooFrame = (ConstraintLayout) findViewById(R.id.zooFrame);

        sticker = (ImageView) findViewById(R.id.animal_sticker);

        if (pieceType.equals("animal")) {
//            if (status.equals("update")) {
//                setAnimalStickerPosition();
//            }
            setAnimal();
            sticker.bringToFront();
        } else {
//            if (status.equals("update")) {
//                setItemStickerPosition();
//            }
            setItem();;
        }


        if (pieceType.equals("item") && status.equals("update")) {
            confirmBtn.setVisibility(View.INVISIBLE);
            itemConfirmBtn.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.VISIBLE);
        }

        sticker.setOnTouchListener(this);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pieceType.equals("animal")) {
                    saveToAnimalDB();
                } else {
                    saveToItemDB();
                }
            }
        });

        itemConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 saveToItemDB();
            }
        });


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
            }
        });
    }

    private void deleteItem() {
        mDB.child("items").child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    AlertDialog alertDialog = new AlertDialog.Builder(PiecePlacingActivity.this).create();
                    alertDialog.setTitle("Delete Item");
                    alertDialog.setMessage("Do you want to delete the item?");

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mDB.child("items").child(mKey).removeValue();
                            Toast.makeText(getApplicationContext(), "item deleted", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();

                } else {
                    Log.i("Saving Item position", "Animal not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setAnimalStickerPosition() {
        mDB.child("animal").child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sticker.setY(snapshot.getValue(Animal.class).getyPosition());
                sticker.setX(snapshot.getValue(Animal.class).getxPosition());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setItemStickerPosition() {
        mDB.child("items").child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sticker.setY(snapshot.getValue(Item.class).getyPosition());
                sticker.setX(snapshot.getValue(Item.class).getxPosition());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int width = zooFrame.getWidth() - v.getWidth();
        int height = zooFrame.getHeight() - v.getHeight();


        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x = event.getX();
            y = event.getY();

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            v.setX(event.getRawX() - x);
            v.setY(event.getRawY() - (y + (zooFrame.getHeight()/3 + sticker.getHeight())));

            selectedX = v.getX();
            selectedY = v.getY();

            if (selectedY > height) {
                selectedY = height;
            }

            if (selectedY < 0) {
                selectedY = 20;
            }


            if (selectedX < 0) {
                selectedX = 0;
            }

            if (selectedX > width) {
                selectedX = width;
            }
            Log.i("Item set at", (selectedX) +"," + (selectedY));


        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (v.getX() > width && v.getY() > height) {
                v.setX(width);
                v.setY(height);
            } else if (v.getX() < 0 && v.getY() > height) {
                v.setX(0);
                v.setY(height);
            } else if (v.getX() > width && v.getY() < 0) {
                v.setX(width);
                v.setY(0);
            } else if (v.getX() < 0 && v.getY() < 0) {
                v.setX(0);
                v.setY(0);
            } else if (v.getX() < 0 || v.getX() > width) {
                if (v.getX() < 0) {
                    v.setX(0);
                    v.setY(event.getRawY() - y - (zooFrame.getHeight()/2 + sticker.getHeight()));
                } else {
                    v.setX(width);
                    v.setY(event.getRawY() - y - (zooFrame.getHeight()/2 + sticker.getHeight()));
                }
            } else if (v.getY() < 0 || v.getY() > height) {
                if (v.getY() < 0) {
                    v.setX(event.getRawX() - x);
                    v.setY(0);
                } else {
                    v.setX(event.getRawX() - x);
                    v.setY(height);
                }
            }
        }

        return true;
    }

    private void setAnimal() {
        mDB.child("animal").child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Animal animalItem = snapshot.getValue(Animal.class);
                    sticker.setImageResource(animalItem.getImageSource());
                    sticker.bringToFront();
                    positionActivityTitle.setText("Where should " + animalItem.getName()+ " be placed?");
                } else {
                    Log.i("Setting Animal", "Animal not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setItem() {
        mDB.child("items").child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Item item = snapshot.getValue(Item.class);
                    sticker.setImageResource(item.getImageSource());
                    positionActivityTitle.setText("Where should " + item.getType() + " be placed?");
                    sticker.bringToFront();
                } else {
                    Log.i("Setting Item", "Item not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void displayZoo() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDB.child("animal").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            Iterator<DataSnapshot> items = snapshot.getChildren().iterator();
                            while (items.hasNext()) {
                                DataSnapshot item = items.next();
                                String itemKey = item.getKey();
                                Animal animalItem = item.getValue(Animal.class);

                                if (pieceType.equals("animal")) {
                                    if (!status.equals("initial")) {
                                        if (itemKey.equals(mKey)) {
                                            continue;
                                        } else {
                                            displayAnimalSticker(animalItem);
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    displayAnimalSticker(animalItem);
                                }
                            }}
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

            }
        },200);


        mDB.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Iterator<DataSnapshot> items = snapshot.getChildren().iterator();
                    while (items.hasNext()) {
                        DataSnapshot itemDS = items.next();
                        String itemKey = itemDS.getKey();
                        Item item = itemDS.getValue(Item.class);

                        if (pieceType.equals("item")) {
                            if (itemKey.equals(mKey)) {
                                continue;
                            } else {
                                displayItemSticker(item);
                            }
                        } else {
                            displayItemSticker(item);
                        } }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void displayAnimalSticker(Animal animalItem) {
        ImageView animalStickerView= new ImageView(PiecePlacingActivity.this);
        //animalStickerView.bringToFront();
        animalStickerView.setImageResource(animalItem.getImageSource());
        animalStickerView.setX(animalItem.getxPosition());
        animalStickerView.setY(animalItem.getyPosition());

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(150, 150);
        animalStickerView.setLayoutParams(params);

        zooFrame.addView(animalStickerView);
    }


    private void displayItemSticker(Item item) {
        ImageView itemStickerView= new ImageView(PiecePlacingActivity.this);
        //itemStickerView.bringToFront();
        itemStickerView.setImageResource(item.getImageSource());
        itemStickerView.setX(item.getxPosition());
        itemStickerView.setY(item.getyPosition());

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(150, 150);
        itemStickerView.setLayoutParams(params);

        zooFrame.addView(itemStickerView);
    }


    private void saveToAnimalDB() {
        mDB.child("animal").child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Animal animalItem = snapshot.getValue(Animal.class);
                    animalItem.setxPosition(selectedX);
                    animalItem.setyPosition(selectedY);
                    Log.i("Saving Animal Position", selectedX +","+ selectedY);
                    mDB.child("animal").child(mKey).child("xPosition").setValue(selectedX);
                    mDB.child("animal").child(mKey).child("yPosition").setValue(selectedY);

                    Toast.makeText(getApplicationContext(), "New position saved!", Toast.LENGTH_SHORT).show();

                    if (newUser.equals("true") || status.equals("additional")) {
                        startActivity(new Intent(getApplicationContext(), AnimalStatusActivity.class)
                                .putExtra("newUser", newUser)
                                .putExtra("status", "initial")
                                .putExtra("category", category)
                                .putExtra("type", pieceType)
                                .putExtra("key", mKey));
                    } else {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class).putExtra("newUser", newUser));
                    }

                } else {
                    Log.i("Saving Animal position", "Animal not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void saveToItemDB() {
        mDB.child("items").child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Item item = snapshot.getValue(Item.class);
                    item.setxPosition(selectedX);
                    item.setyPosition(selectedY);
                    Log.i("Saving Item Position", String.valueOf(selectedX +","+ selectedY));
                    mDB.child("items").child(mKey).child("xPosition").setValue(selectedX);
                    mDB.child("items").child(mKey).child("yPosition").setValue(selectedY);

                    Toast.makeText(getApplicationContext(), "New position saved!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                } else {
                    Log.i("Saving Item position", "Animal not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void grantRewards() {
        mDB.child("rewards").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer reward = snapshot.getValue(int.class);
                Log.i("Grant rewards", "current reward: " + String.valueOf(reward));
                mDB.child("rewards").setValue(reward + 15);
                Toast.makeText(PiecePlacingActivity.this, "Glad you found a new supporter! 💰Reward+15💰", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}