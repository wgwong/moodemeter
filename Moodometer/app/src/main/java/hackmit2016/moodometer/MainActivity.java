package hackmit2016.moodometer;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button imagesButton = (Button) findViewById(R.id.imagesButton);
        Button musicButton = (Button) findViewById(R.id.musicButton);
        imagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ImagesActivity.class);
                startActivity(intent);
            }
        });
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MusicActivity.class);
                startActivity(intent);
            }
        });
    }

    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, LaunchActivity.class);
        startActivity(intent);
    }

    // Checks if user has input a mood rating for today. If not, launches mood picking
    // activity. Otherwise chooses a a therapy to launch.
    public void launchInitialActivity() {
        Date date = new Date();
        Gson todayGson = new Gson();
        String todayString = todayGson.toJson(date);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (!sharedPref.contains(todayString)) {
            Intent intent = new Intent(MainActivity.this, LaunchActivity.class);
            startActivity(intent);
        } else {
            // intent logic board activity
        }

    }
}
