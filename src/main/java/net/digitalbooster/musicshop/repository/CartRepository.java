package net.digitalbooster.musicshop.repository;

import net.digitalbooster.musicshop.model.Cart;
import net.digitalbooster.musicshop.model.Invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByCustomerId(Integer customerId);

    // Native query fallback: fetch invoices rows for a customer using SQL
	// @Query(value = "SELECT * FROM cart WHERE CustomerId = :customerId", nativeQuery = true)
	// Cart findByCustomerId(@Param("customerId") Integer customerId);

    // Native query fallback: fetch invoices rows for a customer using SQL
    // @Query(value = "SELECT * FROM cart WHERE CartId = :cartId", nativeQuery = true)
    // @NonNull
    // Optional<Cart> findById(@NonNull @Param("cartId") Integer cartId);
// }   Optional findById(@Param("cartId") Integer cartId);
}