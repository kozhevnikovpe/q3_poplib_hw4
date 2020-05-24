package com.pavelekozhevnikov.lesson4;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.pavelekozhevnikov.lesson4.api.Api;
import com.pavelekozhevnikov.lesson4.model.GithubUser;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    private TextView mInfoTextView;
    private ProgressBar progressBar;
    private EditText editText;

    Api api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInfoTextView = findViewById(R.id.tvLoad);
        progressBar = findViewById(R.id.progressBar);
        editText = findViewById(R.id.editText);
    }

    public void onButtonClick(View view) {
        if(editText.getText().toString().length()==0)
            return;
        mInfoTextView.setText("");
        Retrofit retrofit = null;
        try {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.github.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            api = retrofit.create(Api.class);
        } catch (Exception io) {
            mInfoTextView.setText("no retrofit: " + io.getMessage());
            return;
        }
        // Подготовили вызов на сервер
        Call<GithubUser> call = api.loadUser(editText.getText().toString());
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();
        if (networkinfo != null && networkinfo.isConnected()) {
            // Запускаем
            try {
                progressBar.setVisibility(View.VISIBLE);
                downloadOneUrl(call);
            } catch (IOException e) {
                e.printStackTrace();
                mInfoTextView.setText(e.getMessage());
            }
        } else {
            Toast.makeText(this, "Подключите интернет", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadOneUrl(Call<GithubUser> call) throws IOException {
        call.enqueue(new Callback<GithubUser>() {
            @Override
            public void onResponse(Call<GithubUser> call, Response<GithubUser> response) {
                if (response.isSuccessful()) {
                    if (response != null) {
                        GithubUser user = response.body();
                            mInfoTextView.append("\nLogin = " + user.getLogin()+
                                    "\nId = " + user.getId()+
                                    "\nURL = " + user.getUrl()+
                                    "\n-----------------");
                    }
                } else {
                    System.out.println("onResponse error: " + response.code());
                    mInfoTextView.setText("onResponse error: " + response.code());
                }
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(Call<GithubUser> call, Throwable t) {
                System.out.println("onFailure " + t);
                mInfoTextView.setText("onFailure " + t.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}
