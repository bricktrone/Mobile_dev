package ru.mirea.khrechkorv.mireaproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.runtime.image.ImageProvider;

import ru.mirea.khrechkorv.mireaproject.R;

public class InterestingPlacesFragment extends Fragment {
    private MapView mapView;
    private MapObjectCollection mapObjects;

    // Слушатель кликов по маркерам
    private final MapObjectTapListener mapObjectTapListener = new MapObjectTapListener() {
        @Override
        public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
            if (mapObject.getUserData() instanceof String[]) {
                String[] data = (String[]) mapObject.getUserData();
                String title = data[0];
                String description = data[1];

                // Выводим описание. Можно заменить на диалоговое окно или bottom sheet
                Toast.makeText(requireContext(), title + "\n\n" + description, Toast.LENGTH_LONG).show();
                return true;
            }
            return false;
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.initialize(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ineresting_places, container, false);
        mapView = root.findViewById(R.id.mapView);

        // Инициализируем коллекцию для маркеров
        mapObjects = mapView.getMap().getMapObjects().addCollection();

        // Установка начальной точки и зума (15.0)
        mapView.getMap().move(
                new CameraPosition(new Point(55.6700, 37.4800), 15.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null
        );

        // Включение слоя пользователя, который активирует встроенный компас
        MapKit mapKit = MapKitFactory.getInstance();
        UserLocationLayer userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingModeActive(true); // Включает стрелку направления/компас

        // Добавление маркеров
        addMarker(55.794229, 37.700772,
                "РТУ МИРЭА",
                "Учебный корпус и интересное место для учебы");
        addMarker(55.829962, 37.640809,
                "ВДНХ",
                "Гигантский выставочный комплекс с фонтанами, павильонами и музеем космонавтики");
        addMarker(55.740557, 37.608502,
                "Музей современного искусства «Гараж»",
                "Музей современного искусства в парке Горького, здание от Рема Колхаса");
        addMarker(55.751244, 37.618423,
                "Красная площадь",
                "Главная площадь страны, здесь находятся Кремль, Храм Василия Блаженного и Мавзолей");

        return root;
    }

    private void addMarker(double latitude, double longitude, String title, String description) {
        Point point = new Point(latitude, longitude);

        // Создаем маркер со стандартной иконкой (нужно добавить иконку в res/drawable, напримерic_pin)
        // Если иконки нет, можно использовать ImageProvider.fromBitmap(...)
        PlacemarkMapObject placemark = mapObjects.addPlacemark(
                point,
                ImageProvider.fromResource(requireContext(), android.R.drawable.ic_dialog_map)
        );

        // Сохраняем данные внутри маркера
        placemark.setUserData(new String[]{title, description});

        // Добавляем слушатель клика
        placemark.addTapListener(mapObjectTapListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }
}
