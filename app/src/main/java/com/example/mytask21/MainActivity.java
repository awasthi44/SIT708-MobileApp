package com.example.mytask21;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerCategory, spinnerFrom, spinnerTo;
    private EditText editValue;
    private TextView txtResult;
    private Button btnConvert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        editValue = findViewById(R.id.editValue);
        txtResult = findViewById(R.id.txtResult);
        btnConvert = findViewById(R.id.btnConvert);

        String[] categories = {"Currency", "Fuel Efficiency", "Liquid Volume", "Distance", "Temperature"};

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        updateUnitSpinners("Currency");

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = spinnerCategory.getSelectedItem().toString();
                updateUnitSpinners(selectedCategory);
                txtResult.setText(getString(R.string.result_placeholder));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnConvert.setOnClickListener(v -> performConversion());
    }

    private void updateUnitSpinners(String category) {
        String[] units;

        switch (category) {
            case "Currency":
                units = new String[]{"USD", "AUD", "EUR", "JPY", "GBP"};
                break;

            case "Fuel Efficiency":
                units = new String[]{"mpg", "km/L"};
                break;

            case "Liquid Volume":
                units = new String[]{"Gallon", "Liter"};
                break;

            case "Distance":
                units = new String[]{"Nautical Mile", "Kilometer"};
                break;

            case "Temperature":
                units = new String[]{"Celsius", "Fahrenheit", "Kelvin"};
                break;

            default:
                units = new String[]{};
                break;
        }

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFrom.setAdapter(unitAdapter);
        spinnerTo.setAdapter(unitAdapter);
    }

    private void performConversion() {
        String input = editValue.getText().toString().trim();

        if (input.isEmpty()) {
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();
            return;
        }

        double value;
        try {
            value = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerCategory.getSelectedItem() == null ||
                spinnerFrom.getSelectedItem() == null ||
                spinnerTo.getSelectedItem() == null) {
            Toast.makeText(this, "Please select category and units", Toast.LENGTH_SHORT).show();
            return;
        }

        String category = spinnerCategory.getSelectedItem().toString();
        String from = spinnerFrom.getSelectedItem().toString();
        String to = spinnerTo.getSelectedItem().toString();

        if (from.equals(to)) {
            txtResult.setText(formatResult(value, from, value, to));
            Toast.makeText(this, "Same unit selected. Value remains unchanged.", Toast.LENGTH_SHORT).show();
            return;
        }

        if ((category.equals("Fuel Efficiency") || category.equals("Liquid Volume") || category.equals("Distance")) && value < 0) {
            Toast.makeText(this, "Negative values are not allowed for this conversion", Toast.LENGTH_SHORT).show();
            return;
        }

        if (category.equals("Temperature") && from.equals("Kelvin") && value < 0) {
            Toast.makeText(this, "Kelvin cannot be negative", Toast.LENGTH_SHORT).show();
            return;
        }

        double result = convertValue(category, from, to, value);

        txtResult.setText(formatResult(value, from, result, to));
    }

    private double convertValue(String category, String from, String to, double value) {

        if (category.equals("Currency")) {
            double usdValue = 0.0;

            switch (from) {
                case "USD":
                    usdValue = value;
                    break;
                case "AUD":
                    usdValue = value / 1.55;
                    break;
                case "EUR":
                    usdValue = value / 0.92;
                    break;
                case "JPY":
                    usdValue = value / 148.50;
                    break;
                case "GBP":
                    usdValue = value / 0.78;
                    break;
            }

            switch (to) {
                case "USD":
                    return usdValue;
                case "AUD":
                    return usdValue * 1.55;
                case "EUR":
                    return usdValue * 0.92;
                case "JPY":
                    return usdValue * 148.50;
                case "GBP":
                    return usdValue * 0.78;
            }
        }

        if (category.equals("Fuel Efficiency")) {
            if (from.equals("mpg") && to.equals("km/L")) return value * 0.425;
            if (from.equals("km/L") && to.equals("mpg")) return value / 0.425;
        }

        if (category.equals("Liquid Volume")) {
            if (from.equals("Gallon") && to.equals("Liter")) return value * 3.785;
            if (from.equals("Liter") && to.equals("Gallon")) return value / 3.785;
        }

        if (category.equals("Distance")) {
            if (from.equals("Nautical Mile") && to.equals("Kilometer")) return value * 1.852;
            if (from.equals("Kilometer") && to.equals("Nautical Mile")) return value / 1.852;
        }

        if (category.equals("Temperature")) {
            if (from.equals("Celsius") && to.equals("Fahrenheit")) return (value * 1.8) + 32;
            if (from.equals("Fahrenheit") && to.equals("Celsius")) return (value - 32) / 1.8;
            if (from.equals("Celsius") && to.equals("Kelvin")) return value + 273.15;
            if (from.equals("Kelvin") && to.equals("Celsius")) return value - 273.15;
            if (from.equals("Fahrenheit") && to.equals("Kelvin")) return ((value - 32) / 1.8) + 273.15;
            if (from.equals("Kelvin") && to.equals("Fahrenheit")) return ((value - 273.15) * 1.8) + 32;
        }

        Toast.makeText(this, "Conversion not supported", Toast.LENGTH_SHORT).show();
        return 0.0;
    }

    private String formatResult(double inputValue, String fromUnit, double outputValue, String toUnit) {
        return String.format("%.2f %s is %.2f %s", inputValue, fromUnit, outputValue, toUnit);
    }
}