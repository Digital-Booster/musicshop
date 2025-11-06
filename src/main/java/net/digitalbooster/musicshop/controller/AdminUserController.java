package net.digitalbooster.musicshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.digitalbooster.musicshop.model.AppUser;
import net.digitalbooster.musicshop.model.Customer;
import net.digitalbooster.musicshop.repository.AppUserRepository;
import net.digitalbooster.musicshop.repository.CustomerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminUserController {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/admin/users")
    public String listUsers(@RequestParam(required = false) String q, Model model) {
        List<AppUser> users = appUserRepository.findByRole("USER");
        if (q != null && !q.isBlank()) {
            String ql = q.toLowerCase();
            users = users.stream().filter(u -> {
                Customer c = u.getCustomer();
                if (c == null) return false;
                return (c.getFirstName() != null && c.getFirstName().toLowerCase().contains(ql))
                        || (c.getLastName() != null && c.getLastName().toLowerCase().contains(ql))
                        || (c.getEmail() != null && c.getEmail().toLowerCase().contains(ql))
                        || (u.getUsername() != null && u.getUsername().toLowerCase().contains(ql));
            }).collect(Collectors.toList());
        }
        model.addAttribute("users", users);
        model.addAttribute("q", q);
        return "admin_users";
    }

    @GetMapping("/admin/users/{id}")
    public String editUser(@PathVariable Integer id, Model model) {
        AppUser user = appUserRepository.findById(id).orElse(null);
        if (user == null) {
            model.addAttribute("error", "User not found");
            return "admin_users";
        }
        Customer c = user.getCustomer();
        model.addAttribute("appUser", user);
        model.addAttribute("customer", c != null ? c : new Customer());
        return "admin_user_edit";
    }

    @PostMapping("/admin/users/{id}")
    public String saveUser(@PathVariable Integer id,
                           @RequestParam(required = false) String firstName,
                           @RequestParam(required = false) String lastName,
                           @RequestParam(required = false) String email,
                           @RequestParam(required = false) String company,
                           @RequestParam(required = false) String address,
                           @RequestParam(required = false) String city,
                           @RequestParam(required = false) String state,
                           @RequestParam(required = false) String country,
                           @RequestParam(required = false) String postalCode,
                           @RequestParam(required = false) String phone,
                           @RequestParam(required = false) String fax,
                           @RequestParam(required = false) String role,
                           RedirectAttributes redirectAttributes) {
        AppUser user = appUserRepository.findById(id).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/admin/users";
        }
        Customer c = user.getCustomer();
        if (c == null) c = new Customer();
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
        user.setCustomer(c);
        if (role != null && !role.isBlank()) user.setRole(role);
        appUserRepository.save(user);
        redirectAttributes.addFlashAttribute("message", "User updated");
        return "redirect:/admin/users/" + id;
    }

    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        AppUser user = appUserRepository.findById(id).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/admin/users";
        }
        Customer customer = user.getCustomer();
        if (customer != null) {
            // Break bidirectional relationship
            customer.setAppUser(null);
            customerRepository.save(customer);
            // Clear the reference in user
            user.setCustomer(null);
            appUserRepository.save(user);
            // Now delete customer if no related records
            try {
                customerRepository.delete(customer);
            } catch (Exception e) {
                // If customer has related records, just leave it in DB
                redirectAttributes.addFlashAttribute("message", "User deleted, but customer data retained due to related records");
                appUserRepository.delete(user);
                return "redirect:/admin/users";
            }
        }
        // Finally delete the user
        appUserRepository.delete(user);
        redirectAttributes.addFlashAttribute("message", "User and associated customer data deleted successfully");
        return "redirect:/admin/users";
    }
}
