package vn.com.mulodo.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
    List<ListMusic> allListMusic;
    FrameLayout musicPlayer;
    ImageButton btnPlay;
    MediaPlayer mediaPlayer;
    Boolean stopPlay = false;
    int songLength = 0;

    ImageButton prev;
    ImageButton next;

    int prevId = 0;
    int nextId = 0;

    TextView titleFrame;
    TextView singerFrame;
    ImageView avatarFrame;

    private Context context = MainActivity.this;

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

        musicPlayer = (FrameLayout) findViewById(R.id.playerController);
        musicPlayer.setVisibility(View.GONE);

        btnPlay = (ImageButton) findViewById(R.id.playPause);
        btnPlay.setOnClickListener(this);

        prev = (ImageButton) findViewById(R.id.prev);
        prev.setOnClickListener(this);

        next = (ImageButton) findViewById(R.id.next);
        next.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void playMusic(int id) {

        try {
            if (null == allListMusic || allListMusic.size() == 0) {
                Toast.makeText(MainActivity.this, "No song to play", Toast.LENGTH_SHORT).show();
                return;
            }

            List<ListMusic> temp = allListMusic;

            int curId = id;
            if (curId >= 0) {
                prevId = curId - 1;
            }

            if (curId < allListMusic.size() - 1)
            {
                nextId = curId + 1;
            }

            Log.i("Id", "CurID: " + curId + " PrevId: " + prevId + " NextId: " + nextId);

            titleFrame = (TextView) findViewById(R.id.title_frame);
            singerFrame = (TextView) findViewById(R.id.singer_frame);
            avatarFrame = (ImageView) findViewById(R.id.avatar_frame);

            String title = allListMusic.get(id).getTitle();
            //title = title.substring(0, 20);
            titleFrame.setText(title);
            singerFrame.setText(allListMusic.get(id).getArtist());
            //context = this;
            Log.i("Avatar", allListMusic.get(id).getAvatar());
            Picasso.with(context)
                    .load(allListMusic.get(id).getAvatar())
                    .placeholder(R.drawable.final1)
                    .error(R.drawable.error)
                    .into(avatarFrame);

            String url = temp.get(id).getUrl(); // your URL here
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mOnCompletionListener);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mediaPlayer != null) {
               nextSong();
            }
        }
    };

    @Override
    protected void onPause() {
        if(!mediaPlayer.isPlaying())
        {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onPause();
        mediaPlayer.pause();
        songLength = mediaPlayer.getCurrentPosition();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mediaPlayer.seekTo(songLength);
        mediaPlayer.start();
    }

    private void stopMusic() {
        if (null != mediaPlayer) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
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
        if (v.getId() == R.id.button) {
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
        else if (v.getId() == R.id.playPause) {
            stopPlay = !stopPlay;
            if(stopPlay) {
                onPause();
                btnPlay.setBackgroundResource(R.drawable.play);
            } else {
                if (mediaPlayer != null)
                {
                    btnPlay.setBackgroundResource(R.drawable.pause);
                    onRestart();
                }
            }
        }
        else if (v.getId() == R.id.prev) {
            prevSong();
        } else if (v.getId() == R.id.next) {
            nextSong();
        }
    }

    private void nextSong() {
        if (nextId != 0) {
            stopMusic();
            playMusic(nextId);
        }
    }

    private void prevSong() {
        if (prevId >= 0) {
            stopMusic();
            playMusic(prevId);
        }
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
                String title, artist, avatar, urlDownload, hostName;


                for (int i = 0; i < rootJson.length(); i++) {
                    JSONObject songNodes = rootJson.getJSONObject(i);
                    title = songNodes.getString("Title");
                    artist = songNodes.getString("Artist");
                    avatar = songNodes.getString("Avatar");
                    urlDownload = songNodes.getString("UrlJunDownload");
                    hostName = songNodes.getString("HostName");


                    listMusic.add(new ListMusic(title, artist, urlDownload, avatar, hostName));
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
            allListMusic = result;
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
                    Toast.makeText(MainActivity.this, result.get(position).getTitle(), Toast.LENGTH_SHORT).show();

                    stopMusic();


                    playMusic((int) id);
                    musicPlayer.setVisibility(View.VISIBLE);
                    btnPlay.setBackgroundResource(R.drawable.pause);
                }
            });
        }


    }
}
