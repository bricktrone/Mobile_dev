package ru.mirea.khrechkorv.mireaproject.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.mirea.khrechkorv.mireaproject.LoginActivity;
import ru.mirea.khrechkorv.mireaproject.R;

public class InternetRecourceFragment extends Fragment {

    private EditText editTextFullName;
    private EditText editTextGroup;
    private EditText editTextNumber;
    private EditText editTextFavoriteFilm;
    private Button buttonSaveProfile;

    private TextView textViewEmail;
    private TextView textViewVerifyStatus;
    private Button buttonSendVerification;
    private Button buttonReloadStatus;
    private Button buttonSignOut;

    private FirebaseAuth mAuth;

    public InternetRecourceFragment() {
        super(R.layout.fragment_internet_recource);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextFullName = view.findViewById(R.id.editTextFullName);
        editTextGroup = view.findViewById(R.id.editTextGroup);
        editTextNumber = view.findViewById(R.id.editTextNumber);
        editTextFavoriteFilm = view.findViewById(R.id.editTextFavoriteFilm);
        buttonSaveProfile = view.findViewById(R.id.buttonSaveProfile);

        textViewEmail = view.findViewById(R.id.textViewEmail);
        textViewVerifyStatus = view.findViewById(R.id.textViewVerifyStatus);
        buttonSendVerification = view.findViewById(R.id.buttonSendVerification);
        buttonReloadStatus = view.findViewById(R.id.buttonReloadStatus);
        buttonSignOut = view.findViewById(R.id.buttonSignOut);

        mAuth = FirebaseAuth.getInstance();

        loadProfile();
        updateFirebaseUserInfo(mAuth.getCurrentUser());

        buttonSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

        buttonSendVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationEmail();
            }
        });

        buttonReloadStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadUser(true);
            }
        });

        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadUser(false);
    }

    private void saveProfile() {
        SharedPreferences preferences = requireActivity().getSharedPreferences(
                "profile_settings",
                Context.MODE_PRIVATE
        );

        preferences.edit()
                .putString("FULL_NAME", editTextFullName.getText().toString())
                .putString("GROUP", editTextGroup.getText().toString())
                .putString("NUMBER", editTextNumber.getText().toString())
                .putString("FAVORITE_FILM", editTextFavoriteFilm.getText().toString())
                .apply();

        Toast.makeText(requireContext(), "Профиль сохранён", Toast.LENGTH_SHORT).show();
    }

    private void loadProfile() {
        SharedPreferences preferences = requireActivity().getSharedPreferences(
                "profile_settings",
                Context.MODE_PRIVATE
        );

        editTextFullName.setText(preferences.getString("FULL_NAME", ""));
        editTextGroup.setText(preferences.getString("GROUP", ""));
        editTextNumber.setText(preferences.getString("NUMBER", ""));
        editTextFavoriteFilm.setText(preferences.getString("FAVORITE_FILM", ""));
    }

    private void updateFirebaseUserInfo(FirebaseUser user) {
        if (user != null) {
            textViewEmail.setText("Email: " + (user.getEmail() != null ? user.getEmail() : "--"));
            textViewVerifyStatus.setText(user.isEmailVerified() ?
                    "Статус почты: Почта подтверждена" :
                    "Статус почты: Почта не подтверждена");

            buttonSendVerification.setEnabled(!user.isEmailVerified());
            buttonReloadStatus.setEnabled(true);
            buttonSignOut.setEnabled(true);
        } else {
            textViewEmail.setText("Email: пользователь не авторизован");
            textViewVerifyStatus.setText("Статус почты: --");

            buttonSendVerification.setEnabled(false);
            buttonReloadStatus.setEnabled(false);
            buttonSignOut.setEnabled(false);
        }
    }

    private void sendVerificationEmail() {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(requireContext(), "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
            return;
        }

        buttonSendVerification.setEnabled(false);

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(
                                    requireContext(),
                                    "Письмо отправлено на " + user.getEmail(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            Toast.makeText(
                                    requireContext(),
                                    "Не удалось отправить письмо",
                                    Toast.LENGTH_SHORT
                            ).show();
                            buttonSendVerification.setEnabled(true);
                        }
                    }
                });
    }

    private void reloadUser(final boolean showToast) {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            updateFirebaseUserInfo(null);
            return;
        }

        buttonReloadStatus.setEnabled(false);

        user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                buttonReloadStatus.setEnabled(true);

                if (task.isSuccessful()) {
                    FirebaseUser refreshedUser = mAuth.getCurrentUser();
                    updateFirebaseUserInfo(refreshedUser);

                    if (showToast) {
                        Toast.makeText(
                                requireContext(),
                                refreshedUser != null && refreshedUser.isEmailVerified() ?
                                        "Почта подтверждена" :
                                        "Почта пока не подтверждена",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                } else {
                    updateFirebaseUserInfo(user);
                    if (showToast) {
                        Toast.makeText(
                                requireContext(),
                                "Не удалось обновить статус",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
            }
        });
    }

    private void signOut() {
        mAuth.signOut();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}