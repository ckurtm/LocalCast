package com.peirra.http.service;


import mbanje.kurt.remote_service.IServiceClient;
import mbanje.kurt.remote_service.RemoteMessageClient;
import mbanje.kurt.remote_service.RemoteServiceClient;

/**
 * Created by kurt on 2015/11/23.
 */
@RemoteServiceClient(SimpleHttpService.class)
public interface ISimpleHttpServiceClient extends IServiceClient {

    @RemoteMessageClient(SimpleHttpService.REQUEST_START)
    void bootup(int port);

    @RemoteMessageClient(SimpleHttpService.REQUEST_STOP)
    void shutdown();

    @RemoteMessageClient(SimpleHttpService.REQUEST_INFO)
    void info(int port);
}
