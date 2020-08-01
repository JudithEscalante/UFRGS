package com.example.second;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.data.Set;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

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
    HorizontalBarChart horizontalBarChart;
    //AnyChartView anyChartView;
    List<String> scrollBarLocation = new ArrayList<>();
    List<String> crimesList = new ArrayList<>();
    HashMap<String, Integer> totalCrimes = new HashMap<String, Integer>(50, 10);
    HashMap<String, Integer> PeriodCrimes = new HashMap<String, Integer>(50, 10);
    LinearLayout listButtons;
    private boolean buttonClicked = false;
    Pie pie;

    //***********************
    private SensorManager sensorManager;
    //private ScrollListener mListener;
    private Sensor accelerometer;
    NestedScrollView mLayout;
    private int velocity = 3;
    private String dataSize;



    /** Called when the activity is first created. */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button menu = (Button) findViewById(R.id.menu);
        menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });

        Button plus = (Button) findViewById(R.id.plus);
        final TextView text = (TextView) findViewById(R.id.velocity);
        String speed = String.valueOf(velocity);
        text.setText(speed);
        plus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                velocity = velocity +2;
                String speed = String.valueOf(velocity);
                text.setText(speed);
            }
        });
        Button minus = (Button) findViewById(R.id.minus);
        minus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                velocity = velocity -2;
                String speed = String.valueOf(velocity);
                text.setText(speed);
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
        mLayout.setOnTouchListener( new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);



        dataSize = getIntent().getStringExtra("option");

        //************* Chart ****************
        pieChart=findViewById(R.id.pieChart);
        horizontalBarChart = findViewById(R.id.horizontalChart);
        //anyChartView = findViewById(R.id.any_chart_view);
        //pie = AnyChart.pie();
        //anyChartView.setVisibility(View.INVISIBLE);

        try {
            loadData();
            createScrollBar(optionLocation);
            createPieChart();
            //PieChartNewVersion();

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
                    pieChart.setVisibility(View.VISIBLE);
                    horizontalBarChart.setVisibility(View.INVISIBLE);
                    createPieChart();

                    pieChart.notifyDataSetChanged();
                    pieChart.invalidate();
                    //PieChartNewVersion();
                    createScrollBar(optionLocation);
                }else {
                    horizontalBarChart.setVisibility(View.VISIBLE);
                    pieChart.setVisibility(View.INVISIBLE);
                    createHorizontalChart();
                    horizontalBarChart.notifyDataSetChanged();
                    horizontalBarChart.invalidate();
                    createScrollBar(optionCrime);
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

       // Log.i("Sensor", "Accel: " + event.values[0]);

        final float acceX = event.values[0];
        final float acceZ = event.values[2];

        final int scrollY = listButtons.getScrollY();
        final int scrollX = listButtons.getScrollX();

        final Button button_move_up = (Button) findViewById(R.id.button_move_up);
        final Button button_move_down = (Button) findViewById(R.id.button_move_down);
        final Button button_tilt = (Button) findViewById(R.id.button_tilt);



        listButtons.post(new Runnable() {
            @Override
            public void run() {
                if(!buttonClicked && button_tilt.isPressed()){
                    if(scrollY<0 || scrollY>2950){//scroll max location 2950    //scroll max crime 264
                        if(scrollY<0){
                            listButtons.scrollTo(scrollX,scrollY+velocity);
                            //mLayout.setBackgroundColor(Color.GREEN);
                            button_move_up.setVisibility(View.VISIBLE);
                            button_move_down.setVisibility(View.INVISIBLE);

                        }
                        RadioButton location = (RadioButton) findViewById(R.id.radioButtonLocation);
                        RadioButton crime = (RadioButton) findViewById(R.id.radioButtonCrime);
                        if(location.isChecked() && scrollY>2950){
                            listButtons.scrollTo(scrollX,scrollY-velocity);
                            //mLayout.setBackgroundColor(Color.BLUE);
                            button_move_up.setVisibility(View.INVISIBLE);
                            button_move_down.setVisibility(View.VISIBLE);
                        }
                        if(crime.isChecked() && scrollY>264){
                            listButtons.scrollTo(scrollX,scrollY-velocity);
                            button_move_up.setVisibility(View.INVISIBLE);
                            button_move_down.setVisibility(View.VISIBLE);
                        }
                    }else{
                        //if(acceX > 0 && acceZ < 10 ) {
                        if(acceZ > 2 ) {
                            //Scroll to Top
                                    listButtons.scrollTo(scrollX,scrollY+velocity);
                                    listButtons.computeScroll();
                                    listButtons.invalidate();
                                    Log.i("Sensor", "scroll Up");
                                    Log.i("Sensor", "Scroll X: " + scrollX);
                                    Log.i("Sensor", "Scroll Y: " + scrollY);
                                    Log.i("Sensor", "velocity: " + velocity);
                                    //mLayout.setBackgroundColor(Color.GREEN);
                                    button_move_up.setVisibility(View.VISIBLE);
                                    button_move_down.setVisibility(View.INVISIBLE);
                        }
                        //else if (acceX <= 10 && acceZ >= 0 ) {
                        else if(acceZ >0 && acceZ <=1){
                            listButtons.scrollBy(0, 0);
                            listButtons.computeScroll();
                            listButtons.invalidate();
                            Log.i("Sensor", "scroll Stop");
                            //mLayout.setBackgroundColor(Color.TRANSPARENT);
                            button_move_up.setVisibility(View.INVISIBLE);
                            button_move_down.setVisibility(View.INVISIBLE);
                            buttonClicked = false;
                        }
                        else if (acceZ <= -1 ) {
                            //Scroll to Bottom
                                    listButtons.scrollTo(scrollX,scrollY-velocity);
                                    listButtons.computeScroll();
                                    listButtons.invalidate();
                                    Log.i("Sensor", "scroll Bottom");
                                    Log.i("Sensor", "Scroll X: " + scrollX);
                                    Log.i("Sensor", "Scroll Y: " + scrollY);
                                    //mLayout.setBackgroundColor(Color.BLUE);
                                    button_move_up.setVisibility(View.INVISIBLE);
                                    button_move_down.setVisibility(View.VISIBLE);
                        }
                    }
                }else{
                            listButtons.scrollBy(0, 0);
                            listButtons.computeScroll();
                            listButtons.invalidate();
                            Log.i("Sensor", "scroll Stop");
                            //mLayout.setBackgroundColor(Color.TRANSPARENT);
                            button_move_up.setVisibility(View.INVISIBLE);
                            button_move_down.setVisibility(View.INVISIBLE);
                            buttonClicked = false;
                }
            }
        });




        //print values on screen
     /*   TextView tvX= (TextView)findViewById(R.id.x_axis);
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
        } */
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
            JSONArray principalArray = json.getJSONArray(dataSize);
            for(int i = 0; i< principalArray.length(); i++){
                String locationName = principalArray.getJSONObject(i).getString("Location");
                if(!scrollBarLocation.contains(locationName)){
                    scrollBarLocation.add(locationName);
                }

                String crime = principalArray.getJSONObject(i).getString("CrimeType");
                Integer w = totalCrimes.get(crime);
                if(w == null) totalCrimes.put(crime, 1);
                else totalCrimes.put(crime, w + 1);

                String period = principalArray.getJSONObject(i).getString("Period");
                Integer z = PeriodCrimes.get(period);
                if(z == null) PeriodCrimes.put(period, 1);
                else PeriodCrimes.put(period, z + 1);


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
                dynamic_button[i].setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        try {
                            //otherPieChartNewVersion(((Button)v).getText().toString());
                            AnotherPieChart(((Button)v).getText().toString());
                            pieChart.notifyDataSetChanged();
                            pieChart.invalidate();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
                dynamic_button[i].setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        //show only the option selected
                        try{
                            createHorizontalAnotherChart(((Button)v).getText().toString());
                            horizontalBarChart.notifyDataSetChanged();
                            horizontalBarChart.invalidate();

                           /* Description description = new Description();
                            description.setText("Crime type");
                            pieChart.setDescription(description);
                            ArrayList<PieEntry> pieEntries= new ArrayList<>();
                            pieEntries.add(new PieEntry( (float) totalCrimes.get(keyValue), keyValue));
                            PieDataSet pieDataSet =new PieDataSet(pieEntries,"text");
                            pieDataSet.setColors (ColorTemplate.COLORFUL_COLORS);
                            PieData pieData = new PieData(pieDataSet);
                            pieChart.setData(pieData);
                            pieChart.notifyDataSetChanged();
                            pieChart.invalidate();*/
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

        ArrayList<PieEntry> pieEntries= new ArrayList<>();

        for(String i : crimes.keySet()){
            Integer value = crimes.get(i);
            pieEntries.add(new PieEntry( (float) value, i));
        }

        piechart(pieChart, pieEntries);

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

        float spaceForBar = 0.5f;
        float barWidth = 0.9f;
        ArrayList<String> xAxisLables = new ArrayList();
        ArrayList<BarEntry> values = new ArrayList<>();
        float z =0.1f;
        for(String i : periods.keySet()){
            Integer value = periods.get(i);
            values.add(new BarEntry((float) (((barWidth)/2 + spaceForBar)*z), (float) value));
            xAxisLables.add(value + " "+ i);
            z++;
        }
        barchart(horizontalBarChart,values,xAxisLables,barWidth);


    }


    public static void barchart(BarChart barChart, ArrayList<BarEntry> arrayList, final ArrayList<String> xAxisValues, float barWith) {


        BarDataSet barDataSet = new BarDataSet(arrayList, "Number of crimes in different periods of the day");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

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


    /*private void PieChartNewVersion(){
        anyChartView.clear();

        List<DataEntry> data = new ArrayList<>();

        for(String i : totalCrimes.keySet()){
            Integer value = totalCrimes.get(i);
            data.add(new ValueDataEntry( i,  value));
        }

        pie.data(data);

        pie.title("Number of crimes for each location");

        pie.labels().position("outside");

        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("Retail channels")
                .padding(0d, 0d, 10d, 0d);

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        anyChartView.setChart(pie);

    }


    private void otherPieChartNewVersion(String filter) throws JSONException {
        anyChartView.clear();
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

        List<DataEntry> data = new ArrayList<>();

        for(String i : crimes.keySet()){
            Integer value = crimes.get(i);
            data.add(new ValueDataEntry( i,  value));
        }

        pie.data(data);

        pie.title("Number of crimes for each location");

        pie.labels().position("outside");

        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("Retail channels")
                .padding(0d, 0d, 10d, 0d);

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        anyChartView.setChart(pie);


    }*/

}
