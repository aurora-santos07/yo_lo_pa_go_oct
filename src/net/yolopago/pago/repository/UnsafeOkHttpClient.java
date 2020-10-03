package net.yolopago.pago.repository;

import android.content.Context;
import android.util.Log;

import com.wizarpos.emvsample.MainApp;
import com.wizarpos.emvsample.R;

import net.yolopago.pago.activity.MainActivity;
import net.yolopago.pago.interceptor.NetworkConnectionInterceptor;
import net.yolopago.pago.interceptor.UnauthorizedAccessInterceptor;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;

public class UnsafeOkHttpClient {

	private static final String TAG = "UnsafeOkHttpClient";


	public static OkHttpClient getUnsafeOkHttpClient(Context context) {

		try {

			TrustManager[] trustAllCerts;
			InputStream certInputStream=null;
			BufferedInputStream bis=null;
			try {
				KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
				keyStore.load(null, null);
				certInputStream = context.getResources().openRawResource(R.raw.preprod_cer);
				 bis = new BufferedInputStream(certInputStream);
				CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

				while (bis.available() > 0) {
					Certificate cert = certificateFactory.generateCertificate(bis);
					keyStore.setCertificateEntry("ca", cert);
				}

				TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				trustManagerFactory.init(keyStore);
				trustAllCerts = trustManagerFactory.getTrustManagers();

			} catch (Exception e) {
				Log.d(TAG, "SSL  Fail");
				throw new RuntimeException(e);
			}finally {
				if(certInputStream!=null){
					certInputStream.close();
				}
				if(bis!=null){
					bis.close();
				}
			}

			final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

			final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

			ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
					.tlsVersions(TlsVersion.TLS_1_2)
					.cipherSuites(
							CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
							CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
							CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
					.build();
			OkHttpClient.Builder builder = new OkHttpClient.Builder();
			builder.connectTimeout(30, TimeUnit.SECONDS);
			builder.readTimeout(30, TimeUnit.SECONDS);
			builder.writeTimeout(30, TimeUnit.SECONDS);
			builder.connectionSpecs(Collections.singletonList(spec));
			builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
			builder.hostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					Log.d(TAG, "HOST NAME SSL:"+hostname);
					if(hostname.equals("161.47.83.33")){
						return true;
					}

					return false;
				}
			});
			builder.addInterceptor(new NetworkConnectionInterceptor());
			builder.addInterceptor(new UnauthorizedAccessInterceptor());
			builder.addInterceptor(new AddCookiesInterceptor(context));
			builder.addInterceptor(new ReceivedCookiesInterceptor(context));

			OkHttpClient okHttpClient = builder.build();
			return okHttpClient;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
