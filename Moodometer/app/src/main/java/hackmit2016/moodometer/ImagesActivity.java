package hackmit2016.moodometer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ImagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        final Button cuteAnimalsButton = (Button) findViewById(R.id.cuteAnimalsButton);
        final Button otherStuffButton = (Button) findViewById(R.id.otherStuffButton);
        final EditText editText = (EditText) findViewById(R.id.textBox);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
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
                        String fileType = "png,jpg";
                        String searchType = "image";

                        String urlComponentLinker = "&";
                        String urlBase = "https://www.googleapis.com/customsearch/v1?";
                        String urlKeyComponent = String.format("key=%s", key);
                        String urlEngineIdComponent = String.format("cx=%s", engineId);
                        String urlQueryComponent = String.format("q=%s", qry);
                        String urlFileTypeComponent = String.format("fileType=%s", fileType);
                        String urlSearchTypeComponent = String.format("searchType=%s", searchType);
                        String urlEnd = "alt=json";
                        String urlString = urlBase + urlComponentLinker +
                                urlKeyComponent + urlComponentLinker +
                                urlEngineIdComponent + urlComponentLinker +
                                urlQueryComponent + urlComponentLinker +
                                urlFileTypeComponent + urlComponentLinker +
                                urlSearchTypeComponent + urlComponentLinker +
                                urlEnd;
                        new LoadImageTask(imageView).execute(urlString);
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

    private static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... queryUrlStrings) {
            Bitmap bitmap = null;
            try {
                // Fetch query URL
                String queryUrlString = queryUrlStrings[0];
                URL queryUrl = new URL(queryUrlString);

                // Make a request to fet a stream of the results
                HttpURLConnection conn = (HttpURLConnection) queryUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                // Store the query result links
                String output;
                List<String> queryResults = new ArrayList<>();
                while ((output = br.readLine()) != null) {
                    if (output.contains("\"link\": \"")) {
                        String link = output.substring(output.indexOf("\"link\": \"") +
                                ("\"link\": \"").length(), output.indexOf("\","));
                        queryResults.add(link);
                    }
                }

                // Disconnect after reading from the stream
                conn.disconnect();

                // Get a random link
                Random rand = new Random();
                int randInt = rand.nextInt(queryResults.size());
                String chosenResultUrl = queryResults.get(randInt);

                // Construct a bitmap from the URL
                InputStream in = new java.net.URL(chosenResultUrl).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Lel, some kind of error happened");
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

}
