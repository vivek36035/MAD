package com.example.intent3;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText etName, etCourse, etEmail;
    Button btnRegister;
    ListView listView;

    ArrayList<String> studentList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etCourse = findViewById(R.id.etCourse);
        etEmail = findViewById(R.id.etEmail);
        btnRegister = findViewById(R.id.btnRegister);
        listView = findViewById(R.id.listView);

        studentList = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                studentList
        );

        listView.setAdapter(adapter);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etName.getText().toString();
                String course = etCourse.getText().toString();
                String email = etEmail.getText().toString();

                String data = "Name: " + name +
                        "\nCourse: " + course +
                        "\nEmail: " + email;

                studentList.add(data);

                adapter.notifyDataSetChanged();

                etName.setText("");
                etCourse.setText("");
                etEmail.setText("");
            }
        });
    }
}
