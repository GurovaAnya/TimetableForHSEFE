package org.hse.android;

import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class BaseActivity extends AppCompatActivity {
    private final static String TAG = "BaseActivity";
    public final static String URL = "http://api.ipgeolocation.io/ipgeo?apiKey=b03018f75ed94023a005637878ec0977";

    protected TextView time;
    protected Date currentTime;

    protected OkHttpClient client = new OkHttpClient();

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

    private void showTime(Date dateTime){
        if(dateTime == null)
            return;
        currentTime = dateTime;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm EEEE", new Locale("ru"));
        time.setText(simpleDateFormat.format(currentTime));
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

    protected void showScheduleImpl(ScheduleMode mode, ScheduleType type, StudentActivity.Group group){
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra(ScheduleActivity.ARG_ID, group.getId());
        intent.putExtra(ScheduleActivity.ARG_NAME, group.getName());
        intent.putExtra(ScheduleActivity.ARG_MODE, mode);
        intent.putExtra(ScheduleActivity.ARG_TYPE, type);
        intent.putExtra(ScheduleActivity.ARG_TIME, currentTime);
        startActivity(intent);
    }

    enum ScheduleType{
        DAY,
        WEEK
    }

    enum ScheduleMode{
        STUDENT,
        TEACHER
    }
}

