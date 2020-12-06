package com.ashish.ecommerceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText registerUserName, registerUserPassword, registeruserPhonenumber, registerUserEmail;
    Button registerCreateAccount;
    ProgressDialog loading;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);
        registerUserName = (EditText) findViewById(R.id.register_username_input);
        registerUserEmail = (EditText) findViewById(R.id.register_email_input);
        registerUserPassword = (EditText) findViewById(R.id.register_password_input);
        registeruserPhonenumber = (EditText) findViewById(R.id.register_phonenumber_input);
        registerCreateAccount = (Button) findViewById(R.id.register_btn);
        loading = new ProgressDialog(this);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Users");

        registerCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loading.setTitle("Create Account");
                loading.setMessage("Please wait while we are checking your credentials..");
                loading.setCanceledOnTouchOutside(false);
                loading.show();
                validateUser();
            }
        });
    }

    public void validateUser() {
        String name = registerUserName.getText().toString().trim();
        String email = registerUserEmail.getText().toString().trim();
        String password = registerUserPassword.getText().toString().trim();
        String phone = registeruserPhonenumber.getText().toString().trim();
        // databaseReference.setValue("ashi");

        //   userhelperClass.setName();
        //   userhelperClass.setPassword(registerUserPassword.getText().toString().trim());
        //     userhelperClass.setPhoneNumber();
        if (TextUtils.isEmpty(name)) {
            loading.dismiss();
            Toast.makeText(this, "Please write your name...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            loading.dismiss();
            Toast.makeText(this, "Please write your email address...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)) {
            loading.dismiss();
            Toast.makeText(this, "Please write your phone number...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            loading.dismiss();
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
        } else {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //to avoid redundancy in database
                    if (dataSnapshot.child(phone).exists()) {
                        Toast.makeText(RegisterActivity.this, "This " + phone + " already exists.", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                        Toast.makeText(RegisterActivity.this, "Please try again using another phone number.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        HashMap<String, Object> userdataMap = new HashMap<>();
                        userdataMap.put("phone", phone);
                        userdataMap.put("email", email);
                        userdataMap.put("password", password);
                        userdataMap.put("name", name);

                        databaseReference.child(phone).updateChildren(userdataMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Congratulations, your account has been created.", Toast.LENGTH_SHORT).show();
                                            loading.dismiss();

                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            loading.dismiss();
                                            Toast.makeText(RegisterActivity.this, "Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    loading.dismiss();
                    Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    Toast.makeText(RegisterActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
