

package com.peirr.localcast.io;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by kurt on 04 02 2015 .
 */
public class CastMessageUtils {
    static String TAG = CastMessageUtils.class.getSimpleName();
    public static final String ARG_URL = "url";

    public enum CastState {
        PAUSE, PAUSED, PLAY, PLAYING, BUFFERING, STOP, STOPPED
    }

    public enum CastType {
        IMAGE, AUDIO, VIDEO
    }

    public static class CastMessage {
        public final int version;
        public CastType typ;
        public CastState state;
        public Map<String, String> data = new HashMap<>();

        public CastMessage(int version) {
            this.version = version;
        }
    }

    public static String toJson(CastMessage message) {
        String json = null;
        JSONObject obj = new JSONObject();
        try {
            obj.put("typ", message.typ.toString());
            obj.put("state", message.state.toString());
            obj.put("version", message.version);
            JSONObject data = new JSONObject();
            for (String key : message.data.keySet()) {
                data.put(key, message.data.get(key));
            }
            obj.put("data", data);
            json = obj.toString();
        } catch (JSONException e) {
            Log.e(TAG, "failed to convert song", e);
        }
        return json;
    }

    public static CastMessage toMessage(int version, String json) {
        CastMessage message = null;
        try {
            message = new CastMessage(version);
            JSONObject obj = new JSONObject(json);
            message.state = CastState.valueOf(obj.getString("state"));
            message.typ = CastType.valueOf(obj.getString("typ"));
            JSONObject array;
            try {
                array = obj.getJSONObject("data");
                Map<String, String> data = new HashMap<>();
                Iterator<String> iterator = array.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    data.put(key, array.getString(key));
                }
                message.data = data;
            } catch (JSONException je) {
                Log.e(TAG, "failed to read data value from json: " + json, je);
            }

        } catch (JSONException e) {
            Log.e(TAG, "failed to read message from json: " + json, e);
        }
        return message;
    }

    public static CastMessage play(CastType type,String file,String host,int version){
        CastMessage message = new CastMessage(version);
        message.typ = type;
        message.state = CastState.PLAY;
        String url = host + file;
        message.data.put(CastMessageUtils.ARG_URL, url);
        return message;
    }

}
