package hackmit2016.moodometer;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ContactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
    }

    public void openContacts(View v) {
        Intent openContacts = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivity(openContacts);
    }

    public void listenToMusic(View v) {
        Intent musicOption = new Intent(ContactsActivity.this, MusicActivity.class);
        ContactsActivity.this.startActivity(musicOption);
    }
}
