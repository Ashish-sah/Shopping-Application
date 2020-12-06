package com.ashish.ecommerceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ashish.ecommerceapp.Model.Users;
import com.ashish.ecommerceapp.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    ProgressDialog loadingBar;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private Button joinNowButton, LoginNowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        joinNowButton = findViewById(R.id.main_join_now_btn);
        LoginNowButton = findViewById(R.id.main_login_btn);
        Paper.init(this);
        loadingBar = new ProgressDialog(this);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        LoginNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();

            }
        });

        //retrieve the user
        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);
        String UserEmailKey = Paper.book().read(Prevalent.UserEmailKey);
        if (UserPhoneKey != "" && UserPasswordKey != "" && UserEmailKey != "") {
            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey) && !TextUtils.isEmpty(UserEmailKey)) {
                AllowAccess(UserPhoneKey, UserPasswordKey, UserEmailKey);

                loadingBar.setTitle("Already Logged in");
                loadingBar.setMessage("Please wait.....");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }
    }

    //This allow to open
    private void AllowAccess(String phone, String password, String email) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Users").child(phone).exists()) {
                    //getting value from database with User class using getters and setters
                    Users userData = snapshot.child("Users").child(phone).getValue(Users.class);
                    Timber.e("There is some error");
                    if (userData.getPhone().equals(phone)) {
                        if (userData.getEmail().equals(email)) {
                            if (userData.getPassword().equals(password)) {
                                Timber.d("Data is  added ");
                                Toast.makeText(MainActivity.this, "Please wait, you are already logged in...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                loadingBar.dismiss();
                                Toast.makeText(MainActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Email is incorrect.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        loadingBar.dismiss();
                        Toast.makeText(MainActivity.this, "Phone Number is incorrect.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Account with this " + phone + " doesn't exist ", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Timber.e("error occured");
            }
        });
    }


}

