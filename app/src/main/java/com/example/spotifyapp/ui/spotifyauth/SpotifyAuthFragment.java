package com.example.spotifyapp.ui.spotifyauth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spotifyapp.R;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLOutput;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyAuthFragment extends Fragment {

    public static final String CLIENT_ID = "66543f1060f94bde954afafe1e5ce2ae";
    public static final String REDIRECT_URI = "spotifyapp://auth";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private Call mCall;

    private TextView tokenTextView, codeTextView, profileTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spotify_auth, container, false);

        // Initialize the views
        tokenTextView = view.findViewById(R.id.token_text_view);
        codeTextView = view.findViewById(R.id.code_text_view);
        profileTextView = view.findViewById(R.id.response_text_view);

        // Initialize the buttons and set their click listeners
        Button tokenBtn = view.findViewById(R.id.token_btn);
        tokenBtn.setOnClickListener(v -> getToken());

        Button codeBtn = view.findViewById(R.id.code_btn);
        codeBtn.setOnClickListener(v -> getCode());

        Button profileBtn = view.findViewById(R.id.profile_btn);
        profileBtn.setOnClickListener(v -> onGetUserProfileClicked());

        return view;
    }

    private void getToken() {
        Log.d("SpotifyAuth", "Requesting token");
        AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        Log.d("SpotifyAuth", "Opening login activity for token. Request: " + request.toString());
        AuthorizationClient.openLoginActivity(getActivity(), AUTH_TOKEN_REQUEST_CODE, request);
    }

    private void getCode() {
        Log.d("SpotifyAuth", "Requesting code");
        AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        Log.d("SpotifyAuth", "Opening login activity for code. Request: " + request.toString());
        AuthorizationClient.openLoginActivity(getActivity(), AUTH_CODE_REQUEST_CODE, request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
        Log.d("SpotifyAuth", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode + ", data=" + data);
        if (response != null) {
            Log.d("SpotifyAuth", "Auth response type: " + response.getType() + ", code: " + response.getCode());
        }

        if (requestCode == AUTH_TOKEN_REQUEST_CODE) {
            mAccessToken = response.getAccessToken();
            Log.d("SpotifyAuth", "Access Token: " + mAccessToken);
            setTextAsync(mAccessToken, tokenTextView);
        } else if (requestCode == AUTH_CODE_REQUEST_CODE) {
            mAccessCode = response.getCode();
            Log.d("SpotifyAuth", "Access Code: " + mAccessCode);
            setTextAsync(mAccessCode, codeTextView);
        }
    }

    private void onGetUserProfileClicked() {
        if (mAccessToken == null) {
            Log.d("SpotifyAuth", "Access token is null");
            Toast.makeText(getContext(), "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("SpotifyAuth", "Fetching user profile with token: " + mAccessToken);
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("SpotifyAuth", "Failed to fetch data: " + e);
                Toast.makeText(getContext(), "Failed to fetch data, watch Logcat for more details", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("SpotifyAuth", "User profile response: " + responseBody);
                try {
                    final JSONObject jsonObject = new JSONObject(responseBody);
                    setTextAsync(jsonObject.toString(3), profileTextView);
                } catch (JSONException e) {
                    Log.d("SpotifyAuth", "Failed to parse data: " + e);
                    Toast.makeText(getContext(), "Failed to parse data, watch Logcat for more details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void setTextAsync(final String text, TextView textView) {
        getActivity().runOnUiThread(() -> textView.setText(text));
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, REDIRECT_URI)
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email"})
                .build();
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    @Override
    public void onDestroy() {
        cancelCall();
        super.onDestroy();
    }
}
