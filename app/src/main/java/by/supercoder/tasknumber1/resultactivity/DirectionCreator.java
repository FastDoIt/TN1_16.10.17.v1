package by.supercoder.tasknumber1.resultactivity;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import by.supercoder.tasknumber1.R;

/**
 * Created by user on 07.07.2017.
 */
public class DirectionCreator {

    interface OnPathCreated {
        void onPathCreated(ArrayList<LatLng> pathArray);
    }

    interface OnShowServerResponse {
        void onShowServerResponse(String serverResponse);
    }

    private ArrayList<LatLng> pathArray;
    private OnPathCreated pathCreatedListener = null;
    private OnShowServerResponse showServerResponseListener = null;
    private Context mContext;
    private RequestQueue queue;

    public void createPathArray(Context context, LatLng startPoint, LatLng endPoint, String keyDirectionsApi) {
        pathArray = new ArrayList<LatLng>();

        queue = Volley.newRequestQueue(context);

        final String HOST = "https://maps.googleapis.com/maps/api/directions/json?";
        String startPointResponse = "origin=" + startPoint.latitude + "," + startPoint.longitude;
        String endPointResponse = "destination=" + endPoint.latitude + "," + endPoint.longitude;
        final String KEY_RESPONSE = "key=" + keyDirectionsApi;
        final String AND = "&";
        String URL = HOST + AND + startPointResponse + AND + endPointResponse + AND + KEY_RESPONSE;

        JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray steps;
                JSONObject stepsObject;
                JSONObject leg;
                JSONArray legs;
                String serverResponse = null;

                try {
                    serverResponse = response.getString("status");
                    JSONArray routesResponse = response.getJSONArray("routes");
                    leg = routesResponse.getJSONObject(0);
                    legs = leg.getJSONArray("legs");
                    stepsObject = legs.getJSONObject(0);
                    steps = stepsObject.getJSONArray("steps");
                    for (int i = 0; i < steps.length(); i++) {
                        JSONObject step = steps.getJSONObject(i);
                        if (i == 0) {
                            JSONObject startPoint = step.getJSONObject("start_location");
                            double latitude = startPoint.getDouble("lat");
                            double longitude = startPoint.getDouble("lng");
                            pathArray.add(new LatLng(latitude, longitude));
                        }
                        JSONObject owerviewPolyline = step.getJSONObject("polyline");
                        if(owerviewPolyline != null){
                            String pointsOwerviewPolyline = "";
                            pointsOwerviewPolyline = owerviewPolyline.getString("points");
                            List<LatLng> decodedPoints;
                            decodedPoints = decodeOwerviewPolyline(pointsOwerviewPolyline);
                            if(decodedPoints != null) {
                                pathArray.addAll(decodedPoints);
                            }
                        }
                        JSONObject endPoint = step.getJSONObject("end_location");
                        double latitude = endPoint.getDouble("lat");
                        double longitude = endPoint.getDouble("lng");
                        pathArray.add(new LatLng(latitude, longitude));
                    }
                    pathCreatedListener.onPathCreated(pathArray);
                    pathCreatedListener = null;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (showServerResponseListener != null) {
                    if (serverResponse != null || !StringEquals.equals(serverResponse, "")) {
                        if(StringEquals.equals(serverResponse, "OK")){
                            showServerResponseListener.onShowServerResponse(mContext.getString(R.string.found_message));
                        }
                        if(StringEquals.equals(serverResponse, "NOT_FOUND") ||
                           StringEquals.equals(serverResponse, "ZERO_RESULTS") ||
                           StringEquals.equals(serverResponse, "INVALID_REQUEST") ||
                           StringEquals.equals(serverResponse, "OVER_QUERY_LIMIT") ||
                           StringEquals.equals(serverResponse, "REQUEST_DENIED") ||
                           StringEquals.equals(serverResponse, "UNKNOWN_ERROR")){
                            showServerResponseListener.onShowServerResponse(serverResponse);

                        }else if(!StringEquals.equals(serverResponse, "OK")){
                            showServerResponseListener.onShowServerResponse(mContext.getString(R.string.unknown_message));
                        }
                        showServerResponseListener = null;
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showServerResponseListener.onShowServerResponse(mContext.getString(R.string.unknown_message));
                showServerResponseListener = null;
            }
        });
        queue.add(jsObjectRequest);
    }

    public void setOnPathListener(MapsActivity view) {
        pathCreatedListener = (OnPathCreated) view;
    }

    public OnShowServerResponse getShowServerResponseListener() {
        return showServerResponseListener;
    }

    public void setShowServerResponseListener(OnShowServerResponse showServerResponseListener) {
        this.showServerResponseListener = showServerResponseListener;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public void clear() {
        if (queue != null) {
            queue.stop();
            queue = null;
        }
        if (pathArray != null) {
            pathArray.clear();
            pathArray = null;
        }
        pathCreatedListener = null;
        showServerResponseListener = null;
    }
    private List<LatLng> decodeOwerviewPolyline(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}

