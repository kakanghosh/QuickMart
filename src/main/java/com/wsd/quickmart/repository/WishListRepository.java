package com.wsd.quickmart.repository;

import com.wsd.quickmart.dto.ProductDTO;
import com.wsd.quickmart.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {
    @Query("""
            SELECT new com.wsd.quickmart.dto.ProductDTO(p.id, p.name, p.description, p.price)
            FROM Customer c
            JOIN WishList w ON c.id = w.customerId
            JOIN WishListItem wi ON w.id = wi.wishListId
            JOIN Product p ON wi.productId = p.id
            WHERE c.id = :customerId
            """)
    List<ProductDTO> findWishListByCustomerId(@Param("customerId") Long customerId);
}
