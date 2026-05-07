package ru.mirea.khrechkorv.mireaproject.ui;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import ru.mirea.khrechkorv.mireaproject.R;

public class SensorFragment extends Fragment implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor orientationSensor;
    private TextView tvDirectionValue;
    private TextView tvDirectionStatus;
    private TextView tvDegreesValue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sensor, container, false);

        tvDirectionValue = root.findViewById(R.id.tvDirectionValue);
        tvDirectionStatus = root.findViewById(R.id.tvDirectionStatus);
        tvDegreesValue = root.findViewById(R.id.tvDegreesValue);

        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        if (orientationSensor == null) {
            tvDirectionStatus.setText("Сенсор ориентации не доступен");
            tvDirectionStatus.setTextColor(Color.RED);
        }

        return root;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            float azimuth = event.values[0];

            tvDegreesValue.setText(String.format(Locale.getDefault(), "Угол поворота: %.1f°", azimuth));

            String direction = getDirection(azimuth);
            int color = getColorForDirection(direction);

            tvDirectionValue.setText(direction);
            tvDirectionValue.setTextColor(color);
            tvDirectionStatus.setText("Текущее направление:");
            tvDirectionStatus.setTextColor(Color.BLACK);
        }
    }

    private String getDirection(float azimuth) {
        if (azimuth >= 337.5f || azimuth < 22.5f) {
            return "СЕВЕР";
        } else if (azimuth >= 22.5f && azimuth < 67.5f) {
            return "СЕВЕРО-ВОСТОК";
        } else if (azimuth >= 67.5f && azimuth < 112.5f) {
            return "ВОСТОК";
        } else if (azimuth >= 112.5f && azimuth < 157.5f) {
            return "ЮГО-ВОСТОК";
        } else if (azimuth >= 157.5f && azimuth < 202.5f) {
            return "ЮГ";
        } else if (azimuth >= 202.5f && azimuth < 247.5f) {
            return "ЮГО-ЗАПАД";
        } else if (azimuth >= 247.5f && azimuth < 292.5f) {
            return "ЗАПАД";
        } else if (azimuth >= 292.5f && azimuth < 337.5f) {
            return "СЕВЕРО-ЗАПАД";
        }
        return "НЕИЗВЕСТНО";
    }

    private int getColorForDirection(String direction) {
        switch (direction) {
            case "СЕВЕР":
                return Color.BLUE;
            case "ЮГ":
                return Color.RED;
            case "ВОСТОК":
                return Color.parseColor("#FFA500");
            case "ЗАПАД":
                return Color.parseColor("#800080");
            case "СЕВЕРО-ВОСТОК":
                return Color.parseColor("#00BFFF");
            case "СЕВЕРО-ЗАПАД":
                return Color.parseColor("#00CED1");
            case "ЮГО-ВОСТОК":
                return Color.parseColor("#FF8C00");
            case "ЮГО-ЗАПАД":
                return Color.parseColor("#FF69B4");
            default:
                return Color.GRAY;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onResume() {
        super.onResume();
        if (orientationSensor != null) {
            sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}
