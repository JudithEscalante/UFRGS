package com.app.labvistilt;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

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

public class ActivitytestB extends AppCompatActivity {

    RadioGroup radioGroup;
    private float mLastX, mLastY, mLastZ;
    private boolean mInitialized = false;
    //private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private final float NOISE = (float) 0.5;
    int optionLocation = 1;
    int optionCrime= 2;
    PieChart pieChart;
    List<String> scrollBarLocation = new ArrayList<>();
    List<String> crimesList = new ArrayList<>();
    HashMap<String, Integer> totalCrimes = new HashMap<String, Integer>(50, 10);
    LinearLayout listButtons;
    private boolean buttonClicked = false;

    //***********************
    private SensorManager sensorManager;
    //private ScrollListener mListener;
    private Sensor accelerometer;
    NestedScrollView mLayout;
    private int velocity = 3;


    /** Called when the activity is first created. */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitytest_b);
        Button menu = (Button) findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });

        mInitialized = false;
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        listButtons = (LinearLayout) findViewById(R.id.scrollBar);
        listButtons.setNestedScrollingEnabled(true);

        mLayout = (NestedScrollView) findViewById(R.id.scrollView);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);



        //************* pie chart ****************
        pieChart=findViewById(R.id.pieChart);


        try {
            loadData();
            createScrollBar(optionLocation);
            createPieChart();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        checkedOnRadioButton();
        //************* pie chart ****************

    }
    public void openNewActivity(){
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }

    public void checkedOnRadioButton() {
        radioGroup =(RadioGroup) findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCheckedChanged (RadioGroup group,@IdRes int checkedId){
                if (checkedId == R.id.radioButtonLocation) {
                    createPieChart();
                    pieChart.notifyDataSetChanged();
                    pieChart.invalidate();
                    createScrollBar(optionLocation);
                }else {
                    createPieChart();
                    pieChart.notifyDataSetChanged();
                    pieChart.invalidate();
                    createScrollBar(optionCrime);
                }
            }
        });
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
        //LinearLayout listButtons = (LinearLayout) findViewById(R.id.scrollBar);

        if (option == 1){
            listButtons.removeAllViews();
            buttonClicked = false;
            Button[] dynamic_button = new Button[scrollBarLocation.size()];
            for(int i = 0; i< scrollBarLocation.size() ; i++){
                dynamic_button[i] = new Button(this);
                dynamic_button[i].setText(scrollBarLocation.get(i));
                dynamic_button[i].setId(i);
                //newButton.setBackgroundColor(0xFF99D6D6);

                dynamic_button[i].setTextSize(10);
                listButtons.addView(dynamic_button[i]);
                dynamic_button[i].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            AnotherPieChart(((Button)v).getText().toString());
                            pieChart.notifyDataSetChanged();
                            pieChart.invalidate();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("Clicked", ((Button)v).getText().toString());
                        buttonClicked = true;
                    }
                });

            }
        }
        else{
            listButtons.removeAllViews();
            buttonClicked = false;
            Button[] dynamic_button = new Button[totalCrimes.size()];
            int i=0;
            for ( String key : totalCrimes.keySet() ) {
                final String keyValue = key;
                dynamic_button[i] = new Button(this);
                dynamic_button[i].setText(key);
                //newButton.setBackgroundColor(0xFF99D6D6);
                dynamic_button[i].setTextSize(10);
                dynamic_button[i].setId(i);
                listButtons.addView(dynamic_button[i]);
                dynamic_button[i].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //show only the option selected
                        try{
                            Description description = new Description();
                            description.setText("Crime type");
                            pieChart.setDescription(description);
                            ArrayList<PieEntry> pieEntries= new ArrayList<>();
                            pieEntries.add(new PieEntry( (float) totalCrimes.get(keyValue), keyValue));
                            PieDataSet pieDataSet =new PieDataSet(pieEntries,"text");
                            pieDataSet.setColors (ColorTemplate.COLORFUL_COLORS);
                            PieData pieData = new PieData(pieDataSet);
                            pieChart.setData(pieData);
                            pieChart.notifyDataSetChanged();
                            pieChart.invalidate();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Log.i("Clicked", ((Button)v).getText().toString() + " " + totalCrimes.get(keyValue).toString());
                        buttonClicked = true;
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




  /*  public class TiltScrollListener implements ScrollListener {

        @Override
        public void onTiltUp() {
            listButtons.post(new Runnable() {
                @Override
                public void run() {
                    listButtons.scrollBy(10,0);
                    listButtons.computeScroll();
                    listButtons.invalidate();
                    Log.d(TAG, "scroll Up");
                }
            });
        }

        @Override
        public void onTiltDown() {
            listButtons.post(new Runnable() {
                @Override
                public void run() {
                    listButtons.scrollBy(-10,0);
                    listButtons.computeScroll();
                    listButtons.invalidate();
                    Log.d(TAG, "scroll Bottom");
                }
            });
        }
    }

    public interface ScrollListener {
        public void onTiltUp();
        public void onTiltDown();
    }   */
}