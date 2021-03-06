package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Pet_Jobs extends AppCompatActivity {
    TextView Job1, Job2, Job3, Job4, Back, Next, Post;
    Cursor cursor;
    String activity, email;
    int art1num , art2num, art3num, art4num;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet__jobs);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        db = new DatabaseHelper(this);
        cursor = db.all_data(4);

        Job1 = findViewById(R.id.tvPetJobs_Report1);
        Job2 = findViewById(R.id.tvPetJobs_Report2);
        Job3 = findViewById(R.id.tvPetJobs_Report3);
        Job4 = findViewById(R.id.tvPetJobs_Report4);
        Back = findViewById(R.id.tvPetJobs_Back);
        Next = findViewById(R.id.tvPetJobs_Next);
        Post = findViewById(R.id.tvPetJobs_Post);

        update();
        refresh();

        Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pet_Jobs.this, PetJobs_Post.class);
                intent.putExtra("email", email);
                intent.putExtra("index", "null");
                activity = "post";
                intent.putExtra("activity", activity);
                startActivity(intent);
            }
        });

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pet_Jobs.this, Forum.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
                refresh();
            }
        });

    }

    public void update(){
        if (cursor.moveToNext()) {
            Job1.setText(cursor.getString(3));
            art1num = cursor.getInt(0);
        }
        if (cursor.moveToNext()) {
            Job2.setText(cursor.getString(3));
            art2num = cursor.getInt(0);
        }
        if (cursor.moveToNext()) {
            Job3.setText(cursor.getString(3));
            art3num = cursor.getInt(0);
        }
        if (cursor.moveToNext()) {
            Job4.setText(cursor.getString(3));
            art4num = cursor.getInt(0);
        }

    }

    public void refresh(){
        Job1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pet_Jobs.this, PetJobs_Post.class);
                intent.putExtra("email", email);
                intent.putExtra("index",art1num);
                activity = "view";
                intent.putExtra("activity", activity);
                startActivity(intent);
            }
        });
        Job2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pet_Jobs.this, PetJobs_Post.class);
                intent.putExtra("email", email);
                intent.putExtra("index",art2num);
                activity = "view";
                intent.putExtra("activity", activity);
                startActivity(intent);
            }
        });
        Job3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pet_Jobs.this, PetJobs_Post.class);
                intent.putExtra("email", email);
                intent.putExtra("index",art3num);
                activity = "view";
                intent.putExtra("activity", activity);
                startActivity(intent);
            }
        });
        Job4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pet_Jobs.this, PetJobs_Post.class);
                intent.putExtra("email", email);
                intent.putExtra("index",art4num);
                activity = "view";
                intent.putExtra("activity", activity);
                startActivity(intent);
            }
        });
    }
}
