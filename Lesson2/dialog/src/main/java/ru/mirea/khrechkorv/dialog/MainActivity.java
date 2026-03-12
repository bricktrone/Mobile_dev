package ru.mirea.khrechkorv.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        rootView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onClickShowDialog(View view) {
        AlertDialogFragment dialogFragment = new AlertDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "mirea");
    }

    public void onOkClicked() {
        Toast.makeText(getApplicationContext(), "Вы выбрали кнопку \"Иду дальше\"!",
                Toast.LENGTH_LONG).show();
    }
    public void onCancelClicked() {
        Toast.makeText(getApplicationContext(), "Вы выбрали кнопку \"Нет\"!",
                Toast.LENGTH_LONG).show();
    }
    public void onNeutralClicked() {
        Toast.makeText(getApplicationContext(), "Вы выбрали кнопку \"На паузе\"!",
                Toast.LENGTH_LONG).show();
    }

    public void onTimePickDialog(View view){
        MyTimeDialogFragment dialogFragment = new MyTimeDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "time picker");
    }

    public void onTimePicked(int hours, int minutes){
        Snackbar snackbar = Snackbar.make(rootView, Integer.toString(hours) + " часов, "
                        + Integer.toString(minutes) + " минут.", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void onDatePickDialog(View view){
        MyDateDialogFragment dialogFragment = new MyDateDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "date picker");
    }

    public void onDatePicked(int year, int month, int day){
        Snackbar snackbar = Snackbar.make(rootView, Integer.toString(year) + " год, "
                + Integer.toString(month + 1) + " месяц, " + Integer.toString(day) + " день."
                , Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void onProgressDialog(View view){
        MyProgressDialogFragment dialogFragment = new MyProgressDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "progress");
    }

    public void onProgressCanceled(){
        Snackbar snackbar = Snackbar.make(rootView, "окно загрузки закрыто", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}