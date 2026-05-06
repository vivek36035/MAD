package com.example.filehandling;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;   // ✅ IMPORTANT
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    Button b1, b2;
    TextView tv;
    EditText ed1;
    String data;
    private String file = "mydata.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = findViewById(R.id.button);
        b2 = findViewById(R.id.button2);
        ed1 = findViewById(R.id.editText);
        tv = findViewById(R.id.textview2);

        // Save File
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = ed1.getText().toString();
                try {
                    FileOutputStream fOut = openFileOutput(file, MODE_APPEND);
                    fOut.write(data.getBytes());
                    fOut.close();
                    Toast.makeText(getBaseContext(), "File saved", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Read File
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FileInputStream fin = openFileInput(file);
                    int c;
                    String temp = "";

                    while ((c = fin.read()) != -1) {
                        temp = temp + Character.toString((char) c);
                    }

                    tv.setText(temp);
                    Toast.makeText(getBaseContext(), "File read", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "Error reading file", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}