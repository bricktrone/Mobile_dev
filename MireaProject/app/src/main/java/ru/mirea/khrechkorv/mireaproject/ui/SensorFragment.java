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
    private Sensor accelerometer;
    private Sensor magnetometer;
    private TextView tvDirectionValue;
    private TextView tvDirectionStatus;
    private TextView tvDegreesValue;

    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sensor, container, false);

        tvDirectionValue = root.findViewById(R.id.tvDirectionValue);
        tvDirectionStatus = root.findViewById(R.id.tvDirectionStatus);
        tvDegreesValue = root.findViewById(R.id.tvDegreesValue);

        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (accelerometer == null || magnetometer == null) {
            tvDirectionStatus.setText("Ошибка: Датчики не найдены");
            tvDirectionStatus.setTextColor(Color.RED);
        }

        return root;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Сохраняем данные с акселерометра
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
            lastAccelerometerSet = true;
        }
        // Сохраняем данные с магнитометра
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
            lastMagnetometerSet = true;
        }

        // Если есть оба набора данных - вычисляем ориентацию
        if (lastAccelerometerSet && lastMagnetometerSet) {
            float[] rotationMatrix = new float[9];
            float[] orientationAngles = new float[3];

            // 1. Получаем матрицу поворота на основе гравитации и магнитного поля Земли [citation:6][citation:8]
            boolean success = SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer);

            if (success) {
                // 2. Получаем углы ориентации из матрицы [citation:2]
                // values[0] = Azimuth (угол между осью Y и севером) в радианах
                // values[1] = Pitch (наклон вперед/назад)
                // values[2] = Roll (наклон вправо/влево)
                SensorManager.getOrientation(rotationMatrix, orientationAngles);

                // 3. Переводим радианы в градусы и нормализуем азимут в диапазон 0..360 [citation:8]
                float azimuth = (float) Math.toDegrees(orientationAngles[0]);
                azimuth = (azimuth + 360) % 360; // Теперь: 0 = Север, 90 = Восток и т.д.

                // Обновляем UI
                tvDegreesValue.setText(String.format(Locale.getDefault(), "Угол поворота: %.1f°", azimuth));

                String direction = getDirection(azimuth);
                int color = getColorForDirection(direction);

                tvDirectionValue.setText(direction);
                tvDirectionValue.setTextColor(color);
                tvDirectionStatus.setText("Текущее направление:");
                tvDirectionStatus.setTextColor(Color.BLACK);
            }
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
        // Регистрируем оба сенсора
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        if (magnetometer != null) {
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        }
}
