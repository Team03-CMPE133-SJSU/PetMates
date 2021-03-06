package com.example.login;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String Database_Name = "PetMates.db";
    private static final String Table1 = "user";
    private static final String Table2 = "user_info";
    private static final String Table3 = "question";
    private static final String Table4 = "suggestion";
    private static final String Table5 = "report";
    private static final String Table6 = "block";
    private static final String Table7 = "friendship";
    private static final String Table8 = "friendrequest";
    private static final String Table9 = "message";
    private static final String Table10 = "forum";
    private static final String Table11 = "general_chat";
    private static final String Table12 = "forum_report";


    private static final int version = 1;

    public DatabaseHelper(Context context) {
        super(context, Database_Name, null, version);
    }

    @Override
    //create database
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Table1 + " (Email varchar(50) PRIMARY KEY, Password varchar(50), Phone varchar(20))");
        db.execSQL("CREATE TABLE " + Table2 + " (Email TEXT PRIMARY KEY, Name TEXT, Bio TEXT,Pet_type TEXT, Pet_Breed TEXT, Pet_gender TEXT, Zip TEXT," +
                "P_Pet_type TEXT, P_Pet_Breed TEXT, P_Pet_gender TEXT, P_Other TEXT, Image blob)");
        db.execSQL("CREATE TABLE " + Table3 + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, questionTitle TEXT, questionContent TEXT, answer TEXT)");
        db.execSQL("CREATE TABLE " + Table4 + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, suggestionTitle TEXT, suggestionContext TEXT)");
        db.execSQL("CREATE TABLE " + Table5 + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, reportedEmail TEXT, reportedReason TEXT, who TEXT)");
        db.execSQL("CREATE TABLE " + Table6 + " (Email TEXT PRIMARY KEY, isBlock TEXT,reason TEXT)");
        db.execSQL("CREATE TABLE " + Table7 + " (ownerEmail TEXT, friendEmail TEXT, PRIMARY KEY(ownerEmail,friendEmail))");
        db.execSQL("CREATE TABLE " + Table8 + " (senderEmail TEXT, receiverEmail TEXT, PRIMARY KEY(senderEmail,receiverEmail))");
        db.execSQL("CREATE TABLE " + Table9 + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,senderEmail TEXT, receiverEmail TEXT, message TEXT)");
        //type 1 is pet_news, type 2 is galleries, type 3 is lost and found, type 4 is jet jobs, type 5 is report
        db.execSQL("CREATE TABLE " + Table10 + " (NUM INTEGER PRIMARY KEY AUTOINCREMENT, Type INTEGER, Title TEXT, Description TEXT, Image blob, Contact TEXT)");
        db.execSQL("CREATE TABLE " + Table11 + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Email TEXT, message TEXT)");
        db.execSQL("CREATE TABLE " + Table12 + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NUM INTEGER, reason TEXT)");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Table1);
        db.execSQL("DROP TABLE IF EXISTS " + Table2);
        db.execSQL("DROP TABLE IF EXISTS " + Table3);
        db.execSQL("DROP TABLE IF EXISTS " + Table4);
        db.execSQL("DROP TABLE IF EXISTS " + Table5);
        db.execSQL("DROP TABLE IF EXISTS " + Table6);
        db.execSQL("DROP TABLE IF EXISTS " + Table7);
        db.execSQL("DROP TABLE IF EXISTS " + Table8);
        db.execSQL("DROP TABLE IF EXISTS " + Table9);
        db.execSQL("DROP TABLE IF EXISTS " + Table10);
        db.execSQL("DROP TABLE IF EXISTS " + Table11);
        db.execSQL("DROP TABLE IF EXISTS " + Table12);
        onCreate(db);
    }
    ///////////////////////////////////////////////////////////////////////
    ///////////////////////////friends and message/////////////////////////
    ////////////////////////////////////////////////////////////////////////
    //send friend request
    public boolean send_Request(String sender, String receiver){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues request = new ContentValues();
        request.put("senderEmail",sender);
        request.put("receiverEmail",receiver);
        long ins = db.insert(Table8, null,request);
        return ins != -1;
    }
    //get friend request(get 0)
    public Cursor get_Friendrequest(String useremail) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT senderEmail FROM " + Table8+ " WHERE receiverEmail=?",
                new String[]{String.valueOf(useremail)});
    }
    // delete friend request after accept or decline
    public void deleterequest(String useremail, String requestemail) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Table8+ " WHERE receiverEmail=? AND senderEmail =?",new String[]{useremail,requestemail});
        db.close();
    }
    //check have friend or not
    public boolean have_friend(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT ownerEmail FROM " + Table7 + " WHERE ownerEmail = ?", new String[]{email});
        if (cursor.getCount() > 0) return true;
        else return false;
    }
    //check is friend or not
    //check is friend or not
    public boolean checkfriend(String sender,String receiver){
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM " + Table7 + " WHERE ownerEmail = ? AND friendEmail =?",
                new String[]{sender,receiver});
        if (cursor.getCount() > 0) return true;
        else return false;
    }
    //check friend request sent or not
    public boolean checkfriendrequest(String sender,String receiver){
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM " + Table8 + " WHERE senderEmail = ? AND receiverEmail =?",
                new String[]{sender,receiver});
        if (cursor.getCount() > 0) return true;
        else return false;
    }

    //add friend
    public boolean add_friend(String owner, String friend){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues request = new ContentValues();
        request.put("ownerEmail",owner);
        request.put("friendEmail",friend);
        long ins = db.insert(Table7, null,request);
        if (ins == -1) return false;
        else return true;
    }
    public void deletefriend(String useremail, String requestemail) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Table7+ " WHERE ownerEmail=? AND friendEmail =?",new String[]{useremail,requestemail});
        db.execSQL("DELETE FROM " + Table7+ " WHERE friendEmail=? AND ownerEmail =?",new String[]{useremail,requestemail});
        db.close();
    }

    //add friend inverse
    public boolean add_friend_inverse(String owner, String friend){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues request = new ContentValues();
        request.put("ownerEmail",friend);
        request.put("friendEmail",owner);
        long ins = db.insert(Table7, null,request);
        if (ins == -1) return false;
        else return true;
    }
    //get friend list
    public Cursor get_Friendlist(String useremail) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT friendEmail FROM " + Table7+ " WHERE ownerEmail=?", new String[]{useremail});
    }
    //send message
    public boolean send_message(String owner, String friend, String message){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues request = new ContentValues();
        request.put("senderEmail",owner);
        request.put("receiverEmail",friend);
        request.put("message",message);
        long ins = db.insert(Table9, null,request);
        if (ins == -1) return false;
        else return true;
    }
    //get message
    public Cursor get_message(String senderEmail, String receiverEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT message, senderEmail FROM " + Table9 +
                        " WHERE (senderEmail=? and receiverEmail=?) OR (receiverEmail=? and senderEmail=?)ORDER BY ID ASC ",
                new String[]{senderEmail, receiverEmail, senderEmail, receiverEmail});
    }
    // get single user's full message history
    public Cursor get_message_history(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT message, receiverEmail FROM " + Table9 + " WHERE senderEmail=? ORDER BY ID ", new String[]{email});
    }

    //sending message to general chat
    public boolean send_generalchat(String email, String message){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues request = new ContentValues();
        request.put("Email",email);
        request.put("message",message);
        long ins = db.insert(Table11, null,request);
        if (ins == -1) return false;
        else return true;
    }

    //get general chat
    Cursor get_generalchat() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT Email, message FROM " + Table11+ " ORDER BY ID",null);
    }


    ///////////////////////////////////////////////////////////////////////
    /////////////////////registration and profile//////////////////////////
    //////////////////////////////////////////////////////////////////////

    //insert data to user
    public boolean insert_user(String Email, String Password, String Phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues user = new ContentValues();

        user.put("email", Email);
        user.put("password", Password);
        user.put("phone", Phone);
        long ins = db.insert(Table1, null, user);
        if (ins == -1) return false;
        else return true;
    }

    //insert data to user_info
    public Boolean insertImage(String Email, String Name, String Bio, String Pet_type,
                               String Pet_breed, String Pet_gender, String Zip, String P_Pet_type,
                               String P_Pet_breed, String P_Pet_gender, String P_Other, String ImagePath ){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            FileInputStream fs = new FileInputStream(ImagePath);
            byte[] imgbyte = new byte[fs.available()];
            fs.read(imgbyte);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Email",Email);
            contentValues.put("Name", Name);
            contentValues.put("Bio", Bio);
            contentValues.put("Pet_type", Pet_type);
            contentValues.put("Pet_Breed", Pet_breed);
            contentValues.put("Pet_gender", Pet_gender);
            contentValues.put("Zip", Zip);
            contentValues.put("P_Pet_type", P_Pet_type);
            contentValues.put("P_Pet_breed", P_Pet_breed);
            contentValues.put("P_Pet_gender", P_Pet_gender);
            contentValues.put("P_Other", P_Other);
            contentValues.put("Image", imgbyte);
            db.insert(Table2,null,contentValues);
            fs.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////Forum functions////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    //inserting data for the news
    public boolean insert_news(String Title, String Description, String Imagepath){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            FileInputStream fs = new FileInputStream(Imagepath);
            byte[] imgbyte = new byte[fs.available()];
            fs.read(imgbyte);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Type", 1);
            contentValues.put("Title", Title);
            contentValues.put("Description", Description);
            contentValues.put("Image", imgbyte);
            contentValues.put("Contact", "null");
            db.insert(Table10, null, contentValues);
            fs.close();
            return true;
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insert_galleries(String name, String Imagepath){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            FileInputStream fs = new FileInputStream(Imagepath);
            byte[] imgbyte = new byte[fs.available()];
            fs.read(imgbyte);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Type", 2);
            contentValues.put("Title", name);
            contentValues.put("Description", "null");
            contentValues.put("Image", imgbyte);
            contentValues.put("Contact", "null");
            db.insert(Table10, null, contentValues);
            fs.close();
            return true;
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insert_lostandfounddata(String name, String description, String Imagepath, String contact){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            FileInputStream fs = new FileInputStream(Imagepath);
            byte[] imgbyte = new byte[fs.available()];
            fs.read(imgbyte);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Type", 3);
            contentValues.put("Title", name);
            contentValues.put("Description", description);
            contentValues.put("Image", imgbyte);
            contentValues.put("Contact", contact);
            db.insert(Table10, null, contentValues);
            fs.close();
            return true;
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insert_academicreportdata(String name, String description, String Imagepath){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            FileInputStream fs = new FileInputStream(Imagepath);
            byte[] imgbyte = new byte[fs.available()];
            fs.read(imgbyte);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Type", 5);
            contentValues.put("Title", name);
            contentValues.put("Description", description);
            contentValues.put("Image", imgbyte);
            contentValues.put("Contact", "null");
            db.insert(Table10, null, contentValues);
            fs.close();
            return true;
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insert_jobdata(String contact, String payment, String description){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("Type", 4);
        contentvalues.put("Title", payment);
        contentvalues.put("Description", description);
        contentvalues.put("Image", "null");
        contentvalues.put("Contact", contact);
        db.insert(Table10,null,contentvalues);
        return true;
    }


    public Cursor all_data(int type){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table10 + " WHERE Type = ?", new String[]{String.valueOf(type)});
        return cursor;
    }

    public Cursor newsarticle_data(int index){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table10 + " WHERE NUM = ? AND Type = 1", new String[]{String.valueOf(index)});
        return cursor;
    }
    public Cursor lostandfound_data(int index){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table10 + " WHERE NUM = ? AND Type = 3", new String[]{String.valueOf(index)});
        return cursor;
    }

    public Cursor petjobs_data(int index){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table10 + " WHERE NUM = ? AND Type = 4", new String[]{String.valueOf(index)});
        return cursor;
    }

    public Cursor academcreport_data(int index){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table10 + " WHERE NUM = ? AND Type = 5", new String[]{String.valueOf(index)});
        return cursor;
    }

    //report a post
    public boolean insert_reportpost(int id, String reason){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("NUM", id);
        contentvalues.put("reason", reason);
        db.insert(Table12,null,contentvalues);
        return true;
    }
    //delete a post
    public void delete_post(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Table10+ " WHERE NUM=?",new Integer[]{id});
        db.execSQL("DELETE FROM " + Table12+ " WHERE NUM=?",new Integer[]{id});
        db.close();
    }
    public Cursor getAll_reported_forum() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table12+
                " ORDER BY id ASC",null);
        return cursor;
    }
    public Cursor all_data_id(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table10 + " WHERE NUM = ?", new String[]{String.valueOf(id)});
        return cursor;
    }


    //////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////check account or profile exist or not///////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    //checking if email exist;
    public Boolean email_Unique(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table1 + " WHERE Email = ?", new String[]{email});
        if (cursor.getCount() > 0) return false;
        else return true;
    }

    //checking the email and password match or not
    public Boolean emailpassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table1 + " WHERE Email=? AND Password=?", new String[]{email, password});
        if (cursor.getCount() > 0) return true;
        else return false;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////update user database////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    //update all user account information
    public void update_user(String Email, String Password, String Phone, String OldEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put("Email", Email);
        user.put("Password", Password);
        user.put("Phone", Phone);
        db.update(Table1, user, "Email=?", new String[]{String.valueOf(OldEmail)});
    }
    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////update user account individually///////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    public void update_user_Email(String Email, String OldEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put("Email", Email);
        db.update(Table1, user, "Email=?", new String[]{String.valueOf(OldEmail)});
    }
    //update user's account password
    public void update_user_Password(String Password, String OldEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put("Password", Password);
        db.update(Table1, user, "Email=?", new String[]{String.valueOf(OldEmail)});
    }
    //update user's account phone
    public void update_user_Phone(String Phone, String OldEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put("Phone", Phone);
        db.update(Table1, user, "Email=?", new String[]{String.valueOf(OldEmail)});
    }

    //after update primary key email,we have to update other table's primary key at same time
    public void update_info_email(String Email, String OldEmail) {
        if (chkprofile_exist(Email)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues user = new ContentValues();
            user.put("Email", Email);
            db.update(Table2, user, "Email=?", new String[]{String.valueOf(OldEmail)});
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////update user profile individually///////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    public Boolean update_info_image(String Email,String ImagePath ){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            FileInputStream fs = new FileInputStream(ImagePath);
            byte[] imgbyte = new byte[fs.available()];
            fs.read(imgbyte);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Email",Email);
            contentValues.put("Image", imgbyte);
            db.update(Table2, contentValues, "Email=?", new String[]{String.valueOf(Email)});
            fs.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void update_P_Other(String P_Other,String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put("P_Other", P_Other);
        db.update(Table2, user, "Email=?", new String[]{String.valueOf(email)});
    }

    public void update_P_Pet_gender(String P_Pet_gender,String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put("P_Pet_gender", P_Pet_gender);
        db.update(Table2, user, "Email=?", new String[]{String.valueOf(email)});
    }

    public void update_P_Pet_Breed(String P_Pet_Breed,String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put("P_Pet_Breed", P_Pet_Breed);
        db.update(Table2, user, "Email=?", new String[]{String.valueOf(email)});
    }

    public void update_P_Pet_type(String P_Pet_type,String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put("P_Pet_type", P_Pet_type);
        db.update(Table2, user, "Email=?", new String[]{String.valueOf(email)});
    }

    public void update_info_Zip(String Zip,String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put("Zip", Zip);
        db.update(Table2, user, "Email=?", new String[]{String.valueOf(email)});
    }

    public void update_info_Pet_gender(String Pet_gender,String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put("Pet_gender", Pet_gender);
        db.update(Table2, user, "Email=?", new String[]{String.valueOf(email)});
    }

    public void update_info_Pet_breed(String Pet_breed,String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put("Pet_Breed", Pet_breed);
        db.update(Table2, user, "Email=?", new String[]{String.valueOf(email)});
    }


    public void update_info_Pet_type(String Pet_type,String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put("Pet_type", Pet_type);
        db.update(Table2, user, "Email=?", new String[]{String.valueOf(email)});
    }

    public void update_info_Name(String name,String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put("Name", name);
        db.update(Table2, user, "Email=?", new String[]{String.valueOf(email)});
    }

    public void update_info_Bio(String Bio,String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put("Bio", Bio);
        db.update(Table2, user, "Email=?", new String[]{String.valueOf(email)});
    }
    //////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////application support//////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////
    //insert data to question
    public boolean insert_question(String title, String content) {
        String a = "0";
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues question = new ContentValues();
        question.put("questionTitle", title);
        question.put("questionContent", content);
        question.put("answer",a);
        long ins = db.insert(Table3, null, question);
        if (ins == -1) return false;
        else return true;
    }

    //insert data to question
    public boolean insert_suggestion(String title, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues suggest = new ContentValues();
        suggest.put("suggestionTitle", title);
        suggest.put("suggestionContext", content);
        long ins = db.insert(Table4, null, suggest);
        if (ins == -1) return false;
        else return true;
    }
    //search question
    //return array, [0]= id, [1]= title, [2] = content,[3] = answer
    public Cursor search_question(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table3+" WHERE questionTitle LIKE ? OR questionTitle LIKE ?" +
                        " OR questionTitle LIKE ? OR questionTitle LIKE ? OR questionContent LIKE ? OR questionContent " +
                        "LIKE ? OR questionContent LIKE ? OR questionContent LIKE ?"
                ,new String[]{key,"%"+key,key+"%","%"+key+"%",key,"%"+key,key+"%","%"+key+"%"});
        return cursor;
    }

    //search key word by answered question title
    public Cursor search_answered_question(String key){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT questionTitle, questionContent FROM " + Table3+
                " WHERE answer != ? ORDER BY RANDOM()LIMIT THREE",new String[]{key});
        return cursor;
    }
    ///////////////////////////////////////////////////////////////////////////
    ////////////////////////for admin view/////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    //admin can view all suggest
    public Cursor getAll_suggest() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table4+
                " ORDER BY ID ASC",null);
        return cursor;
    }
    //get all unanswered question
    public Cursor getAll_question() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID, questionTitle, questionContent FROM " + Table3+
                " WHERE answer = ? ORDER BY ID ASC",new String[]{"0"});
        return cursor;
    }
    //admin answer question by id
    public void  answer_question(int ID, String answer){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put("answer", answer);
        db.update(Table3, user, "ID=?", new String[]{String.valueOf(ID)});

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////report///////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    //report some one
    public boolean insert_report(String targetEmail, String reason, String who) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues question = new ContentValues();
        question.put("reportedEmail", targetEmail);
        question.put("reportedReason", reason);
        question.put("who",who);
        long ins = db.insert(Table5, null, question);
        if (ins == -1) return false;
        else return true;
    }
    //read reported information
    public Cursor getReported_information() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID, reportedEmail, reportedReason, who FROM " + Table5+
                " ORDER BY ID ASC",null);
        return cursor;
    }

    // block user
    public boolean insert_block(String Email, String reason) {
        String i = "YES";
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues question = new ContentValues();
        question.put("Email", Email);
        question.put("isBlock", i);
        question.put("reason", reason);
        long ins = db.insert(Table6, null, question);
        if (ins == -1) return false;
        else return true;
    }
    //unblock user
    public void unblock(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Table6+ " WHERE Email=?",new String[]{email});
        db.close();
    }
    //check the user been block or not
    public Boolean isBlock(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table6 + " WHERE Email = ?", new String[]{email});
        if (cursor.getCount() > 0) return false;
        else return true;
    }

    //tell the user the reason he been reported
    public Cursor getReason(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table6+
                " WHERE Email =?",new String[]{email});
        return cursor;
    }

    //delete report
    public void deleteReport(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Table5+ " WHERE id=?",new Integer[]{id});
        db.close();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    //check the user profile exist or not
    public Boolean chkprofile_exist(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table2 + " WHERE Email = ?", new String[]{email});
        if (cursor.getCount() > 0) return false;
        else return true;
    }


   //get all data from a table
    public Cursor getAll_Data() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table1,null);
        return cursor;
    }
    //get entire row of account data for current user
    public Cursor getAll_User(String Email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table1+" WHERE Email=?",new String[]{Email});
        return cursor;
    }
    //get entire row of profile data for current user
    public Cursor getAll_User_info(String Email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table2+" WHERE Email=?",new String[]{Email});
        return cursor;
    }

    //get user name
    public Cursor getName(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table2 + " WHERE Email = ?", new String[]{email});
        return cursor;
    }


    ///////////////////////////////////////////////////////
    ///////////////////for pairing/////////////////////////
    ///////////////////////////////////////////////////////
    //randomly pick 1 mate by preferences
    public Cursor pair_pre(String email, String P_Pet_type, String P_Pet_breed, String P_Pet_gender) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT Email, Name, Bio, Pet_type, Pet_breed, Pet_gender FROM " + Table2 +
                " WHERE Pet_type =? AND Pet_breed=? AND Pet_gender=? AND Email !=? COLLATE NOCASE ORDER BY RANDOM() LIMIT 1", new String[]{P_Pet_type, P_Pet_breed, P_Pet_gender, email});
        return cursor;
    }

    //randomly pick 1 mate by location
    public Cursor pair_location(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT Email, Name, Bio, Pet_type, Pet_breed, Pet_gender FROM " + Table2 +
                " WHERE zip = (SELECT Zip FROM " + Table2 + " WHERE Email =?) AND Email !=? ORDER BY RANDOM() LIMIT 1", new String[]{email,email});
        return cursor;
    }

    //Retrieve all database from user_info
    public Cursor allData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table2 + ";", null);
        return cursor;
    }

    //Retrieve user preferences from user_info
    public Cursor getPreferences(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT P_Pet_type, P_Pet_breed, P_Pet_gender FROM " + Table2 + " WHERE Email =?;", new String[]{email});
        return cursor;
    }

    //Retrieve user zip from user_info
    public Cursor getZip(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT Zip FROM " + Table2 + " WHERE Email =?;", new String[]{email});
        return cursor;
    }

    //if couldn't find any pair mate, give a random mate
    public Cursor getRandom(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT Email, Name, Bio, Pet_type, Pet_breed, Pet_gender FROM " + Table2 +
                " WHERE Email !=? ORDER BY RANDOM() LIMIT 1", new String[]{email});
        return cursor;
    }

    //////////////////////////////////////////////////////////////
    //////////////////////get image////////////////////////////
    //////////////////////////////////////////////////////////////

    //retrieve image from database where email = given
    public Bitmap getimage(String email){
        SQLiteDatabase db = this.getWritableDatabase();
        Bitmap bt = null;
        Cursor cursor = db.rawQuery("SELECT * from "+Table2+" WHERE email=?",new String[]{email});
        if(cursor.moveToNext()){
            byte[] Image = cursor.getBlob(11);
            bt = BitmapFactory.decodeByteArray(Image,0,Image.length);
        }
        return bt;
    }
    //////////////////////////////////////////////////////////////////////////////////////////

    public Bitmap getforumimage(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Bitmap bt = null;
        Cursor cursor = db.rawQuery("SELECT Image from "+Table10+" WHERE NUM =?",new String[]{String.valueOf(id)});
        if(cursor.moveToNext()){
            byte[] Image = cursor.getBlob(0);
            bt = BitmapFactory.decodeByteArray(Image,0,Image.length);
        }
        return bt;
    }

}
