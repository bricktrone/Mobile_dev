// InternetRecourceFragment.java
package ru.mirea.khrechkorv.mireaproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mirea.khrechkorv.mireaproject.LoginActivity;
import ru.mirea.khrechkorv.mireaproject.R;
import ru.mirea.khrechkorv.mireaproject.ui.Firebase.FirebaseApiService;
import ru.mirea.khrechkorv.mireaproject.ui.Firebase.RetrofitClient;
import ru.mirea.khrechkorv.mireaproject.ui.Firebase.UserProfile;

public class InternetRecourceFragment extends Fragment {

    private EditText editTextFullName;
    private EditText editTextGroup;
    private EditText editTextNumber;
    private EditText editTextFavoriteFilm;
    private Button buttonSaveProfile;
    private Button buttonLoadProfile;

    private TextView textViewEmail;
    private TextView textViewVerifyStatus;
    private Button buttonSendVerification;
    private Button buttonReloadStatus;
    private Button buttonSignOut;

    private FirebaseAuth mAuth;
    private FirebaseApiService apiService;
    private String currentUserId;
    private boolean profileExists = false; // Флаг существования профиля

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
        buttonLoadProfile = view.findViewById(R.id.buttonLoadProfile);

        textViewEmail = view.findViewById(R.id.textViewEmail);
        textViewVerifyStatus = view.findViewById(R.id.textViewVerifyStatus);
        buttonSendVerification = view.findViewById(R.id.buttonSendVerification);
        buttonReloadStatus = view.findViewById(R.id.buttonReloadStatus);
        buttonSignOut = view.findViewById(R.id.buttonSignOut);

        mAuth = FirebaseAuth.getInstance();
        apiService = RetrofitClient.getApiService();

        updateFirebaseUserInfo(mAuth.getCurrentUser());

        if (mAuth.getCurrentUser() != null) {
            currentUserId = mAuth.getCurrentUser().getUid();
            // Проверяем существование профиля при загрузке
            checkIfProfileExists();
        }

        buttonSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileToFirebase();
            }
        });

        buttonLoadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadProfileFromFirebase();
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

    // Новый метод: проверяем существует ли профиль
    private void checkIfProfileExists() {
        if (currentUserId == null) return;

        showProgress(true);
        Call<UserProfile> call = apiService.getProfile(currentUserId);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                showProgress(false);

                if (response.isSuccessful() && response.body() != null) {
                    // Профиль существует - загружаем данные
                    profileExists = true;
                    UserProfile profile = response.body();
                    editTextFullName.setText(profile.getFullName() != null ? profile.getFullName() : "");
                    editTextGroup.setText(profile.getGroup() != null ? profile.getGroup() : "");
                    editTextNumber.setText(profile.getNumber() != null ? profile.getNumber() : "");
                    editTextFavoriteFilm.setText(profile.getFavoriteFilm() != null ? profile.getFavoriteFilm() : "");

                    Toast.makeText(requireContext(), "Профиль загружен автоматически", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 404) {
                    // Профиль не существует - это нормально
                    profileExists = false;
                    Toast.makeText(requireContext(),
                            "Добро пожаловать! Заполните данные и нажмите 'Сохранить'",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                showProgress(false);
                Toast.makeText(requireContext(), "Ошибка проверки: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfileToFirebase() {
        if (currentUserId == null) {
            Toast.makeText(requireContext(), "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверяем обязательные поля
        if (editTextFullName.getText().toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Введите ФИО", Toast.LENGTH_SHORT).show();
            return;
        }

        UserProfile profile = new UserProfile(
                editTextFullName.getText().toString(),
                editTextGroup.getText().toString(),
                editTextNumber.getText().toString(),
                editTextFavoriteFilm.getText().toString()
        );

        setButtonsEnabled(false);
        showProgress(true);

        // Используем PUT для создания/обновления профиля
        Call<UserProfile> call = apiService.saveProfile(currentUserId, profile);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                setButtonsEnabled(true);
                showProgress(false);

                if (response.isSuccessful()) {
                    profileExists = true;
                    Toast.makeText(requireContext(), "✅ Профиль успешно сохранен в Firebase!", Toast.LENGTH_LONG).show();

                    // Проверяем, что данные действительно сохранились
                    verifyProfileSaved();
                } else {
                    Toast.makeText(requireContext(),
                            "Ошибка сохранения. Код: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                setButtonsEnabled(true);
                showProgress(false);
                Toast.makeText(requireContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Проверяем, что профиль действительно сохранился
    private void verifyProfileSaved() {
        Call<UserProfile> call = apiService.getProfile(currentUserId);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(),
                            "✅ Данные подтверждены в Firebase",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(),
                            "⚠️ Проверьте данные в Firebase Console",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                // Игнорируем ошибку проверки
            }
        });
    }

    private void loadProfileFromFirebase() {
        if (currentUserId == null) {
            Toast.makeText(requireContext(), "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
            return;
        }

        setButtonsEnabled(false);
        showProgress(true);

        Call<UserProfile> call = apiService.getProfile(currentUserId);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                setButtonsEnabled(true);
                showProgress(false);

                if (response.isSuccessful() && response.body() != null) {
                    // Профиль найден
                    UserProfile profile = response.body();
                    editTextFullName.setText(profile.getFullName() != null ? profile.getFullName() : "");
                    editTextGroup.setText(profile.getGroup() != null ? profile.getGroup() : "");
                    editTextNumber.setText(profile.getNumber() != null ? profile.getNumber() : "");
                    editTextFavoriteFilm.setText(profile.getFavoriteFilm() != null ? profile.getFavoriteFilm() : "");

                    profileExists = true;
                    Toast.makeText(requireContext(), "📥 Профиль загружен из Firebase", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 404) {
                    // Профиль не найден
                    profileExists = false;
                    Toast.makeText(requireContext(),
                            "❌ Профиль не найден!\nСначала заполните данные и нажмите 'Сохранить профиль'",
                            Toast.LENGTH_LONG).show();

                    // Очищаем поля для нового профиля
                    clearProfileFields();
                } else {
                    Toast.makeText(requireContext(),
                            "Ошибка загрузки. Код: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                setButtonsEnabled(true);
                showProgress(false);
                Toast.makeText(requireContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearProfileFields() {
        editTextFullName.setText("");
        editTextGroup.setText("");
        editTextNumber.setText("");
        editTextFavoriteFilm.setText("");
    }

    private void setButtonsEnabled(boolean enabled) {
        buttonSaveProfile.setEnabled(enabled);
        buttonLoadProfile.setEnabled(enabled);
    }

    private void showProgress(boolean show) {
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
                                    Toast.LENGTH_LONG
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}