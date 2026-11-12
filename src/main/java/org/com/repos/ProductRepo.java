package org.com.repos;

import org.com.domain.entity.ProductEntity;
import org.com.domain.entity.enums.CategoryOfProd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
    Репозиторий товаров
    4 способа найти товары:
    - поиск по категории
    - поиск по диапазону цен
    - поиск по ключевому слову в name, brand или keywords (JPQL)
    - фильтр с 4 опциональными параметрами (JPQL)
 */
@Repository
public interface ProductRepo extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByCategory(CategoryOfProd category);

    List<ProductEntity> findByPriceBetweenOrderByPrice(BigDecimal min, BigDecimal max);

    @Query("""
            select distinct p from ProductEntity p
            left join p.keywords k
            where lower(p.name) like lower(concat('%',:keyword, '%'))
                or lower(p.brand) like lower(concat('%',:keyword,'%'))
                or lower(k) like lower(concat('%',:keyword,'%'))
            order by p.id asc
            """)
    List<ProductEntity> searchByKeyword(@Param("keyword") String keyword);

    // Комбинированная фильтрация товаров
    // все параметры опциональны: если параметр null, соответствующее условие игнорируется
    // если параметр = null условие игнорируется, принцип dynamic query in single JPQL
    @Query("""
            select distinct p from ProductEntity p
            left join p.keywords k
            where (:category is null or p.category = :category)
                and (:minPrice is null or p.price >= :minPrice)
                and (:maxPrice is null or p.price <= :maxPrice)
                and (:keyword is null or lower(p.name) like lower(concat('%', :keyword, '%'))
                    or lower(p.brand) like lower(concat('%',:keyword,'%'))
                    or lower(k) like lower(concat('%',:keyword,'%')))
            order by p.price asc
            """)
    List<ProductEntity> filterProducts(@Param("category") CategoryOfProd category,
                                       @Param("minPrice") BigDecimal minPrice,
                                       @Param("maxPrice") BigDecimal maxPrice,
                                       @Param("keyword") String keyword);


//    List<ProductEntity> getAllProduct(); // список продуктов
    //    void deleteById(Long id);
//    void save(ProductEntity product);
//    Optional<Product> findBySimplifiedName(String name);
}