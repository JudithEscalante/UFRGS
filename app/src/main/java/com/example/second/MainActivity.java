package com.example.second;

import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private float mLastX, mLastY, mLastZ;
    private boolean mInitialized;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private final float NOISE = (float) 0.5;

    PieChart pieChart;
    List<String> scrollBarList = new ArrayList<>();
    BubbleChart bubbleChart;
    BubbleData bubbleData;
    BubbleDataSet bubbleDataSet;
    ArrayList bubbleEntries;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        pieChart=findViewById(R.id.pieChart);
        createPieChart();

        try {
            loadData();
            createScrollBar();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void createPieChart() {
        Description description = new Description();
        description.setText("Crime type");

        pieChart.setDescription(description);
        ArrayList<PieEntry> pieEntries= new ArrayList<>();
        pieEntries.add(new PieEntry(2,3));
        pieEntries.add(new PieEntry(3,8));
        pieEntries.add(new PieEntry(6,7));

        PieDataSet pieDataSet =new PieDataSet(pieEntries,"text");
        pieDataSet.setColors (ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
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

    public void loadData() throws JSONException {

        JSONObject json = loadJsonObjectFromAsset("file.json");
        try {
            JSONArray refArray = json.getJSONArray("dataCryme");
            for(int i = 0; i< refArray.length(); i++){
                String ref = refArray.getJSONObject(i).getString("Location");
                if(!scrollBarList.contains(ref)){
                    scrollBarList.add(ref);
                }

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

    public void createScrollBar(){
        LinearLayout listButtons = (LinearLayout) findViewById(R.id.scrollBarLocation);

       for(int i = 0; i< scrollBarList.size() ; i++){
            Button newButton = new Button(this);
            newButton.setText(scrollBarList.get(i));
            //newButton.setBackgroundColor(0xFF99D6D6);
            newButton.setTextSize(10);
           listButtons.addView(newButton);
        }

    }

}
