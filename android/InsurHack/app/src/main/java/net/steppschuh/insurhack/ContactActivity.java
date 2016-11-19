package net.steppschuh.insurhack;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class ContactActivity extends AppCompatActivity {

    private static final String TAG = ContactActivity.class.getSimpleName();

    private static final long MINIMUM_DELAY = TimeUnit.SECONDS.toMillis(1);
    private static final long MAXIMUM_DELAY = TimeUnit.SECONDS.toMillis(4);

    private TextView headingTextView;
    private TextView descriptionTextView;
    private ProgressBar progressBar;
    private ImageView statusImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_ok);
        setupUi();

        long delay = Math.max(MINIMUM_DELAY, Math.round(MAXIMUM_DELAY * Math.random()));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyContacts();
            }
        }, delay);
    }

    private void setupUi() {
        headingTextView = (TextView) findViewById(R.id.headingTextView);
        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        statusImageView = (ImageView) findViewById(R.id.statusImageView);
    }

    private void notifyContacts() {
        Log.d(TAG, "Notifying contacts");
        // TODO: trigger IFTTT
        onContactsNotified();
    }

    private void onContactsNotified() {
        Log.d(TAG, "Contacts notified");
        headingTextView.setText("Help is on the way!");
        descriptionTextView.setText("We\'ve notified your emergency contacts, help will be at your side soon!");
        progressBar.setVisibility(View.GONE);
        statusImageView.setVisibility(View.VISIBLE);
    }

}
