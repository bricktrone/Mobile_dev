package ru.mirea.khrechkorv.thread;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;

import ru.mirea.khrechkorv.thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private	int	counter	= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding	= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(new	View.OnClickListener()	{
            @Override
            public	void	onClick(View	v)	{
                new	Thread(new	Runnable()	{
                    public	void run()	{
                        int	numberThread	=	counter++;
                        Log.d("ThreadProject", String.format("Запущен поток № %d студентом группы № %s номер по " +
                                "списку № %d ",	numberThread, "БСБО-09-23",	-1));
                        long	endTime	=	System.currentTimeMillis()	+	20	*	1000;
                        while	(System.currentTimeMillis()	<	endTime)	{
                            synchronized	(this)	{
                                try	{
                                    wait(endTime	- System.currentTimeMillis());
                                    Log.d(MainActivity.class.getSimpleName(),	"Endtime: "	+	endTime);
                                }	catch	(Exception	e)	{
                                    throw	new	RuntimeException(e);
                                }
                            }
                            Log.d("ThreadProject",	"Выполнен поток № "	+	numberThread);
                        }
                    }
                }).start();
            }
        });

        Thread mainThread = Thread.currentThread();
        binding.TextView.setText("Имя текущего потока: " + mainThread.getName());
        // Меняем имя и выводим в текстовом поле
        mainThread.setName("МОЙ НОМЕР ГРУППЫ: 09-23, НОМЕР ПО СПИСКУ: 25, МОЙ ЛЮБИИМЫЙ ФИЛЬМ: а я и не знаю");
        binding.TextView.append("\n Новое имя потока: " + mainThread.getName());
        Log.d(MainActivity.class.getSimpleName(),	"Stack:	"	+	Arrays.toString(mainThread.getStackTrace()));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}