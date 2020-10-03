package net.yolopago.pago.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.HashSet;
import java.util.prefs.Preferences;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookiesInterceptor implements Interceptor {
    private static final String TAG = "AddCookiesInterceptor";
    Context context;

    public AddCookiesInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        String url=chain.request().url().uri().getPath();
        Log.d(TAG, "intercept: "+chain.request());
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        HashSet<String> preferences = (HashSet<String>) sp.getStringSet("COOKIEYLP", new HashSet<String>());

        for (String cookie : preferences) {
            if(url.indexOf("/contract/")==0 && cookie.indexOf("CTSESSIONID")==0){builder.addHeader("Cookie", cookie);}
            if(url.indexOf("/ticket/")==0 && cookie.indexOf("TKSESSIONID")==0){builder.addHeader("Cookie", cookie);}
            if(url.indexOf("/payment/")==0 && cookie.indexOf("PYSESSIONID")==0){builder.addHeader("Cookie", cookie);}

        }

        return chain.proceed(builder.build());
    }
}