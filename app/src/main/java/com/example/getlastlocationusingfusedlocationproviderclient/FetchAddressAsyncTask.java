package com.example.getlastlocationusingfusedlocationproviderclient;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

interface OnTaskCompleted {
    void onTaskCompleted(String result);
}
public class FetchAddressAsyncTask extends AsyncTask<Location,Void,String> {

    private Context context;
    private OnTaskCompleted mlistener;
    public FetchAddressAsyncTask(Context context,OnTaskCompleted taskCompleted)
    {
        this.context=context;
        mlistener=taskCompleted;
    }

    @Override
    protected void onPostExecute(String s) {
        mlistener.onTaskCompleted(s);
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(Location... locations) {

        Geocoder geocoder=new Geocoder(context, Locale.getDefault());
        Location location=locations[0];
        String resultMessage="";
        List<Address> addresses=null;

        try {
            addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TAG","Service Not available");
            resultMessage="service not available";
        }catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values
            resultMessage = "Invalid coordinates";
             Log.d("TAG","invalid coordiantes");
        }

        if (addresses == null || addresses.size() == 0) {
            if (resultMessage.isEmpty()) {
                resultMessage = "No Address Found";
            }
        }
        else {
            // If an address is found, read it into resultMessage
            Address address = addresses.get(0);
            ArrayList<String> addressParts = new ArrayList<>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressParts.add(address.getAddressLine(i));
            }

            resultMessage = TextUtils.join("\n", addressParts);
        }
        return resultMessage;
    }
}
