package net.yolopago.pago.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.HashSet;
import java.util.prefs.Preferences;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ReceivedCookiesInterceptor implements Interceptor {
    private static final String TAG = "ReceivedCookiesIntercep";
    Context context;

    public ReceivedCookiesInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        Log.d(TAG, "Recive intercept: "+originalResponse.toString());
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            SharedPreferences sp = context.getApplicationContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
            HashSet<String> cookies = (HashSet<String>) sp.getStringSet("COOKIEYLP", new HashSet<String>());

            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }
            SharedPreferences.Editor memes = sp.edit();
            memes.putStringSet("COOKIEYLP", cookies).apply();
            memes.commit();
        }

        return originalResponse;
    }
}