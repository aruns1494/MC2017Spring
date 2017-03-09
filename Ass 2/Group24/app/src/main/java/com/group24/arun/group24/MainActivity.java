package com.group24.arun.group24;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.*;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.InputMismatchException;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    final Handler graphHandler = new Handler();
    Thread t,t1;
    boolean flag=false;
    LineGraphSeries<DataPoint> series;
    final float[] val = new float[50];
    final float[] random_val=new float[4];
    boolean count=false;
    EditText idField;
    EditText patientNameField;
    EditText ageField;
    RadioGroup genderField;
    RadioButton genderSelected;
    //Button submit;
    private SensorManager sensorManager;
    private Sensor sensor;
    private double ax,ay,az;
    private long timestamp;
    Context context;
    Helper db = null;
    SQLiteDatabase sqldb =null;
    String tableName = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        db = new Helper(context);
        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        idField=(EditText) findViewById(R.id.id);
        patientNameField=(EditText) findViewById(R.id.patientName);
        ageField=(EditText) findViewById(R.id.age);
        genderField=(RadioGroup) findViewById(R.id.gender);
        sqldb=db.getWritableDatabase();
        for (int i = 0; i < 12; i++) {
            Random r = new Random();
            val[i] = r.nextFloat();
        }
        for (int i = 0; i < 4; i++) {
            Random r = new Random();
            random_val[i] = r.nextFloat();
        }
        Button button = (Button) findViewById(R.id.start);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getFormValues(v);
                flag=true;
            }
        });
        Button stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    flag=false;
                    count=false;
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
                GraphView layout = (GraphView) findViewById(R.id.graph);
                layout.removeAllSeries();
            }
        });
    }

    public void onResume() {
        super.onResume();
        final GraphView graph=(GraphView) findViewById(R.id.graph);
        graph.setTitle("X axis : Timestamp Y axis: Axes of accelerometer");
        graph.removeAllSeries();
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                graph.removeAllSeries();
                if(flag==true) {
                    String result="";
                    if(tableName.isEmpty()==false){
                        result=getTop10();
                        Toast.makeText(context,result,Toast.LENGTH_LONG).show();
                        String[] data = result.split("\n");
                        System.out.print(data);
                        if(data.length>=1)
                        {
                            try {
                                DataPoint[] dpx = new DataPoint[data.length];
                                DataPoint[] dpy = new DataPoint[data.length];
                                DataPoint[] dpz = new DataPoint[data.length];
                                int ct = 0;
                                for (int j = data.length - 1; j >= 0; j--) {
                                    String i = data[j];
                                    String[] temp = i.split(" ");
                                    dpx[ct] = new DataPoint(Long.parseLong(temp[0]), Double.parseDouble(temp[1]));
                                    dpy[ct] = new DataPoint(Long.parseLong(temp[0]), Double.parseDouble(temp[2]));
                                    dpz[ct] = new DataPoint(Long.parseLong(temp[0]), Double.parseDouble(temp[3]));
                                    ct++;
                                }
                            series = new LineGraphSeries<DataPoint>(dpx);
                            series.setColor(Color.BLACK);
                            graph.addSeries(series);
                            series = new LineGraphSeries<DataPoint>(dpy);
                            series.setColor(Color.RED);
                            graph.addSeries(series);
                            series = new LineGraphSeries<DataPoint>(dpz);
                            series.setColor(Color.GREEN);
                            graph.addSeries(series);
                            graph.getViewport().setXAxisBoundsManual(true);
                            Long min=Long.parseLong(data[data.length-1].split(" ")[0]);
                            Long max=Long.parseLong(data[0].split(" ")[0]);
                            graph.getViewport().setMaxX(max);
                            graph.getViewport().setMinX(min);

                            } catch(Exception e) {
                                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                else if (flag==false){
                    for(int i=0;i<10;i++){
                        val[i]=0;
                    }
                    graph.removeAllSeries();
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMaxX(10);
                    graph.getViewport().setMinX(-10);

                    graph.getViewport().setYAxisBoundsManual(true);
                    graph.getViewport().setMaxY(10);
                    graph.getViewport().setMinY(-10);
                }
                graphHandler.postDelayed(this, 1000);
            }
        });
        t.start();}
    //}

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Display mDisplay = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;

        switch (mDisplay.getRotation()) {
            case Surface.ROTATION_0:
                ax = event.values[0];
                ay = event.values[1];
                break;
            case Surface.ROTATION_90:
                ax = -event.values[1];
                ay = event.values[0];
                break;
            case Surface.ROTATION_180:
                ax = -event.values[0];
                ay = -event.values[1];
                break;
            case Surface.ROTATION_270:
                ax = event.values[1];
                ay = -event.values[0];
                break;
        }
        az = event.values[2];
        timestamp = event.timestamp;
    }

    //Added Input validaiton and thread to insert into DB.
    public String getTop10(){
        sqldb=db.getReadableDatabase();
        String query="SELECT * FROM "+tableName+" order by timestamp desc LIMIT 10";
        String[] columns=new String[]{"timeStamp","xValue","yValue","zValue"};
        Cursor cursor=sqldb.query(tableName,columns,null,null,null,null,"timestamp desc","10");
        String result="";
        int timestamp_CONTENT=cursor.getColumnIndex("timestamp");
        int x_CONTENT=cursor.getColumnIndex("xValue");
        int y_CONTENT=cursor.getColumnIndex("yValue");
        int z_CONTENT=cursor.getColumnIndex("zValue");
        for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
            result = result + cursor.getString(timestamp_CONTENT) + " " + cursor.getString(x_CONTENT) + " " + cursor.getString(y_CONTENT) + " " + cursor.getString(z_CONTENT) + "\n";
        }
        return result;
    }
    public void getFormValues(View v){
        String id=idField.getText().toString();
        String name=patientNameField.getText().toString();
        String age=ageField.getText().toString();
        int selectedRadio=genderField.getCheckedRadioButtonId();
        genderSelected=(RadioButton) findViewById(selectedRadio);
        String gender=genderSelected.getText().toString();
        SQLiteDatabase sqldb=db.getWritableDatabase();
        try{
            if(id.isEmpty() || name.isEmpty() || age.isEmpty() || gender.isEmpty()){
                throw new InputMismatchException();
            }
            Integer.parseInt(id);
            Integer.parseInt(age);
            boolean fl=true;
            try{
                Integer.parseInt(name.charAt(0)+"");
                fl=false;
            }catch (Exception e){
                fl=true;
            }
            if(fl==true)
                createTable(id,name,age,gender);
            else
                Toast.makeText(this, "Wrong input", Toast.LENGTH_LONG).show();
            Toast.makeText(this, name+'_'+id+'_'+age+'_'+gender, Toast.LENGTH_LONG).show();
            createTable(id,name,age,gender);
            t1=new Thread(new Runnable(){
                public void run(){

                    AccelerometerData ar;
                    try{
                        while(true){
                            Long tsLong = System.currentTimeMillis()/1000;
                            String temp = String.valueOf(tsLong);
                            ar=new AccelerometerData(temp,ax,ay,az);
                            db.addDataToTable(ar,tableName);
                            Thread.sleep(1000);
                        }}catch (InterruptedException e){

                    }
                }
            });
            t1.start();
        }catch(InputMismatchException ime){
            Toast.makeText(this,"Wrong input",Toast.LENGTH_LONG).show();
        }
    }

    public void createTable(String id, String name,String age,String gender){

        sqldb=db.getWritableDatabase();
        tableName=name+'_'+id+'_'+age+'_'+gender;
        sqldb.execSQL("CREATE TABLE IF NOT EXISTS "+tableName+"(timestamp varchar(10) primary key,xValue float, yValue float, zValue float)");
    }

    public void uploadDatabase(View view){
        String[] s= new String[1];
        UploadWebService uws = new UploadWebService(this);
        uws.execute(s);
    }

    public void downloadDatabase(View view){
        String[] s=new String[1];
        DownloadWeb dw=new DownloadWeb(this);
        dw.execute(s);
    }

}