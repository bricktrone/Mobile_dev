package ru.mirea.khrechkorv.mireaproject;

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

public class App extends Application {

    // Вставьте сюда ваш API-ключ, полученный в Кабинете Разработчика Яндекса
    private static final String MAPKIT_API_KEY = "19b554fc-3b4a-435a-a343-7aec6a8adf31";

    @Override
    public void onCreate() {
        super.onCreate();

        // Установка API-ключа выполняется строго ДО вызова MapKitFactory.initialize()
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
    }
}