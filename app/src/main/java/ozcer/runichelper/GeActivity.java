package ozcer.runichelper;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import static android.R.attr.id;

public class GeActivity extends AppCompatActivity {
    TextView tvGeDisplay;
    JSONObject itemInfo;
    EditText edtGeSearchBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ge);

        Button btnGeSearch = (Button) findViewById(R.id.geSearchButton);
        tvGeDisplay  = (TextView) findViewById(R.id.geDisplay);
        edtGeSearchBar = (EditText) findViewById(R.id.geSearchBar);

        //new getItemInfoTask().execute("https://rsbuddy.com/exchange/summary.json");

        btnGeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemId = edtGeSearchBar.getText().toString();
                new JSONTask().execute(itemId);
            }
        });




    }

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item="+params[0]);
                connection  = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();
                JSONObject item = (new JSONObject(finalJson)).getJSONObject("item");
                String name = item.getString("name");
                String price = item.getJSONObject("current").getString("price");

                return String.format("Name: %s\nPrice: %s", name, price);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result != null) {
                tvGeDisplay.setText(result);
            } else {
                Toast.makeText(GeActivity.this, "no item found", Toast.LENGTH_SHORT).show();
            }
        }
    }



}


