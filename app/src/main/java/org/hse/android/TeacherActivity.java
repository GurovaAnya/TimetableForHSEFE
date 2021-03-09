package org.hse.android;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TeacherActivity extends BaseActivity {

    TextView status;
    TextView subject;
    TextView cabinet;
    TextView corp;
    TextView teacher;
    String TAG = "TeacherActivity";
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        spinner = findViewById(R.id.groupList);
        List<StudentActivity.Group> groups = new ArrayList<>();
        initGroupList(groups);

        ArrayAdapter<?> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View itemSelected,
                                       int selectedItemPosition, long id) {
                Object item = adapter.getItem(selectedItemPosition);
                Log.d(TAG, "selectedItem: " + item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        time = findViewById(R.id.time);
        initTime();
        status = findViewById(R.id.status);
        subject = findViewById(R.id.subject);
        cabinet = findViewById(R.id.cabinet);
        corp = findViewById(R.id.corp);
        teacher = findViewById(R.id.teacher);
        initData();

        View dayScheduleButton = findViewById(R.id.dayScheduleButton);
        dayScheduleButton.setOnClickListener(v -> showSchedule(ScheduleType.DAY));

        View weekScheduleButton = findViewById(R.id.weekScheduleButton);
        weekScheduleButton.setOnClickListener(v -> showSchedule(ScheduleType.WEEK));
    }

    private void initGroupList(List<StudentActivity.Group> groups){
        groups.add(new StudentActivity.Group(1, "Преподаватель 1"));
        groups.add(new StudentActivity.Group(2, "Преподаватель 2"));
    }

    private void initData(){
        status.setText("Нет пар");
        subject.setText("Дисциплина");
        cabinet.setText("Кабинет");
        corp.setText("Корпус");
        teacher.setText("Преподаватель");
    }

    private void showSchedule(ScheduleType type){
        Object selectedItem = spinner.getSelectedItem();
        if (!(selectedItem instanceof StudentActivity.Group)){
            return;
        }
        showScheduleImpl(ScheduleMode.TEACHER, type, (StudentActivity.Group) selectedItem);
    }
}