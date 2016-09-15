/*
 * Copyright (c) 2015. Peirr, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any means is strictly prohibited.
 * Proprietary and Confidential
 */

package com.peirra.http.server;

import android.text.TextUtils;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

class ServerRequestHandler extends Thread {

    String TAG = ServerRequestHandler.class.getSimpleName();
    private Socket toClient;
    private String documentRoot;
    private String html = "<html><body bgcolor=\"#000\" text=\"#fff\">{CONTENT}<body><html>";
    private final int BUFFER_SIZE = 16 * 1024;

    public ServerRequestHandler(String d, Socket s) {
        toClient = s;
        documentRoot = d;
    }

    public void run() {
        String path = "";
        try {
            if (!toClient.isClosed()) {
                RequestParser parser2 = new RequestParser(toClient.getInputStream());
                parser2.parseRequest();
                path = parser2.getRequestURL();
                Log.d(TAG, "M[ " + parser2.getMethod() + "] [path:" + path + "]");
            }
        } catch (Exception e) {
            Log.e(TAG, "error reading request: ", e);
            SocketThread.remove(toClient);
            try {
                toClient.close();
            } catch (Exception ex) {
                Log.e(TAG, "error closing client: ", ex);
            }
        }
        process(path);
    }


    public void release() {
        if (toClient != null) {
            try {
                toClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void process(String path) {
        Log.d(TAG, "process [path:" + path + "]");
        // Standard-Doc
        if (TextUtils.isEmpty(path)) {
            path = "index.html";
        }

        // Don't allow directory traversal
        if (path.contains("..")) {
            path = "403.html";
        }

        // Search for files in docroot
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "failed to decode path", e);
        }
        path = documentRoot + path;
//        Log.d(TAG, "file: " + path);
        path = path.replaceAll("[/]+", "/");

        if (path.charAt(path.length() - 1) == '/') {
            path = documentRoot + "404.html";
        }

        String header = getHeaderBase(path);
        header = header.replace("%code%", "403 Forbidden");

        try {
            File f = new File(path);
            if (!f.exists()) {
                header = getHeaderBase(path);
                header = header.replace("%code%", "404 File not found");
                path = "404.html";
            }
        } catch (Exception e) {
            //TODO and then what?
            Log.e(TAG,Log.getStackTraceString(e));
        }
        if (!path.equals(documentRoot + "403.html")) {
            header = getHeaderBase(path).replace("%code%", "200 OK");
        }
//        Log.d(TAG, "Serving " + path);
        try {
            File f = new File(path);
            if (f.exists()) { //TODO only allow access to files in the apps folders
                Log.d(TAG, "FOUND [" + path + "]");
                header = header.replace("%code%", "200");
                header = header.replace("%length%", "" + f.length());
                FileInputStream fis = new FileInputStream(f);
                OutputStream os = toClient.getOutputStream();
                os.write(header.getBytes());
                copy(fis, os);
//                okioCopy(fis,os);
                fis.close();
            } else {
                Log.d(TAG, "NOT FOUND [" + path + "]");
                // Send HTML-File (Ascii, not as a stream)
                header = getHeaderBase(path);
                header = header.replace("%code%", "404");
                header = header.replace("%length%", "" + get404().length());
                PrintWriter out = new PrintWriter(toClient.getOutputStream(), true);
                out.print(header);
                out.print(get404());
                out.flush();
            }
            SocketThread.remove(toClient);
            toClient.close();
        } catch (Exception e) {
//TODO and then what??
            Log.e(TAG,Log.getStackTraceString(e));

        }
    }


    private String get404() {
        return html.replace("{CONTENT}", "");
    }

    private String getHeaderBase(String path) {
        return "HTTP/1.1 %code%\n" +
                "Content-Type: " + getMimeType(path) + "\n" +
                "Content-Length: " + getContentLength(path) + "\n" +
                "X-Cache: HIT\n" +
                "Accept-Ranges: bytes\n" +
//                "Cache-Control: no-cache\n" +
//                "Pragma: no-cache\n" +
                "Content-Encoding: identity\n" +
                "Connection: close\n" +
                "Access-Control-Allow-Origin: *\n" + //TODO i should not allow any origin here, it should just be the server
                "SimpleHttpService: Kurt.Mbanje/1.0\n\n";
    }

    private String getDefaultHeaders(String origin) {
        return "HTTP/1.1 200\n" +
                "Content-Type: text/html; charset=utf-8\n" +
                "Access-Control-Allow-Origin: " + origin + "\n" +
                "SimpleHttpService: Kurt.Mbanje/1.0\n\n";
    }


    public String getMimeType(String url) {
        File file = new File(url);
        String type = URLConnection.guessContentTypeFromName(file.getAbsolutePath());
        Log.d(TAG, "getMimeType() [url: " + url + "] [type: " + type + "]");
        return type;
    }

    public String getContentLength(String url) {
        File file = new File(url);
        return String.valueOf(file.length());
    }


    private void copy(final InputStream src, final OutputStream dest) throws IOException {

        ReadableByteChannel inputChannel = Channels.newChannel(src);
        WritableByteChannel outputChannel = Channels.newChannel(dest);
        copy(inputChannel, outputChannel);
    }

    private void copy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        while (src.read(buffer) != -1) {
            buffer.flip();
            dest.write(buffer);
            buffer.compact();
        }
        buffer.flip();
        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }

    void okioCopy(InputStream in, OutputStream out) throws IOException {
//        BufferedSource source = Okio.buffer(Okio.source(in));
//        BufferedSink sink = Okio.buffer(Okio.sink(out));
//        while(!source.exhausted()) {
//            sink.write(source, BUFFER_SIZE);
//        }
    }
}
