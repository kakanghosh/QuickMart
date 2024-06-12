package com.wsd.quickmart.repository;

import com.wsd.quickmart.dto.SaleSummaryDTO;
import com.wsd.quickmart.dto.TopSellingItemDTO;
import com.wsd.quickmart.entity.Sale;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    @Query("""
            SELECT SUM(s.totalAmount) FROM Sale s
            WHERE FUNCTION('DATE', s.saleDate) = :expectedDate
            """)
    Optional<BigDecimal> getTotalSaleAmountByDate(@Param("expectedDate") LocalDate expectedDate);

    @Query("""
            SELECT new com.wsd.quickmart.dto.SaleSummaryDTO(s.saleDate, SUM(s.totalAmount) AS totalSales)
            FROM Sale s
            WHERE FUNCTION('DATE', s.saleDate) BETWEEN :startDate AND :endDate
            GROUP BY s.saleDate
            ORDER BY totalSales DESC
            LIMIT 1
            """)
    Optional<SaleSummaryDTO> findMaxSaleDayOfTimeRange(@Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT new com.wsd.quickmart.dto.TopSellingItemDTO(p.id, p.name, sum(si.quantity), SUM(si.totalPrice) AS salesPrice)
            FROM SaleItem si
            JOIN Product p ON p.id = si.productId
            GROUP BY p.id
            ORDER BY salesPrice DESC
            """)
    List<TopSellingItemDTO> findTopSellingItemsOfAllTime(Pageable pageable);

    @Query("""
            SELECT new com.wsd.quickmart.dto.TopSellingItemDTO(p.id, p.name, SUM(si.quantity) AS totalQuantity, SUM(si.totalPrice))
            FROM Sale s
            JOIN SaleItem si ON si.saleId = s.id
            JOIN Product p ON p.id = si.productId
            WHERE FUNCTION('DATE_FORMAT', s.saleDate, '%Y-%m-01') = FUNCTION('DATE_FORMAT', :expectedDate, '%Y-%m-01')
            GROUP BY p.id
            ORDER BY totalQuantity DESC
            """)
    List<TopSellingItemDTO> findTopSellingItemsOfLastMonth(@Param("expectedDate") LocalDate expectedDate, Pageable pageable);
}
