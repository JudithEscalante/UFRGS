package com.example.second;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MyActivity";
    RadioGroup radioGroup;
    private float mLastX, mLastY, mLastZ;
    private boolean mInitialized;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private final float NOISE = (float) 0.5;
    int optionLocation = 1;
    int optionCrime= 2;
    PieChart pieChart;
    List<String> scrollBarLocation = new ArrayList<>();
    List<String> crimesList = new ArrayList<>();
    HashMap<String, Integer> totalCrimes = new HashMap<String, Integer>(50, 10);



    /** Called when the activity is first created. */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        pieChart=findViewById(R.id.pieChart);


        try {
            loadData();
            createScrollBar(optionLocation);
            createPieChart();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        checkedOnRadioButton();


    }
    public void checkedOnRadioButton() {
        radioGroup =(RadioGroup) findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCheckedChanged (RadioGroup group,@IdRes int checkedId){
                if (checkedId == R.id.radioButtonLocation) {
                    createScrollBar(optionLocation);
                }else {
                    createScrollBar(optionCrime);
                }
            }
        });
    }




    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // can be safely ignored for this demo
    }
    @Override
    public void onSensorChanged(SensorEvent event) {

        TextView tvX= (TextView)findViewById(R.id.x_axis);
        TextView tvY= (TextView)findViewById(R.id.y_axis);
        TextView tvZ= (TextView)findViewById(R.id.z_axis);
        ImageView iv = (ImageView)findViewById(R.id.image);
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (!mInitialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;

            tvX.setText("0.0");
            tvY.setText("0.0");
            tvZ.setText("0.0");
            mInitialized = true;
        } else {
            float deltaX = Math.abs(mLastX - x);
            float deltaY = Math.abs(mLastY - y);
            float deltaZ = Math.abs(mLastZ - z);

            if (deltaX < NOISE) deltaX = (float)0.0;
            if (deltaY < NOISE) deltaY = (float)0.0;
            if (deltaZ < NOISE) deltaZ = (float)0.0;
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            tvX.setText(Float.toString(x));
            tvY.setText(Float.toString(y));
            tvZ.setText(Float.toString(z));
            /*iv.setVisibility(View.VISIBLE);*/

            if (deltaX > deltaY) {
                /*iv.setImageResource(R.drawable.horizontal)*/;
            } else if (deltaY > deltaX) {
                /*iv.setImageResource(R.drawable.vertical);*/
            } else {
                /*iv.setVisibility(View.INVISIBLE);*/
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadData() throws JSONException {
        JSONObject json = loadJsonObjectFromAsset("file.json");
        try {
            JSONArray principalArray = json.getJSONArray("dataCryme");
            for(int i = 0; i< principalArray.length(); i++){
                String locationName = principalArray.getJSONObject(i).getString("Location");
                if(!scrollBarLocation.contains(locationName)){
                    scrollBarLocation.add(locationName);
                }

                String crime = principalArray.getJSONObject(i).getString("CrimeType");
                /*if(!crimesList.contains(crime)){
                    crimesList.add(crime);
                    totalCrimes.put(crime, 1);
                }else{
                    Integer num = totalCrimes.get(crime);
                    Integer newNum = num + 1;
                    totalCrimes.replace(crime, newNum);
                }*/
                Integer w = totalCrimes.get(crime);
                if(w == null) totalCrimes.put(crime, 1);
                else totalCrimes.put(crime, w + 1);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public JSONObject loadJsonObjectFromAsset(String assetName) {
        try {
            String json = loadStringFromAsset(assetName);
            if (json != null)
                return new JSONObject(json);
        } catch (Exception e) {
            Log.e("JsonUtils", e.toString());
        }

        return null;
    }

    private String loadStringFromAsset(String assetName) throws Exception {
        AssetManager assetManager = getAssets();
        InputStream is = assetManager.open(assetName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new String(buffer, "UTF-8");
    }

    public void createScrollBar(int option){
        LinearLayout listButtons = (LinearLayout) findViewById(R.id.scrollBar);

        if (option == 1){
            listButtons.removeAllViews();
            Button[] dynamic_button = new Button[scrollBarLocation.size()];
            for(int i = 0; i< scrollBarLocation.size() ; i++){
                dynamic_button[i] = new Button(this);
                dynamic_button[i].setText(scrollBarLocation.get(i));
                dynamic_button[i].setId(i);
                //newButton.setBackgroundColor(0xFF99D6D6);
                dynamic_button[i].setTextSize(10);
                listButtons.addView(dynamic_button[i]);
                dynamic_button[i].setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        try {
                            AnotherPieChart(((Button)v).getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("Clicked", ((Button)v).getText().toString());
                    }
                });

            }
        }
        else{
            listButtons.removeAllViews();
            Button[] dynamic_button = new Button[totalCrimes.size()];
            int i=0;
            for ( String key : totalCrimes.keySet() ) {
                dynamic_button[i] = new Button(this);
                dynamic_button[i].setText(key);
                //newButton.setBackgroundColor(0xFF99D6D6);
                dynamic_button[i].setTextSize(10);
                dynamic_button[i].setId(i);
                listButtons.addView(dynamic_button[i]);
                dynamic_button[i].setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Log.i("Clicked", ((Button)v).getText().toString());
                    }
                });
                i++;
            }
        }
    }

    private void createPieChart() {
        Description description = new Description();
        description.setText("Crime type");

        pieChart.setDescription(description);
        ArrayList<PieEntry> pieEntries= new ArrayList<>();

        for(String i : totalCrimes.keySet()){
            Integer value = totalCrimes.get(i);
            pieEntries.add(new PieEntry( (float) value, i));
        }



        PieDataSet pieDataSet =new PieDataSet(pieEntries,"text");
        pieDataSet.setColors (ColorTemplate.COLORFUL_COLORS);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
    }

    private void AnotherPieChart(String filter) throws JSONException {
        HashMap<String, Integer> crimes = new HashMap<String, Integer>(50, 10);
        JSONObject json = loadJsonObjectFromAsset("file.json");
        JSONArray principalArray = json.getJSONArray("dataCryme");
        for(int i = 0; i< principalArray.length(); i++) {
            String locationName = principalArray.getJSONObject(i).getString("Location");
            String crimeType = principalArray.getJSONObject(i).getString("CrimeType");

            if (filter.equals(locationName)) {
                Integer w = crimes.get(crimeType);
                if (w == null) crimes.put(crimeType, 1);
                else crimes.put(crimeType, w + 1);

            }

        }

        Description description = new Description();
        description.setText("Crime type");

        pieChart.setDescription(description);
        ArrayList<PieEntry> pieEntries= new ArrayList<>();

        for(String i : crimes.keySet()){
            Integer value = crimes.get(i);
            pieEntries.add(new PieEntry( (float) value, i));
        }

        PieDataSet pieDataSet =new PieDataSet(pieEntries,"text");
        pieDataSet.setColors (ColorTemplate.COLORFUL_COLORS);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

    }

}
