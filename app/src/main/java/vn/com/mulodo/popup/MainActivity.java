package vn.com.mulodo.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpConnection;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener {

    Button btn;
    String strKeyword, strServer;
    EditText keyword;
    ListView listView;
    Spinner server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // alert button
        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.list);
        server = (Spinner) findViewById(R.id.server);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.server, android.R.layout.simple_spinner_dropdown_item);
        server.setAdapter(adapter);
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


    @Override
    public void onClick(View v) {
        keyword = (EditText) findViewById(R.id.keyword);

        //str = "http://j.ginggong.com/jOut.ashx?k=" + keyword.getText().toString() + "&h=&code=test_code";
        strKeyword = keyword.getText().toString();
        getMusic list = new getMusic();

        strServer = "";

        long serverId = (null == server) ? 0 : server.getSelectedItemId();

        if (serverId > 0) {
            strServer = server.getItemAtPosition((int) serverId).toString();
        }


        //String[] urls = {"http://download 1", "http://download 2"};
        //String urls = "http://download3"; // = param[0]
        list.execute(new String[]{strKeyword, strServer});
    }

    private class getMusic extends AsyncTask<String, Void, List<ListMusic>> {



//        private getMusic(String url) {
//            this.url = url;
//        }

        @Override
        protected List<ListMusic> doInBackground(String... params) {


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            List<ListMusic> listMusic = new ArrayList<ListMusic>();

            try {
                String keyWord = "";
                String server = "";
                keyWord = params[0];
                if (params.length > 0) {
                    server = params[1];
                }
                Uri url = new Uri.Builder()
                        .scheme("http")
                        .authority("j.ginggong.com")
                        .appendPath("jOut.ashx")
                        .appendQueryParameter("k", keyWord)
                        .appendQueryParameter("h", server)
                        .appendQueryParameter("code", "test_code")
                        .build();
                Log.i("POPUP", url.toString());
                urlConnection = (HttpURLConnection) new URL(url.toString()).openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                // Read input stream into a string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    Log.i("Popup", "Null");
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }

                if(buffer.length() == 0) {
                    Log.i("Popup", "buffer is null");
                    return null;
                }
                String jsonString = buffer.toString();
                Log.i("Popup", jsonString);

                JSONArray rootJson = new JSONArray(jsonString);
                String title, artist;


                for (int i = 0; i < rootJson.length(); i++) {
                    JSONObject songNodes = rootJson.getJSONObject(i);
                    title = songNodes.getString("Title");
                    artist = songNodes.getString("Artist");

                    listMusic.add(new ListMusic(title, artist));
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
                return listMusic;
            }
        }

        @Override
        protected void onPostExecute(final List<ListMusic> result) {
            Log.i("Popup", "onPostExecute");
            if(result == null) {
                Log.i("Popup", "Null");
                return;
            }
            ArrayAdapter adapter = new MusicAdapter(MainActivity.this, R.layout.listview_row, result);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(MainActivity.this, result.get((int) id).getTitle(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }
}
