package com.app.labvistilt;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.transition.Fade;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import android.widget.Chronometer;
import android.widget.Toast;

import com.anychart.charts.Pie;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements SensorEventListener, com.app.labvistilt.BoxDialogFragment.NoticeDialogListener {

    private static final String TAG = "MyActivity";
    RadioGroup radioGroup;
    private float mLastX, mLastY, mLastZ;
    private boolean mInitialized = false;
    //private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private final float NOISE = (float) 0.5;
    int optionRadioButton= 1;
    int optionCrime= 2;
    PieChart pieChart;
    HorizontalBarChart horizontalBarChart;
    //AnyChartView anyChartView;
    List<String> scrollBarLocation = new ArrayList<>();
    List<String> crimesList = new ArrayList<>();
    HashMap<String, Integer> totalCrimes = new HashMap<String, Integer>(50, 10);
    HashMap<String, Integer> PeriodCrimes = new HashMap<String, Integer>(50, 10);
    LinearLayout listButtons;
    private boolean buttonClicked = false;
    private boolean play = false;
    int actualIndex;
    int beforeIndex;
    Pie pie;

    //***********************
    private SensorManager sensorManager;
    //private ScrollListener mListener;
    private Sensor accelerometer;
    NestedScrollView mLayout;
    private int velocity = 3;
    private String dataSize;
    private String dialogBox;
    private int maxScrollY = 2950;
    private String generalNameS = "SL1 'Inclination'";
    private String generalNameM = "ML1 'Inclination'";
    private String generalNameL = "LL1 'Inclination'";

    ProgressDialog nDialog;
    Button button_play;
    Button button_pause;
    Switch buttonFinger;
    TextView titleChart;
    Toolbar toolbar;
    TextView testType;
    private int testId = 1;
    //chronometer
    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;




    /** Called when the activity is first created. */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("Time: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        startChronometer();

        Button menu = (Button) findViewById(R.id.menu);
        menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });


        final RadioButton crime = (RadioButton) findViewById(R.id.radioButtonCrime);
        crime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(crime.isChecked()){
                    scrollBarLocation.clear();
                    totalCrimes.clear();
                    PeriodCrimes.clear();
                    listButtons.scrollTo(0 ,0);
                    loadScrollBar();
                    checkedOnRadioButton();
                    horizontalBarChart.invalidate();
                }
            }
        });

        final RadioButton location = (RadioButton) findViewById(R.id.radioButtonLocation);
        location.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(location.isChecked()){
                    scrollBarLocation.clear();
                    totalCrimes.clear();
                    PeriodCrimes.clear();
                    listButtons.scrollTo(0 ,0);
                    loadScrollBar();
                    checkedOnRadioButton();
                    pieChart.invalidate();
                }
            }
        });

        final TextView text = (TextView) findViewById(R.id.testType);
        text.setText(generalNameS);
        int inteiro = getIntent().getIntExtra("testId",9);
        if(inteiro!=9){
            try {
                testId = inteiro;
                text.setText(((testId==1)) ? generalNameS: ((testId==3) ? generalNameM: generalNameL));
            }catch (NumberFormatException e){
                Log.i("Sensor", "Error: getStringExtra " + testId);
            }
        }
        dataSize = showTest(testId);
        final Button next = (Button) findViewById(R.id.next);
        final Button back = (Button) findViewById(R.id.back);

        //back.setVisibility(View.INVISIBLE);
        if(testId==1){
            back.setVisibility(View.INVISIBLE);
        }

         next.setOnClickListener(new OnClickListener() {
         @Override
            public void onClick(View view) {
                //scrollBarLocation.clear();
                //totalCrimes.clear();
                //PeriodCrimes.clear();
                pauseChronometer();
                // Create and show the dialog.
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                BoxDialogFragment newFragment = new BoxDialogFragment ().newInstance(chronometer.getText().toString());
                newFragment.setCancelable(false);
                newFragment.show(ft, "dialog");

                Log.i("Sensor", "TestId main before: " + testId);

                if(testId>=1){
                    back.setVisibility(View.VISIBLE);
                    testId = testId +1;
                    dataSize = showTest(testId);
                    listButtons.scrollTo(0 ,0);
                    loadScrollBar();
                    checkedOnRadioButton();
                    pieChart.invalidate();
                }
                Log.i("Sensor", "TestId main after: " + testId);

            }
         });

         back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //scrollBarLocation.clear();
                //totalCrimes.clear();
                //45PeriodCrimes.clear();
                if(testId>1) {
                    next.setVisibility(View.VISIBLE);
                    testId = testId - 1;
                    dataSize = showTest(testId);
                    listButtons.scrollTo(0, 0);
                    //text.setText(((dataSize.equals(1)) ? generalNameS: ((dataSize.equals(3)) ? generalNameM: generalNameL)));
                    loadScrollBar();
                    checkedOnRadioButton();
                    Intent intent = new Intent(getApplicationContext(), com.app.labvistilt.ActivitytestB.class);
                    intent.putExtra("testId", testId);
                    startActivity(intent);
                }
                if(testId==1){
                    //back.setVisibility(View.INVISIBLE);
                }
                resetChronometer();
                startChronometer();
            }
        });

        buttonFinger = (Switch) findViewById(R.id.switchButton);
        final Button button_tilt = (Button) findViewById(R.id.button_tilt);
        button_play = (Button) findViewById(R.id.button_play);
        button_pause =(Button) findViewById(R.id.button_pause);
        buttonFinger.setChecked(true);
        buttonFinger.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!buttonFinger.isChecked()){
                    button_tilt.setVisibility(View.INVISIBLE);
                    button_play.setVisibility(View.VISIBLE);

                }else{
                    button_tilt.setVisibility(View.VISIBLE);
                    button_pause.setVisibility(View.INVISIBLE);
                    button_play.setVisibility(View.INVISIBLE);
                }
            }
        });

        mInitialized = false;
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        listButtons = (LinearLayout) findViewById(R.id.scrollBar);
        mLayout = (NestedScrollView) findViewById(R.id.scrollView);
        mLayout.setOnTouchListener( new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);



        //dataSize = getIntent().getStringExtra("option");

        toolbar = (Toolbar) findViewById(R.id.toolbar);



        //************* Chart ****************
        titleChart = (TextView) findViewById(R.id.ChartTitle);
        titleChart.setText("Total crimes by location");

        pieChart=findViewById(R.id.pieChart);
        horizontalBarChart = findViewById(R.id.horizontalChart);
        //anyChartView = findViewById(R.id.any_chart_view);
        //pie = AnyChart.pie();
        //anyChartView.setVisibility(View.INVISIBLE);

        loadScrollBar();
        checkedOnRadioButton();
        //************* pie chart ****************

    }

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new BoxDialogFragment();
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        resetChronometer();
        startChronometer();
        if(testId%2==0){
            Intent intent = new Intent(getApplicationContext(), com.app.labvistilt.ActivitytestB.class);
            intent.putExtra("testId", testId);
            nDialog = new ProgressDialog(MainActivity.this);
            nDialog.setMessage("Loading..");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle();



            startActivity(intent, bundle);
        }
        Log.i("Sensor", "TestId main: " + testId);
    }



    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        final TextView text = (TextView) findViewById(R.id.testType);
        startChronometer();
        scrollBarLocation.clear();
        totalCrimes.clear();
        PeriodCrimes.clear();
        if(testId>1 ) {
            testId = testId - 1;
            dataSize = showTest(testId);
            //text.setText("Test A : " + dataSize);
            listButtons.scrollTo(0, 0);
            loadScrollBar();
            checkedOnRadioButton();
        }
        Log.i("Sensor", "TestId main: " + testId);
    }





    public void startChronometer() {
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }
    public void pauseChronometer() {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }
    public void resetChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }




    public void openNewActivity(){
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }

    public void openNewActivityTestB(){
        Intent intent = new Intent(this, ActivitytestB.class);
        startActivity(intent);
    }

    public void loadScrollBar(){
        try {
            loadData();
            createScrollBar(optionRadioButton);
            if(optionRadioButton ==1){
                createPieChart();
                pieChart.notifyDataSetChanged();
                pieChart.invalidate();
            }
            else{
                createHorizontalChart();
                horizontalBarChart.notifyDataSetChanged();
                horizontalBarChart.invalidate();
            }


            RadioButton location = (RadioButton) findViewById(R.id.radioButtonLocation);
            RadioButton crime = (RadioButton) findViewById(R.id.radioButtonCrime);
            if(location.isChecked()){
                if(dataSize.equals("small")) maxScrollY = 3300;
                if(dataSize.equals("medium")) maxScrollY = 7020;
                if(dataSize.equals("large")) maxScrollY = 14646;
            }
            if(crime.isChecked()){
                maxScrollY = 1428;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String showSpeed(int velocity){
        String st = "";
        if(velocity==1) st = "1";
        if(velocity==3) st = "2";
        if(velocity==5) st = "3";
        if(velocity==7) st = "4";
        if(velocity==9) st = "5";
        return st;
    }

    public String showTest(int test){
        String st = "";
        if(test==1) st = "small";
        if(test==3) st = "medium";
        if(test==5) st = "large";
        return st;
    }

    public void checkedOnRadioButton() {
        radioGroup =(RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCheckedChanged (RadioGroup group,@IdRes int checkedId){
                if (checkedId == R.id.radioButtonLocation) {
                    pieChart.setVisibility(View.VISIBLE);
                    horizontalBarChart.setVisibility(View.INVISIBLE);
                    createPieChart();

                    pieChart.notifyDataSetChanged();
                    pieChart.invalidate();
                    optionRadioButton = 1;
                    createScrollBar(optionRadioButton);
                }else {
                    horizontalBarChart.setVisibility(View.VISIBLE);
                    pieChart.setVisibility(View.INVISIBLE);
                    createHorizontalChart();
                    horizontalBarChart.notifyDataSetChanged();
                    horizontalBarChart.invalidate();
                    optionRadioButton=2;
                    createScrollBar(optionRadioButton);
                }
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_MAX);
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

        final float acceX = event.values[0]; // TYPE_ACCELEROMETER_UNCALIBRATED sem nenhuma compensação de tendência.
        final float acceY = event.values[1];
        final float acceZ = event.values[2]; // TYPE_ACCELEROMETER_UNCALIBRATED sem nenhuma compensação de tendência. //com estimativa de compensação não funciona



        final Button button_move_up = (Button) findViewById(R.id.button_move_up);
        final Button button_move_down = (Button) findViewById(R.id.button_move_down);
        final Button button_tilt = (Button) findViewById(R.id.button_tilt);
        final Switch buttonFinger = (Switch) findViewById(R.id.switchButton);


        listButtons.post(new Runnable() {
            @Override
            public void run() {
                if (buttonFinger.isChecked()) {

                    if(!buttonClicked && button_tilt.isPressed()) {
                        useTilt(acceX, acceZ, acceY);
                    } else {
                        listButtons.scrollBy(0, 0);
                        listButtons.computeScroll();
                        listButtons.invalidate();
                        //Log.i("Sensor", "scroll Stop");
                        //mLayout.setBackgroundColor(Color.TRANSPARENT);
                        button_move_up.setVisibility(View.INVISIBLE);
                        button_move_down.setVisibility(View.INVISIBLE);
                        buttonClicked = false;
                    }
                } else {
                        button_play.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                button_pause.setVisibility(View.VISIBLE);
                                button_play.setVisibility(View.INVISIBLE);
                                play=true;
                                buttonClicked = false;
                            }
                        });
                       button_pause.setOnClickListener(new OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               button_play.setVisibility(View.VISIBLE);
                               button_pause.setVisibility(View.INVISIBLE);
                               play=false;
                           }
                       });
                    if(!buttonClicked && play) {
                        useTilt(acceX, acceZ, acceY);
                    }
                    else{
                        listButtons.scrollBy(0, 0);
                        listButtons.computeScroll();
                        listButtons.invalidate();
                        //Log.i("Sensor", "scroll Stop");
                        //mLayout.setBackgroundColor(Color.TRANSPARENT);
                        button_move_up.setVisibility(View.INVISIBLE);
                        button_move_down.setVisibility(View.INVISIBLE);
                        buttonClicked = false;
                    }
                }

            }
        });
    }
    public void useTilt(float acceX, float acceZ, float acceY){
        final int scrollY = listButtons.getScrollY();
        final int scrollX = listButtons.getScrollX();

        final Button button_move_up = (Button) findViewById(R.id.button_move_up);
        final Button button_move_down = (Button) findViewById(R.id.button_move_down);
                        if (scrollY < 0 || scrollY > maxScrollY) {//scroll max location 2950    //scroll max crime 264   //
                            if (scrollY < 0) {
                                listButtons.scrollTo(scrollX, scrollY + velocity);
                                //mLayout.setBackgroundColor(Color.GREEN);
                                //button_move_up.setVisibility(View.VISIBLE);
                                button_move_down.setVisibility(View.INVISIBLE);

                            }
                            RadioButton location = (RadioButton) findViewById(R.id.radioButtonLocation);
                            RadioButton crime = (RadioButton) findViewById(R.id.radioButtonCrime);
                            if (scrollY > maxScrollY) {  //&& scrollY>2950
                                listButtons.scrollTo(scrollX, scrollY - velocity);
                                //mLayout.setBackgroundColor(Color.BLUE);
                                button_move_up.setVisibility(View.INVISIBLE);
                                //button_move_down.setVisibility(View.VISIBLE);
                            }

                        } else {
                            //if(acceX > 0 && acceZ < 10 ) {
                            if (acceZ > 2 && acceZ < 6) {
                                //Scroll to Top
                                listButtons.scrollTo(scrollX, scrollY + velocity);
                                listButtons.computeScroll();
                                listButtons.invalidate();
                                //Log.i("Sensor", "scroll Up - vertical");
                                //Log.i("Sensor", "Scroll X: " + scrollX);
                                //Log.i("Sensor", "Scroll Y: " + scrollY);
                                //Log.i("Sensor", "velocity: " + velocity);
                                //Log.i("Sensor", "dataSize: " + dataSize);
                                //Log.i("Sensor", "acceX: " + acceX);
                                //Log.i("Sensor", "acceY: " + acceY);
                                //Log.i("Sensor", "acceZ: " + acceZ);
                                //mLayout.setBackgroundColor(Color.GREEN);
                                button_move_up.setVisibility(View.VISIBLE);
                                button_move_down.setVisibility(View.INVISIBLE);
                            }
                            //else if (acceX <= 10 && acceZ >= 0 ) {
                            else if (acceZ > 0 && acceZ <= 1) {
                                listButtons.scrollBy(0, 0);
                                listButtons.computeScroll();
                                listButtons.invalidate();
                                //Log.i("Sensor", "scroll Stop");
                                //mLayout.setBackgroundColor(Color.TRANSPARENT);
                                button_move_up.setVisibility(View.INVISIBLE);
                                button_move_down.setVisibility(View.INVISIBLE);
                                buttonClicked = false;
                            } else if (acceZ <= -1) {
                                //Scroll to Bottom
                                listButtons.scrollTo(scrollX, scrollY - velocity);
                                listButtons.computeScroll();
                                listButtons.invalidate();
                                //Log.i("Sensor", "scroll Bottom - vertical");
                                //Log.i("Sensor", "Scroll X: " + scrollX);
                                //Log.i("Sensor", "Scroll Y: " + scrollY);
                                //Log.i("Sensor", "acceX: " + acceX);
                                //Log.i("Sensor", "acceY: " + acceY);
                                //Log.i("Sensor", "acceZ: " + acceZ);
                                //mLayout.setBackgroundColor(Color.BLUE);
                                button_move_up.setVisibility(View.INVISIBLE);
                                button_move_down.setVisibility(View.VISIBLE);
                            }else if(acceZ >= 6){ //smartphoone in horizontal
                                if(acceX < -1){
                                   //Scroll to Top
                                    listButtons.scrollTo(scrollX, scrollY + velocity);
                                    listButtons.computeScroll();
                                    listButtons.invalidate();
                                    //Log.i("Sensor", "scroll Up - horizontal");
                                    //Log.i("Sensor", "acceX: " + acceX);
                                    //Log.i("Sensor", "acceY: " + acceY);
                                    //Log.i("Sensor", "acceZ: " + acceZ);
                                    button_move_up.setVisibility(View.VISIBLE);
                                    button_move_down.setVisibility(View.INVISIBLE);
                                }else if(acceX >= 1 && acceX <=2){
                                    listButtons.scrollBy(0, 0);
                                    listButtons.computeScroll();
                                    listButtons.invalidate();
                                    //Log.i("Sensor", "scroll Stop");
                                    //mLayout.setBackgroundColor(Color.TRANSPARENT);
                                    button_move_up.setVisibility(View.INVISIBLE);
                                    button_move_down.setVisibility(View.INVISIBLE);
                                    buttonClicked = false;
                                }else if(acceX > 2 && acceX <5){
                                    //Scroll to Bottom
                                    listButtons.scrollTo(scrollX, scrollY - velocity);
                                    listButtons.computeScroll();
                                    listButtons.invalidate();
                                    //Log.i("Sensor", "scroll Bottom - horizontal");
                                    //Log.i("Sensor", "acceX: " + acceX);
                                    //Log.i("Sensor", "acceY: " + acceY);
                                   //Log.i("Sensor", "acceZ: " + acceZ);
                                    //mLayout.setBackgroundColor(Color.BLUE);
                                    button_move_up.setVisibility(View.INVISIBLE);
                                    button_move_down.setVisibility(View.VISIBLE);
                                }else if(acceX >=5){
                                    listButtons.scrollBy(0, 0);
                                    listButtons.computeScroll();
                                    listButtons.invalidate();
                                    //Log.i("Sensor", "scroll Stop");
                                    //mLayout.setBackgroundColor(Color.TRANSPARENT);
                                    button_move_up.setVisibility(View.INVISIBLE);
                                    button_move_down.setVisibility(View.INVISIBLE);
                                }
                            }
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
            int count =0;
            JSONArray principalArray = json.getJSONArray(dataSize);
            count = ((dataSize.equals("small")) ? 30: ((dataSize.equals("medium")) ? 60: 120));
            scrollBarLocation.add("All location");
            totalCrimes.put("All crimes",0);

            for(int i = 0; i< principalArray.length(); i++){
                String locationName = principalArray.getJSONObject(i).getString("Location");
                if(!scrollBarLocation.contains(locationName) && scrollBarLocation.size()<count ){
                    scrollBarLocation.add(locationName);
                }

                String crime = principalArray.getJSONObject(i).getString("CrimeType");
                Integer w = totalCrimes.get(crime);
                if(w == null) totalCrimes.put(crime, 1);
                else totalCrimes.put(crime, w + 1);
                totalCrimes = sortHashMapByValues(totalCrimes);

                String period = principalArray.getJSONObject(i).getString("Period");
                Integer z = PeriodCrimes.get(period);
                if(z == null) PeriodCrimes.put(period, 1);
                else PeriodCrimes.put(period, z + 1);


            }

            //Log.i(TAG, "loadDatasort: " + totalCrimes);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public LinkedHashMap<String, Integer> sortHashMapByValues(HashMap<String, Integer> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<String, Integer>sortedMap =
                new LinkedHashMap<>();

        Iterator<Integer> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Integer val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                Integer comp1 = passedMap.get(key);
                Integer comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createScrollBar(int option){
        //LinearLayout listButtons = (LinearLayout) findViewById(R.id.scrollBar);

        if (option == 1){
            listButtons.removeAllViews();
            buttonClicked = false;
            final Button[] dynamic_button = new Button[scrollBarLocation.size()];//scrollBarLocation.size()

            for(int i = 0; i< scrollBarLocation.size(); i++){ //scrollBarLocation.size()
                final int index = i;
                dynamic_button[i] = new Button(this);
                dynamic_button[i].setText(scrollBarLocation.get(i));
                dynamic_button[i].setId(i);
                //newButton.setBackgroundColor(0xFF99D6D6);
                //dynamic_button[i].setFocusableInTouchMode(true);
                if(scrollBarLocation.get(i).equals("All location")){
                    beforeIndex = i;
                    dynamic_button[i].getBackground().setColorFilter(0xFF99D6D6, PorterDuff.Mode.MULTIPLY);
                }
                dynamic_button[i].setTextSize(10);

                listButtons.addView(dynamic_button[i]);
                dynamic_button[i].setOnClickListener(new OnClickListener() {

                    public void onClick(View v) {
                        try {
                            dynamic_button[beforeIndex].getBackground().clearColorFilter();
                            //dynamic_button[beforeIndex].getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
                            AnotherPieChart(((Button)v).getText().toString());
                            pieChart.notifyDataSetChanged();
                            titleChart.setText("Total crimes by location" + " : " + ((Button)v).getText().toString());
                            pieChart.invalidate();
                            beforeIndex = index;
                            dynamic_button[index].getBackground().setColorFilter(0xFF99D6D6, PorterDuff.Mode.MULTIPLY);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(!buttonFinger.isChecked()){
                            button_play.setVisibility(View.VISIBLE);
                            button_pause.setVisibility(View.INVISIBLE);
                            play=false;
                        }
                        buttonClicked = true;
                    }
                });




            }
        }
        else{
            listButtons.removeAllViews();
            buttonClicked = false;
            final Button[] dynamic_button = new Button[totalCrimes.size()];
            int i=0;
            for ( String key : totalCrimes.keySet() ) {
                final String keyValue = key;
                final int index = i;
                dynamic_button[i] = new Button(this);
                dynamic_button[i].setText(key);
                if(key.equals("All crimes")){
                    beforeIndex = i;
                    dynamic_button[i].getBackground().setColorFilter(0xFF99D6D6, PorterDuff.Mode.MULTIPLY);
                }
                dynamic_button[i].setTextSize(10);
                dynamic_button[i].setId(i);
                listButtons.addView(dynamic_button[i]);
                dynamic_button[i].setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        //show only the option selected
                        try{
                            dynamic_button[beforeIndex].getBackground().clearColorFilter();
                            createHorizontalAnotherChart(((Button)v).getText().toString());
                            horizontalBarChart.notifyDataSetChanged();
                            titleChart.setText("Amount of crimes distributed in three periods of the day" );
                            horizontalBarChart.invalidate();
                            beforeIndex = index;
                            dynamic_button[index].getBackground().setColorFilter(0xFF99D6D6, PorterDuff.Mode.MULTIPLY);


                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        buttonClicked = true;
                    }
                });
                i++;
            }
        }
    }

    private void createPieChart() {

        ArrayList<PieEntry> pieEntries= new ArrayList<>();

        for(String i : totalCrimes.keySet()){
            Integer value = totalCrimes.get(i);
            pieEntries.add(new PieEntry( (float) value, i));
        }

        piechart(pieChart, pieEntries);
    }


    private void AnotherPieChart(String filter) throws JSONException {
        HashMap<String, Integer> crimes = new HashMap<String, Integer>(50, 10);
        JSONObject json = loadJsonObjectFromAsset("file.json");
        JSONArray principalArray = json.getJSONArray(dataSize);
        for(int i = 0; i< principalArray.length(); i++) {
            String locationName = principalArray.getJSONObject(i).getString("Location");
            String crimeType = principalArray.getJSONObject(i).getString("CrimeType");

            if (filter.equals(locationName)) {
                Integer w = crimes.get(crimeType);
                if (w == null) crimes.put(crimeType, 1);
                else crimes.put(crimeType, w + 1);
            }
        }

        if(!filter.equals("All location")){
            ArrayList<PieEntry> pieEntries= new ArrayList<>();
            for(String i : crimes.keySet()){
                Integer value = crimes.get(i);
                pieEntries.add(new PieEntry( (float) value, i));
            }
            piechart(pieChart, pieEntries);
        }
        else{
            createPieChart();
        }




    }

    public static void piechart(PieChart pieChart, ArrayList<PieEntry> arrayList){


        PieDataSet pieDataSet =new PieDataSet(arrayList,"");

        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueLinePart1OffsetPercentage(90.f);
        pieDataSet.setValueLinePart1Length(0.5f);
        pieDataSet.setValueLinePart2Length(0.0f);
        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        pieDataSet.setColors (colors);
        pieDataSet.setDrawIcons(false);


        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(11f);
        pieData.setValueTextColor(Color.DKGRAY);
        pieData.setHighlightEnabled(true);

        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);
        Legend legend = pieChart.getLegend();
        legend.setWordWrapEnabled(true);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDrawSliceText(false);
        pieChart.setData(pieData);


    }

    private void createHorizontalChart(){

        ArrayList<String> xAxisLables = new ArrayList();
        ArrayList<BarEntry> values = new ArrayList<>();
        float spaceForBar = 0.5f;
        float barWidth = 0.9f;
        float z =0.1f;
        for(String i : PeriodCrimes.keySet()){
            Integer value = PeriodCrimes.get(i);
            values.add(new BarEntry((float) (((barWidth)/2 + spaceForBar)*z), (float) value));
            xAxisLables.add(value +  " "+ i);
            z++;
        }
        barchart(horizontalBarChart,values,xAxisLables,barWidth);

    }
    private void createHorizontalAnotherChart(String filter) throws JSONException {
        HashMap<String, Integer> periods = new HashMap<String, Integer>(50, 10);
        periods.put("Night", 0);
        periods.put("Morning", 0);
        periods.put("Afternoon", 0);
        JSONObject json = loadJsonObjectFromAsset("file.json");
        JSONArray principalArray = json.getJSONArray(dataSize);
        for(int i = 0; i< principalArray.length(); i++) {
            String period= principalArray.getJSONObject(i).getString("Period");
            String crimeType = principalArray.getJSONObject(i).getString("CrimeType");

            if (filter.equals(crimeType)) {
                Integer w = periods.get(period);
                if (w == null) periods.put(period, 1);
                else periods.put(period, w + 1);
            }


        }

        if(!filter.equals("All crimes")){
           float spaceForBar = 0.5f;
            float barWidth = 0.9f;
            float z =0.1f;
           ArrayList<String> xAxisLables = new ArrayList();
           ArrayList<BarEntry> values = new ArrayList<>();
            for(String i : periods.keySet()){
                 Integer value = periods.get(i);
                 values.add(new BarEntry((float) (((barWidth)/2 + spaceForBar)*z), (float) value));
                 xAxisLables.add(value + " "+ i);
                  z++;
             }
            barchart(horizontalBarChart,values,xAxisLables,barWidth);
        }
        else{
                createHorizontalChart();
        }


    }


    public static void barchart(BarChart barChart, ArrayList<BarEntry> arrayList, final ArrayList<String> xAxisValues, float barWith) {


        BarDataSet barDataSet = new BarDataSet(arrayList, "Periods of the day");
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(barWith);
        barData.setValueTextSize(7f);

        Legend l = barChart.getLegend();
        l.setTextSize(10f);
        l.setFormSize(10f);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(13f);
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setValueFormatter(new   IndexAxisValueFormatter(xAxisValues));
        xAxis.setDrawGridLines(false);

        barChart.setDrawGridBackground(true);
        barChart.setBackgroundColor(Color.TRANSPARENT);
        barChart.setDrawGridBackground(false);
        barChart.getDescription().setEnabled(false);
        barChart.setData(barData);

    }



}

