package com.nfjs.icndb.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;


public class MainActivity extends Activity {
    private static final String URL = "http://api.icndb.com/jokes/random?" +
            "limitTo=[nerdy]&firstName={first}&lastName={last}";

    private TextView jokeView;
    private RestTemplate template = new RestTemplate();

    private AsyncTask<String, Void, String> task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jokeView = (TextView) findViewById(R.id.text_view);
        Button jokeButton = (Button) findViewById(R.id.icndb_button);
        jokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task = new JokeTask().execute(
                        getString(R.string.first_name),
                        getString(R.string.last_name));
            }
        });

        template.getMessageConverters().add(new GsonHttpMessageConverter());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (task != null) task.cancel(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_joke:
                task = new JokeTask().execute(
                        getString(R.string.first_name),
                        getString(R.string.last_name));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class JokeTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            IcndbJoke joke = template.getForObject(URL, IcndbJoke.class,
                    params[0], params[1]);
            return joke.getJoke();
        }

        @Override
        protected void onPostExecute(String result) {
            jokeView.setText(result);
        }
    }
}
