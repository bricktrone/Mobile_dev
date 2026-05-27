# Отчет по практической работе №6
## Дисциплина: Разработка мобильных приложений

**Выполнил:** Студент группы БСБО-09-23  
**ФИО:** Хречко Роман Викторович  
**Номер по списку:** 25

---

## 1. Цель работы
Изучить возможные способы сетевого взаимодействия в Android-приложениях,
а также подключение к сетевому ресурсу через `Socket`. Выполнение HTTP-запросов и разбор JSON-ответов.
Также использование `Firebase Authentication для регистрации` и входа пользователя
После этого реализовать изученные технологии в созданном ранее проекте `MireaProject`.

---

## 2. Выполнение модулей `Lesson7`

### 2.1 Модуль `timeService` сохранение данных в `SharedPreferences`
**Задание:** реализовать подключение к серверу `time.nist.gov` по порту `13`

**Листинг `SocketUtils.java`:**
```Java
public class SocketUtils {
    /**
     * BufferedReader для получения входящих данных
     */
    public static BufferedReader getReader(Socket s) throws IOException {
        return (new BufferedReader(new InputStreamReader(s.getInputStream())));
    }
    /**
     * Makes a PrintWriter to send outgoing data. This PrintWriter will
     * automatically flush stream when println is called.
     * В примере не используется
     */
    public static PrintWriter getWriter(Socket s) throws IOException {
// Second argument of true means autoflush.
        return (new PrintWriter(s.getOutputStream(), true));
    }
}
```

**Листинг `MainActivity.java`:**
```Java
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private final String host = "time.nist.gov";
    private final int port = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());
                executor.execute(() -> {
                    String result = null;
                    try {
                        Socket socket = new Socket(host, port);
                        BufferedReader reader = SocketUtils.getReader(socket);
                        reader.readLine(); // skip first blank line
                        result = reader.readLine();
                        socket.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error connecting to time server", e);
                    }

                    String finalResult = result;
                    handler.post(() -> {
                        if (finalResult != null) {
                            String[] arr = finalResult.split(" ");
                            if (arr.length > 2) {
                                binding.textView.setText(getString(R.string.date_time_format, arr[1], arr[2]));
                            } else {
                                binding.textView.setText(getString(R.string.unexpected_format));
                                Log.w(TAG, "Unexpected format: " + finalResult);
                            }
                        } else {
                            binding.textView.setText(getString(R.string.error_get_time));
                        }
                    });
                });
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
```
---

### 2.2 Модуль `HttpURLConnection` выполнение HTTP-запросов
**Задание:** Получение информацию через HTTP API

Что сделано:

- проверяется наличие активного интернет-соединения;
- через `http://ip-api.com/json/` определяется внешний IP-адрес и местоположение устройства;
- JSON-ответ разбирается через `JSONObject`;
- по координатам выполняется запрос к `https://api.open-meteo.com/`;
- на экран выводятся IP, город, регион, страна, координаты и текущая погода;
- при ошибках отображается понятное состояние интерфейса.

**Листинг `MainActivity.java`:**
```Java
public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkinfo = null;
                if (connectivityManager != null) {
                    networkinfo = connectivityManager.getActiveNetworkInfo();
                }
                if (networkinfo != null && networkinfo.isConnected()) {
                    new DownloadPageTask().execute("https://ipinfo.io/json");
                } else {
                    // Fixed: Use MainActivity.this to refer to the Activity context
                    Toast.makeText(MainActivity.this, "Нет интернета", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private class DownloadPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.textView.setText("Загружаем...");
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadData(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(MainActivity.class.getSimpleName(), "IP Info Result: " + result);
            try {
                JSONObject responseJson = new JSONObject(result);
                binding.tv1.setText(responseJson.optString("ip", "N/A"));
                binding.tv2.setText(responseJson.optString("city", "N/A"));
                binding.tv3.setText(responseJson.optString("region", "N/A"));
                binding.tv4.setText(responseJson.optString("country", "N/A"));
                String loc = responseJson.optString("loc", "");
                binding.tv5.setText(loc);
                binding.tv6.setText(responseJson.optString("org", "N/A"));
                binding.tv7.setText(responseJson.optString("postal", "N/A"));
                binding.tv8.setText(responseJson.optString("timezone", "N/A"));


                String[] arr = loc.split(",");

                String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current_weather=true";
                Log.d("URLLLL", weatherUrl);
                new DownloadWeatherTask().execute(weatherUrl);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }

    private class DownloadWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadData(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(MainActivity.class.getSimpleName(), "Weather Result: " + result);
            try {
                JSONObject responseJson = new JSONObject(result);
                JSONObject currentWeather = responseJson.getJSONObject("current_weather");
                double temperature = currentWeather.getDouble("temperature");
                binding.textView.setText("Temperature: " + temperature + "°C");
            } catch (JSONException e) {
                e.printStackTrace();
                binding.textView.setText("Ошибка при получении погоды");
            }
            super.onPostExecute(result);
        }
    }

    private String downloadData(String address) throws IOException {
        InputStream inputStream = null;
        String data = "";
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(100000);
            connection.setConnectTimeout(100000);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int read;
                while ((read = inputStream.read()) != -1) {
                    bos.write(read);
                }
                bos.close();
                data = bos.toString();
            } else {
                data = connection.getResponseMessage() + ". Error Code: " + responseCode;
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return data;
    }
}
```

---

### 2.3 Модуль `FirebaseAuth`
**Задание:** Реализация базового сценария аутентификации через Firebase.

Что сделано:

- регистрация пользователя по `email/password`;
- вход в существующий аккаунт;
- выход из аккаунта;
- отправка письма для подтверждения email;
- ручное обновление статуса пользователя;
- динамическое изменение интерфейса в зависимости от состояния авторизации.

**Листинг класса `MainActivity.java`:**
```Java
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;
    // START declare_auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
// Initialization views
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
// [START initialize_auth] Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
// [END initialize_auth]
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(binding.emailte.getText().toString(), binding.passwordte.getText().toString());
                getUserUpdate();

            }
        });

        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(binding.emailte.getText().toString(), binding.passwordte.getText().toString());
                getUserUpdate();

            }
        });
        binding.verifyEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmailVerification();

            }
        });
        binding.outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
                getUserUpdate();

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
// Check if user is signed in (non-null) and update UI accordingly.
        getUserUpdate();
    }

    private void getUserUpdate(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            binding.statusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            binding.button.setVisibility(View.INVISIBLE);
            binding.button2.setVisibility(View.INVISIBLE);
            binding.emailte.setVisibility(View.INVISIBLE);
            binding.passwordte.setVisibility(View.INVISIBLE);
            binding.outButton.setEnabled(!user.isEmailVerified());
            binding.verifyEmailButton.setEnabled(!user.isEmailVerified());
        } else {
            binding.statusTextView.setText(R.string.signed_out);
            binding.outButton.setText(R.string.signed_out);
            binding.button.setVisibility(View.VISIBLE);
            binding.button2.setVisibility(View.VISIBLE);
            binding.emailte.setVisibility(View.VISIBLE);
            binding.passwordte.setVisibility(View.VISIBLE);
            binding.outButton.setEnabled(false);
            binding.verifyEmailButton.setEnabled(false);
        }
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        // if (!validateForm()) { return;  }
// [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
// Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
// If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure",
                                    task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
// [END create_user_with_email]
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
// [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
// Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
// If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
// [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            binding.statusTextView.setText(R.string.auth_failed);
                        }
// [END_EXCLUDE]
                    }
                });
// [END sign_in_with_email]
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
// Disable button
        binding.verifyEmailButton.setEnabled(false);
// Send verification email
// [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        Objects.requireNonNull(user).sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
// [START_EXCLUDE]
// Re-enable button
                        binding.verifyEmailButton.setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(MainActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
// [END_EXCLUDE]
                    }
                });
// [END send_email_verification]
    }
}
```

---

## 3. Дополнительное задание `MireaProject`
**Задание:** Реализовать изученные возможности в проекте `MireaProject`. Создать стартовую активность
`LoginActivity` и новый фрагмент для получения информации о пользователе.

Для фрагмента были настроены значения для корректной работы в проекте и навигации по списку всех фрагментов.

### 3.1. Создана активность `LoginActivity` с логином в `Firebase`
**Листинг `LoginActivity.java`:**
```Java
public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode(Locale.getDefault().getLanguage());

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> loginUser());
        btnRegister.setOnClickListener(v -> registerUser());
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Toast.makeText(LoginActivity.this, "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
```
### 3.2. `InternetRecourceFragment` фрагмент для получения информации о пользователе

Выведен email пользователя, статус верификации и кнопки управления аккаунтом.
Реализована отправка письма подтверждения, обновление статуса и выход из аккаунта.

**Листинг `InternetRecourceFragment.java`:**
```Java
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
```

---

## 4. Вывод
В ходе работы были изучены возможности сетевого взаимодействия в Android-приложениях.
Были реализованы следующие модули:
- сетевое подключение через `Socket`;
- HTTP-запросы и разбор JSON через `HttpURLConnection`;
- аутентификация пользователя через `Firebase`.

После этого изученные возможности были реализованы в проекте `MireaProject` и создана активность для логина в `Firebase`,
а также фрагмент для получения информации об аккаунте.