package com.xlx.treds;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.internal.util.Base64;

import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.utilities.CommonUtilities;

import groovy.json.JsonBuilder;

public class JerseyClient {
	//
	public static Logger logger = Logger.getLogger(JerseyClient.class);
	private static JerseyClient theInstance;
	// this will come from regisry
	public static String baseUri = "https://4bfad37a.beta.monetago.com:3500/v2";
	private static String API_SECRET = "ed372810-9fff-4e5c-bc33-91153ac7c9ca";
	private static String API_KEY = "1135d5d1-29f3-44c4-9e57-d5c1fce6cbb4";
	//
	private static String PARAM_API_KEY = "X-API-KEY";
	private static String PARAM_API_PAYLOAD = "X-API-PAYLOAD";
	private static String PARAM_API_SIGNATURE = "X-API-SIGNATURE";
	private static String PARAM_NONCE = "nonce";
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA384";
	private static String proxyIp = "172.20.7.21";
	private static Long proxyPort = new Long(8080);
	
	//
	// register POST Upload a new Invoice to the distributed Ledger.
	private static final String TARGET_METHOD_REGISTER = "register";
	private static final String TARGET_METHOD_TYPE_REGISTER = "POST";
	// factor PATCH Factor the Invoice
	private static final String TARGET_METHOD_FACTOR = "factor";
	private static final String TARGET_METHOD_TYPE_FACTOR = "PATCH";
	// cancel PATCH Cancel the invoice with a given reason code.
	private static final String TARGET_METHOD_CANCEL = "cancel";
	private static final String TARGET_METHOD_TYPE_CANCEL = "PATCH";
	// getInvoiceByLedgerID GET Query for Invoice by Ledger ID
	private static final String TARGET_METHOD_GETINVOICEBYLEDGERID = "getInvoiceByLedgerID";
	private static final String TARGET_METHOD_TYPE_GETINVOICEBYLEDGERID = "GET";
	// registeredInvoices GET Query for all registered Invoices for a given
	// Exchange
	private static final String TARGET_METHOD_REGISTEREDINVOICES = "registeredInvoices";
	private static final String TARGET_METHOD_TYPE_REGISTEREDINVOICES = "GET";
	// factoredInvoices GET Query for all factored Invoices for a given Exchange
	private static final String TARGET_METHOD_FACTOREDINVOICES = "factoredInvoices";
	private static final String TARGET_METHOD_TYPE_FACTOREDINVOICES = "GET";
	// canceledInvoices GET Query for all canceled Invoices for a given Exchange
	private static final String TARGET_METHOD_CANCELEDINVOICES = "canceledInvoices";
	private static final String TARGET_METHOD_TYPE_CANCELEDINVOICES = "GET";
	// allInvoices GET Query for all Invoices for a given Exchange, regardless
	// of status.
	private static final String TARGET_METHOD_ALLINVOICES = "allInvoices";
	private static final String TARGET_METHOD_TYPE_ALLINVOICES = "GET";
	// duplicateInvoices GET Query for Invoices for a given Exchange that are
	// duplicates of other invoices
	private static final String TARGET_METHOD_DUPLICATEINVOICES = "duplicateInvoices";
	private static final String TARGET_METHOD_TYPE_DUPLICATEINVOICES = "GET";

	private ClientBuilder clientBuilder = null;
	//
	public static JerseyClient getInstance() {
		if (theInstance == null) {
			synchronized (JerseyClient.class) {
				if (theInstance == null) {
					try {
						JerseyClient tmpTheInstance = new JerseyClient();
						theInstance = tmpTheInstance;
					} catch (Exception lException) {
						logger.fatal("Error while instantiating JerseyClient", lException);
					}
				}
			}
		}
		return theInstance;
	}

	protected JerseyClient() {
		//
		HashMap<String, Object> lMonetagoSettings = new HashMap<String,Object>();
		if(MonetagoTredsHelper.getInstance().isDisasterEnabled()){
			lMonetagoSettings = (HashMap<String, Object>) RegistryHelper.getInstance().getStructure(AppConstants.REGISTRY_DESMONETAGO);	
		}else{
			lMonetagoSettings= (HashMap<String, Object>) RegistryHelper.getInstance().getStructure(AppConstants.REGISTRY_MONETAGO);
		}
		baseUri = (String) lMonetagoSettings.get(AppConstants.ATTRIBUTE_MONETAGO_BASEURI);
		API_SECRET = (String) lMonetagoSettings.get(AppConstants.ATTRIBUTE_MONETAGO_VALUE_SECRET);
		API_KEY = (String) lMonetagoSettings.get(AppConstants.ATTRIBUTE_MONETAGO_VALUE_APIKEY);
		PARAM_API_KEY = (String) lMonetagoSettings.get(AppConstants.ATTRIBUTE_MONETAGO_NAME_APIKEY);
		PARAM_API_PAYLOAD = (String) lMonetagoSettings.get(AppConstants.ATTRIBUTE_MONETAGO_NAME_APIPAYLOAD);
		PARAM_NONCE = (String) lMonetagoSettings.get(AppConstants.ATTRIBUTE_MONETAGO_NAME_APINONCE);
		PARAM_API_SIGNATURE = (String) lMonetagoSettings.get(AppConstants.ATTRIBUTE_MONETAGO_NAME_APISIGNATURE);
		
		proxyIp = TredsHelper.getInstance().getProxyIp();
		proxyPort = TredsHelper.getInstance().getProxyPort();
		ClientConfig lClientConfig = new ClientConfig();
		if (StringUtils.isNotBlank(proxyIp) && proxyPort != null) {
			ProxyConnectionFactory lConnectionFactory = new ProxyConnectionFactory(proxyIp, proxyPort.intValue());
			HttpUrlConnectorProvider lHttpUrlConnectorProvider = new HttpUrlConnectorProvider();
			lHttpUrlConnectorProvider.connectionFactory(lConnectionFactory);
			lClientConfig.connectorProvider(lHttpUrlConnectorProvider);
			lClientConfig.property(ClientProperties.CONNECT_TIMEOUT, 30000);
			lClientConfig.property(ClientProperties.READ_TIMEOUT,    30000);
		}
		if (baseUri.startsWith("https://")) {
			clientBuilder = ClientBuilder.newBuilder().withConfig(lClientConfig).sslContext(getSSLContext())
					.hostnameVerifier(getHostNameVerifier());

		} else {
			clientBuilder = ClientBuilder.newBuilder().withConfig(lClientConfig);
		}
	}

	private HostnameVerifier getHostNameVerifier() {
		HostnameVerifier lHostnameVerifier = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				return true;
			}
		};
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
			return lSSLCotext;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * // load crypto library var crypto = require('crypto') // set api key and
	 * secret var _api_key = <SET_API_KEY> var _api_secret = <SET_API_SECRET> //
	 * generate payload var _payload = new Buffer(JSON.stringify({ nonce:
	 * Date.now().toString() })).toString('base64'); // sign payload using api
	 * secret var _signature = crypto.createHmac('sha384',
	 * _api_secret).update(_payload).digest('hex'); var _headers = {
	 * 'Content-Type': 'application/json', 'X-API-KEY': _api_key,
	 * 'X-API-PAYLOAD': _payload, 'X-API-SIGNATURE': _signature
	 */
	public Invocation.Builder getRequestInvoker(String pPath) {
		Client lClient = clientBuilder.build();
		WebTarget lWebTarget = lClient.target(baseUri);
		lWebTarget = lWebTarget.path("/" + pPath);
		Invocation.Builder lBuilder = lWebTarget.request(MediaType.APPLICATION_JSON_TYPE);
		// add key
		lBuilder.header(PARAM_API_KEY, API_KEY);
		// add payload
		Map<String, Object> lPayLoadHash = new HashMap<String, Object>();
		lPayLoadHash.put(PARAM_NONCE, (CommonUtilities.getCurrentDateTime().getTime()));
		String lPayLoad = Base64.encodeAsString((new JsonBuilder(lPayLoadHash)).toString());
		lBuilder.header(PARAM_API_PAYLOAD, lPayLoad);
		// add signature
		Mac mac = null;
		try {
			mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// init with key
		SecretKeySpec key = new SecretKeySpec(API_SECRET.toString().getBytes(), HMAC_SHA1_ALGORITHM);
		try {
			mac.init(key);
			mac.update(lPayLoad.getBytes());
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		byte[] digest = mac.doFinal();
		String lSignature = getHexString(digest);
		lBuilder.header(PARAM_API_SIGNATURE, lSignature);
		return lBuilder;
	}

	private static String getHexString(byte[] mdbytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

}
