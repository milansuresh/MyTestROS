package com.relianceit.relianceorder.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.relianceit.relianceorder.AppController;
import com.relianceit.relianceorder.models.ROSNewOrder;
import com.relianceit.relianceorder.models.ROSNewOrderItem;
import com.relianceit.relianceorder.models.ROSUser;
import com.relianceit.relianceorder.util.AppURLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sura on 5/5/15.
 */
public class NewOrderServiceHandler {

    public static final String TAG = NewOrderServiceHandler.class.getSimpleName();

    private Context context = null;

    public NewOrderServiceHandler(Context context) {
        this.context = context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void cancelRequests(final String requestTag) {
        AppController.getInstance().cancelPendingRequests(requestTag);
    }

    public static interface NewOrderSyncListener {
        public abstract void onOrderSyncSuccess(String orderId);
        public abstract void onOrderSyncError(String orderId, VolleyError error);
    }

    private void syncSuccess(String orderId, final String requestTag, final NewOrderSyncListener listener) {

        //update db
        listener.onOrderSyncSuccess(orderId);
    }

    private void syncFailed(String orderId, final String requestTag, final NewOrderSyncListener listener, VolleyError volleyError) {
        listener.onOrderSyncError(orderId, volleyError);
    }

    public void syncNewOrder(ROSNewOrder order, final String requestTag, final NewOrderSyncListener listener) {

        ROSUser user = ROSUser.getInstance();
        //Authorization: Token <auth token>:<deviceId>
        final String params = "Token " + user.getAccessToken() + ":" + user.getDeviceToken();
        Log.i(TAG, "NewOrder Authorization: " + params);

        final String orderId = order.getSalesOrdNum();

        Type listType = new TypeToken<ROSNewOrder>(){}.getType();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String jsonString = gson.toJson(order);

        Log.i(TAG, "NewOrder Json: " + jsonString);

        JSONObject postBody = null;
        try {
            postBody = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (postBody == null) {
            syncFailed(orderId, requestTag, listener, null);
            return;
        }

        JsonObjectRequest syncRequest = new JsonObjectRequest(Request.Method.POST, AppURLs.NEW_ORDER_SYNC_ENDPOINT, postBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.i(TAG, "Sync NewOrder success " + jsonObject.toString());
                        syncSuccess(orderId, requestTag, listener);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //TODO 401 if unauthorized
                        Log.i(TAG, "Sync NewOrder error " + volleyError.toString());
                        if (volleyError.networkResponse != null && volleyError.networkResponse.statusCode == 401) {
                            Log.i(TAG, "Sync NewOrder failed ====== Unauthorized");
                        }else {
                            Log.i(TAG, "Sync NewOrder failed ====== Server error");
                        }
                        syncFailed(orderId, requestTag, listener, volleyError);
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", params);
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(syncRequest, requestTag);
    }

    public void testSyncNewOrder() {

        ROSNewOrderItem orderItem = new ROSNewOrderItem();
        orderItem.setProductBatchCode("0001");
        orderItem.setProductDescription("Testing Product3");
        orderItem.setProductCode("001");
        orderItem.setQtyOrdered(1);
        orderItem.setUnitPrice(100);
        orderItem.setProdDiscount(0);
        orderItem.setQtyBonus(0);
        orderItem.setEffPrice(100.00);

        ArrayList<ROSNewOrderItem> products = new ArrayList<ROSNewOrderItem>();
        products.add(orderItem);

        ROSNewOrder order = new ROSNewOrder();
        order.setCustCode("00001");
        order.setGrossValue(100);
        order.setOVDiscount(1);
        order.setProducts(products);
        order.setOrderValue(1000.20);

        syncNewOrder(order, TAG, new NewOrderSyncListener() {
            @Override
            public void onOrderSyncSuccess(String orderId) {

            }

            @Override
            public void onOrderSyncError(String orderId, VolleyError error) {

            }
        });
    }
}
