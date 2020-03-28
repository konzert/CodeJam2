package ca.hh.codejam_android;


import com.google.android.gms.maps.model.LatLng;

import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem {
    private final LatLng mPosition;
    private final String mTitle;
    private final String mSnippet;
    private String mProb;

    public MyItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
        mTitle = "title";
        mSnippet = "snippet";
    }

    public MyItem(double lat, double lng, String title, String snippet, String prob) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
        mProb = prob;
    }


    public LatLng getPosition() {
        return mPosition;
    }


    public String getTitle() {
        return mTitle;
    }


    public String getSnippet() {
        return mSnippet;
    }

    public String getProb() { return mProb; }
}
