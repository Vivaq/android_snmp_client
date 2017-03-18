package start.rknapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity
         implements NavigationView.OnNavigationItemSelectedListener {
    private View view;
    private String serverResp = "Server Error", command;
    private boolean serverCompleted = false;
    JSONObject jsonResp;
    public static String url_prefix;

    private int[] marks = new int[3];

    private final static String[] INTERNET_PERMISSIONS = {
            Manifest.permission.INTERNET
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        url_prefix = "http://192.168.1.177:8080/android/get/";

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
     }

    protected void onStart()  {
        super.onStart();
        ActivityCompat.requestPermissions(this, INTERNET_PERMISSIONS, 1);
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(this, ShowOptionsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_system_info){
            getServerResponse("systemName");
            if (!serverCompleted) return false;
            try {
                showAlert("Nazwa systemu", jsonResp.getString("value"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            serverCompleted = false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        View windowForButtons = findViewById(R.id.frame);
        ViewGroup parent = (ViewGroup) windowForButtons.getParent();
        if(view != null) {
            parent.removeView(view);
        }
        switch (id) {
            case R.id.op1:
                view = getLayoutInflater().inflate(R.layout.system_info, parent, false);
                parent.addView(view, parent.indexOfChild(windowForButtons));

                getServerResponse("Interfaces");
                if (!serverCompleted) return false;

                try {

                    TableLayout tableLayout = (TableLayout) findViewById(R.id.interfaces);
                    TableRow tableRow = new TableRow(this);
                    int columns = jsonResp.getJSONArray("columnsNames").length();
                    for (int i = 0; i < columns; i++){
                        TextView tv = new TextView(this);
                        tv.setText(jsonResp.getJSONArray("columnsNames").getString(i));
                        tv.setPadding(20, 20, 20, 20);
                        tv.setTextSize(16);
                        tv.setTypeface(null, Typeface.BOLD);
                        tableRow.addView(tv);
                        tableRow.setBackground(getDrawable(R.drawable.cell_shape));
                    }
                    tableLayout.addView(tableRow, 0);
                    JSONArray jsonVal = jsonResp.getJSONArray("values");
                    for (int i = 0; i < jsonVal.length(); i++) {
                        JSONArray jsonFields = jsonVal.getJSONObject(i).getJSONArray("fields");
                        tableRow = new TableRow(this);
                        for (int j = 0; j < columns; j++){
                            TextView tv = new TextView(this);
                            tv.setText(jsonFields.getString(j));
                            tv.setPadding(20, 20, 20, 20);
                            tv.setTextSize(12);
                            tableRow.addView(tv);
                        }
                        tableLayout.addView(tableRow);
                        serverCompleted = false;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.op2:
                view = getLayoutInflater().inflate(R.layout.tcp_connections, parent, false);
                parent.addView(view, parent.indexOfChild(windowForButtons));

                getServerResponse("TcpConnections");
                if (!serverCompleted) return false;
                try {

                    TableLayout tableLayout = (TableLayout) findViewById(R.id.tcpTable);
                    TableRow tableRow = new TableRow(this);
                    int columns = jsonResp.getJSONArray("columnsNames").length();

                    for (int i = 0; i<columns; i++){
                        TextView tv = new TextView(this);
                        tv.setText(jsonResp.getJSONArray("columnsNames").getString(i));
                        tv.setPadding(20, 20, 20, 20);
                        tv.setTextSize(16);
                        tv.setTypeface(null, Typeface.BOLD);
                        tableRow.addView(tv);
                        tableRow.setBackground(getDrawable(R.drawable.cell_shape));
                    }
                    tableLayout.addView(tableRow, 0);
                    JSONArray jsonVal = jsonResp.getJSONArray("values");
                    for (int i = 0; i < jsonVal.length(); i++) {
                        JSONArray jsonFields = jsonVal.getJSONObject(i).getJSONArray("fields");
                        tableRow = new TableRow(this);
                        for (int j = 0; j < columns; j++){
                            TextView tv = new TextView(this);
                            tv.setText(jsonFields.getString(j));
                            tv.setPadding(20, 20, 20, 20);
                            tv.setTextSize(12);
                            tableRow.addView(tv);
                        }
                        tableLayout.addView(tableRow);
                        serverCompleted = false;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.op3:
                view = getLayoutInflater().inflate(R.layout.udp_ports, parent, false);
                parent.addView(view, parent.indexOfChild(windowForButtons));

                getServerResponse("UdpOpenPorts");
                if (!serverCompleted) return false;

                try {

                    TableLayout tableLayout = (TableLayout) findViewById(R.id.udpTable);
                    TableRow tableRow = new TableRow(this);
                    int columns = jsonResp.getJSONArray("columnsNames").length();

                    for (int i = 0; i < columns; i++){
                        TextView tv = new TextView(this);
                        tv.setText(jsonResp.getJSONArray("columnsNames").getString(i));
                        tv.setPadding(20, 20, -220*i + 500, 20);
                        tv.setTextSize(16);
                        tv.setTypeface(null, Typeface.BOLD);
                        tableRow.addView(tv);
                        tableRow.setBackground(getDrawable(R.drawable.cell_shape));
                    }
                    tableLayout.addView(tableRow, 0);
                    JSONArray jsonVal = jsonResp.getJSONArray("values");
                    for (int i = 0; i < jsonVal.length(); i++) {
                        JSONArray jsonFields = jsonVal.getJSONObject(i).getJSONArray("fields");
                        tableRow = new TableRow(this);
                        for (int j = 0; j < columns; j++){
                            TextView tv = new TextView(this);
                            tv.setText(jsonFields.getString(j));
                            tv.setPadding(20, 20, 20, 20);
                            tv.setTextSize(12);
                            tableRow.addView(tv);
                        }
                        tableLayout.addView(tableRow);
                        serverCompleted = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_view);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getServerResponse(String c){
        command = c;
        int i = 0;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection urlConnection = (HttpURLConnection) new URL(url_prefix + command).openConnection();

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
                try {
                    jsonResp = new JSONObject(serverResp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        if (serverCompleted)  {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        else {
            showAlert("Uwaga", "Blad serwera");
            serverCompleted = false;
        }
    }

    public static void setUrl(String new_url){
        url_prefix = new_url;
    }

    private void showAlert(String title, String text){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(text);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
