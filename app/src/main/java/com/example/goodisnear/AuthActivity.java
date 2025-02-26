package com.example.goodisnear;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {

    private EditText etLogin, etPassword, etConfirmPassword;
    private Button btnLogin, btnRegister;
    private boolean isRegisterMode = false; // Флаг для переключения между входом и регистрацией

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(view -> handleLogin());
        btnRegister.setOnClickListener(view -> toggleRegisterMode());
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", null);

        if (username != null) {
            startMainActivity(username);
        }

    }

    private void toggleRegisterMode() {
        isRegisterMode = !isRegisterMode;
        if (isRegisterMode) {
            etConfirmPassword.setVisibility(View.VISIBLE);
            btnLogin.setText("Зарегистрироваться");
            btnRegister.setText("Уже есть аккаунт? Войти");
        } else {
            etConfirmPassword.setVisibility(View.GONE);
            btnLogin.setText("Войти");
            btnRegister.setText("Регистрация");
        }
    }

    private void handleLogin() {
        String login = etLogin.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Введите логин и пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isRegisterMode) {
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                return;
            }
            registerUser(login, password);
        } else {
            loginUser(login, password);
        }
    }

    private void registerUser(String login, String password) {
        Toast.makeText(this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
        startMainActivity(login);
    }


    private void loginUser(String login, String password) {
        Toast.makeText(this, "Вход успешен", Toast.LENGTH_SHORT).show();
        startMainActivity(login);
    }


    private void startMainActivity(String login) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", login); // Сохраняем логин пользователя
        editor.apply();

        SharedPreferences appPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        appPrefs.edit().putBoolean("isLoggedIn", true).apply();

        Intent intent = new Intent(this, ScrollingMainActivity.class);
        startActivity(intent);
        finish(); // Закрываем экран авторизации
    }

}
