package org.hse.android.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import org.hse.android.R;
import org.hse.android.entities.GroupEntity;
import org.hse.android.entities.TimeTableEntity;
import org.hse.android.entities.TimeTableWithTeacherEntity;

import java.util.ArrayList;
import java.util.List;

public class StudentActivity extends BaseActivity {

    TextView status;
    TextView subject;
    TextView cabinet;
    TextView corp;
    TextView teacher;
    String TAG = "StudentActivity";
    Spinner spinner;

    ArrayAdapter<Group> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        spinner = findViewById(R.id.groupList);
        List<Group> groups = new ArrayList<>();
        initGroupList(groups);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View itemSelected,
                                       int selectedItemPosition, long id) {
                onSpinnerItemSelected(selectedItemPosition);
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

    private void onSpinnerItemSelected(int selectedItemPosition) {
        Object item = adapter.getItem(selectedItemPosition);
        Log.d(TAG, "selectedItem: " + item);
        initTime();
    }

    private void initGroupList(final List<Group> groups){
        mainViewModel.getGroups().observe(this, new Observer<List<GroupEntity>>() {
            @Override
            public void onChanged(@Nullable List<GroupEntity> groupEntities) {
                List<Group> groupsResult = new ArrayList<>();
                for (GroupEntity listEntity : groupEntities){
                    groupsResult.add(new Group(listEntity.id, listEntity.name));
                }
                adapter.clear();
                adapter.addAll(groupsResult);
            }
        });
    }

    private void addGroup(List<Group> groups, String educationalProgram, int year, int groupCount){
        for (int i = 1; i<=groupCount;i++)
            groups.add(new Group(groups.size(), String.format("%s-%d-%d", educationalProgram, year, i)));
    }


    private void initData(){
        initDataFromTimeTable(null);
    }

    @Override
    protected void showTime() {
        super.showTime();
        if (getSelectedGroup() != null) {
            mainViewModel.getTimeWithGroupByDate(currentTime, getSelectedGroup().getId()).observe(this, new Observer<TimeTableWithTeacherEntity>() {
                @Override
                public void onChanged(@Nullable TimeTableWithTeacherEntity listEntity) {
                    initDataFromTimeTable(listEntity);
                }
            });
        }
    }

    private void initDataFromTimeTable(TimeTableWithTeacherEntity timeTableWithTeacherEntity){
        if (timeTableWithTeacherEntity == null){
            status.setText(getString(R.string.status_no_pairs));
            subject.setText(getString(R.string.subject_null));
            cabinet.setText(getString(R.string.cabinet_null));
            corp.setText(getString(R.string.corp_null));
            teacher.setText(getString(R.string.teacher_null));
            return;
        }
        status.setText(getString(R.string.status_in_progress));
        TimeTableEntity timeTableEntity = timeTableWithTeacherEntity.timeTableEntity;
        subject.setText(timeTableEntity.subjName);
        cabinet.setText(timeTableEntity.cabinet);
        corp.setText(timeTableEntity.corp);
        teacher.setText(timeTableWithTeacherEntity.teacherEntity.fio);
    }

    private void showSchedule(ScheduleType type){
        Object selectedItem = spinner.getSelectedItem();
        if (!(selectedItem instanceof Group)){
            return;
        }
        showScheduleImpl(ScheduleMode.STUDENT, type, (Group) selectedItem);
    }


    protected Group getSelectedGroup(){
        return (Group) spinner.getSelectedItem();
    }

    ////////////////////////
    static class Group{
        private Integer id;
        private String name;

        public Group(Integer id, String name){
            this.id = id;
            this.name = name;
        }

        public Integer getId(){
            return this.id;
        }

        public void setId(Integer id){
            this.id = id;
        }

        @Override
        public String toString(){
            return name;
        }

        public String getName(){
            return name;
        }

        public void setName(String name){
            this.name = name;
        }
    }
}