package rdm.restartscheduler;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper{

    String str;

    public static final String DB_NAME="ToDoList";
    public static final int DB_VER=1;
    public static final String DB_TABLE="Task";

    public static final String KEYROWID = "ID";
    public static final String DB_COLUMN = "TaskName";
    public static final String DB_COLUMN2 = "TaskTime";
    public static final String DB_COLUMN3 = "TaskDay";
    public static final String DB_COLUMN4 = "TaskBox";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE Task(ID INTEGER PRIMARY KEY AUTOINCREMENT,TaskName TEXT NOT NULL,TaskTime TEXT NOT NULL,TaskDay TEXT NOT NULL,TaskBox TEXT NOT NULL)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = String.format("DELETE TABLE IF EXISTS %s",DB_TABLE);
        db.execSQL(query);
        onCreate(db);

    }

    public void insertNewTask(String task, String day, String time){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN, task);
        values.put(DB_COLUMN2, time);
        values.put(DB_COLUMN3, day);
        values.put(DB_COLUMN4, "No");
        String newRowID = "" + db.insertWithOnConflict(DB_TABLE,null, values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }


    public void deleteTask(String task){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT ID FROM Task WHERE " + DB_COLUMN + " = ?" ,new String[]{task}); //sets cursor for matching name
        if (c.moveToFirst()) {
            str = c.getString(c.getColumnIndex("ID")); //moves cursor to first ID with the name
        }
        db.delete("Task", "id=?", new String[]{str});
        db.close();
    }

    public void updateBox(String task){
        SQLiteDatabase db = this.getWritableDatabase();
        String tempCompare = "";
        Cursor c = db.rawQuery("SELECT ID FROM Task WHERE " + DB_COLUMN + " = ?" ,new String[]{task}); //sets cursor for matching name
        if (c.moveToFirst()) {
            str = c.getString(c.getColumnIndex("ID")); //moves cursor to first ID with the name
        }
        String query2 = "SELECT TaskBox FROM Task WHERE ID = " + str;
        Cursor c2 = db.rawQuery(query2,null);
        if(c2.moveToFirst()){
            tempCompare = c2.getString(c2.getColumnIndex("TaskBox"));
        }
        //changes between yes/no
        if(tempCompare.equals("No")){
            ContentValues dataToInsert = new ContentValues();
            dataToInsert.put("TaskBox","Yes");
            db.update("Task", dataToInsert, "ID="+str,null);
        }else{
            ContentValues dataToInsert = new ContentValues();
            dataToInsert.put("TaskBox","No");
            db.update("Task", dataToInsert, "ID="+str,null);
        }
        db.close();

    }

    public void swapOffBox(String task){
        SQLiteDatabase db = this.getWritableDatabase();
        String tempCompare = "";
        Cursor c = db.rawQuery("SELECT ID FROM Task WHERE " + DB_COLUMN + " = ?" ,new String[]{task}); //sets cursor for matching name
        if (c.moveToFirst()) {
            str = c.getString(c.getColumnIndex("ID")); //moves cursor to first ID with the name
        }
        String query2 = "SELECT TaskBox FROM Task WHERE ID = " + str;
        Cursor c2 = db.rawQuery(query2,null);
        if(c2.moveToFirst()){
            tempCompare = c2.getString(c2.getColumnIndex("TaskBox"));
        }
        //toggles yes to no
        if(tempCompare.equals("Yes")){
            ContentValues dataToInsert = new ContentValues();
            dataToInsert.put("TaskBox","No");
            db.update("Task", dataToInsert, "ID="+str,null);
        }
        db.close();

    }

    public ArrayList<String> getTaskList(){
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DB_TABLE,new String[]{DB_COLUMN},null,null,null,null,null);
        while(cursor.moveToNext()){
            int index = cursor.getColumnIndex(DB_COLUMN);
            taskList.add(cursor.getString(index));
        }
        cursor.close();
        db.close();
        return taskList;
    }
    public ArrayList<String> getTaskList2(){
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DB_TABLE,new String[]{DB_COLUMN2},null,null,null,null,null);
        while(cursor.moveToNext()){
            int index = cursor.getColumnIndex(DB_COLUMN2);
            taskList.add(cursor.getString(index));
        }
        cursor.close();
        db.close();
        return taskList;
    }

    public ArrayList<String> getTaskList3(){
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DB_TABLE,new String[]{DB_COLUMN3},null,null,null,null,null);
        while(cursor.moveToNext()){
            int index = cursor.getColumnIndex(DB_COLUMN3);
            taskList.add(cursor.getString(index));
        }
        cursor.close();
        db.close();
        return taskList;
    }


    public ArrayList<String> getTaskList4(){
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DB_TABLE,new String[]{DB_COLUMN4},null,null,null,null,null);
        while(cursor.moveToNext()){
            int index = cursor.getColumnIndex(DB_COLUMN4);
            taskList.add(cursor.getString(index));
        }
        cursor.close();
        db.close();
        return taskList;
    }

}