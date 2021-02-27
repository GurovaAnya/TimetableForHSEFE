package org.hse.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements SensorEventListener {

    private static final String  TAG = "SettingsActivity";
    private static final Integer REQUEST_IMAGE_CAPTURE = 1;
    private static final String  PERMISSION = Manifest.permission.CAMERA;
    private static final Integer REQUEST_PERMISSION_CODE = 100;

    private ImageView userPhoto;
    private String imageFilePath;
    private PreferenceManager preferenceManager;

    private SensorManager sensorManager;
    private Sensor light;
    private TextView sensorLight;

    private EditText nameEdit;

    private TextView allSensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preferenceManager = new PreferenceManager(getApplicationContext());

        userPhoto = findViewById(R.id.userPhoto);
        getPhoto();

        Button uploadPhoto = findViewById(R.id.uploadPhoto);
        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });

        Button saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorLight = findViewById(R.id.lightLevel);

        nameEdit = findViewById(R.id.name);
        getName();

        allSensors = findViewById(R.id.allSensors);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        String allSensorsText = "";
        for (Sensor sensor: sensors)
            allSensorsText += sensor.getName() + '\n';
        allSensors.setText(allSensorsText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            loadPhoto();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getPhoto(){
        String path = preferenceManager.getAvatarPath();
        if( path != null) {
            imageFilePath = path;
            loadPhoto();
        }
    }

    private void getName(){
        String name = preferenceManager.getName();
        if (name != null)
            nameEdit.setText(name);
    }

    private void save(){
        if(nameEdit.getText()!=null)
            preferenceManager.saveName(nameEdit.getText().toString());
        if (imageFilePath!=null)
            preferenceManager.saveAvatarPath(imageFilePath);
        Toast toast = Toast.makeText(getApplicationContext(), "Сохранено", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void loadPhoto(){
        if(imageFilePath != null){
            Glide.with(this).load(imageFilePath).into(userPhoto);
        }
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager())!=null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
                imageFilePath = photoFile.getAbsolutePath();
            }catch (IOException ex){
                Log.e(TAG, "Create file", ex);
            }
            if (photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                try{
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }catch (ActivityNotFoundException e){
                    Log.e(TAG, "Start activity", e);
                }
            }
        }
    }

    public void checkPermission(){
        int permissionCheck = ActivityCompat.checkSelfPermission(this, PERMISSION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION)){
                /*showExplanation("Нужно предоставить права",
                        "Для снятия фото нужно предоставить права на фото", PERMISSION, REQUEST_PERMISSION_CODE);*/
            }else{
                ActivityCompat.requestPermissions(this, new String[]{PERMISSION}, REQUEST_PERMISSION_CODE);
            }
        }else{
            dispatchTakePictureIntent();
        }
    }

    private File createImageFile() throws IOException {
        File pathOfStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filePrefix = "img_" + timeStamp + "_";
        String suffix = ".jpg";

        File image = File.createTempFile(filePrefix, suffix, pathOfStorageDir);
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float lux = event.values[0];
        sensorLight.setText(lux + "lux");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}