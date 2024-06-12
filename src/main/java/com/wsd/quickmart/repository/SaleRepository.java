package com.wsd.quickmart.repository;

import com.wsd.quickmart.dto.SaleAmountDTO;
import com.wsd.quickmart.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    @Query("""
            SELECT new com.wsd.quickmart.dto.SaleAmountDTO(SUM(s.totalAmount)) FROM Sale s
            WHERE FUNCTION('DATE', s.saleDate) = :expectedDate
            """)
    SaleAmountDTO getTotalSaleAmountByDate(@Param("expectedDate") LocalDate expectedDate);
}
