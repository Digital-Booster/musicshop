package net.digitalbooster.musicshop.controller;

import net.digitalbooster.musicshop.model.AppUser;
import net.digitalbooster.musicshop.model.Customer;
import net.digitalbooster.musicshop.model.Invoice;
import net.digitalbooster.musicshop.repository.AppUserRepository;
import net.digitalbooster.musicshop.repository.InvoiceRepository;
import net.digitalbooster.musicshop.repository.InvoiceItemRepository;
import net.digitalbooster.musicshop.model.InvoiceItem;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
public class InvoiceController {


    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    // Show invoices for a specific customer
    @GetMapping("/invoices")
    public String customerInvoices(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        AppUser user = appUserRepository.findByUsername(username);
        if (user == null) {
            return "redirect:/login";
        }
        Customer customer = user.getCustomer();
        if (customer == null) {
            return "redirect:/login";
        }

        // First load invoices using native query to avoid JPQL fetch join converter issues
        List<Invoice> invoices = invoiceRepository.findByCustomerIdNative(customer.getId());

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
        return "invoices";
    }
}
