package edu.neu.madcourse.spenzoo_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import edu.neu.madcourse.spenzoo_finalproject.Model.Animal;
import edu.neu.madcourse.spenzoo_finalproject.Model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private Button signUpButton;

    private String username;
    private String email;
    private String password;

    private FirebaseAuth mAuth;
    private DatabaseReference mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameInput = (EditText) findViewById(R.id.signup_username);
        emailInput = (EditText) findViewById(R.id.signup_email);
        passwordInput = (EditText) findViewById(R.id.signup_password);
        signUpButton = (Button) findViewById(R.id.signup_btn);

        mDB = FirebaseDatabase.getInstance().getReference("Users");

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }

    private void registerUser() {
        mAuth = FirebaseAuth.getInstance();
        username = usernameInput.getText().toString().trim();
        email = emailInput.getText().toString().trim();
        password = passwordInput.getText().toString().trim();

        // Check if input fields are empty
        if (username.isEmpty()) {
            usernameInput.setError("Username cannot be empty");
            usernameInput.requestFocus();
        } else if (email.isEmpty()) {
            emailInput.setError("Email cannot be empty");
            emailInput.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailInput.setError("Provide valid email");
            passwordInput.requestFocus();
        } else if (password.isEmpty()) {
            passwordInput.setError("Password cannot be empty");
            passwordInput.requestFocus();
        } else if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            passwordInput.requestFocus();
        }


        // Check if email already exists
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    boolean check =!task.getResult().getSignInMethods().isEmpty();
                    if (!check){
                        // If email does not exist create new user
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User user = new User(username, email);
                                user.setCreationDate(System.currentTimeMillis());

                                mDB.child(mAuth.getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Failed to Register: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }
                        });
                    } else {
                        // Show that email already exists
                        Toast.makeText(getApplicationContext(),"email already exist",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }




}