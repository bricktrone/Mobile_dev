package ru.mirea.khrechkorv.mireaproject;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class MyWorker extends Worker {
    static final String TAG = "MyWorker";
    public MyWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }
    @Override
    public Result doWork() {
        try {
            Thread.sleep(5000);

            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> {
                Toast.makeText(getApplicationContext(),
                        "Работа завершена через 5 секунд!",
                        Toast.LENGTH_LONG).show();
            });

            return Result.success();

        } catch (InterruptedException e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}
