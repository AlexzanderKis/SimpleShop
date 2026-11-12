package org.com.repos;

import org.com.domain.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
        Репозиторий позиций заказов
        Даёт стандартные CRUD-операции (save, findById, deleteById и т.д.) и кастомный поиск по ID заказа

        30/10/2026 метод не используется
        OrderEntity.items загружается через @OneToMany с FetchType.EAGER
 */
@Repository
public interface OrderItemRepo extends JpaRepository<OrderItemEntity, Long> {
    /**
         Возвращает все позиции указанного заказа
         @param orderId строковый ID заказа (например, "ORD-2026-X8Y9Z1A2B3C4")
         @return список позиций; пустой список, если заказ не содержит позиций
     */
    List<OrderItemEntity> findByOrderId(String orderId);
}
