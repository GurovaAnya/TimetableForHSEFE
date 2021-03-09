package org.hse.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ScheduleActivity extends AppCompatActivity {

    private BaseActivity.ScheduleType type;
    private BaseActivity.ScheduleMode mode;
    private Integer id;
    private String name;
    private String TAG = "ScheduleActivity";
    public final static String URL = "http://api.ipgeolocation.io/ipgeo?apiKey=b03018f75ed94023a005637878ec0977";
    protected OkHttpClient client = new OkHttpClient();

    public static String ARG_TYPE = "ARG_TYPE";
    public static String ARG_MODE = "ARG_MODE";
    public static String ARG_ID = "ARG_ID";
    public static String ARG_NAME = "ARG_NAME";
    public Integer DEFAULT_ID = -1;

    RecyclerView recyclerView;
    ItemAdapter adapter;
    TextView title;
    Date currentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        type = (BaseActivity.ScheduleType) getIntent().getSerializableExtra(ARG_TYPE);
        mode = (BaseActivity.ScheduleMode) getIntent().getSerializableExtra(ARG_MODE);
        id = getIntent().getIntExtra(ARG_ID, DEFAULT_ID);
        name = getIntent().getStringExtra(ARG_NAME);


        recyclerView = findViewById(R.id.listView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter = new ItemAdapter(this::OnScheduleItemClick);
        recyclerView.setAdapter(adapter);

        initTitle();
        initTime();
        initData();
    }


    protected void getTime(){
        Request request = new Request.Builder().url(URL).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "getTime", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                parseResponse(response);
            }
        });
    }

    protected void initTime(){
        getTime();
    }


    private void parseResponse(Response response){
        Gson gson = new Gson();
        ResponseBody responseBody = response.body();
        try{
            if(responseBody==null)
                return;
            String string = responseBody.string();
            Log.d(TAG, string);
            TimeResponse timeResponse = gson.fromJson(string, TimeResponse.class);
            String currentTimeVal = timeResponse.getTimeZone().getCurrentTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSX", Locale.getDefault());
            Date dateTime = simpleDateFormat.parse(currentTimeVal);
            runOnUiThread(() -> showTime(dateTime));
        }
        catch (Exception e){
            Log.e(TAG, "", e);
        }
    }

    private void showTime(Date dateTime) {
        TextView time = findViewById(R.id.time);
        if(dateTime == null)
            return;
        currentTime = dateTime;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd MMMM", new Locale("ru"));
        time.setText(simpleDateFormat.format(currentTime));
    }

    private void initTitle() {
        title = findViewById(R.id.title);
        title.setText(name);
    }


    private void OnScheduleItemClick(ScheduleItem scheduleItem) {
        //
    }


    private void initData(){
        List<ScheduleItem> list = new ArrayList<>();

        ScheduleItemHeader header = new ScheduleItemHeader();
        header.setTitle("Понедельник, 28 января");
        list.add(header);

        ScheduleItem item = new ScheduleItem();
        item.setStart("10:00");
        item.setEnd("11:00");
        item.setType("Практическое занятие");
        item.setName("Анализ данных");
        item.setPlace("Ауд. 503, Кочновский пр-д, д.3");
        item.setTeacher("Пред. Гущим Михаил Иванович");
        list.add(item);

        item = new ScheduleItem();
        item.setStart("11:00");
        item.setEnd("12:00");
        item.setType("Практическое занятие");
        item.setName("Анализ данных");
        item.setPlace("Ауд. 503, Кочновский пр-д, д.3");
        item.setTeacher("Пред. Гущим Михаил Иванович");
        list.add(item);

        adapter.setDataList(list);
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

