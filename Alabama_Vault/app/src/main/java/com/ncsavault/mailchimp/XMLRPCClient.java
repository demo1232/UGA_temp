package com.ncsavault.mailchimp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URI;
import java.util.Map;


public class XMLRPCClient extends XMLRPCCommon {
	private final HttpClient client;
	private final HttpPost postMethod;

	/**
	 * XMLRPCClient constructor. Creates new instance based on server URI
	 */
	@SuppressWarnings("deprecation")
	private XMLRPCClient(URI uri) {
		postMethod = new HttpPost(uri);
		postMethod.addHeader("Content-Type", "text/xml");
		
		// WARNING
		// I had to disable "Expect: 100-Continue" header since I had 
		// two second delay between sending http POST request and POST body 
		HttpParams httpParams = postMethod.getParams();
		HttpProtocolParams.setUseExpectContinue(httpParams, false);
		client = new DefaultHttpClient();
	}
	
	/**
	 * Convenience constructor. Creates new instance based on server String address
	 */
	public XMLRPCClient(String url) {
		this(URI.create(url));
	}


	/**
	 * Call method with optional parameters. This is general method.
	 * If you want to call your method with 0-8 parameters, you can use more
	 * convenience call() methods
	 * 
	 * @param method name of method to call
	 * @param params parameters to pass to method (may be null if method has no parameters)
	 * @return deserialize method return value
	 * @throws XMLRPCException handle exception
	 */
	@SuppressWarnings({"unchecked", "deprecation", "SameParameterValue"})
	public Object callEx(String method, Object[] params) throws XMLRPCException {
		try {
			// prepare POST body
			String body = methodCall(method, params);
			
//			Log.d("rsg.mail chimp", "XMLRPC body:\n" + body);

			// set POST body
			HttpEntity entity = new StringEntity(body);
			postMethod.setEntity(entity);

			//Log.d(Tag.LOG, "ros HTTP POST");
			// execute HTTP POST request
			HttpResponse response = client.execute(postMethod);
			//Log.d(Tag.LOG, "ros HTTP POSTed");

			// check status code
			int statusCode = response.getStatusLine().getStatusCode();
			//Log.d(Tag.LOG, "ros status code:" + statusCode);
			if (statusCode != HttpStatus.SC_OK) {
				throw new XMLRPCException("HTTP status code: " + statusCode + " != " + HttpStatus.SC_OK);
			}

			// parse response stuff
			//
			// setup pull parser
			XmlPullParser pullParser = XmlPullParserFactory.newInstance().newPullParser();
			entity = response.getEntity();
			Reader reader = new InputStreamReader(new BufferedInputStream(entity.getContent()));
// for testing purposes only
// reader = new StringReader("<?xml version='1.0'?><methodResponse><params><param><value>\n\n\n</value></param></params></methodResponse>");
			pullParser.setInput(reader);
			
			// lets start pulling...
			pullParser.nextTag();
			pullParser.require(XmlPullParser.START_TAG, null, Tag.METHOD_RESPONSE);
			
			pullParser.nextTag(); // either Tag.PARAMS (<params>) or Tag.FAULT (<fault>)  
			String tag = pullParser.getName();
			switch (tag) {
				case Tag.PARAMS:
					// normal response
					pullParser.nextTag(); // Tag.PARAM (<param>)

					pullParser.require(XmlPullParser.START_TAG, null, Tag.PARAM);
					pullParser.nextTag(); // Tag.VALUE (<value>)

					// no parser.require() here since its called in XMLRPCSerializer.deserialize() below

					// deserialize result
					Object obj = iXMLRPCSerializer.deserialize(pullParser);
					entity.consumeContent();
					return obj;
				case Tag.FAULT:
					// fault response
					pullParser.nextTag(); // Tag.VALUE (<value>)

					// no parser.require() here since its called in XMLRPCSerializer.deserialize() below

					// deserialize fault result
					Map<String, Object> map = (Map<String, Object>) iXMLRPCSerializer.deserialize(pullParser);
					String faultString = (String) map.get(Tag.FAULT_STRING);
					int faultCode = (Integer) map.get(Tag.FAULT_CODE);
					entity.consumeContent();
					throw new XMLRPCFault(faultString, faultCode);
				default:
					entity.consumeContent();
					throw new XMLRPCException("Bad tag <" + tag + "> in XMLRPC response - neither <params> nor <fault>");
			}
		} catch (XMLRPCException e) {
			// catch & propagate XMLRPCException/XMLRPCFault
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			// wrap any other Exception(s) around XMLRPCException
			throw new XMLRPCException(e);
		}
	}
	
	private String methodCall(String method, Object[] params)
	throws IllegalArgumentException, IllegalStateException, IOException {
		StringWriter bodyWriter = new StringWriter();
		serializer.setOutput(bodyWriter);
		serializer.startDocument(null, null);
		serializer.startTag(null, Tag.METHOD_CALL);
		// set method name
		serializer.startTag(null, Tag.METHOD_NAME).text(method).endTag(null, Tag.METHOD_NAME);
		
		serializeParams(params);

		serializer.endTag(null, Tag.METHOD_CALL);
		serializer.endDocument();

		return bodyWriter.toString();
	}




}
