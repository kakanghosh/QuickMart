package com.wsd.quickmart.repository;

import com.wsd.quickmart.dto.SaleSummaryDTO;
import com.wsd.quickmart.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    @Query("""
            SELECT SUM(s.totalAmount) FROM Sale s
            WHERE FUNCTION('DATE', s.saleDate) = :expectedDate
            """)
    Optional<BigDecimal> getTotalSaleAmountByDate(@Param("expectedDate") LocalDate expectedDate);

    @Query("SELECT new com.wsd.quickmart.dto.SaleSummaryDTO(s.saleDate, SUM(s.totalAmount) AS totalSales) " +
            "FROM Sale s " +
            "WHERE FUNCTION('DATE', s.saleDate) BETWEEN :startDate AND :endDate " +
            "GROUP BY s.saleDate " +
            "ORDER BY totalSales DESC " +
            "LIMIT 1")
    Optional<SaleSummaryDTO> findMaxSaleDayOfTimeRange(@Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

}
