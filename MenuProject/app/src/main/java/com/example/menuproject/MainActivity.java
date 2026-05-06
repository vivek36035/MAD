package com.example.menuproject;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ContextMenu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    RelativeLayout mainLayout;
    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar setup (IMPORTANT for 3 dots)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainLayout = findViewById(R.id.mainLayout);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);

        // Context Menu
        registerForContextMenu(textView);

        // Popup Menu
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, button);

                popupMenu.getMenu().add("Android");
                popupMenu.getMenu().add("JAVA");
                popupMenu.getMenu().add("Kotlin");

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(MainActivity.this,
                                "You Clicked " + item.getTitle(),
                                Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                popupMenu.show();
            }
        });
    }

    // OPTION MENU (3 dots)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Android");
        menu.add(0, 2, 0, "JAVA");
        menu.add(0, 3, 0, "Kotlin");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                mainLayout.setBackgroundColor(Color.RED);
                return true;
            case 2:
                mainLayout.setBackgroundColor(Color.GREEN);
                return true;
            case 3:
                mainLayout.setBackgroundColor(Color.BLUE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // CONTEXT MENU
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Choose a color");
        menu.add("Yellow");
        menu.add("Gray");
        menu.add("Cyan");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Yellow")) {
            mainLayout.setBackgroundColor(Color.YELLOW);

        } else if (item.getTitle().equals("Gray")) {
            mainLayout.setBackgroundColor(Color.GRAY);

        } else if (item.getTitle().equals("Cyan")) {
            mainLayout.setBackgroundColor(Color.CYAN);
        }
        return true;
    }
}