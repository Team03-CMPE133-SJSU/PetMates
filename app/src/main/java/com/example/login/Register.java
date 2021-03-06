package com.example.login;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class Register extends AppCompatActivity {
    Button _Register;
    EditText _Email,_Password,_Repassword,_Phone;
    TextView Back;
    DatabaseHelper db;
    CheckBox checkbox;
    boolean check;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = new DatabaseHelper(this);


        _Email = (EditText) findViewById(R.id.etEmail);
        _Password = (EditText) findViewById(R.id.etPassword);
        _Repassword = (EditText) findViewById(R.id.etrePassword);
        _Phone = (EditText) findViewById(R.id.etPhone);
        _Register = (Button) findViewById(R.id.btnRegister);
        Back = findViewById(R.id.btnBackR);
        checkbox = findViewById(R.id.checkBox);

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = ((CheckBox) v).isChecked();
            }
        });
            _Register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String EmailValue = _Email.getText().toString();
                    String PasswordValue = _Password.getText().toString();
                    String RepasswordValue = _Repassword.getText().toString();
                    String PhoneValue = _Phone.getText().toString();

                    //if one of inputs are empty, send alert
                    if (EmailValue.equals("") || PasswordValue.equals("") || RepasswordValue.equals("") || PhoneValue.equals("")) {
                        Toast.makeText(getApplicationContext(), "Fields are empty, check your input.", Toast.LENGTH_SHORT).show();
                    } else if(!check) {
                        Toast.makeText(getApplicationContext(), "You must agree to term of service", Toast.LENGTH_SHORT).show();
                    }else{
                        //check the email is unique
                        if (PasswordValue.equals(RepasswordValue)) {
                            Boolean chkemail = db.email_Unique(EmailValue);
                            if (chkemail) {//if email is unique, save data to database
                                boolean insert = db.insert_user(EmailValue, PasswordValue, PhoneValue);
                                if (insert) {//if successfully insert to database, message and jump to login
                                    Toast.makeText(getApplicationContext(), "Registered Successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Register.this, Login.class);
                                    intent.putExtra("email", EmailValue);
                                    startActivity(intent);
                                }
                            } else
                                Toast.makeText(getApplicationContext(), "Email already exists", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getApplicationContext(), "Password doesn't match", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Register.this, Login.class);
                    startActivity(intent);
                }
            });
        }
}


