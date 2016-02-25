package com.tyler.phpmysqltest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    EditText name, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name = (EditText) findViewById(R.id.name);
        password = (EditText) findViewById(R.id.password);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEditTextEmpty(name) || isEditTextEmpty(password)) {
                    Snackbar.make(view, "Please fill in all fields", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    Post post = new Post(name.getText().toString(), password.getText().toString());
                    post.execute(0);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isEditTextEmpty(EditText content) {
        String userinput = content.getText().toString();
        return userinput.isEmpty();
    }

    public class Post extends AsyncTask {
        public static final String KEY_NAME = "name";
        public static final String KEY_PASSWORD = "password";
        String name, password;
        TextView responseView;
        String response = null;

        Post(String name, String password)
        {
            this.name = name;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            responseView = (TextView) findViewById(R.id.response);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                String urlParameters = KEY_NAME + "=" + name + "&" +
                        KEY_PASSWORD + "=" + password;
                byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
                int postDataLength = postData.length;
                String request = "http://php-tsorenson.rhcloud.com/androidPost.php";
                URL url = new URL(request);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("charset", "utf-8");
                conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                conn.setUseCaches(false);
                try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                    wr.write(postData);
                }

                InputStream in = new BufferedInputStream(conn.getInputStream());
                byte[] contents = new byte[1024];
                int bytesRead=0;
                while( (bytesRead = in.read(contents)) != -1){
                    response += new String(contents, 0, bytesRead);
                }
                Log.d("Retrieved", response);
                conn.disconnect();
                return response;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            responseView.setText(response);
        }
    }



}