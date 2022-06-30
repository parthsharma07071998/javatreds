package com.xlx.treds;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.glassfish.jersey.client.HttpUrlConnectorProvider;

public class ProxyConnectionFactory implements HttpUrlConnectorProvider.ConnectionFactory {

	private String proxyHost;
	private Integer proxyPort;

	public ProxyConnectionFactory(String proxyHost, Integer proxyPort) {
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
	}

	private Proxy getProxy() {
		return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
	}

	private HostnameVerifier getHostNameVerifier() {
		HostnameVerifier lHostnameVerifier = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				return true;
			}
		};
		// HttpsURLConnection.setDefaultHostnameVerifier(lHostnameVerifier);
		return lHostnameVerifier;
	}

	private SSLContext getSSLContext() {
		TrustManager[] lTrustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };
		try {
			SSLContext lSSLCotext = SSLContext.getInstance("SSL");
			lSSLCotext.init(null, lTrustAllCerts, new java.security.SecureRandom());
			// HttpsURLConnection.setDefaultSSLSocketFactory(lSSLCotext.getSocketFactory());
			return lSSLCotext;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public HttpURLConnection getConnection(URL url) throws IOException {
		Proxy proxy = getProxy();
		HttpURLConnection con = (HttpURLConnection) url.openConnection(proxy);
		if (con instanceof HttpsURLConnection) {
			HttpsURLConnection httpsCon = (HttpsURLConnection) con;
			httpsCon.setHostnameVerifier(getHostNameVerifier());
			httpsCon.setSSLSocketFactory(getSSLContext().getSocketFactory());
			return httpsCon;
		} else {
			return con;
		}
	}
}