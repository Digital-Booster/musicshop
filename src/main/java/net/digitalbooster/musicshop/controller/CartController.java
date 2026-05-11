package net.digitalbooster.musicshop.controller;

import net.digitalbooster.musicshop.model.AppUser;
import net.digitalbooster.musicshop.model.Cart;
import net.digitalbooster.musicshop.model.CartItem;
import net.digitalbooster.musicshop.model.Customer;
import net.digitalbooster.musicshop.model.Track;
import net.digitalbooster.musicshop.repository.AppUserRepository;
import net.digitalbooster.musicshop.repository.CartItemRepository;
import net.digitalbooster.musicshop.repository.CartRepository;
import net.digitalbooster.musicshop.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

@Controller
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @GetMapping("/cart")
    public String viewCart(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        AppUser user = appUserRepository.findByUsername(username);
        Customer customer = user.getCustomer();

        Cart cart = cartRepository.findByCustomerId(customer.getId()).orElse(new Cart());
        if (cart.getCartItems() != null) {
            BigDecimal total = cart.getCartItems().stream()
                    .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            cart.setTotal(total);
        }

        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam @NonNull Integer trackId, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        AppUser user = appUserRepository.findByUsername(username);
        Customer customer = user.getCustomer();

        Optional<Track> trackOpt = trackRepository.findById(trackId);
        if (trackOpt.isPresent()) {

            Track track = trackOpt.get();
            Cart cart = cartRepository.findByCustomerId(customer.getId()).orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setCustomer(customer);
                newCart.setCartDate(OffsetDateTime.now());
                newCart.setTotal(BigDecimal.ZERO);
                return cartRepository.save(newCart);
            });

            if (cart.getCartItems() != null && cart.getCartItems().size() > 0) {
                Optional<CartItem> existingItemOpt = cart.getCartItems().stream()
                        .filter(item -> item.getTrack().getTrackId().equals(trackId))
                        .findFirst();

                if (existingItemOpt.isPresent()) {
                    CartItem item = existingItemOpt.get();
                    item.setQuantity(item.getQuantity() + 1);
                    cartItemRepository.save(item);
                } else {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setTrack(track);
                    newItem.setQuantity(1);
                    newItem.setUnitPrice(track.getUnitPrice());
                    cartItemRepository.save(newItem);
                }
            } else {
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setTrack(track);
                newItem.setQuantity(1);
                newItem.setUnitPrice(track.getUnitPrice());
                cartItemRepository.save(newItem);
            }
            redirectAttributes.addFlashAttribute("message", track.getName() + "Added to Cart");
        }

        return "redirect:/catalog";
    }

    @PostMapping("/cart/item/{id}/del")
    public String deleteCartItem(@PathVariable("id") @NonNull Integer cartLineId) {
        cartItemRepository.deleteById(cartLineId);
        return "redirect:/cart";
    }

    @PostMapping("/cart/item/{id}/inc")
    public String increaseCartItem(@PathVariable("id") @NonNull Integer cartLineId) {
        CartItem item = cartItemRepository.findById(cartLineId).orElse(null);
        if (item != null) {
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/item/{id}/dec")
    public String decreaseCartItem(@PathVariable("id") @NonNull Integer cartLineId) {
        CartItem item = cartItemRepository.findById(cartLineId).orElse(null);
        if (item != null) {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                cartItemRepository.save(item);
            } else {
                cartItemRepository.deleteById(cartLineId);
            }
        }
        return "redirect:/cart";
    }
}
