package com.ncsavault.mailchimp.api;

import android.content.Context;

import com.ncsavault.mailchimp.XMLRPCClient;
import com.ncsavault.mailchimp.XMLRPCException;

import java.text.MessageFormat;

import applicationId.R;


/**
 * This is the main class for doing operations with the MailChimp API.
 */
@SuppressWarnings("unchecked")
public class MailChimpApi {
    private static final MessageFormat API_URL = new MessageFormat("https://{0}.api.mailchimp.com/1.2/");
    private static final String DEFAULT_DATA_CENTER = "us1";

    private final String apiKey;
    private final String endpointUrl;

    /**
     * Default constructor will look for the API key in the Resources file with:
     * R.strings.mc_api_key
     */
    public MailChimpApi(Context ctx) {
        this(ctx.getResources().getText(R.string.mc_api_key));
    }


    /**
     * Constructor that takes a specific apikey.
     *
     * @param apiKey key
     */
    public MailChimpApi(CharSequence apiKey) {
        this.apiKey = apiKey.toString();
        this.endpointUrl = API_URL.format(new Object[]{parseDataCenter(this.apiKey)});
    }


    /**
     * Simple utility method to get an instance of XMLRPCClient with the URL set
     *
     * @return client
     */
    private XMLRPCClient getClient() {
        return new XMLRPCClient(this.endpointUrl);
    }

    /**
     * Convenience method to handle calling the XMLRPC library and throwing the MailChimp exception in case
     * there was an XMLRPCException
     *
     * @param params     parameters to pass, if any
     * @return name
     * @throws MailChimpApiException exception
     */
    @SuppressWarnings("RedundantThrows")
    protected Object callMethod(Object... params) throws MailChimpApiException, XMLRPCException {
        // build the parameters to send and copy the input params, if necessary
        Object[] parameters = new Object[params == null ? 1 : params.length + 1];
        if (params != null && params.length > 0) {
            System.arraycopy(params, 0, parameters, 1, params.length);
        }

        // add the api key
        parameters[0] = apiKey;

        // call it
        return getClient().callEx("listSubscribe", parameters);
    }

    /**
     * Utility method to parse the data center out of the API key.  The apikey has the data center after
     * the last dash in the key
     *
     * @param apiKey key
     * @return the data center value
     */
    private static String parseDataCenter(String apiKey) {
        String dataCenter = DEFAULT_DATA_CENTER;
        int index = apiKey.lastIndexOf('-');
        // add the data center
        if (index > 0) {
            dataCenter = apiKey.substring(index + 1);
        }
        return dataCenter;
    }
}
