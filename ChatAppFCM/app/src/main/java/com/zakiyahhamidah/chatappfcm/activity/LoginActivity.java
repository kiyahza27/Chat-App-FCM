package com.zakiyahhamidah.chatappfcm.activity;

import static com.zakiyahhamidah.chatappfcm.activity.StartActivity.BASE_URL;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.zakiyahhamidah.chatappfcm.R;
import com.zakiyahhamidah.chatappfcm.api.ApiInterface;
import com.zakiyahhamidah.chatappfcm.database.DbRepo;
import com.zakiyahhamidah.chatappfcm.model.LoggedInUserModel;
import com.zakiyahhamidah.chatappfcm.model.UserModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    Context context;
    DbRepo databaseRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        databaseRepository = new DbRepo(context);

        bind_views();
    }

    TextInputEditText phone_number_view;
    TextView status;

    private void bind_views() {

        phone_number_view = findViewById(R.id.phone_number_view);
        status = findViewById(R.id.data);
        findViewById(R.id.tittlelogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LoginActivity.class);
            }
        });

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_user();
            }
        });
    }

    String phone_value;
    ProgressDialog progressDialog;
    LoggedInUserModel loggedInUserModel;

    private void login_user() {

        phone_value = phone_number_view.getText().toString();
        if (phone_value.length() < 5) {
            Toast.makeText(this, "Enter valid phone number.", Toast.LENGTH_SHORT).show();
            phone_number_view.requestFocus();
            return;
        }

        Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show();
        status.setText("Loading....");
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //step 1
        Retrofit ret = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Step 2
        ApiInterface webInterface = ret.create(ApiInterface.class);

        //Step 3
        Call<List<UserModel>> my_call = webInterface.get_user(
                phone_value
        );

        my_call.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                progressDialog.hide();
                progressDialog.dismiss();
                if (!response.isSuccessful()) {
                    status.setText("Success BUT " + response.errorBody());
                    return;
                }
                if(response.body().isEmpty()){
                    status.setText("Phone number not found on databse.");
                    return;
                }

                try {

                    loggedInUserModel = new LoggedInUserModel();
                    loggedInUserModel.last_seen = response.body().get(0).last_seen;
                    loggedInUserModel.name = response.body().get(0).name;
                    loggedInUserModel.phone_number = response.body().get(0).phone_number;
                    loggedInUserModel.profile_photo = response.body().get(0).profile_photo;
                    loggedInUserModel.reg_date = response.body().get(0).reg_date;
                    loggedInUserModel.user_id = response.body().get(0).user_id;
                    databaseRepository.login_user(loggedInUserModel);
                    Intent i = new Intent(context, ChatsActivity.class);
                    context.startActivity(i);
                    finish();

                } catch (Exception e) {
                    status.setText("Failed to login user because " + e.getMessage());
                    Toast.makeText(context, "Failed to login user because " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }



            }

            @Override
            public void onFailure(Call<List<UserModel>> call, Throwable t) {
                progressDialog.hide();
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                status.setText("FAILED: " + t.getMessage());
            }
        });


    }
}