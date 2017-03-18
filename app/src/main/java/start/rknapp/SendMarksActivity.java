package start.rknapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class SendMarksActivity extends AppCompatActivity {
    private static URL url;
    private String serverResp = "Server Error";
    private boolean serverCompleted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //noinspection ConstantConditions
    protected void onStart(){
        super.onStart();
        int i = 0;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String toSend = "getsysname";

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);

                    PrintWriter sender = new PrintWriter(urlConnection.getOutputStream());
                    sender.write(toSend);
                    sender.close();

                    Scanner reader = new Scanner(urlConnection.getInputStream());
                    serverResp = "Server Error";
                    while (reader.hasNextLine()) {
                        serverResp = reader.nextLine();
                    }
                    serverCompleted = true;
                    urlConnection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Thread.interrupted();
            }
        });
        thread.start();
        while (!serverCompleted && i < 100) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
        if (serverCompleted)  getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        TextView text = (TextView) findViewById(R.id.response);
        text.setTextSize(40);
        text.setText(serverResp);

        thread.interrupt();
    }

    public static void setUrl(String newUrl){
        try {
            url = new URL(newUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static URL isUrlSet(){
        return url;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
