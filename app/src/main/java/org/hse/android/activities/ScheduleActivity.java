package org.hse.android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hse.android.R;
import org.hse.android.models.ScheduleItem;
import org.hse.android.models.ScheduleItemHeader;
import org.hse.android.entities.TimeTableWithTeacherEntity;
import org.hse.android.viewmodels.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class ScheduleActivity extends AppCompatActivity {

    private BaseActivity.ScheduleType type;
    private BaseActivity.ScheduleMode mode;
    private Integer argId;
    private String name;
    private String TAG = "ScheduleActivity";

    public static String ARG_TYPE = "ARG_TYPE";
    public static String ARG_MODE = "ARG_MODE";
    public static String ARG_ID = "ARG_ID";
    public static String ARG_NAME = "ARG_NAME";
    public static String ARG_TIME = "ARG_TIME";
    public Integer DEFAULT_ID = -1;

    RecyclerView recyclerView;
    ItemAdapter adapter;
    TextView title;
    Date currentTime;

    protected MainViewModel mainViewModel;
    List<ScheduleItem> scheduleList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        type = (BaseActivity.ScheduleType) getIntent().getSerializableExtra(ARG_TYPE);
        mode = (BaseActivity.ScheduleMode) getIntent().getSerializableExtra(ARG_MODE);
        argId = getIntent().getIntExtra(ARG_ID, DEFAULT_ID);
        name = getIntent().getStringExtra(ARG_NAME);
        currentTime = (Date)getIntent().getSerializableExtra(ARG_TIME);


        recyclerView = findViewById(R.id.listView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter = new ItemAdapter(this::OnScheduleItemClick);
        recyclerView.setAdapter(adapter);

        initTitle();
        initTime();
        initData();
    }


    private void initTime() {
        TextView time = findViewById(R.id.time);
        time.setText(formatDateDay(currentTime));
    }

    private String formatDateDay(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd MMMM", new Locale("ru"));
        return simpleDateFormat.format(date);
    }

    private void initTitle() {
        title = findViewById(R.id.title);
        title.setText(name);
    }


    private void OnScheduleItemClick(ScheduleItem scheduleItem) {
        //
    }


    private void initData(){

        Observer observer = new Observer<List<TimeTableWithTeacherEntity>>() {
            @Override
            public void onChanged(List<TimeTableWithTeacherEntity> timeTableWithTeacherEntities) {
                scheduleList = getScheduleItems(timeTableWithTeacherEntities);
                adapter.setDataList(scheduleList);
                recyclerView.setAdapter(adapter);
            }
        };
        applyFunctionForTimeTable(observer);
    }

    private List<ScheduleItem> getScheduleItems(List<TimeTableWithTeacherEntity> timeTableWithTeacherEntities) {
        List<ScheduleItem> list = new ArrayList<>();

        Map<String, List<TimeTableWithTeacherEntity>> days =
                timeTableWithTeacherEntities
                .stream()
                .sorted(Comparator.comparing(t -> t.timeTableEntity.timeStart))
                .collect(Collectors.groupingBy(t -> formatDateDay(t.timeTableEntity.timeStart)));

        for (Map.Entry<String, List<TimeTableWithTeacherEntity>> day: days.entrySet()
             ) {
            ScheduleItemHeader header = new ScheduleItemHeader();
            header.setTitle(day.getKey());
            list.add(header);

            for(TimeTableWithTeacherEntity timetable: day.getValue()){
                list.add(convertItem(timetable));
            }
        }
        return list;
    }

    private void applyFunctionForTimeTable(Observer observer) {
        switch (type){
            case DAY:
                switch (mode){
                    case STUDENT:
                        mainViewModel.getTimeTableForStudentDay(currentTime, argId).observe(this,observer);
                        break;
                    case TEACHER:
                        mainViewModel.getTimeTableForTeacherDay(currentTime, argId).observe(this,observer);
                        break;
                }
                break;
            case WEEK:
                switch (mode){
                    case STUDENT:
                        mainViewModel.getTimeTableForStudentWeek(currentTime, argId).observe(this,observer);
                        break;
                    case TEACHER:
                        mainViewModel.getTimeTableForTeacherWeek(currentTime, argId).observe(this,observer);
                        break;
                }
                break;
        }
    }

    private void processStudentDay() {

    }

    private ScheduleItem convertItem(TimeTableWithTeacherEntity timeTableWithTeacherEntity){
        ScheduleItem item = new ScheduleItem();
        item.setStart(formatToMinutes(timeTableWithTeacherEntity.timeTableEntity.timeStart));
        item.setEnd(formatToMinutes(timeTableWithTeacherEntity.timeTableEntity.timeEnd));
        item.setType(String.valueOf(timeTableWithTeacherEntity.timeTableEntity.type));
        item.setName(timeTableWithTeacherEntity.timeTableEntity.subjName);
        item.setPlace(timeTableWithTeacherEntity.timeTableEntity.corp);
        item.setTeacher(timeTableWithTeacherEntity.teacherEntity.fio);
        return item;
    }

    private String formatToMinutes(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static final class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private final static int TYPE_ITEM = 0;
        private final static int TYPE_HEADER = 1;
        private List<ScheduleItem> dataList = new ArrayList<>();
        private OnItemClick onItemClick;
        public ItemAdapter(OnItemClick onItemClick) {this.onItemClick = onItemClick;}

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            if (viewType == TYPE_ITEM) {
                View contactView = inflater.inflate(R.layout.item_schedule, parent, false);
                return new ViewHolder(contactView, context, onItemClick);
            } else if (viewType == TYPE_HEADER){
                View contactView = inflater.inflate(R.layout.item_schedule_header, parent, false);
                return new ViewHolderHeader(contactView, context, onItemClick);
            }
            throw new IllegalArgumentException("Invalid view type");
        }

        public int getItemViewType(int position){
            ScheduleItem data = dataList.get(position);
            if (data instanceof ScheduleItemHeader) {
                return TYPE_HEADER;
            }
            return TYPE_ITEM;
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            ScheduleItem data = dataList.get(position);
            if (viewHolder instanceof ViewHolder){
                ((ViewHolder)viewHolder).bind(data);
            } else if (viewHolder instanceof ViewHolderHeader) {
                ((ViewHolderHeader) viewHolder).bind((ScheduleItemHeader) data);
            }
        }

        public void setDataList(List<ScheduleItem> list){
            dataList = list;
        }


    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private Context context;
        private OnItemClick onItemClick;
        private TextView start;
        private TextView end;
        private TextView type;
        private TextView name;
        private TextView place;
        private TextView teacher;

        public ViewHolder(View itemView, Context context, OnItemClick onItemClick) {
            super(itemView);
            this.context = context;
            this.onItemClick = onItemClick;
            start = itemView.findViewById(R.id.start);
            end = itemView.findViewById(R.id.end);
            type = itemView.findViewById(R.id.type);
            name = itemView.findViewById(R.id.name);
            place = itemView.findViewById(R.id.place);
            teacher = itemView.findViewById(R.id.teacher);
        }

        public void bind(final ScheduleItem data){
            start.setText(data.getStart());
            end.setText(data.getEnd());
            type.setText(data.getType());
            name.setText(data.getName());
            place.setText(data.getPlace());
            teacher.setText(data.getTeacher());
        }
    }

    public static class ViewHolderHeader extends RecyclerView.ViewHolder{
        private Context context;
        private OnItemClick onItemClick;
        private TextView title;

        public ViewHolderHeader(@NonNull View itemView, Context context, OnItemClick onItemClick) {
            super(itemView);
            this.context = context;
            this.onItemClick = onItemClick;
            title = itemView.findViewById(R.id.header);
        }

        public void bind(final ScheduleItemHeader data){
            title.setText(data.getTitle());
        }
    }
}

interface OnItemClick{
    void OnClick(ScheduleItem data);
}

