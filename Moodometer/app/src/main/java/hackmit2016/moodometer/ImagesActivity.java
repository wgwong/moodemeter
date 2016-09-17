package hackmit2016.moodometer;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        final Button cuteAnimalsButton = (Button) findViewById(R.id.cuteAnimalsButton);
        final Button otherStuffButton = (Button) findViewById(R.id.otherStuffButton);
        final EditText editText = (EditText) findViewById(R.id.textBox);
        cuteAnimalsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    try {
                        String key = "AIzaSyAI-oaRGKwCNEAD5K5awchj8c6mANL5u9o";
                        String engineId = "007910975279267573429:m1wymjkh5no";
                        String qry = "batman";// search key word

                        String urlComponentLinker = "&";
                        String urlBase = "https://www.googleapis.com/customsearch/v1?";
                        String urlKeyComponent = String.format("key=%s", key);
                        String urlEngineIdComponent = String.format("cx=%s", engineId);
                        String urlQueryComponent = String.format("q=%s", qry);
                        String urlEnd = "alt=json";
                        String urlString = urlBase + urlComponentLinker +
                                urlKeyComponent + urlComponentLinker +
                                urlEngineIdComponent + urlComponentLinker +
                                urlQueryComponent + urlComponentLinker +
                                urlEnd;
                        URL url = new URL(urlString);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("Accept", "application/json");
                        BufferedReader br = new BufferedReader(new InputStreamReader(
                                (conn.getInputStream())));

                        String output;
                        String text = "";
                        while ((output = br.readLine()) != null) {

                            if (output.contains("\"link\": \"")) {
                                String link = output.substring(output.indexOf("\"link\": \"") +
                                        ("\"link\": \"").length(), output.indexOf("\","));
                                text += link;
                            }
                        }
                        editText.setText(text);
                        conn.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("Hacking... just throw exception whenever");
                    }
                }
            }
        });

        otherStuffButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
         });

    }

}
