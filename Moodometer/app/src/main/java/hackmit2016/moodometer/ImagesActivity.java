package hackmit2016.moodometer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.MediaStore;
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

    private List<String> imageLinks;
    private int linkIndex = -1;
    boolean isSearchingImage = false;
    boolean wasImageSet = false;

    Button cuteAnimalsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        cuteAnimalsButton = (Button) findViewById(R.id.cuteAnimalsButton);
        final Button otherStuffButton = (Button) findViewById(R.id.otherStuffButton);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        cuteAnimalsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isSearchingImage) {
                    int SDK_INT = android.os.Build.VERSION.SDK_INT;
                    if (SDK_INT > 8) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        try {
                            String key = "AIzaSyAI-oaRGKwCNEAD5K5awchj8c6mANL5u9o";
                            String engineId = "007910975279267573429:m1wymjkh5no";
                            String qry = "cute+animals";// search key word
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
                            isSearchingImage = true;
                            wasImageSet = false;
                            cuteAnimalsButton.setText("Searching for cute animal image...");
                            new LoadImageTask(imageView, urlString).execute(urlString);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException("Hacking... just throw exception whenever");
                        }
                    }
                }
            }
        });

        otherStuffButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
    }

    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;
        private String queryUrl;

        public LoadImageTask(ImageView imageView, String queryUrl) {
            this.imageView = imageView;
            this.queryUrl = queryUrl;
        }

        protected Bitmap doInBackground(String... queryUrlStrings) {
            Bitmap bitmap = null;
            try {
                if (ImagesActivity.this.imageLinks == null) {
                    // Fetch query URL
                    String queryUrlString = queryUrlStrings[0];
                    URL queryUrl = new URL(queryUrlString);

                    // Make a request to fetch a stream of the results
                    HttpURLConnection conn = (HttpURLConnection) queryUrl.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));

                    // Store the query result links
                    String output;
                    imageLinks = new ArrayList<>();
                    while ((output = br.readLine()) != null) {
                        if (output.contains("\"link\": \"")) {
                            String link = output.substring(output.indexOf("\"link\": \"") +
                                    ("\"link\": \"").length(), output.indexOf("\","));
                            imageLinks.add(link);
                        }
                    }

                    // Disconnect after reading from the stream
                    conn.disconnect();
                }

                if (imageLinks.isEmpty()) {
                    // There are no query results, just return
                    return bitmap;
                } else {
                    // Get a random link
                    int randInt = 0;
                    do {
                        Random rand = new Random();
                        randInt = rand.nextInt(imageLinks.size());
                    } while (randInt == linkIndex && imageLinks.size() != 1);
                    String chosenResultUrl = imageLinks.get(randInt);

                    // Construct a bitmap from the URL
                    InputStream in = new java.net.URL(chosenResultUrl).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                    in.close();
                    return bitmap;
                }
            } catch (Exception e) {
                return bitmap;
            }
        }

        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                new LoadImageTask(imageView, queryUrl).execute(queryUrl);
                return;
            }
            imageView.setImageBitmap(result);
            wasImageSet = true;
            isSearchingImage = false;
            cuteAnimalsButton.setText("Cute animals");
        }
    }

    public void openSuggestionsActivity(View v) {
        Intent goToSuggestions = new Intent(ImagesActivity.this, Suggestions.class);
        ImagesActivity.this.startActivity(goToSuggestions);
    }

}
