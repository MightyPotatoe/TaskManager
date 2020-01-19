package com.example.taskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    //constants
    private static final String ID_TAG = "textView_ID_";
    private static final String TASK_TAG = "textView_task_";
    private static final String STATUS_TAG = "textView_status_";
    private static final String BUTTON_TAG = "button_";
    private static final String ROW_TAG = "row_";
    private static final double ID_WIDTH = 0.07d;
    private static final double TASK_NAME_WIDTH = 0.5d;
    private static final double STATUS_WIDTH = 0.23d;
    private static final double BUTTON_WIDTH = 0.2d;


    private DataBaseHelper dataBaseHelper;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //creating DatabaseHelper instance
        this.dataBaseHelper = new DataBaseHelper(this);
        //assigning view to tableLayout
        this.tableLayout = findViewById(R.id.mainActivity_TableLayout);

        //------Filling table with data from DB-------
        Cursor cursor = dataBaseHelper.getAllRecords();
        while (cursor.moveToNext()){
            String id = cursor.getString(0);
            String taskName = cursor.getString(1);
            String status = cursor.getString(2);
            addRow(id, taskName, status);
        }
        //-----Updating fields to match their status----
        updateFields();
    }


    //Method for adding textView to TableRow with specified Text, Width(% of screen size), Tag and TagNumber
    public void addTextView(TableRow tableRow, String text, double width, String tagBase, String tagNumber){
        //Creating new TextView
        final TextView textView = new TextView(this);
        //TextView configuration
        textView.setGravity(Gravity.CENTER);
        textView.setTypeface(null, Typeface.NORMAL);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(15);
        textView.setText(text);
        //Tagging and adding TextView to TableRow
        textView.setTag(tagBase + tagNumber);
        tableRow.addView(textView);
        //LayoutParams configuration
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        layoutParams.width = (int)(displayMetrics.widthPixels * width);
        textView.setLayoutParams(layoutParams);
    }

    //Method for adding button to TableRow with specified Width(%of screen), Tag and TagNumber
    public void addButton(TableRow tableRow, double width, String tagBase, String tagNumber){
        //Creating new Button
        final Button button = new Button(this);
        //Button configuration
        button.setGravity(Gravity.CENTER);
        button.setTypeface(null, Typeface.BOLD);
        button.setTextSize(15);
        //Tagging and adding to view
        button.setTag(tagBase + tagNumber);
        tableRow.addView(button);
        TableRow.LayoutParams layoutParams = (TableRow.LayoutParams) button.getLayoutParams();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        layoutParams.width = (int)(displayMetrics.widthPixels * width);
        button.setLayoutParams(layoutParams);

        //Creating onClickListener for Button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Scanner tag = new Scanner(button.getTag().toString()).useDelimiter("[^0-9]+");
                int id = tag.nextInt();
                Cursor cursor = dataBaseHelper.findStatusByID(id);
                cursor.moveToNext();
                String status = cursor.getString(0);
                boolean isUpdated = false;
                switch (status){
                    case TaskStatus.OPEN:
                        isUpdated = dataBaseHelper.changeStatusOnID(id, TaskStatus.TRAVELLING);
                        break;
                    case TaskStatus.TRAVELLING:
                        isUpdated = dataBaseHelper.changeStatusOnID(id, TaskStatus.WORKING);
                        break;
                    case TaskStatus.WORKING:
                        isUpdated = dataBaseHelper.changeStatusOnID(id, TaskStatus.OPEN);
                        break;
                }
                if(!isUpdated){
                    Toast.makeText(MainActivity.this, "Error updating status in DB", Toast.LENGTH_LONG).show();
                }
                updateFields();
            }
        });
    }

    //Method for adding TableRow to TableLayout with specified id, taskName and status
    public void addRow(String id, String taskName, String status){
        TableRow tableRow = new TableRow(this);
        tableLayout.addView(tableRow, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TableLayout.LayoutParams layoutParams = (TableLayout.LayoutParams) tableRow.getLayoutParams();
        layoutParams.setMargins(0,20,0,0);
        tableRow.setLayoutParams(layoutParams);
        tableRow.setBackground(getDrawable(R.drawable.background_open_status));
        tableRow.setTag(ROW_TAG + id);
        //creating textView for ID
        addTextView(tableRow, id, ID_WIDTH, ID_TAG, id);
        //creating textView for taskName
        addTextView(tableRow, taskName, TASK_NAME_WIDTH, TASK_TAG, id);
        //creating textView for Status
        addTextView(tableRow, status, STATUS_WIDTH, STATUS_TAG, id);
        //creating button
        addButton(tableRow, BUTTON_WIDTH, BUTTON_TAG , id);
    }


    //Method for updating fields to match their DataBase statuses
    public void updateFields(){
        //Checking if there is an active task
        int tasksActive = 0;
        Cursor cursor = dataBaseHelper.checkForStatus(TaskStatus.TRAVELLING);
        tasksActive += cursor.getCount();
        cursor = dataBaseHelper.checkForStatus(TaskStatus.WORKING);
        tasksActive += cursor.getCount();

        //Updating field states
        cursor = dataBaseHelper.getAllStatuses();
        while(cursor.moveToNext()){
            String id = cursor.getString(0);
            String status = cursor.getString(1);
            changeTaskView(id, status, tasksActive);
        }
    }

    //Method for changing the appearance for UI elements to match their statuses
    public void changeTaskView(String id, String status, int tasksActive){
        Button button = tableLayout.findViewWithTag(BUTTON_TAG + id);
        TextView textView_Status = tableLayout.findViewWithTag(STATUS_TAG + id);
        TableRow tableRow = tableLayout.findViewWithTag(ROW_TAG + id);
        //Set smaller textSize for status, make it BOLD and show corresponding button
        textView_Status.setTypeface(null, Typeface.BOLD);
        textView_Status.setTextSize(13);
        button.setVisibility(View.VISIBLE);
        button.setClickable(true);
        //check for which status i currently enabled
        switch (status){
            case TaskStatus.OPEN:
                /*for OPEN status if there is no active tasks change the background to ACTIVE
                and change button name to "START TRAVEL" */
                //Change text status to OPEN
                if(tasksActive == 0){
                    button.setText(R.string.button_start_travel);
                    button.setBackground(getDrawable(R.drawable.button_open_status));
                    textView_Status.setText(TaskStatus.OPEN);
                    tableRow.setBackground(getDrawable(R.drawable.background_open_status));
                }
                //Else change button status to INVISIBLE and make it INACTIVE
                else{
                    button.setVisibility(View.INVISIBLE);
                    button.setClickable(false);
                }
                break;
            case TaskStatus.TRAVELLING:
                //for TRAVELING status set button name to "START WORK" and change backgrounds
                button.setText(R.string.button_start_work);
                button.setBackground(getDrawable(R.drawable.button_travelling_status));
                textView_Status.setText(TaskStatus.TRAVELLING);
                tableRow.setBackground(getDrawable(R.drawable.background_trevelling_status));
                break;
            case TaskStatus.WORKING:
                //for WORKING status set name to "STOP" and change backgrounds
                button.setText(R.string.button_stop);
                button.setBackground(getDrawable(R.drawable.button_working_status));
                textView_Status.setText(TaskStatus.WORKING);
                tableRow.setBackground(getDrawable(R.drawable.background_working_status));
                break;
        }
    }
}
