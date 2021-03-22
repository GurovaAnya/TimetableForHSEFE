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
import org.hse.android.entities.TeacherEntity;
import org.hse.android.entities.TimeTableEntity;
import org.hse.android.entities.TimeTableWithTeacherEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TeacherActivity extends BaseActivity {

    TextView status;
    TextView subject;
    TextView cabinet;
    TextView corp;
    TextView teacher;
    String TAG = "TeacherActivity";
    Spinner spinner;
    ArrayAdapter<StudentActivity.Group> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        spinner = findViewById(R.id.groupList);
        List<StudentActivity.Group> groups = new ArrayList<>();
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

    private void initGroupList(List<StudentActivity.Group> groups){
        mainViewModel.getTeachers().observe(this, new Observer<List<TeacherEntity>>() {
            @Override
            public void onChanged(@NotNull List<TeacherEntity> teacherEntities) {
                List<StudentActivity.Group> groupResult = new ArrayList<>();
                for(TeacherEntity listEntity : teacherEntities){
                    groupResult.add(new StudentActivity.Group(listEntity.id, listEntity.fio));
                }
                adapter.clear();
                adapter.addAll(groupResult);
            }
        });
    }

    @Override
    protected void showTime() {
        super.showTime();

        if (getSelectedGroup() != null) {
            mainViewModel.getTimeWithTeacherByDate(currentTime, getSelectedGroup().getId()).observe(this, new Observer<TimeTableWithTeacherEntity>() {
                @Override
                public void onChanged(@Nullable TimeTableWithTeacherEntity listEntity) {
                    initDataFromTimeTable(listEntity);
                }
            });
        }
    }

    private void initData(){
        initDataFromTimeTable(null);

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
        if (!(selectedItem instanceof StudentActivity.Group)){
            return;
        }
        showScheduleImpl(ScheduleMode.TEACHER, type, (StudentActivity.Group) selectedItem);
    }

    protected StudentActivity.Group getSelectedGroup(){
        return (StudentActivity.Group) spinner.getSelectedItem();
    }
}