package org.com;

import org.com.repos.ProductRepo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// класс, внутри которого данные хранятся в Map (в оперативной памяти)
public class InMemoryProductRepo implements ProductRepo {

    private final Map<Long, Product> productMap = new HashMap<>();

    @Override
    public List<Product> getAllProduct() { // former findAll()
        return List.copyOf(productMap.values().stream().toList());
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(productMap.get(id));
    }

    @Override
    public void save(Product product) {
        productMap.put(product.getProductId(), product);
    }

    @Override
    public void deleteById(Long id) {
        productMap.remove(id);
    }
}
