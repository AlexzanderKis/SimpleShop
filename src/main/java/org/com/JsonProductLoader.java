package org.com;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonProductLoader {
    public List<Product> loadProducts() throws IOException {
        Gson gson = new GsonBuilder().create();
        try (InputStream inputStream = JsonProductLoader.class.getClassLoader().getResourceAsStream("products.json")) {
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found: products.json");
            }
            try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                Product[] productArray = gson.fromJson(reader, Product[].class);
                return Arrays.asList(productArray);
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}