package org.com;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class JsonProductLoader {
    public List<Product> loadProducts() {
        Gson gson = new GsonBuilder().create();
        try (InputStream inputStream = JsonProductLoader.class.getClassLoader().getResourceAsStream("products.json")){
            assert inputStream != null;
            try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)){
                Product[] productsArray = gson.fromJson(reader, Product[].class);
                return Arrays.asList(productsArray);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}