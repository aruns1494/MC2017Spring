package com.example.rajan.group24;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.Series;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements Runnable{

    private Random random=new Random();
    private static float xLast=0;
    GraphView graph;
    Button start,pause;
    private boolean startFlag=false,stopFlag=false;
    private LineGraphSeries<DataPoint> series;
    private Thread thread;
    private EditText patientId;
    private EditText patientName;
    private EditText patientAge;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>();
        start=(Button)findViewById(R.id.run);
        patientId = (EditText) findViewById(R.id.patientIDtext);
        patientName = (EditText) findViewById(R.id.PatientNametext);
        patientAge = (EditText) findViewById(R.id.Agetext);
        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Time in ms");
        gridLabel.setVerticalAxisTitle("Amplitude in mV");


        start.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {

                if (patientId.getText().toString().length()> 0 && patientName.getText().toString().length()> 0 && patientAge.getText().toString().length()> 0) {

                    if (startFlag == false) {
                        Viewport viewport = graph.getViewport();
                        viewport.setYAxisBoundsManual(true);
                        viewport.setMinX(0);
                        viewport.setMaxY(10);
                        graph.addSeries(series);
                        viewport.setScrollable(false);
                        stopFlag = false;
                        run();
                        startFlag = true;

                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please fill the data",Toast.LENGTH_SHORT).show();

                }
            }
        });
        //Handle multiple clicks to pause and resume
        pause=(Button)findViewById(R.id.stop);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopFlag=true;
                startFlag=false;
                graph.removeAllSeries();
            }
        });
    }

    private void addEntry()
    {
        xLast=xLast+0.2f;
        series.appendData(new DataPoint(xLast,random.nextInt(8)+1),true,80);
    }



    @Override

    public void run() {
        for (int i = 0; i <= 50 && stopFlag==false; i++) {
            addEntry();
        }
        if(stopFlag==false) {
            graph.removeAllSeries();
            graph.addSeries(series);
            graph.postDelayed(this, 500);
        }
    }
}

