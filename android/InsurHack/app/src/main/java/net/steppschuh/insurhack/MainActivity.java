package net.steppschuh.insurhack;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int INITIAL_COUNTDOWN_VALUE = 8;

    private Handler countdownHandler;
    private Runnable countdownRunnable;
    private boolean countdownActive = false;
    private int countdownValue = INITIAL_COUNTDOWN_VALUE;

    private Vibrator vibrator;
    private TextView countdownTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUi();

        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                countdownValue -= 1;
                countdownTextView.setText(String.valueOf(countdownValue));
                Log.d(TAG, "Countdown: " + countdownTextView.getText());

                if (countdownValue == 0) {
                    stopCountdown();
                    userNeedsHelp();
                    vibrator.vibrate(500);
                } else if (countdownActive) {
                    vibrator.vibrate(100);
                    countdownHandler.postDelayed(this, TimeUnit.SECONDS.toMillis(1));
                }
            }
        };

        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCountdown();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCountdown();
    }

    private void setupUi() {
        countdownTextView = (TextView) findViewById(R.id.countdownTextView);
        countdownTextView.setText(String.valueOf(INITIAL_COUNTDOWN_VALUE));

        findViewById(R.id.positiveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userIsFine();
            }
        });

        findViewById(R.id.negativeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userNeedsHelp();
            }
        });
    }

    private void startCountdown() {
        if (countdownActive) {
            return;
        }
        countdownActive = true;
        countdownValue = INITIAL_COUNTDOWN_VALUE;
        countdownHandler = new Handler();
        countdownHandler.postDelayed(countdownRunnable, TimeUnit.SECONDS.toMillis(1));

        vibrator.vibrate(500);
        Log.d(TAG, "Countdown started");
    }

    private void stopCountdown() {
        if (!countdownActive) {
            return;
        }
        countdownActive = false;
        countdownHandler.removeCallbacks(countdownRunnable);
        Log.d(TAG, "Countdown stopped");
    }

    private void userIsFine() {
        Log.d(TAG, "User is fine. Meh.");
    }

    private void userNeedsHelp() {
        Log.d(TAG, "User needs help. Jay.");
        Intent intent = new Intent(getApplicationContext(), ContactActivity.class);
        finish();
        startActivity(intent);
    }

}
