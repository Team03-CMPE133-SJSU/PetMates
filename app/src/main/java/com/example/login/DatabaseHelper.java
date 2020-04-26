package com.example.login;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    static String name = "database";
    static int version = 1;

    String User =
            "CREATE TABLE IF NOT EXISTS \"user\" (\n" +
            "\t\"Email\"\tINTEGER,\n" +
            "\t\"Password\"\tTEXT,\n" +
            "\t\"First_Name\"\tTEXT,\n" +
            "\t\"Last_name\"\tTEXT,\n" +
            "\t\"Phone\"\tINTEGER,\n" +
            "\tPRIMARY KEY(\"Email\")\n" +
            ")";


    public DatabaseHelper(@Nullable Context context) {
        super(context, name, null, version);
        getWritableDatabase().execSQL(User);
    }

    public void insertUser(ContentValues contentValues){
        getWritableDatabase().insert("user","",contentValues);
    }

    public boolean isLoginVaild(String email, String password){
        String sql = "SELECT COUNT(*) FROM user WHERE Email ='"+ email+"' and Password =" + password+ "'";
        SQLiteStatement statement = getReadableDatabase().compileStatement(sql);
        long result = statement.simpleQueryForLong();
        statement.close();

        return result == 1;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}