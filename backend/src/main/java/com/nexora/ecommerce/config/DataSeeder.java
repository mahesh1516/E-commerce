package com.nexora.ecommerce.config;

import com.nexora.ecommerce.entity.*;
import com.nexora.ecommerce.repository.*;
import com.nexora.ecommerce.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Inserts sample data on first start (only when the users table is empty).
 *
 * DEVELOPMENT-ONLY ACCOUNTS - change or remove before any real use:
 *   admin@nexora.com / Admin@123   (ROLE_ADMIN)
 *   user@nexora.com  / User@123    (ROLE_USER)
 *
 * Disable with: app.seed.enabled=false
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, Category> categories = new HashMap<>();

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Sample data already present - skipping seed");
            return;
        }

        Role userRole = roleRepository.findByName(Role.USER)
                .orElseGet(() -> roleRepository.save(new Role(Role.USER)));
        Role adminRole = roleRepository.findByName(Role.ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(Role.ADMIN)));

        User admin = user("Nexora Admin", "admin@nexora.com", "Admin@123", "9000000001");
        admin.getRoles().add(adminRole);
        admin.getRoles().add(userRole);
        userRepository.save(admin);

        User customer = user("Mahesh Kumar", "user@nexora.com", "User@123", "9000000002");
        customer.getRoles().add(userRole);
        userRepository.save(customer);

        Address address = new Address();
        address.setUser(customer);
        address.setName("Mahesh Kumar");
        address.setPhone("9000000002");
        address.setAddressLine1("12, MG Road");
        address.setAddressLine2("Near Metro Station");
        address.setCity("Bengaluru");
        address.setState("Karnataka");
        address.setPostalCode("560001");
        address.setCountry("India");
        address.setDefaultAddress(true);
        addressRepository.save(address);

        category("Mobiles", "Smartphones from top brands");
        category("Laptops", "Laptops for work, study and play");
        category("Electronics", "Audio, TVs and gadgets");
        category("Clothing", "Everyday and formal wear");
        category("Shoes", "Sneakers, sports and formal shoes");
        category("Home Appliances", "Kitchen and home essentials");
        category("Books", "Bestsellers and technical books");

        Product iphone = product("Mobiles", "Apple iPhone 15 (128 GB)", "Apple", "APL-IP15-128",
                "79900", "69999", 25,
                "Dynamic Island, 48MP main camera and USB-C in a durable colour-infused glass design.",
                "Display: 6.1-inch Super Retina XDR\nChip: A16 Bionic\nCamera: 48MP + 12MP\nStorage: 128 GB");
        Product s24 = product("Mobiles", "Samsung Galaxy S24 (256 GB)", "Samsung", "SAM-S24-256",
                "89999", "74999", 18,
                "Compact flagship with Galaxy AI features, bright AMOLED display and pro-grade cameras.",
                "Display: 6.2-inch Dynamic AMOLED 2X\nRAM: 8 GB\nStorage: 256 GB\nBattery: 4000 mAh");
        product("Mobiles", "OnePlus 12R (8 GB / 128 GB)", "OnePlus", "OPL-12R-128",
                "42999", "39999", 30,
                "Smooth 120Hz display, fast charging and a large battery for all-day use.",
                "Display: 6.78-inch LTPO AMOLED\nChip: Snapdragon 8 Gen 2\nBattery: 5500 mAh\nCharging: 100W");
        product("Mobiles", "Redmi Note 13 Pro 5G", "Xiaomi", "XMI-RN13P",
                "25999", "23999", 4,
                "200MP camera and a vivid AMOLED screen at a mid-range price.",
                "Display: 6.67-inch AMOLED\nCamera: 200MP\nBattery: 5100 mAh");

        Product macbook = product("Laptops", "Apple MacBook Air 13\" (M3)", "Apple", "APL-MBA-M3",
                "114900", "104900", 10,
                "Thin, silent and fast with the M3 chip and up to 18 hours of battery life.",
                "Chip: Apple M3\nRAM: 8 GB\nStorage: 256 GB SSD\nDisplay: 13.6-inch Liquid Retina");
        product("Laptops", "Dell Inspiron 15 (i5, 16 GB)", "Dell", "DEL-INS15-I5",
                "62990", "55990", 12,
                "Reliable everyday laptop with a full-size keyboard and FHD display.",
                "CPU: Intel Core i5 13th Gen\nRAM: 16 GB\nStorage: 512 GB SSD\nDisplay: 15.6-inch FHD");
        product("Laptops", "HP Pavilion x360 14", "HP", "HP-PAVX360",
                "58999", null, 3,
                "2-in-1 convertible touchscreen laptop for work and creativity.",
                "CPU: Intel Core i5\nRAM: 16 GB\nStorage: 512 GB SSD\nDisplay: 14-inch FHD Touch");
        product("Laptops", "Lenovo IdeaPad Slim 3", "Lenovo", "LEN-IPS3",
                "45990", "39990", 20,
                "Lightweight laptop with long battery life, ideal for students.",
                "CPU: AMD Ryzen 5\nRAM: 8 GB\nStorage: 512 GB SSD\nWeight: 1.6 kg");

        Product sony = product("Electronics", "Sony WH-1000XM5 Wireless Headphones", "Sony", "SNY-WH1000XM5",
                "34990", "26990", 15,
                "Industry-leading noise cancellation with crystal-clear calls and 30-hour battery.",
                "Type: Over-ear\nBattery: 30 hours\nNoise cancellation: Yes\nConnectivity: Bluetooth 5.2");
        product("Electronics", "boAt Airdopes 141 Earbuds", "boAt", "BOAT-AD141",
                "4490", "1299", 100,
                "True wireless earbuds with 42 hours of playback and low-latency mode.",
                "Playback: 42 hours\nCharging: USB-C\nWater resistance: IPX4");
        product("Electronics", "Samsung 55\" Crystal 4K Smart TV", "Samsung", "SAM-TV55-4K",
                "64900", "42990", 8,
                "4K UHD smart TV with vivid colour and built-in streaming apps.",
                "Screen: 55-inch\nResolution: 3840 x 2160\nHDR: HDR10+\nSmart OS: Tizen");

        product("Clothing", "Levi's 511 Slim Fit Jeans", "Levi's", "LEV-511-32",
                "3599", "2159", 40,
                "Classic slim fit jeans with a bit of stretch for comfort.",
                "Fit: Slim\nMaterial: 99% cotton, 1% elastane\nSize: 32");
        product("Clothing", "Allen Solly Men's Formal Shirt", "Allen Solly", "ALS-FS-M",
                "1999", "1199", 50,
                "Crisp cotton formal shirt for office and occasions.",
                "Fit: Regular\nMaterial: Cotton\nSleeve: Full");
        product("Clothing", "Puma Essentials Hoodie", "Puma", "PUM-ESS-HOOD",
                "2999", null, 0,
                "Soft fleece hoodie for everyday comfort.",
                "Material: Cotton blend\nFit: Regular");

        Product nike = product("Shoes", "Nike Air Max SC", "Nike", "NIK-AMSC-9",
                "6795", "5436", 22,
                "Everyday sneakers with visible Air cushioning.",
                "Size: UK 9\nUpper: Leather and textile\nSole: Rubber");
        product("Shoes", "Adidas Ultraboost Light", "Adidas", "ADI-UBL-9",
                "17999", "12599", 9,
                "Lightweight running shoes with responsive Boost cushioning.",
                "Size: UK 9\nUse: Running\nCushioning: Light Boost");
        product("Shoes", "Bata Formal Oxford Shoes", "Bata", "BAT-OXF-8",
                "2499", "1999", 35,
                "Polished lace-up Oxfords for the office.",
                "Size: UK 8\nUpper: Synthetic leather");

        Product airFryer = product("Home Appliances", "Philips Air Fryer HD9252", "Philips", "PHI-AF-HD9252",
                "9995", "7499", 14,
                "Fry, bake and roast with up to 90% less fat.",
                "Capacity: 4.1 L\nPower: 1400 W\nPresets: 7");
        product("Home Appliances", "Prestige Induction Cooktop PIC 20", "Prestige", "PRE-PIC20",
                "3395", "2299", 27,
                "Energy-efficient induction cooktop with Indian menu presets.",
                "Power: 1200 W\nControls: Push button\nAuto shut-off: Yes");
        product("Home Appliances", "LG 7 kg Front Load Washing Machine", "LG", "LG-FL7KG",
                "39990", "30990", 6,
                "Inverter direct-drive washer with steam wash.",
                "Capacity: 7 kg\nType: Front load\nEnergy rating: 5 star");

        Product atomic = product("Books", "Atomic Habits", "Penguin Random House", "BK-ATOMIC",
                "799", "499", 60,
                "A practical guide to building good habits and breaking bad ones.",
                "Author: James Clear\nFormat: Paperback\nPages: 320");
        product("Books", "The Psychology of Money", "Jaico", "BK-PSYMONEY",
                "399", "299", 45,
                "Timeless lessons on wealth, greed and happiness.",
                "Author: Morgan Housel\nFormat: Paperback\nPages: 252");
        product("Books", "Clean Code", "Pearson", "BK-CLEANCODE",
                "899", null, 12,
                "A handbook of agile software craftsmanship.",
                "Author: Robert C. Martin\nFormat: Paperback\nPages: 464");

        review(customer, iphone, 5, "Fantastic camera and battery life. Totally worth it!");
        review(customer, s24, 4, "Great display, compact size. Battery could be better.");
        review(customer, macbook, 5, "Silent, fast and the battery lasts all day.");
        review(customer, sony, 5, "Best noise cancellation I've used.");
        review(customer, nike, 4, "Comfortable for daily wear. Runs slightly large.");
        review(customer, airFryer, 4, "Crispy fries with very little oil. Easy to clean.");
        review(customer, atomic, 5, "Simple ideas that genuinely changed my routine.");

        log.info("Sample data created: 2 users, {} categories, {} products",
                categories.size(), productRepository.count());
        log.warn("DEV accounts: admin@nexora.com / Admin@123 and user@nexora.com / User@123 - change before real use");
    }

    private User user(String name, String email, String rawPassword, String phone) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setPhone(phone);
        return u;
    }

    private void category(String name, String description) {
        Category c = new Category();
        c.setName(name);
        c.setSlug(SlugUtil.toSlug(name));
        c.setDescription(description);
        categories.put(name, categoryRepository.save(c));
    }

    private Product product(String category, String name, String brand, String sku,
                            String price, String discountPrice, int stock,
                            String description, String specifications) {
        Product p = new Product();
        p.setCategory(categories.get(category));
        p.setName(name);
        p.setBrand(brand);
        p.setSku(sku);
        p.setPrice(new BigDecimal(price));
        p.setDiscountPrice(discountPrice == null ? null : new BigDecimal(discountPrice));
        p.setDescription(description);
        p.setSpecifications(specifications);

        Inventory inventory = new Inventory();
        inventory.setProduct(p);
        inventory.setQuantity(stock);
        p.setInventory(inventory);

        return productRepository.save(p);
    }

    private void review(User user, Product product, int rating, String comment) {
        Review r = new Review();
        r.setUser(user);
        r.setProduct(product);
        r.setRating(rating);
        r.setComment(comment);
        reviewRepository.save(r);
        product.setRating(rating);
        product.setReviewCount(1);
    }
}
