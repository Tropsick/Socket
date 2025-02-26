package com.example.goodisnear;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.goodisnear.databinding.ActivityScrollingHelpBinding;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ScrollingActivityHelp extends AppCompatActivity {

    private Spinner categorySpinner, priceSpinner;
    private EditText problemDescription;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_help);

        categorySpinner = findViewById(R.id.category_spinner);
        priceSpinner = findViewById(R.id.price_spinner);
        problemDescription = findViewById(R.id.problem_description);
        sendButton = findViewById(R.id.send);

        sendButton.setOnClickListener(v -> sendHelpRequest());
    }

    private void sendHelpRequest() {
        String category = categorySpinner.getSelectedItem().toString();
        String price = priceSpinner.getSelectedItem().toString();
        String description = problemDescription.getText().toString().trim();

        if (description.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, опишите свою проблему", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "Аноним");

        new Thread(() -> {
            try {
                URL url = new URL("https://yourserver.com/api/help_requests"); // Укажите свой API-адрес
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setDoOutput(true);

                JSONObject requestBody = new JSONObject();
                requestBody.put("username", username);
                requestBody.put("category", category);
                requestBody.put("price", price);
                requestBody.put("description", description);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = requestBody.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    runOnUiThread(() -> Toast.makeText(this, "Запрос отправлен!", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Ошибка подключения", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
}