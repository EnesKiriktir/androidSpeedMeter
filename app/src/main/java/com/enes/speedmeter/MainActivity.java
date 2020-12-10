package com.enes.speedmeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.github.anastr.speedviewlib.SpeedView;

import java.math.BigDecimal;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.SpeedTestTask;
import fr.bmartel.speedtest.inter.IRepeatListener;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class MainActivity extends AppCompatActivity {



    SpeedView speedView;
    Button buttonStart, buttonQuit;
    TextView txtResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speedView = findViewById(R.id.speedView);
        buttonStart = findViewById(R.id.button_start);
        txtResult = findViewById(R.id.txt_test_result);
        buttonQuit = findViewById(R.id.button_quit);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpeedTestTask speedTestTask =new SpeedTestTask();
                speedTestTask.execute();
            }
        });


        buttonQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
               System.exit(0);
            }
        });




    }

    public class SpeedTestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            SpeedTestSocket speedTestSocket = new SpeedTestSocket();

            // add a listener to wait for speedtest completion and progress
            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                @Override
                public void onCompletion(final SpeedTestReport report) {
                    // called when download/upload is finished
                    Log.v("speedtest", "[COMPLETED] rate in octet/s : " + report.getTransferRateOctet());
                    Log.v("speedtest", "[COMPLETED] rate in bit/s   : " + report.getTransferRateBit());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BigDecimal bg = report.getTransferRateBit();
                            final double big = bg.doubleValue()*Math.pow(10.0,-6.0);

                            txtResult.setText("Your download speed: "+String.valueOf(big).substring(0,5)+" Mbps");

                        }
                    });
                }

                @Override
                public void onError(SpeedTestError speedTestError, final String errorMessage) {
                    // called when a download/upload error occur
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           txtResult.setText("Error occured! Please check your internet connection.Error message :"+errorMessage);
                        }
                    });

                }

                @Override
                public void onProgress(float percent, final SpeedTestReport report) {
                    // called to notify download/upload progress
                    Log.v("speedtest", "[PROGRESS] progress : " + percent + "%");
                    Log.v("speedtest", "[PROGRESS] rate in octet/s : " + report.getTransferRateOctet());
                    Log.v("speedtest", "[PROGRESS] rate in bit/s   : " + report.getTransferRateBit());
                    BigDecimal bg = report.getTransferRateBit();
                    final double big = bg.doubleValue()*Math.pow(10.0,-6.0);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            speedView.speedTo((int)big);
                        }
                    });
                }
            });

            speedTestSocket.startDownload("http://ipv4.ikoula.testdebit.info/1M.iso");
            //speedTestSocket.startUpload("http://ipv4.ikoula.testdebit.info/", 1000000);


            return null;
        }
    }

}



