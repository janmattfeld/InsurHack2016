package com.zurich.authenticator.service.message;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

public class MessageBuilder {

    private static final String TAG = MessageBuilder.class.getSimpleName();

    public static final int ID_ECHO = 2;
    public static final int ID_START_SERVICE = 3;
    public static final int ID_STOP_SERVICE = 4;
    public static final int ID_START_RECORDING = 5;
    public static final int ID_STOP_RECORDING = 6;

    private int id;
    private Bundle data = new Bundle();
    private Messenger messenger;

    public MessageBuilder() {
    }

    public MessageBuilder(int id) {
        this.id = id;
    }

    public MessageBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public MessageBuilder withData(Bundle data) {
        this.data = data;
        return this;
    }

    public MessageBuilder addData(String key, String value) {
        data.putString(key, value);
        return this;
    }

    public MessageBuilder replyTo(Messenger messenger) {
        this.messenger = messenger;
        return this;
    }

    public MessageBuilder replyTo(Handler handler) {
        this.messenger = new Messenger(handler);
        return this;
    }

    public Message build() {
        Message message = Message.obtain();
        message.what = id;
        message.replyTo = messenger;
        message.setData(data);
        return message;
    }

}
