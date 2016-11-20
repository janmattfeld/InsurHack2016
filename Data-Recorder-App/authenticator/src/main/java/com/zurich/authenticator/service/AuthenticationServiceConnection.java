package com.zurich.authenticator.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.zurich.authenticator.util.logging.Logger;

public class AuthenticationServiceConnection implements ServiceConnection {

    private static final String TAG = AuthenticationServiceConnection.class.getSimpleName();

    private Messenger messenger = null;
    private boolean bound;

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Logger.d(TAG, "onServiceConnected() called with: componentName = [" + componentName + "], iBinder = [" + iBinder + "]");
        // This is called when the connection with the service has been
        // established, giving us the object we can use to
        // interact with the service.  We are communicating with the
        // service using a Messenger, so here we get a client-side
        // representation of that from the raw IBinder object.
        messenger = new Messenger(iBinder);
        bound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Logger.d(TAG, "onServiceDisconnected() called with: componentName = [" + componentName + "]");
        // This is called when the connection with the service has been
        // unexpectedly disconnected -- that is, its process crashed.
        messenger = null;
        bound = false;
    }

    public void bind(Context context, Intent intent) {
        context.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    public void unBind(Context context) {
        if (bound) {
            context.unbindService(this);
        }
    }

    public void sendMessage(Message message) throws RemoteException {
        if (!bound) {
            throw new RemoteException("Service not bound");
        }
        messenger.send(message);
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public boolean isBound() {
        return bound;
    }

}
