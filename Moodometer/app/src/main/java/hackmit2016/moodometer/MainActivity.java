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
    }

    // Records user's mood rating on a scale of 1 to 5 for the day.
    public void recordResponse(View v) {
        String userResponseID = getResources().getResourceEntryName(v.getId());
        int userResponse;
        switch (userResponseID) {
            case "mood1": userResponse = 1;
                break;
            case "mood2": userResponse = 2;
                break;
            case "mood3": userResponse = 3;
                break;
            case "mood4": userResponse = 4;
                break;
            default: userResponse = 5;
        }
        Date today = new Date();
        Gson gson = new Gson();
        String date = gson.toJson(today);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(date, userResponse);
        editor.commit();
        //intent logic board
        Class<? extends AppCompatActivity> activityClass = null;
        if (userResponse == 1 || userResponse == 2) {
            activityClass = Suggestions.class;
        } else if (userResponse == 3 || userResponse == 4) {
            activityClass = ImagesActivity.class;
        } else {
            activityClass = MusicActivity.class;
        }
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}
