package net.digitalbooster.musicshop.repository;

import net.digitalbooster.musicshop.model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Integer> {

	@Query("select ii from InvoiceItem ii left join fetch ii.track where ii.invoice.invoiceId = :invoiceId")
	List<InvoiceItem> findByInvoiceIdWithTrack(@Param("invoiceId") Integer invoiceId);
}