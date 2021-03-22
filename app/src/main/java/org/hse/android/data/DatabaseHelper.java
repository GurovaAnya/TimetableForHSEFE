package org.hse.android.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import org.hse.android.data.Converters;
import org.hse.android.data.HseDao;
import org.hse.android.entities.GroupEntity;
import org.hse.android.entities.TeacherEntity;
import org.hse.android.entities.TimeTableEntity;

@Database(entities = {GroupEntity.class, TeacherEntity.class, TimeTableEntity.class},
        version = 1,
        exportSchema = false)
@TypeConverters({Converters.class})
public abstract class DatabaseHelper extends RoomDatabase{

    public static final String DATABASE_NAME = "hse_timetable";

    public abstract HseDao hseDao();
}
