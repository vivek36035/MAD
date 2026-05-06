package com.example.inputcontrols;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    ToggleButton toggleButton;
    CheckBox checkBox1;
    Button button;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleButton = findViewById(R.id.toggleButton);
        checkBox1 = findViewById(R.id.checkBox1);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String result = "";

                if(toggleButton.isChecked()) {
                    result += "Toggle is ON\n";
                } else {
                    result += "Toggle is OFF\n";
                }

                if(checkBox1.isChecked()) {
                    result += "Mobile Technology Selected";
                } else {
                    result += "Mobile Technology Not Selected";
                }

                textView.setText(result);
            }
        });

    }
}