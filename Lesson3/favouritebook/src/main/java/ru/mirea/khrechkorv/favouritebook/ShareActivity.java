package ru.mirea.khrechkorv.favouritebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class ShareActivity extends AppCompatActivity {

    private EditText inputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_share);
        inputText = findViewById(R.id.editText);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            TextView developerBookText = findViewById(R.id.textView2);
            String developerBook = extras.getString(MainActivity.KEY);
            developerBookText.setText(String.format("Любимая книга разработчика – %s", developerBook));
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void SendResult(View view) {
        Intent data = new Intent();
        data.putExtra(MainActivity.USER_MESSAGE, inputText.getText().toString());
        setResult(Activity.RESULT_OK, data);
        finish();
    }
}