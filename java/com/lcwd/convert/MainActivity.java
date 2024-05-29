package com.lcwd.convert;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.lcwd.convert.databinding.ActivityMainBinding;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    double fromValue = 0.0;
    String fromUnit = "";
    double toValue = 0.0;
    String toUnit = "";
    private Map<String, Double> exchangeRates = new HashMap<>();

    private List<String> units = Arrays.asList(
            "JPY", "CNY", "SDG", "RON", "MKD", "MXN", "CAD",
            "ZAR", "AUD", "NOK", "ILS", "ISK", "SYP", "LYD", "UYU", "YER", "CSD",
            "EEK", "THB", "IDR", "LBP", "AED", "BOB", "QAR", "BHD", "HNL", "HRK",
            "COP", "ALL", "DKK", "MYR", "SEK", "RSD", "BGN", "DOP", "KRW", "LVL",
            "VEF", "CZK", "TND", "KWD", "VND", "JOD", "NZD", "PAB", "CLP", "PEN",
            "GBP", "DZD", "CHF", "RUB", "UAH", "ARS", "SAR", "EGP", "INR", "PYG",
            "TWD", "TRY", "BAM", "OMR", "SGD", "MAD", "BYR", "NIO", "HKD", "LTL",
            "SKK", "GTQ", "BRL", "EUR", "HUF", "IQD", "CRC", "PHP", "SVC", "PLN",
            "USD"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initComponents();
        fetchExchangeRates();
    }

    private void initComponents() {
        Collections.sort(units);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, units);
        binding.toUnit.setAdapter(arrayAdapter);
        binding.fromUnit.setAdapter(arrayAdapter);

        binding.convertButton.setOnClickListener(view -> {
            try {
                fromValue = Double.parseDouble(binding.fromValue.getText().toString());
                fromUnit = binding.fromUnit.getSelectedItem().toString();
                toUnit = binding.toUnit.getSelectedItem().toString();
                convertValue();
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchExchangeRates() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://openexchangerates.org/api/latest.json?app_id=406c1a45911747bea509035a046cc7e0"; // Free API for testing

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to fetch exchange rates", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String jsonData = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONObject rates = jsonObject.getJSONObject("rates");

                        for (String unit : units) {
                            if (rates.has(unit)) {
                                exchangeRates.put(unit, rates.getDouble(unit));
                            }
                        }

                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Exchange rates updated", Toast.LENGTH_SHORT).show());
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error parsing exchange rates", Toast.LENGTH_LONG).show());
                    }
                }
            }
        });
    }

    private void convertValue() {
        try {
            if (exchangeRates.containsKey(fromUnit) && exchangeRates.containsKey(toUnit)) {
                double fromRate = exchangeRates.get(fromUnit);
                double toRate = exchangeRates.get(toUnit);

                // Convert from base currency (EUR) to target currency
                double baseValue = fromValue / fromRate;
                toValue = baseValue * toRate;

                binding.toValue.setText(String.valueOf(toValue));
            } else {
                Toast.makeText(this, "Conversion rate not available", Toast.LENGTH_LONG).show(); // Corrected here
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }
}
