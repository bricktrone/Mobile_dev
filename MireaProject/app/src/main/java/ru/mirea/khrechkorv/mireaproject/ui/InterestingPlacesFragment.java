package ru.mirea.khrechkorv.mireaproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;

import ru.mirea.khrechkorv.mireaproject.R;

public class InterestingPlacesFragment extends Fragment {
    private MapView mapView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ineresting_places, container, false);
        mapView = root.findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(55.6700, 37.4800); // Near MIREA
        mapController.setCenter(startPoint);

        // Add Compass
        CompassOverlay compassOverlay = new CompassOverlay(getContext(), new InternalCompassOrientationProvider(getContext()), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);

        // Add Establishments
        addMarker(55.794229, 37.700772,
                "РТУ МИРЭА", "Учебный корпус и интересное место для учебы");
        addMarker(55.829962, 37.640809,
                "ВДНХ", "Гигантский выставочный комплекс с фонтанами, павильонами и музеем космонавтики");
        addMarker(55.740557, 37.608502,
                "Музей современного искусства «Гараж»", "Музей современного искусства в парке Горького, здание от Рема Колхаса");
        addMarker(55.751244, 37.618423,
                "Красная площадь", "Главная площадь страны, здесь находятся Кремль, Храм Василия Блаженного и Мавзолей");
        return root;
    }

    private void addMarker(double latitude, double longitude, String title, String description) {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(latitude, longitude));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
        marker.setSnippet(description);
        mapView.getOverlays().add(marker);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
