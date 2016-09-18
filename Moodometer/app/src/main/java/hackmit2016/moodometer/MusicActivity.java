package hackmit2016.moodometer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MusicActivity extends AppCompatActivity {

    Button selectRandomMusicButton;
    Button openAndroidMediaPlayerButton;
    TextView prompt;
    MediaPlayer[] player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        selectRandomMusicButton = (Button) findViewById(R.id.selectRandomMusicButton);
        openAndroidMediaPlayerButton = (Button) findViewById(R.id.openAndroidMediaPlayerButton);
        prompt = (TextView) findViewById(R.id.prompt);
        //player = new MediaPlayer[1];

        //Will change to read in all mp3s at once
        int numSongs = 1;

//        player = new MediaPlayer[numSongs];
//        player[0] = MediaPlayer.create(this, R.raw.happy1);

        selectRandomMusicButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                 //randomly selects music
//                 int random = (int) (Math.random() * player.length) + 1;
//                 player[random].start();
            }
        });

        openAndroidMediaPlayerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER));
            }
        });
    }

    public void openImagesActivity(View v) {
        Intent goToImages = new Intent(MusicActivity.this, ImagesActivity.class);
        MusicActivity.this.startActivity(goToImages);
    }
}
