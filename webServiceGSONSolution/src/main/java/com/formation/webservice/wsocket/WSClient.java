package com.formation.webservice.wsocket;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by Anthony on 29/08/2017.
 */

public class WSClient extends WebSocketListener {

    private static final String SERVER_URL = "ws://192.168.102.2:8080/WebSocketServer/websocketendpoint";

    private WebSocket webSocket;
    private WSClientI wsClientI;

    public void open() {
        if (webSocket == null) {
            OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
            Request request = new Request.Builder().url(SERVER_URL).build();
            client.newWebSocket(request, this);
        }
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "end");
        }
    }

    public void sendMessage(String message) throws Exception {
        if (webSocket == null) {
            throw new Exception("WebSocket not open");
        }
        else {
            webSocket.send(message);
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {

        // TODO Auto-generated method stub
        super.onOpen(webSocket, response);
        this.webSocket = webSocket;
        if (wsClientI != null) {
            wsClientI.wsOpen();
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        // TODO Auto-generated method stub
        super.onMessage(webSocket, text);
        if (wsClientI != null) {
            wsClientI.wsMessageReceive(text);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        // TODO Auto-generated method stub
        super.onMessage(webSocket, bytes);
        if (wsClientI != null) {
            wsClientI.wsMessageReceive(bytes.hex());
        }
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        // TODO Auto-generated method stub
        super.onClosing(webSocket, code, reason);
        System.out.println("CLOSE: " + code + " " + reason);
        this.webSocket = null;
        if (wsClientI != null) {
            wsClientI.wsClose();
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        // TODO Auto-generated method stub
        super.onFailure(webSocket, t, response);
        t.printStackTrace();
        if (wsClientI != null) {
            wsClientI.wsErrorReceive(t);
        }
    }

    public void setWsClientI(WSClientI wsClientI) {
        this.wsClientI = wsClientI;
    }

    public interface WSClientI {
        void wsOpen();

        void wsClose();

        void wsMessageReceive(String msg);

        void wsErrorReceive(Throwable e);
    }
}
