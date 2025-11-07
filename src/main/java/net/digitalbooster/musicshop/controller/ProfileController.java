package net.digitalbooster.musicshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.digitalbooster.musicshop.model.AppUser;
import net.digitalbooster.musicshop.model.Customer;
import net.digitalbooster.musicshop.repository.AppUserRepository;
import net.digitalbooster.musicshop.repository.CustomerRepository;

import java.security.Principal;

@Controller
public class ProfileController {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/profile")
    public String profileForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        AppUser user = appUserRepository.findByUsername(username);
        if (user == null) {
            return "redirect:/login";
        }
        Customer c = user.getCustomer();
        model.addAttribute("firstName", c != null ? c.getFirstName() : "");
        model.addAttribute("lastName", c != null ? c.getLastName() : "");
        model.addAttribute("email", c != null ? c.getEmail() : username);
        model.addAttribute("company", c != null ? c.getCompany() : "");
        model.addAttribute("address", c != null ? c.getAddress() : "");
        model.addAttribute("city", c != null ? c.getCity() : "");
        model.addAttribute("state", c != null ? c.getState() : "");
        model.addAttribute("country", c != null ? c.getCountry() : "");
        model.addAttribute("postalCode", c != null ? c.getPostalCode() : "");
        model.addAttribute("phone", c != null ? c.getPhone() : "");
        model.addAttribute("fax", c != null ? c.getFax() : "");
        return "profile";
    }

    @PostMapping("/profile")
    public String profileSave(
            Principal principal,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String fax,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        AppUser user = appUserRepository.findByUsername(username);
        if (user == null) {
            return "redirect:/login";
        }
        Customer c = user.getCustomer();
        if (c == null) {
            c = new Customer();
        }
        // Validate email uniqueness: allow current value but prevent using someone else's email
        Customer existingCustomer = customerRepository.findByEmail(email);
        if (existingCustomer != null) {
            Integer existingId = existingCustomer.getId();
            Integer currentId = c.getId();
            if (currentId == null || !existingId.equals(currentId)) {
                model.addAttribute("error", "Email is already used by another account.");
                // preserve submitted values
                model.addAttribute("firstName", firstName);
                model.addAttribute("lastName", lastName);
                model.addAttribute("email", email);
                model.addAttribute("company", company);
                model.addAttribute("address", address);
                model.addAttribute("city", city);
                model.addAttribute("state", state);
                model.addAttribute("country", country);
                model.addAttribute("postalCode", postalCode);
                model.addAttribute("phone", phone);
                model.addAttribute("fax", fax);
                return "profile";
            }
        }
        // Also ensure AppUser username uniqueness
        AppUser otherUser = appUserRepository.findByUsername(email);
        if (otherUser != null && !otherUser.getUserId().equals(user.getUserId())) {
            model.addAttribute("error", "Username (email) is already used by another account.");
            model.addAttribute("firstName", firstName);
            model.addAttribute("lastName", lastName);
            model.addAttribute("email", email);
            model.addAttribute("company", company);
            model.addAttribute("address", address);
            model.addAttribute("city", city);
            model.addAttribute("state", state);
            model.addAttribute("country", country);
            model.addAttribute("postalCode", postalCode);
            model.addAttribute("phone", phone);
            model.addAttribute("fax", fax);
            return "profile";
        }
        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setEmail(email);
        c.setCompany(company);
        c.setAddress(address);
        c.setCity(city);
        c.setState(state);
        c.setCountry(country);
        c.setPostalCode(postalCode);
        c.setPhone(phone);
        c.setFax(fax);

        customerRepository.save(c);
        // ensure relation and update username if email changed
        user.setCustomer(c);
        if (!email.equals(user.getUsername())) {
            user.setUsername(email);
        }
        appUserRepository.save(user);

        redirectAttributes.addFlashAttribute("message", "Profile updated successfully.");
        return "redirect:/profile";
    }
}
