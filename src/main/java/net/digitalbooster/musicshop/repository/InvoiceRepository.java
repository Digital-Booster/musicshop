package net.digitalbooster.musicshop.repository;

import net.digitalbooster.musicshop.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
	// Fetch invoices for a customer and eagerly load invoice items and their tracks
	@Query("select distinct i from Invoice i left join fetch i.invoiceItems ii left join fetch ii.track t where i.customer.customerId = :customerId")
	List<Invoice> findByCustomerIdWithItems(@Param("customerId") Integer customerId);

	List<Invoice> findByCustomerId(Integer customerId);

	// Native query fallback: fetch invoices rows for a customer using SQL
	@Query(value = "SELECT * FROM invoices WHERE CustomerId = :customerId", nativeQuery = true)
	List<Invoice> findByCustomerIdNative(@Param("customerId") Integer customerId);
}