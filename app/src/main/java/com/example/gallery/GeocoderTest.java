package com.example.gallery;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.List;

public class GeocoderTest {
//    public GeocoderTest newInstance() {
//        return new GeocoderTest();
//    }

    public String coordsToAddres(Context context, Location location) {
        Geocoder gc = new Geocoder(context);
        String result = "";
        try {
            List<Address> addressList = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Address address = addressList.get(0);

            StringBuffer str = new StringBuffer();
            str.append("City: " + address.getLocality() + "\n");
            str.append("Address: " + address.getAddressLine(0));
            result = str.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
