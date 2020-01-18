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

import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    //constants for view TAGS
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

        //assigning layout items
        this.tableLayout = findViewById(R.id.mainActivity_TableLayout);


        //------Filling table with data-------
        Cursor cursor = dataBaseHelper.getAllRecords();
        while (cursor.moveToNext()){
            String id = cursor.getString(0);
            String taskName = cursor.getString(1);
            String status = cursor.getString(2);
            addRow(tableLayout, id, taskName, status, "", Integer.parseInt(id));
        }

        updateFields();
    }

    public void addTextView(TableRow tableRow, String text, int textSize, int typeFace, double width, String tagBase, int tagNumber){
        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(textSize);
        textView.setTypeface(null, typeFace);
        textView.setTextColor(Color.BLACK);
        textView.setText(text);
        textView.setTag(tagBase + tagNumber);
        tableRow.addView(textView);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        layoutParams.width = (int)(displayMetrics.widthPixels * width);
        textView.setLayoutParams(layoutParams);
    }

    public void addRow(TableLayout tableLayout, String id, String taskName, String status, String buttonName, int tagNumber){
        TableRow tableRow = new TableRow(this);
        tableLayout.addView(tableRow, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TableLayout.LayoutParams layoutParams = (TableLayout.LayoutParams) tableRow.getLayoutParams();
        layoutParams.setMargins(0,20,0,0);
        tableRow.setLayoutParams(layoutParams);
        tableRow.setBackground(getDrawable(R.drawable.background_open_status));
        tableRow.setTag(ROW_TAG + tagNumber);
        //creating textView for ID
        addTextView(tableRow, id, 15, Typeface.NORMAL, ID_WIDTH, ID_TAG, tagNumber);
        //creating textView for taskName
        addTextView(tableRow, taskName, 15, Typeface.NORMAL, TASK_NAME_WIDTH, TASK_TAG, tagNumber);
        //creating textView for Status
        addTextView(tableRow, status, 15, Typeface.NORMAL, STATUS_WIDTH, STATUS_TAG, tagNumber);
        //creating button
        addButton(tableRow, buttonName, 15, Typeface.BOLD, BUTTON_WIDTH, BUTTON_TAG , tagNumber);
    }

    public void addButton(TableRow tableRow, String text, int textSize, int typeFace, double width, String tagBase, int tagNumber){
        final Button button = new Button(this);
        button.setGravity(Gravity.CENTER);
        button.setTextSize(textSize);
        button.setTypeface(null, typeFace);
        button.setText(text);
        button.setTag(tagBase + tagNumber);
        button.setBackground(getDrawable(R.drawable.button_open_status));
        tableRow.addView(button);
        TableRow.LayoutParams layoutParams = (TableRow.LayoutParams) button.getLayoutParams();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        layoutParams.width = (int)(displayMetrics.widthPixels * width);
        button.setLayoutParams(layoutParams);

        //Creating onClickListenersFor Button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Scanner tag = new Scanner(button.getTag().toString()).useDelimiter("[^0-9]+");
                int id = tag.nextInt();
                Cursor cursor = dataBaseHelper.findStatusByID(id);
                cursor.moveToNext();
                String status = cursor.getString(0);
                switch (status){
                    case TaskStatus.OPEN:
                        dataBaseHelper.changeStatusOnID(id, TaskStatus.TRAVELLING);
                        break;
                    case TaskStatus.TRAVELLING:
                        dataBaseHelper.changeStatusOnID(id, TaskStatus.WORKING);
                        break;
                    case TaskStatus.WORKING:
                        dataBaseHelper.changeStatusOnID(id, TaskStatus.OPEN);
                        break;
                }
                updateFields();
            }
        });
    }

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

    public void changeTaskView(String id, String status, int tasksActive){
        Button button = tableLayout.findViewWithTag(BUTTON_TAG + id);
        TextView textView_Status = tableLayout.findViewWithTag(STATUS_TAG + id);
        TableRow tableRow = tableLayout.findViewWithTag(ROW_TAG + id);
        switch (status){
            case TaskStatus.OPEN:
                /*for OPEN status if there is no active tasks change button to visible and ACTIVE
                and change its name to "START TRAVEL" */
                //Change text status to OPEN
                if(tasksActive == 0){
                    button.setVisibility(View.VISIBLE);
                    button.setClickable(true);
                    button.setText("START TRAVEL");
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
                //for TRAVELING status set button to VISIBLE, ACTIVE and change its name to "START WORK"
                button.setVisibility(View.VISIBLE);
                button.setClickable(true);
                button.setText("START WORK");
                button.setBackground(getDrawable(R.drawable.button_travelling_status));
                textView_Status.setText(TaskStatus.TRAVELLING);
                tableRow.setBackground(getDrawable(R.drawable.background_trevelling_status));
                break;
            case TaskStatus.WORKING:
                //for WORKING status set button to VISIBLE, ACTIVE and change its name to "STOP"
                button.setVisibility(View.VISIBLE);
                button.setClickable(true);
                button.setText("STOP");
                button.setBackground(getDrawable(R.drawable.button_working_status));
                textView_Status.setText(TaskStatus.WORKING);
                tableRow.setBackground(getDrawable(R.drawable.background_working_status));
                break;
        }
    }
}
