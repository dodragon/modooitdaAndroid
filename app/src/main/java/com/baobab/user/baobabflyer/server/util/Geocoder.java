package com.baobab.user.baobabflyer.server.util;

import android.content.Context;
import android.location.Address;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Geocoder {

    Context context;

    public Geocoder(Context context) {
        this.context = context;
    }

    public String getCurrentAddress(double latitude, double longitude){
        android.location.Geocoder geocoder = new android.location.Geocoder(context, Locale.getDefault());

        List<Address> addresses;

        try{
            addresses = geocoder.getFromLocation(latitude, longitude, 7);
        }catch (IOException | IllegalArgumentException e){
            return "주소 미발견 : 여기를 눌러 직접 설정해주세요.";
        }

        if(addresses == null || addresses.size() == 0){
            return "주소 미발견 : 여기를 눌러 직접 설정해주세요.";
        }

        Address address = addresses.get(0);
        return address.getAddressLine(0) + "\n";
    }
}
