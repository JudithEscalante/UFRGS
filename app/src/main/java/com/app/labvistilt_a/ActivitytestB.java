package com.app.labvistilt;

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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class ActivitytestB extends AppCompatActivity implements com.app.labvistilt.BoxDialogFragment.NoticeDialogListener {

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
    private int velocity = 1;
    private String dataSize;
    private int maxScrollY = 2950;

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
        setContentView(R.layout.activity_activitytest_b);

        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("Time: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        startChronometer();

        Button menu = (Button) findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });

        dataSize = "small";
        final Button next = (Button) findViewById(R.id.next);
        final Button back = (Button) findViewById(R.id.back);
        final TextView text = (TextView) findViewById(R.id.testType);
        back.setVisibility(View.INVISIBLE);
        text.setText("Test B : "+dataSize);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollBarLocation.clear();
                totalCrimes.clear();
                PeriodCrimes.clear();
                pauseChronometer();
                // Create and show the dialog.
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                BoxDialogFragment newFragment = new BoxDialogFragment ().newInstance(chronometer.getText().toString());
                newFragment.show(ft, "dialog");
                if(testId>=1 && testId <=3){
                    back.setVisibility(View.VISIBLE);
                    testId = testId +1;
                    dataSize = showTest(testId);
                    listButtons.scrollTo(0 ,0);
                    text.setText("Test B : "+dataSize);
                    loadScrollBar();
                    checkedOnRadioButton();
                    pieChart.invalidate();
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollBarLocation.clear();
                totalCrimes.clear();
                PeriodCrimes.clear();
                if(testId>1 && testId <=3) {
                    next.setVisibility(View.VISIBLE);
                    testId = testId - 1;
                    dataSize = showTest(testId);
                    listButtons.scrollTo(0, 0);
                    text.setText("Test B : " + dataSize);
                    loadScrollBar();
                    checkedOnRadioButton();
                }
                if(testId==1){
                    back.setVisibility(View.INVISIBLE);
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
        buttonFinger.setOnClickListener(new View.OnClickListener() {
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



      /*  next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                // Create and show the dialog.
                BoxDialogFragment newFragment = new BoxDialogFragment ();
                newFragment.show(ft, "dialog");
            }
        });*/

        mInitialized = false;
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        listButtons = (LinearLayout) findViewById(R.id.scrollBar);
        mLayout = (NestedScrollView) findViewById(R.id.scrollView);
       /* mLayout.setOnTouchListener( new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        }); */

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
        Log.i("Sensor", "TestId: " + testId);
        if(testId==4){
            openNewActivity();
        }


    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        final TextView text = (TextView) findViewById(R.id.testType);
        startChronometer();
        scrollBarLocation.clear();
        totalCrimes.clear();
        PeriodCrimes.clear();
        if(testId>1 && testId <3) {
            testId = testId - 1;
            dataSize = showTest(testId);
            text.setText("Test B : " + dataSize);
            listButtons.scrollTo(0, 0);
            loadScrollBar();
            checkedOnRadioButton();
        }else if(testId==3){
            dataSize = showTest(testId);
            text.setText("Test B : " + dataSize);
            listButtons.scrollTo(0, 0);
            loadScrollBar();
            checkedOnRadioButton();
        }
        Log.i("Sensor", "TestId: " + testId);
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
        Intent intent = new Intent(this, com.app.labvistilt.Menu.class);
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

            if(dataSize.equals("small")) maxScrollY = 3300;
            if(dataSize.equals("medium")) maxScrollY = 7020;
            if(dataSize.equals("large")) maxScrollY = 14646;

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
        if(test==2) st = "medium";
        if(test==3) st = "large";
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
                dynamic_button[i].setOnClickListener(new View.OnClickListener() {

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
                dynamic_button[i].setOnClickListener(new View.OnClickListener() {
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
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        xAxis.setDrawGridLines(false);

        barChart.setDrawGridBackground(true);
        barChart.setBackgroundColor(Color.TRANSPARENT);
        barChart.setDrawGridBackground(false);
        barChart.getDescription().setEnabled(false);
        barChart.setData(barData);

    }

}