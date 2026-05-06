package com.example.registrationform;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button button1, button2, button3, button4;
    ListView listView;

    String[] items = {
            "Apple",
            "Banana",
            "Mango",
            "Orange",
            "Grapes"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔹 Grid Buttons
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);

        button1.setOnClickListener(v ->
                Toast.makeText(this, "Button 1 Clicked", Toast.LENGTH_SHORT).show());

        button2.setOnClickListener(v ->
                Toast.makeText(this, "Button 2 Clicked", Toast.LENGTH_SHORT).show());

        button3.setOnClickListener(v ->
                Toast.makeText(this, "Button 3 Clicked", Toast.LENGTH_SHORT).show());

        button4.setOnClickListener(v ->
                Toast.makeText(this, "Button 4 Clicked", Toast.LENGTH_SHORT).show());

        // 🔹 ListView
        listView = findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                items
        );

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) ->
                Toast.makeText(this,
                        "Selected: " + items[position],
                        Toast.LENGTH_SHORT).show());
    }
}
