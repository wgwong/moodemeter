package hackmit2016.moodometer;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int NUM_DAYS = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hardcode the previous 7 days for mood
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        Date today = new Date();
        Gson gson = new Gson();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        Date[] dates = new Date[NUM_DAYS];
        int[] moods = { 4, 5, 5, 4, 2, 3, 4};
        for (int i = 0; i < NUM_DAYS; i++) {
            cal.add(Calendar.DATE, -1);
            Date date = cal.getTime();
            dates[i] = date;
            editor.putInt(gson.toJson(date), moods[i]);
        }
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
        String todayDate = gson.toJson(today);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(todayDate, userResponse);
        editor.apply();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        Date[] dates = new Date[NUM_DAYS];
        List<Integer> moods = new ArrayList<>();
        for (int i = 0; i < NUM_DAYS; i++) {
            cal.add(Calendar.DATE, -1);
            Date date = cal.getTime();
            dates[i] = date;
            int mood = sharedPref.getInt(gson.toJson(date), 0);
            moods.add(mood);
        }
        moods.add(userResponse);
        //intent logic board
        Class<? extends AppCompatActivity> activityClass = null;
        DangerLevelDeterminer determiner = new DangerLevelDeterminer();
        DangerLevelDeterminer.Danger danger = determiner.getDangerLevel(moods);
        if (danger == DangerLevelDeterminer.Danger.HIGH) {
            activityClass = MusicActivity.class;
        } else if (danger == DangerLevelDeterminer.Danger.MEDIUM) {
            activityClass = ImagesActivity.class;
        } else {
            activityClass = Suggestions.class;
        }
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}
