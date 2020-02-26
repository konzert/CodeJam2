package ca.hh.codejam_android;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class CustomClusterRenderer extends DefaultClusterRenderer<MyItem> {

    private final Context mContext;

    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
    }

    @Override protected void onBeforeClusterItemRendered(MyItem item,
                                                         MarkerOptions markerOptions) {
        BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
        if (item.getSnippet().contains("Unavailable")) {
            markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
        } else {
            for (int i = 0; i< 100 ; i++) {
                if (item.getSnippet().contains("Availability: " + i + "%")) {
                    markerDescriptor = BitmapDescriptorFactory.defaultMarker(i);
                    break;
                }
            }
        }
        markerOptions.icon(markerDescriptor);

    }
}
