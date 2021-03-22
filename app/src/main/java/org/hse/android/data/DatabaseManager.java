package org.hse.android.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.hse.android.entities.GroupEntity;
import org.hse.android.entities.TeacherEntity;
import org.hse.android.entities.TimeTableEntity;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class DatabaseManager {
    private DatabaseHelper db;

    private static DatabaseManager instance;

    public static DatabaseManager getInstance(Context context){
        if(instance == null){
            instance = new DatabaseManager(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseManager(Context context){
        db = Room.databaseBuilder(context,
                DatabaseHelper.class, DatabaseHelper.DATABASE_NAME)
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                initData(context);
                            }
                        });
                    }
                })
                .build();
    }

    public HseDao getHseDao(){return db.hseDao();}

    private void initData(Context context){

        List<GroupEntity> groups = new ArrayList<>();
        GroupEntity group = new GroupEntity();
        group.id = 1;
        group.name = "Группа ПИ-18-1";
        groups.add(group);

        group = new GroupEntity();
        group.id = 2;
        group.name = "Группа ПИ-18-2";
        groups.add(group);

        DatabaseManager.getInstance(context).getHseDao().insertGroup(groups);

        List<TeacherEntity> teachers = new ArrayList<>();
        TeacherEntity teacher = new TeacherEntity();
        teacher.id = 1;
        teacher.fio = "Петров Петр Петрович";
        teachers.add(teacher);

        teacher = new TeacherEntity();
        teacher.id = 2;
        teacher.fio = "Иванов Иван Иванович";
        teachers.add(teacher);

        DatabaseManager.getInstance(context).getHseDao().insertTeacher(teachers);

        List<TimeTableEntity> timeTables = new ArrayList<>();
        TimeTableEntity timeTable = new TimeTableEntity();
        timeTable.id = 1;
        timeTable.cabinet = "Кабинет 1";
        timeTable.subGroup = "ПИ";
        timeTable.subjName = "Философия";
        timeTable.corp = "Бульвар Гагарина 37";
        timeTable.type = "Практика";
        timeTable.timeStart = dateFromString("2021-03-22 15:00");
        timeTable.timeEnd = dateFromString("2021-03-22 16:30");
        timeTable.groupId = 1;
        timeTable.teacherId = 1;
        timeTables.add(timeTable);

        timeTable = new TimeTableEntity();
        timeTable.id = 10;
        timeTable.cabinet = "Кабинет 2";
        timeTable.subGroup = "ПИ";
        timeTable.subjName = "Мобильная разработка";
        timeTable.corp = "Бульвар Гагарина 37";
        timeTable.type = "Лекция";
        timeTable.timeStart = dateFromString("2021-03-22 17:00");
        timeTable.timeEnd = dateFromString("2021-03-22 18:30");
        timeTable.groupId = 2;
        timeTable.teacherId = 2;
        timeTables.add(timeTable);

        timeTable = new TimeTableEntity();
        timeTable.id = 2;
        timeTable.cabinet = "Кабинет 2";
        timeTable.subGroup = "ПИ";
        timeTable.subjName = "Мобильная разработка";
        timeTable.corp = "Бульвар Гагарина 37";
        timeTable.type = "Лекция";
        timeTable.timeStart = dateFromString("2021-03-22 14:00");
        timeTable.timeEnd = dateFromString("2021-03-22 15:30");
        timeTable.groupId = 1;
        timeTable.teacherId = 2;
        timeTables.add(timeTable);

        timeTable = new TimeTableEntity();
        timeTable.id = 3;
        timeTable.cabinet = "Кабинет 3";
        timeTable.subGroup = "ПИ";
        timeTable.subjName = "Мобильная разработка";
        timeTable.corp = "Бульвар Гагарина 37";
        timeTable.type = "Практика";
        timeTable.timeStart = dateFromString("2021-03-23 10:00");
        timeTable.timeEnd = dateFromString("2021-03-23 11:30");
        timeTable.groupId = 1;
        timeTable.teacherId = 2;
        timeTables.add(timeTable);

        timeTable = new TimeTableEntity();
        timeTable.id = 4;
        timeTable.cabinet = "Кабинет 3";
        timeTable.subGroup = "ПИ";
        timeTable.subjName = "Мобильная разработка";
        timeTable.corp = "Бульвар Гагарина 37";
        timeTable.type = "Лекция";
        timeTable.timeStart = dateFromString("2021-03-23 12:00");
        timeTable.timeEnd = dateFromString("2021-03-23 13:30");
        timeTable.groupId = 1;
        timeTable.teacherId = 1;
        timeTables.add(timeTable);

        DatabaseManager.getInstance(context).getHseDao().insertTimeTable(timeTables);
    }

    private Date dateFromString (String val){
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm",
                        Locale.getDefault());
        try{
            return simpleDateFormat.parse(val);
        }
        catch (ParseException e) {
            //
        }
        return null;
    }

}
