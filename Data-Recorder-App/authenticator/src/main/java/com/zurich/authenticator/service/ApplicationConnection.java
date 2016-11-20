package com.zurich.authenticator.service;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class ApplicationConnection {

    private static final String TAG = ApplicationConnection.class.getSimpleName();

    public static final String KEY_MESSENGER = "messenger";

    private Messenger messenger;
    private boolean bound;

    public ApplicationConnection() {
    }

    public void sendMessage(Message message) throws RemoteException {
        if (!bound) {
            throw new RemoteException("Application not bound");
        }
        messenger.send(message);
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public void setMessenger(Messenger messenger) {
        this.messenger = messenger;
    }

    public boolean isBound() {
        return bound;
    }

    public void setBound(boolean bound) {
        this.bound = bound;
    }

}
