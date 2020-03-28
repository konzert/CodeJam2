package ca.hh.codejam_android;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class CustomClusterRenderer extends DefaultClusterRenderer<MyItem> {

    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override protected void onBeforeClusterItemRendered(MyItem item,
                                                         MarkerOptions markerOptions) {
        BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
        if (item.getProb().equals("Unavailable")) {
            markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
        } else {
            markerDescriptor = BitmapDescriptorFactory.defaultMarker(Float.parseFloat(item.getProb()));
        }
        markerOptions.icon(markerDescriptor);

    }
}
