package com.peirra.http.service;



import mbanje.kurt.remote_service.IServiceServer;
import mbanje.kurt.remote_service.RemoteMessageServer;
import mbanje.kurt.remote_service.RemoteServiceServer;

/**
 * Created by kurt on 2015/11/23.
 */
@RemoteServiceServer(SimpleHttpService.class)
public interface ISimpleHttpServiceServer extends IServiceServer {
    @RemoteMessageServer({
            SimpleHttpService.STATE_RUNNING,
            SimpleHttpService.STATE_STOPPED,
            SimpleHttpService.STATE_ERROR
    })
    void onHttpServerStateChanged(int state, SimpleHttpInfo info);
}
