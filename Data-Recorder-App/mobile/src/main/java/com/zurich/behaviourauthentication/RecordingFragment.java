package com.zurich.behaviourauthentication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.zurich.authenticator.data.recorder.Record;
import com.zurich.authenticator.service.message.MessageBuilder;
import com.zurich.authenticator.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RecordingFragment extends DialogFragment {

    public static final String TAG = RecordingFragment.class.getSimpleName();

    private static final String KEY_USER_NAME_LIST = "userNameList";
    private static final String KEY_LABEL_LIST = "labelList";
    private static final String KEY_COMMENT_LIST = "commentList";
    private static final String KEY_DELAY_LIST = "delayList";
    private static final String KEY_DURATION_LIST = "durationList";

    private AutoCompleteTextView userNameEditText;
    private AutoCompleteTextView labelEditText;
    private AutoCompleteTextView commentEditText;
    private AutoCompleteTextView delayEditText;
    private AutoCompleteTextView durationEditText;

    private Button startButton;
    private Button stopButton;

    private SharedPreferences sharedPreferences;

    private PhoneApplication app;

    public RecordingFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (PhoneApplication) getActivity().getApplication();
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recording, container, false);

        getDialog().setTitle(getString(R.string.title_recording));

        userNameEditText = (AutoCompleteTextView) rootView.findViewById(R.id.userNameEditText);

        labelEditText = (AutoCompleteTextView) rootView.findViewById(R.id.labelEditText);
        commentEditText = (AutoCompleteTextView) rootView.findViewById(R.id.commentEditText);
        delayEditText = (AutoCompleteTextView) rootView.findViewById(R.id.delayEditText);
        durationEditText = (AutoCompleteTextView) rootView.findViewById(R.id.durationEditText);

        startButton = (Button) rootView.findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        stopButton = (Button) rootView.findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        restoreSettings();

        return rootView;
    }

    private void start() {
        Log.d(TAG, "start() called");
        saveSettings();
        try {

            app.getServiceConnection().sendMessage(new MessageBuilder()
                    .withId(MessageBuilder.ID_START_RECORDING)
                    .withData(getSettings())
                    .build());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        dismiss();
    }

    private void stop() {
        Log.d(TAG, "stop() called");
        try {
            app.getServiceConnection().sendMessage(new MessageBuilder()
                    .withId(MessageBuilder.ID_STOP_RECORDING)
                    .build());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        dismiss();
    }

    private Bundle getSettings() {
        Bundle settings = new Bundle();
        settings.putString(Record.KEY_USER_NAME, userNameEditText.getText().toString());
        settings.putString(Record.KEY_LABEL, labelEditText.getText().toString());
        settings.putString(Record.KEY_COMMENT, commentEditText.getText().toString());
        try {
            settings.putLong(Record.KEY_DELAY, TimeUnit.SECONDS.toMillis(Long.parseLong(delayEditText.getText().toString())));
        } catch (NumberFormatException e) {
            settings.putLong(Record.KEY_DELAY, TimeUnit.SECONDS.toMillis(10));
        }
        try {
            settings.putLong(Record.KEY_DURATION, TimeUnit.SECONDS.toMillis(Long.parseLong(durationEditText.getText().toString())));
        } catch (NumberFormatException e) {
            settings.putLong(Record.KEY_DURATION, TimeUnit.SECONDS.toMillis(30));
        }
        return settings;
    }

    private void restoreSettings() {
        Log.d(TAG, "restoreSettings() called");

        userNameEditText.setText(sharedPreferences.getString(Record.KEY_USER_NAME, null));
        userNameEditText.setAdapter(createValueListAdapter(KEY_USER_NAME_LIST));

        labelEditText.setText(sharedPreferences.getString(Record.KEY_LABEL, null));
        labelEditText.setAdapter(createValueListAdapter(KEY_LABEL_LIST));

        commentEditText.setText(sharedPreferences.getString(Record.KEY_COMMENT, "Normal"));
        commentEditText.setAdapter(createValueListAdapter(KEY_COMMENT_LIST));

        delayEditText.setText(sharedPreferences.getString(Record.KEY_DELAY, "10"));
        delayEditText.setAdapter(createValueListAdapter(KEY_DELAY_LIST));

        durationEditText.setText(sharedPreferences.getString(Record.KEY_DURATION, "30"));
        durationEditText.setAdapter(createValueListAdapter(KEY_DURATION_LIST));
    }

    private void saveSettings() {
        Log.d(TAG, "saveSettings() called");
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(Record.KEY_USER_NAME, userNameEditText.getText().toString());
        appendToValueList(KEY_USER_NAME_LIST, userNameEditText.getText().toString());

        editor.putString(Record.KEY_LABEL, labelEditText.getText().toString());
        appendToValueList(KEY_LABEL_LIST, labelEditText.getText().toString());

        editor.putString(Record.KEY_COMMENT, commentEditText.getText().toString());
        appendToValueList(KEY_COMMENT_LIST, commentEditText.getText().toString());

        editor.putString(Record.KEY_DELAY, delayEditText.getText().toString());
        appendToValueList(KEY_DELAY_LIST, delayEditText.getText().toString());

        editor.putString(Record.KEY_DURATION, durationEditText.getText().toString());
        appendToValueList(KEY_DURATION_LIST, durationEditText.getText().toString());

        editor.apply();
    }

    private ArrayAdapter<String> createValueListAdapter(String key) {
        String[] values = getValueList(key).toArray(new String[0]);
        return new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, values);
    }

    private List<String> getValueList(String key) {
        String csv = sharedPreferences.getString(key, "");
        List<String> csvValues = Arrays.asList(StringUtils.deserializeCsvToStrings(csv));
        return new ArrayList<>(csvValues);
    }

    private void saveValueList(String key, List<String> valueList) {
        String csv = StringUtils.serializeToCsv(valueList.toArray(new String[0]));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, csv);
        editor.apply();
    }

    private void appendToValueList(String key, String value) {
        value = value.trim();
        List<String> valueList = getValueList(key);
        if (valueList.contains(value)) {
            return;
        }
        valueList.add(value);
        saveValueList(key, valueList);
    }


}
