package net.digitalbooster.musicshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import net.digitalbooster.musicshop.service.CustomerService;

@Controller
public class RegistrationController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String fax,
            @RequestParam(required = false) String role,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        // Check if email already exists
        if (customerService.emailExists(email)) {
            model.addAttribute("error", "An account with this email already exists");
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
            return "register";
        }

        try {
            // Create Customer
            Customer customer = new Customer();
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setEmail(email);
            customer.setCompany(company);
            customer.setAddress(address);
            customer.setCity(city);
            customer.setState(state);
            customer.setCountry(country);
            customer.setPostalCode(postalCode);
            customer.setPhone(phone);
            customer.setFax(fax);

            customer = customerRepository.save(customer);

            // Create AppUser - username is email
            AppUser user = new AppUser();
            user.setUsername(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole((role == null || role.isBlank()) ? "USER" : role);
            user.setCustomer(customer);

            appUserRepository.save(user);
            
            redirectAttributes.addFlashAttribute("message", "Registration successful. Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred during registration. Please try again.");
            return "register";
        }
    }
}
