package org.com.repos;

import org.com.domain.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Репозиторий OrderRepo отвечает за хранение и поиск объектов Order
// получает заказы по ID пользователя или по email, отсортированные по дате создания (новые сверху)

/**
  Репозиторий заказов
  Содержит стандартные CRUD-операции и два кастомных запроса для получения заказов пользователя (по ID и по email), отсортированных от новых к старым
  JPQL-запросы используют связь o.user — Hibernate сам сделает JOIN
 */
@Repository
public interface OrderRepo extends JpaRepository<OrderEntity, String> {

//    void save(Order order);

    // Возвращает заказы пользователя по его ID, отсортированные по дате создания (DESC ORDER)
    @Query("select o from OrderEntity o where o.user.id = :userId order by o.createdAt desc")
    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId);

    // Возвращает заказы пользователя по его email, отсортированные по дате создания (DESC ORDER)
    @Query("select o from OrderEntity o where o.user.email = :email order by o.createdAt desc")
    List<OrderEntity> findByUserEmailOrderByCreatedAtDesc(@Param("email") String email);
}