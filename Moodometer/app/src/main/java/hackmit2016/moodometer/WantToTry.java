package hackmit2016.moodometer;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class WantToTry extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_want_to_try);
    }

    /** Called when the user clicks the Yes button
     * takes the user to the selected activity
     * */
    public void startTask(View view) {
        //currently not using string taskId but we will use this to determine which task to start.

        //currently hardcoded to go to suggestions
        Intent intent = new Intent(this, Suggestions.class);
        startActivity(intent);
    }

    public void openContactsActivity(View v) {
        Intent goToContacts = new Intent(WantToTry.this, ContactsActivity.class);
        WantToTry.this.startActivity(goToContacts);
    }

}
