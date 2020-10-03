package net.yolopago.pago.interceptor;


import com.wizarpos.emvsample.MainApp;

import net.yolopago.pago.activity.MainActivity;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkConnectionInterceptor implements Interceptor {
	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		if (!isInternetAvailable()) {
			onInternetUnavailable();
		}
		return chain.proceed(request);
	}

	private boolean isInternetAvailable() {
		return MainApp.getInstance().isNetworkAvailable();
	}

	private void onInternetUnavailable() {
		if (MainApp.theActivity instanceof MainActivity) {
			((MainActivity) MainApp.theActivity).onInternetUnavailable();
		}
	}
}
