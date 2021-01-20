package com.example.simpletodov2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
// Deprecated import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    //ArrayList<String> items = new ArrayList<String>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
    ArrayList<String> items = new ArrayList<String>();

    Button addBtn;
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addBtn = findViewById((R.id.addBtn));
        etItem = findViewById((R.id.etItem));
        rvItems = findViewById((R.id.rvItems));
        //etItem.setText("This output is from Java");

        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                // Delete the item from the model; notify the adapter
                items.remove(position);
                itemsAdapter.notifyItemRemoved(position);
                //Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("MainActivity", "Single click at position" + position);
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                // display the activity
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };
        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoItem = etItem.getText().toString();
                items.add(todoItem);
                itemsAdapter.notifyItemInserted(items.size() - 1);
                etItem.setText("");
                //Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            items.set(position, itemText);
            itemsAdapter.notifyItemChanged(position);
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    private void loadItems() {
        try {
            // Deprecated items = new ArrayList<String>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
            FileInputStream fis = new FileInputStream(getDataFile());
            InputStreamReader streamReader = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(streamReader);
            items = new ArrayList<String>();
            String newline = reader.readLine();
            while (newline != null) {
                items.add(newline);
                newline = reader.readLine();
            }

            // Prevent memory leak; close resource
            reader.close();
            streamReader.close();
            fis.close();
            Toast.makeText(getApplicationContext(), "Items loaded [" + String.valueOf( items.size() ) +"]", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Error loading items: ", Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", "Error reading items", e);
            e.printStackTrace();
            items = new ArrayList<String>();
        }
    }

    // Saves the items by writing them to a data file
    private void saveItems() {
        try {
            // Deprecated FileUtils.writeLines(getDataFile(), items);
            FileWriter writer = new FileWriter(getDataFile());
            BufferedWriter buffer = new BufferedWriter(writer);
            for (String str : items) {
                if (str == null) {
                    continue;
                } else {
                    buffer.write(str);
                    buffer.newLine();
                }
        }
            // Close so that another process can access the resource
            buffer.flush();
            buffer.close();
            writer.close();
            Toast.makeText(getApplicationContext(), "Items saved [" + String.valueOf( items.size() ) +"]", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Error saving items", Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", "Error saving items", e);
            e.printStackTrace();
            items = new ArrayList<String>();
        }
    }
}