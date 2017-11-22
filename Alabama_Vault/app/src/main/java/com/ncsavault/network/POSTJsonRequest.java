package com.ncsavault.network;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ncsavault.controllers.AppController;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import applicationId.R;


/**
 * Class used for post the json object request using JSONObject.
 */
public class POSTJsonRequest extends JsonObjectRequest {

    public POSTJsonRequest(String url, String jsonData, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {

        super(Method.POST, url, jsonData, listener, errorListener);

    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        headers.put("appID", String.valueOf(AppController.getInstance().getApplicationContext().getResources()
                .getString(R.string.app_id)));
        headers.put("appVersion", AppController.getInstance().getApplicationContext().getResources()
                .getString(R.string.app_version));
        headers.put("deviceType", AppController.getInstance().getApplicationContext().getResources()
                .getString(R.string.device_type));

        return headers;
    }

}
