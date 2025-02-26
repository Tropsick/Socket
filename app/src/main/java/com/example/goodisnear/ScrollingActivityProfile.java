package com.example.goodisnear;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.BitmapShader;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;

public class ScrollingActivityProfile extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private ImageView profileImageView;
    private ImageButton btnShop, btnMain;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_profile);

        setupUI();
        loadProfileImage();
    }

    private void setupUI() {
        TextView usernameTextView = findViewById(R.id.textView5);
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "Гость");
        usernameTextView.setText(username);
        btnLogout = findViewById(R.id.exit); // Инициализируем кнопку выхода

        // Прозрачный Status Bar
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.background));

        btnMain = findViewById(R.id.btnMain);
        btnShop = findViewById(R.id.btnShop);
        profileImageView = findViewById(R.id.plus_icon);
        btnLogout.setOnClickListener(v -> logoutUser()); // Устанавливаем обработчик для выхода
        profileImageView.setOnClickListener(v -> checkCameraPermission());
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }

    private void logoutUser() {
        // Очистка данных из SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();  // Очищаем все данные пользователя
        editor.apply();

        // Закрытие текущей активности и переход на экран входа
        Intent intent = new Intent(this, AuthActivity.class); // Переходим в экран входа
        startActivity(intent);
        finish(); // Закрываем текущую активность
    }
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "Камера недоступна", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            Toast.makeText(this, "Разрешение на камеру не предоставлено", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                // Обрезаем изображение до круга
                imageBitmap = cropToCircle(imageBitmap);

                profileImageView.setImageBitmap(imageBitmap);

                // Меняем размеры ImageView только после установки фото
                profileImageView.getLayoutParams().width = WindowManager.LayoutParams.MATCH_PARENT;
                profileImageView.getLayoutParams().height = WindowManager.LayoutParams.MATCH_PARENT;
                profileImageView.requestLayout();
                profileImageView.setBackground(null);
                // Устанавливаем корректный ScaleType
                profileImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                saveProfileImage(imageBitmap);
            }
        }
    }

    private void loadProfileImage() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String encodedImage = prefs.getString("profile_image", null);

        if (encodedImage != null) {
            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            // Обрезаем загруженное изображение до круга
            decodedBitmap = cropToCircle(decodedBitmap);

            profileImageView.setImageBitmap(decodedBitmap);


            // Применяем параметры размера и ScaleType
            profileImageView.getLayoutParams().width = WindowManager.LayoutParams.MATCH_PARENT;
            profileImageView.getLayoutParams().height = WindowManager.LayoutParams.MATCH_PARENT;
            profileImageView.requestLayout();
            profileImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            profileImageView.setBackground(null);

        }
    }

    private Bitmap cropToCircle(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newSize = Math.min(width, height); // Определяем меньшую сторону (квадрат)

        // Вычисляем начальные координаты для центрирования обрезки
        int xOffset = (width - newSize) / 2;
        int yOffset = (height - newSize) / 2;

        Bitmap squareBitmap = Bitmap.createBitmap(bitmap, xOffset, yOffset, newSize, newSize);
        Bitmap outputBitmap = Bitmap.createBitmap(newSize, newSize, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(outputBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(squareBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        float radius = newSize / 2f;
        canvas.drawRoundRect(new RectF(0, 0, newSize, newSize), radius, radius, paint);

        return outputBitmap;
    }

    private void saveProfileImage(Bitmap bitmap) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

        editor.putString("profile_image", encodedImage);
        editor.apply();
    }

    public void OnClick(View view) {
        Intent intent;
        if (view.getId() == btnShop.getId()) {
            intent = new Intent(this, ScrollingActivityShop.class);
        } else if (view.getId() == btnMain.getId()) {
            intent = new Intent(this, ScrollingMainActivity.class);
        } else {
            intent = new Intent(this, ScrollingActivityProfile.class);
        }
        startActivity(intent);
    }
}
