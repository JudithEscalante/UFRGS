package com.example.second;

import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

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




public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MyActivity";
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

    //***********************
    private SensorManager sensorManager;
    //private ScrollListener mListener;
    private Sensor accelerometer;
    private Sensor magnetSensor;
    private float mCurrentPosition;
    private boolean isCurrentPositionSet = false;
    private int count = 0;

    float[] mMagnetValues      = new float[3];
    float[] mAccelValues       = new float[3];
    float[] mOrientationValues = new float[3];
    float[] mRotationMatrix    = new float[9];

    private long lastUpdate;
    private int minValue =  -820;//right
    private int maxValue = 0;//left
    private int minY = -30;//down
    private int maxY = 0;//up

    private ImageView mDrawable;
    NestedScrollView mLayout;
    public static int x;
    public static int y;
    int acc_x = 0;
    //same for every image
    private double MARGIN_RATIO = 1; //0.03
    // DIFFERENT in every image
    private int IMAGE_WIDTH = 2128;
    private int IMAGE_HEIGHT = 1500;
    private int SCROLL_START = 500;
    // user input (DIFFERENT scroll speed vary in every android device)
    //0.5 , 1.0 , 1.5 , 2.0 , 2.5 (VERY SLOW, SLOW, MODERATE, FAST, VERY FAST)
    private double TIMES_FASTER = 1.5;

    //****************************


    /** Called when the activity is first created. */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInitialized = false;
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        listButtons = (LinearLayout) findViewById(R.id.scrollBar);
        listButtons.setNestedScrollingEnabled(true);

        listButtons.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = listButtons.getScrollY();
                int scrollX = listButtons.getScrollX();// For HorizontalScrollView
                // DO SOMETHING WITH THE SCROLL COORDINATES
                Log.i("Sensor", "Scroll X: " + scrollX);
                Log.i("Sensor", "Scroll Y: " + scrollY);
            }
        });


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




    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
      /*  switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                Log.i("Sensor", "Accelerometer");
                System.arraycopy(event.values, 0, mAccelValues, 0, 3);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(event.values, 0, mMagnetValues, 0, 3);
                break;
        }*/

        /*boolean success = SensorManager.getRotationMatrix(mRotationMatrix, null, mAccelValues, mMagnetValues);
        if(success){
            SensorManager.getOrientation(mRotationMatrix, mOrientationValues);
            Log.i("Sensor", "SUCCESS");
        }else{
            Log.i("Sensor", "NOT SUCCESS!!!!!!");
        } */

        //Log.i("Sensor", "mCurrentPosition: " + mCurrentPosition);
        //Log.i("Sensor", "mOrientationValues[0]: " + mOrientationValues[0]);
        Log.i("Sensor", "Accel: " + event.values[0]);

        float acceX = event.values[0];
        float acceZ = event.values[2];
        if(acceX > 0 && acceZ < 10) {
            //Scroll to Top
            //mListener.onTiltUp();
            //Log.i("Sensor", "AQUI");
            listButtons.post(new Runnable() {
                @Override
                public void run() {
                    int scrollY = listButtons.getScrollY();
                    int scrollX = listButtons.getScrollX();
                    listButtons.scrollBy(scrollX,scrollY+10);
                    listButtons.computeScroll();
                    listButtons.invalidate();
                    Log.i("Sensor", "scroll Up");
                }
            });
        }
        else if (acceX <= 10 && acceZ >= 0) {
            //Scroll to Bottom
            //mListener.onTiltDown();

            listButtons.post(new Runnable() {
                @Override
                public void run() {
                    int scrollY = listButtons.getScrollY();
                    int scrollX = listButtons.getScrollX();
                    listButtons.scrollBy(scrollX,scrollY-10);
                    listButtons.computeScroll();
                    listButtons.invalidate();
                    Log.i("Sensor", "scroll Bottom");
                }
            });
        }

        //print values on screen
        TextView tvX= (TextView)findViewById(R.id.x_axis);
        TextView tvY= (TextView)findViewById(R.id.y_axis);
        TextView tvZ= (TextView)findViewById(R.id.z_axis);
        float xx = event.values[0];
        float yy = event.values[1];
        float zz = event.values[2];
        if (!mInitialized) {
            mLastX = xx;
            mLastY = yy;
            mLastZ = zz;
            tvX.setText("0.0");
            tvY.setText("0.0");
            tvZ.setText("0.0");
            mInitialized = true;
        } else {
            float deltaX = Math.abs(mLastX - xx);
            float deltaY = Math.abs(mLastY - yy);
            float deltaZ = Math.abs(mLastZ - zz);

            if (deltaX < NOISE) deltaX = (float)0.0;
            if (deltaY < NOISE) deltaY = (float)0.0;
            if (deltaZ < NOISE) deltaZ = (float)0.0;
            mLastX = xx;
            mLastY = yy;
            mLastZ = zz;
            tvX.setText(Float.toString(xx));
            tvY.setText(Float.toString(yy));
            tvZ.setText(Float.toString(zz));
        }
    }

    public void requestAllSensors() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void killAllSensors() {
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, accelerometer);
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
                            pieChart.notifyDataSetChanged();
                            pieChart.invalidate();
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
                final String keyValue = key;
                dynamic_button[i] = new Button(this);
                dynamic_button[i].setText(key);
                //newButton.setBackgroundColor(0xFF99D6D6);
                dynamic_button[i].setTextSize(10);
                dynamic_button[i].setId(i);
                listButtons.addView(dynamic_button[i]);
                dynamic_button[i].setOnClickListener(new OnClickListener() {
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
