package com.nexora.ecommerce.service;

import com.nexora.ecommerce.entity.*;

import java.math.BigDecimal;

/** Small helpers to build entities for unit tests. */
public final class TestData {

    private TestData() {
    }

    public static User user() {
        User u = new User();
        u.setId(1L);
        u.setName("Mahesh");
        u.setEmail("user@nexora.com");
        u.getRoles().add(new Role(Role.USER));
        return u;
    }

    public static Category category() {
        Category c = new Category();
        c.setId(1L);
        c.setName("Mobiles");
        c.setSlug("mobiles");
        return c;
    }

    /** Product priced 1000, on sale for 900. */
    public static Product product(long id, int stock) {
        Product p = new Product();
        p.setId(id);
        p.setName("Test Phone");
        p.setBrand("Nexora");
        p.setSku("TST-" + id);
        p.setPrice(new BigDecimal("1000"));
        p.setDiscountPrice(new BigDecimal("900"));
        p.setCategory(category());
        Inventory inv = new Inventory();
        inv.setProduct(p);
        inv.setQuantity(stock);
        p.setInventory(inv);
        return p;
    }

    public static Cart cart(User user) {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        return cart;
    }

    public static CartItem cartItem(Cart cart, Product product, int qty) {
        CartItem item = new CartItem();
        item.setId(100L);
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(qty);
        cart.getItems().add(item);
        return item;
    }

    public static Address address(User user) {
        Address a = new Address();
        a.setId(5L);
        a.setUser(user);
        a.setName("Mahesh");
        a.setPhone("9000000002");
        a.setAddressLine1("12, MG Road");
        a.setCity("Bengaluru");
        a.setState("Karnataka");
        a.setPostalCode("560001");
        a.setCountry("India");
        a.setDefaultAddress(true);
        return a;
    }
}
