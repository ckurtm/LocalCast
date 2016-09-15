package com.peirra.http;


import com.peirra.http.service.ISimpleHttpServiceServer;

/**
 * Created by kurt on 2015/11/24.
 */
public interface IServerRequest {
    void startService();
    void stopService();
    void bootup();
    void shutdown();
    void info();
    void setListener(ISimpleHttpServiceServer listener);
    void connect();
    void disconnect();
}
