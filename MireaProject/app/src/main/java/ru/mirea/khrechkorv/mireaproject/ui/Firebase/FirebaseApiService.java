package ru.mirea.khrechkorv.mireaproject.ui.Firebase;

import retrofit2.Call;
import retrofit2.http.*;

import ru.mirea.khrechkorv.mireaproject.ui.Firebase.ApiResponse;
import ru.mirea.khrechkorv.mireaproject.ui.Firebase.UserProfile;

public interface FirebaseApiService {
    @GET("profiles/{userId}.json")
    Call<UserProfile> getProfile(@Path("userId") String userId);

    @PATCH("profiles/{userId}.json")
    Call<UserProfile> saveProfile(@Path("userId") String userId, @Body UserProfile profile);
    @PATCH("profiles/{userId}.json")
    Call<UserProfile> updateProfile(@Path("userId") String userId, @Body UserProfile profile);
}
