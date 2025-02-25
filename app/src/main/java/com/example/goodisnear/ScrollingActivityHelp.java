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

public class ScrollingActivityHelp extends AppCompatActivity {

    public ImageButton btnShop,btnMain,btnProf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_help);

        // Делаем Status Bar прозрачным
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.background));

        btnMain = (ImageButton) findViewById(R.id.btnMain);
        btnShop = (ImageButton) findViewById(R.id.btnShop);
        btnProf = (ImageButton) findViewById(R.id.btnProf);


        // Связываем Spinner с ресурсами
        Spinner categorySpinner = findViewById(R.id.category_spinner);
        Spinner priceSpinner = findViewById(R.id.price_spinner);

        // Создаём адаптер с кастомным стилем
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.category_items, R.layout.spinner_item);
        categoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> priceAdapter = ArrayAdapter.createFromResource(
                this, R.array.price_items, R.layout.spinner_item);
        priceAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        priceSpinner.setAdapter(priceAdapter);
    }
    public void OnClick(View view) {
        Intent intent;
        if (view.getId() == btnShop.getId()) {
            intent = new Intent(this, ScrollingActivityShop.class);
            startActivity(intent);
        } else if (view.getId() == btnMain.getId()) {
            intent = new Intent(this, ScrollingMainActivity.class);
            startActivity(intent);
        } else if (view.getId() == btnProf.getId()) {
            intent = new Intent(this, ScrollingActivityProfile.class);
            startActivity(intent);
        }
    }

}