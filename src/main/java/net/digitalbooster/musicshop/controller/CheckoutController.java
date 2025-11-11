package net.digitalbooster.musicshop.controller;

import net.digitalbooster.musicshop.model.AppUser;
import net.digitalbooster.musicshop.model.Cart;
import net.digitalbooster.musicshop.model.Customer;
import net.digitalbooster.musicshop.model.Invoice;
import net.digitalbooster.musicshop.model.InvoiceItem;
import net.digitalbooster.musicshop.repository.AppUserRepository;
import net.digitalbooster.musicshop.repository.CartItemRepository;
import net.digitalbooster.musicshop.repository.CartRepository;
import net.digitalbooster.musicshop.repository.InvoiceRepository;
import net.digitalbooster.musicshop.repository.InvoiceItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class CheckoutController {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private CartRepository cartRepository;

     @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @PostMapping("/checkout/confirm")
    @Transactional
    public String confirmOrder(Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        AppUser user = appUserRepository.findByUsername(username);
        Customer customer = user.getCustomer();

        Cart cart = cartRepository.findByCustomerId(customer.getId()).orElse(null);

        if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Your cart is empty.");
            return "redirect:/cart";
        }

        // Create a new Invoice
        Invoice invoice = new Invoice();
        invoice.setCustomer(customer);
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setBillingAddress(customer.getAddress());
        invoice.setBillingCity(customer.getCity());
        invoice.setBillingState(customer.getState());
        invoice.setBillingCountry(customer.getCountry());
        invoice.setBillingPostalCode(customer.getPostalCode());

        BigDecimal total = cart.getCartItems().stream()
                .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        invoice.setTotal(total);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Transfer cart items to invoice items
        for (var cartItem : cart.getCartItems()) {
            InvoiceItem invoiceItem = new InvoiceItem();
            invoiceItem.setInvoice(savedInvoice);
            invoiceItem.setTrack(cartItem.getTrack());
            invoiceItem.setUnitPrice(cartItem.getUnitPrice());
            invoiceItem.setQuantity(cartItem.getQuantity());
            invoiceItemRepository.save(invoiceItem);
            cartItemRepository.delete(cartItem);
        }

        // Clear the cart
        cartRepository.delete(cart);

        redirectAttributes.addFlashAttribute("message", "Order confirmed! Your invoice number is " + savedInvoice.getInvoiceId());
        return "redirect:/invoices";
    }
}
