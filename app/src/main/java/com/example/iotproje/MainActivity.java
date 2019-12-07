package com.example.iotproje;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView bakiye;
    Button btn;
    public int eskiborc,bakiyem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bakiye = (TextView) findViewById(R.id.bakiye);
        final EditText borc = (EditText) findViewById(R.id.borc);
        btn = (Button) findViewById(R.id.button);
        bakiye.setText(bakiye());
        new BackgroundTask().execute((Void) null);

        btn.setOnClickListener(new View.OnClickListener() {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(borc.getText().toString())> Integer.parseInt(bakiye())){
                    builder.setTitle("Hata");
                    builder.setMessage("Borcunuzdan fazla ödeme yapamazsınız.");
                    builder.setPositiveButton("Tamam", null);
                    builder.show();
                }
                else{
                    bakiye_dusur(Integer.parseInt(borc.getText().toString()));
                    bakiye.setText(bakiye());
                    eskiborc = Integer.parseInt(bakiye());
                    builder.setTitle("Ödeme İslemi");
                    builder.setMessage("Ödemeniz başarıyla gerçekleşmiştir.");
                    builder.setPositiveButton("Tamam", null);
                    builder.show();
                    borc.setText("");
                }
            }
        });

    }

    public String bakiye() {
        HttpURLConnection connection = null;
        BufferedReader br = null;
        String link = "http://ahmetmanga.com/iot/index.php?user_id=1";

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            URL url = new URL(link); // url parametresi
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream is = connection.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            String satir;
            String dosya = "";
            while ((satir = br.readLine()) != null) {
                dosya += satir;
            }
            Log.d("sa", dosya);
            return dosya;


        } catch (Exception e) {
            e.printStackTrace();
            String hata = "500";
            return hata;
        }

    }

    public void bakiye_dusur(int odeme) {
        HttpURLConnection connection = null;
        BufferedReader br = null;
        String link = "http://ahmetmanga.com/iot/index.php?case=decrease_balance&user_id=1&m=" + odeme;

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            URL url = new URL(link); // url parametresi
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream is = connection.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class BackgroundTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            eskiborc= Integer.parseInt(bakiye());
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {
            new Thread(new Runnable() {  // Yeni bir Thread (iş parcacığı) oluşturuyorum.
                @Override
                public void run() { // Thread'ım başladığında bitmemesi için while
                    // ile sonsuz döngüye soktum. senaryo gereği
                    while (1 == 1) {
                        try {
                            Thread.sleep(3000); // Her döngümde Thread'ımı 15000 ms uyutuyorum.
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // ve 1.5 saniyem dolduktan sonra bildirimimi basıyorum.

                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID", "YOUR_CHANNEL_NAME", NotificationManager.IMPORTANCE_DEFAULT);
                            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
                            mNotificationManager.createNotificationChannel(channel);
                        }

                        if (Integer.parseInt(bakiye()) > eskiborc ) {
                            eskiborc = Integer.parseInt(bakiye());
                            bakiye.setText(bakiye());
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                                    .setSmallIcon(R.drawable.ic_launcher_background) // notification icon
                                    .setContentTitle("Ödeme") // title for notification
                                    .setContentText("Ödemeniz gerçekleşti.Bakiyeniz="+eskiborc)// message for notification
                                    .setAutoCancel(true); // clear notification after click

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);


                            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(pi);
                            mNotificationManager.notify(0, mBuilder.build());
                        }

                    }
                }
            }).start();  // burada Thread'ımı başlatıyorum.
            return null;
        }

    }
}


