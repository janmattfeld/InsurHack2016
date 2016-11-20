package com.zurich.authenticator;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;
import com.zurich.authenticator.service.AuthenticationService;
import com.zurich.authenticator.service.AuthenticationServiceConnection;
import com.zurich.authenticator.service.message.MessageBuilder;
import com.zurich.authenticator.util.logging.Logger;

public abstract class AuthenticationApplication extends MultiDexApplication {

    private static final String TAG = AuthenticationApplication.class.getSimpleName();

    protected final AuthenticationServiceConnection serviceConnection = new AuthenticationServiceConnection();
    protected final Messenger applicationMessenger = createMessenger();

    public AuthenticationApplication() {
        super();
    }

    protected abstract void bindServices();

    protected abstract void unbindServices();

    @Override
    public void onCreate() {
        super.onCreate();
        AuthenticationService.setupLogging();

        Logger.d(TAG, "onCreate() called");

        // setup debugging tools
        Stetho.initializeWithDefaults(this);

        // bind authentication services
        bindServices();
    }

    @Override
    public void onTerminate() {
        Logger.d(TAG, "onTerminate() called");
        unbindServices();
        super.onTerminate();
    }

    private Messenger createMessenger() {
        return new Messenger(new Handler() {
            @Override
            public void handleMessage(Message message) {
                AuthenticationApplication.this.handleMessage(message);
            }
        });
    }

    protected void handleMessage(Message message) {
        Logger.d(TAG, "handleMessage() called with: message = [" + message + "]");
        if (message.replyTo == null) {
            message.replyTo = serviceConnection.getMessenger();
        }

        try {
            switch (message.what) {
                case MessageBuilder.ID_ECHO: {
                    // reply with the same message
                    Message reply = new MessageBuilder()
                            .withData(message.getData())
                            .build();

                    message.replyTo.send(reply);
                    break;
                }
                default: {
                    throw new Exception("Unknown message ID");
                }
            }
        } catch (Exception ex) {
            Logger.w(TAG, "Unable to handle message: " + ex.getMessage());
        }
    }

    public AuthenticationServiceConnection getServiceConnection() {
        return serviceConnection;
    }
}
