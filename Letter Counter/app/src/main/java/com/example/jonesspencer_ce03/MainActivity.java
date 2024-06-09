// Spencer Jones
// MDV3832-0 - 062024
// MainActivity.java

package com.example.jonesspencer_ce03;

// Imports
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    // Logging tag
    private static final String TAG = "MainActivity";

    // UI Elements
    private EditText addWordText;
    private Button addButton, viewButton;
    private TextView avgWordCountView, medWordCountView;
    private NumberPicker numberPicker;

    // String collection
    private ArrayList<String> stringCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            // Initializing UI elements
            addWordText = findViewById(R.id.addWordText);
            addButton = findViewById(R.id.addButton);
            viewButton = findViewById(R.id.viewButton);
            avgWordCountView = findViewById(R.id.avgWordCountView);
            medWordCountView = findViewById(R.id.MedWordCountView);
            numberPicker = findViewById(R.id.numberPicker);

            // Initializing string collection
            if (stringCollection == null) {
                stringCollection = new ArrayList<>();
            }

            // Initializing NumberPicker
            numberPicker.setMinValue(0);
            numberPicker.setWrapSelectorWheel(false);
            disableNumberPickerEditText(numberPicker);

            // On click listener for add button
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addStringToCollection();
                }
            });

            // On click listener for view button
            viewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewStringFromCollection();
                }
            });

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Disable text input for number picker
    private void disableNumberPickerEditText(NumberPicker numberPicker) {
        int count = numberPicker.getChildCount();// Count of children
        // Iterating over children
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i); // Get child view
            // Id child is EditText
            if (child instanceof EditText) {
                EditText editText = (EditText) child;
                editText.setFocusable(false); // Disable focus
                editText.setInputType(0); // Disable input
            }
        }
    }

    // Add string to collection
    private void addStringToCollection() {
        String input = addWordText.getText().toString().trim();
        // If no input
        if (input.isEmpty()) {
            showToast("String cannot be empty or whitespace.");
            return;
        }

        // If string already exists
        if (stringCollection.contains(input)) {
            showToast("String already exists in the collection.");
            return;
        }

        stringCollection.add(input); // Add string to collection
        Log.d(TAG, "String added: " + input);
        addWordText.setText(""); // Clear text
        updateUI(); // Update UI
    }

    // Update UI
    private void updateUI() {
        int collectionSize = stringCollection.size();
        Log.d(TAG, "Updating UI. Collection size: " + collectionSize);
        if (collectionSize == 0) {
            numberPicker.setMinValue(0); // Min value of number picker
            numberPicker.setMaxValue(0); // Max value of number picker
            avgWordCountView.setText("Average Length: 0.00"); // Set Avg length text
            medWordCountView.setText("Median Length: 0.00"); // Set med length text
        } else {
            numberPicker.setMinValue(0); // Min value of number picker
            numberPicker.setMaxValue(collectionSize - 1); // Max value of number picker
            avgWordCountView.setText(String.format("Average Length: %.2f", calculateAverage())); // Set Avg length text
            medWordCountView.setText(String.format("Median Length: %.2f", calculateMedian())); // Set med length text
        }

        Log.d(TAG, "NumberPicker range: " + numberPicker.getMinValue() + " to " + numberPicker.getMaxValue()); // Log number picker range
    }

    // View string from collection based on NumberPicker index
    private void viewStringFromCollection() {
        int index = numberPicker.getValue(); // Get selected index
        Log.d(TAG, "Selected index: " + index + ", Collection size: " + stringCollection.size());
        Log.d(TAG, "Current collection: " + stringCollection.toString());
        // If index is valid
        if (index < 0 || index >= stringCollection.size()) {
            showToast("Invalid index selected.");
            return;
        }

        String selectedString = stringCollection.get(index); // Get selected string
        showAlertDialog(selectedString, index); // Show alert
    }

    // Calculates average length of strings in collection
    private double calculateAverage() {
        // if collection is empty, 0.0
        if (stringCollection.isEmpty()) return 0.0;
        int totalLength = 0; // Initialize

        // Iterate over strings
        for (String s : stringCollection) {
            totalLength += s.length(); // Add length to total
        }
        return (double) totalLength / stringCollection.size(); // Return average length
    }

    // Calculates median length of strings in collection
    private double calculateMedian() {
        // if collection is empty, 0.0
        if (stringCollection.isEmpty()) return 0.0;

        ArrayList<Integer> lengths = new ArrayList<>(); // List of lengths

        // Iterate over strings
        for (String s : stringCollection) {
            lengths.add(s.length()); // Add string length
        }

        Collections.sort(lengths); // Sort lengths
        int middle = lengths.size() / 2; // Get middle index

        // If even number
        if (lengths.size() % 2 == 0) {
            return (lengths.get(middle - 1) + lengths.get(middle)) / 2.0; // Return average of two middle lengths
            // If odd number
        } else {
            return lengths.get(middle); // Return middle length
        }
    }

    // Show alert to remove or close
    private void showAlertDialog(final String selectedString, final int index) {
        // Creating and show alert
        new AlertDialog.Builder(this)
                .setTitle("Selected String") // Title
                .setMessage(selectedString) // Message
                .setPositiveButton("Remove", (dialog, which) -> {
                    stringCollection.remove(index); // Remove string
                    Log.d(TAG, "String removed: " + selectedString);
                    updateUI();
                    dialog.dismiss();
                })
                .setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Show toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}