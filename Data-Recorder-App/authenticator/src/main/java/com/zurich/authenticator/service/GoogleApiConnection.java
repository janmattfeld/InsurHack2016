package com.zurich.authenticator.service;

import android.os.Bundle;
import android.os.Message;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.zurich.authenticator.util.logging.Logger;
import com.zurich.authenticator.util.markdown.list.UnorderedList;

import java.util.ArrayList;
import java.util.List;

public class GoogleApiConnection implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = GoogleApiConnection.class.getSimpleName();

    public static final String PATH_PREFIX = "behaviour_auth_";

    private GoogleApiClient googleApiClient;
    private List<Node> nearbyNodes = new ArrayList<>();

    public GoogleApiConnection(AuthenticationService service) {
        Logger.d(TAG, "GoogleApiConnection() called with: service = [" + service + "]");
        googleApiClient = new GoogleApiClient.Builder(service)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        googleApiClient.connect();
        Wearable.MessageApi.addListener(googleApiClient, service);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Logger.d(TAG, "onConnected: " + bundle);
        updateNearbyNodes();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Logger.d(TAG, "onConnectionSuspended: " + cause);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.d(TAG, "onConnectionFailed: " + connectionResult);
    }

    public void updateNearbyNodes() {
        Logger.d(TAG, "updateNearbyNodes() called");
        Wearable.NodeApi.getConnectedNodes(googleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(@NonNull NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                nearbyNodes = new ArrayList<>();
                for (Node node : getConnectedNodesResult.getNodes()) {
                    if (node.isNearby()) {
                        nearbyNodes.add(node);
                    }
                }
                Logger.d(TAG, "Nearby connected nodes:\n" + new UnorderedList(new ArrayList<Object>(nearbyNodes)));
            }
        });
    }

    public boolean isConnected() {
        return getGoogleApiClient().isConnected();
    }

    public boolean hasNearbyNodes() {
        return nearbyNodes.size() > 0;
    }

    public void sendMessageToNearbyNodes(Message message) {
        String path = getPathFromMessageId(message.what);
        byte[] data = getBytesFromBundle(message.getData());
        sendMessageToNearbyNodes(path, data);
    }

    private void sendMessageToNearbyNodes(String path, byte[] payload) {
        for (Node node : nearbyNodes) {
            sendMessageToNode(node.getId(), path, payload);
        }
    }

    private PendingResult<MessageApi.SendMessageResult> sendMessageToNode(String nodeId, String path, byte[] payload) {
        Logger.d(TAG, "sendMessageToNode() called with: nodeId = [" + nodeId + "], path = [" + path + "], payload = [" + payload + "]");
        return Wearable.MessageApi.sendMessage(googleApiClient, nodeId, path, payload);
    }

    public static String getPathFromMessageId(int id) {
        return PATH_PREFIX + String.valueOf(id);
    }

    public static int getMessageIdFromPath(String path) {
        if (!path.contains(PATH_PREFIX)) {
            return 0;
        }
        try {
            String idString = path.substring(PATH_PREFIX.length());
            return Integer.parseInt(idString);
        } catch (Exception ex) {
            Logger.w(TAG, "Unable to parse message ID from path: " + path);
            return 0;
        }
    }

    public static byte[] getBytesFromBundle(Bundle bundle) {
        Parcel parcel = Parcel.obtain();
        bundle.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }

    public static Bundle getBundleFromBytes(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        return Bundle.CREATOR.createFromParcel(parcel);
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

}
