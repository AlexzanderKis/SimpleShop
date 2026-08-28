package org.com.tools;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.com.Product;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

// парсер списка продуктов из файла products.json
public class ProductListParser {
    public static void jsonParser() throws IOException {
        String jsonContent = new String(Files.readAllBytes(Paths.get("C:\\Users\\Gordon Freeman\\IdeaProjects\\SimpleShop\\src\\main\\resources\\products.json")));
        Gson gson = new Gson();
        Type productListType = new TypeToken<List<Product>>() { }.getType();
        List<Product> productList = gson.fromJson(jsonContent, productListType);

        System.out.println("Available Product List:");
        for (Product p : productList) {
            System.out.printf("""
                            ID %s [%s] | %s, Brand: %s - %s
                            """,                            // %.2f%n for price
                    p.getProductId(),
                    p.getProductCategory(),
                    p.getProductName(),
                    p.getProductBrand(),
                    p.getProductPrice());
        }
    }
}
