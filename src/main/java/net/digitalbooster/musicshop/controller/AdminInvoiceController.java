package net.digitalbooster.musicshop.controller;

import net.digitalbooster.musicshop.model.Customer;
import net.digitalbooster.musicshop.model.Invoice;
import net.digitalbooster.musicshop.repository.CustomerRepository;
import net.digitalbooster.musicshop.repository.InvoiceRepository;
import net.digitalbooster.musicshop.repository.InvoiceItemRepository;
import net.digitalbooster.musicshop.model.InvoiceItem;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class AdminInvoiceController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    // Show invoices for a specific customer
    @GetMapping("/admin/invoices/customer/{customerId}")
    public String customerInvoices(@PathVariable Integer customerId, Model model) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            model.addAttribute("error", "Customer not found");
            return "admin_users";
        }

        // // First load invoices using native query to avoid JPQL fetch join converter issues
        // List<Invoice> invoices = invoiceRepository.findByCustomerIdNative(customerId);
        List<Invoice> invoices = invoiceRepository.findByCustomerId(customerId);

        // For each invoice load its items with track eagerly
        for (Invoice inv : invoices) {
            List<InvoiceItem> items = invoiceItemRepository.findByInvoiceIdWithTrack(inv.getInvoiceId());
            // attach items to invoice to let the template render them
            Set<InvoiceItem> set = new HashSet<>(items);
            inv.setInvoiceItems(set);
        }

        // Compute total of all invoices for this customer
        BigDecimal total = BigDecimal.ZERO;
        for (Invoice inv : invoices) {
            if (inv.getTotal() != null) total = total.add(inv.getTotal());
        }

        model.addAttribute("customer", customer);
        model.addAttribute("invoices", invoices);
        model.addAttribute("total", total);
        return "admin_customer_invoices";
    }

    // Delete an invoice and its items
    @PostMapping("/admin/invoices/{invoiceId}/delete")
    @Transactional
    public String deleteInvoice(@PathVariable Integer invoiceId, RedirectAttributes redirectAttributes) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElse(null);
        if (invoice == null) {
            redirectAttributes.addFlashAttribute("error", "Invoice not found.");
            return "redirect:/admin/users"; // Redirect to users list if invoice not found
        }

        // Get customerId before deleting the invoice
        Integer customerId = invoice.getCustomer().getCustomerId();

        try {
            // Delete associated invoice items first
            invoiceItemRepository.deleteByInvoice(invoice);
            // Then delete the invoice
            invoiceRepository.delete(invoice);
            redirectAttributes.addFlashAttribute("message", "Invoice deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting invoice: " + e.getMessage());
        }

        return "redirect:/admin/invoices/customer/" + customerId;
    }
}
