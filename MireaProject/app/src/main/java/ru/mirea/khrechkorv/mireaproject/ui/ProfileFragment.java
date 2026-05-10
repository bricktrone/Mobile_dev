package ru.mirea.khrechkorv.mireaproject.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.mirea.khrechkorv.mireaproject.R;

public class ProfileFragment extends Fragment {
    private EditText etFullName, etAge, etCity, etEmail, etFavoriteGenre;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "user_profile";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        etFullName = root.findViewById(R.id.editTextFullName);
        etAge = root.findViewById(R.id.editTextAge);
        etCity = root.findViewById(R.id.editTextCity);
        etEmail = root.findViewById(R.id.editTextEmail);
        etFavoriteGenre = root.findViewById(R.id.editTextFavoriteGenre);
        Button btnSave = root.findViewById(R.id.buttonSaveProfile);

        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        loadProfile();

        btnSave.setOnClickListener(v -> saveProfile());

        return root;
    }

    private void loadProfile() {
        etFullName.setText(sharedPreferences.getString("full_name", ""));
        etAge.setText(sharedPreferences.getString("age", ""));
        etCity.setText(sharedPreferences.getString("city", ""));
        etEmail.setText(sharedPreferences.getString("email", ""));
        etFavoriteGenre.setText(sharedPreferences.getString("favorite_genre", ""));
    }

    private void saveProfile() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("full_name", etFullName.getText().toString());
        editor.putString("age", etAge.getText().toString());
        editor.putString("city", etCity.getText().toString());
        editor.putString("email", etEmail.getText().toString());
        editor.putString("favorite_genre", etFavoriteGenre.getText().toString());
        editor.apply();

        Toast.makeText(getContext(), "Профиль сохранен", Toast.LENGTH_SHORT).show();
    }
}
