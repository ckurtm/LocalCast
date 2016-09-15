package com.peirr.localcast.demo;

import android.content.Context;

import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kurt on 2016/09/09.
 */

public class CastOptionsProvider implements OptionsProvider {

    public static final String CUSTOM_NAMESPACE = "urn:x-cast:com.peirr.local-cast";

    @Override
    public CastOptions getCastOptions(Context context) {
        List<String> supportedNamespaces = new ArrayList<>();
        supportedNamespaces.add(CUSTOM_NAMESPACE);
        CastOptions castOptions = new CastOptions.Builder()
                .setReceiverApplicationId(context.getString(R.string.cast_app_id))
                .build();
        return castOptions;
    }
    @Override
    public List<SessionProvider> getAdditionalSessionProviders(Context context) {
        return null;
    }
}
