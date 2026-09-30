package com.eventcart.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Data Transfer Object encapsulating pre-checkout summary and user credentials.
 */
public class CheckoutDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private CartDto cart;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public CheckoutDto() {
    }

    public CheckoutDto(Long userId, String fullName, String email, String phoneNumber, CartDto cart) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.cart = cart;
        if (cart != null) {
            this.subtotal = cart.getTotalAmount();
            this.totalAmount = this.subtotal.subtract(this.discountAmount);
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public CartDto getCart() {
        return cart;
    }

    public void setCart(CartDto cart) {
        this.cart = cart;
        if (cart != null) {
            this.subtotal = cart.getTotalAmount();
            this.totalAmount = this.subtotal.subtract(this.discountAmount);
        }
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
        this.totalAmount = this.subtotal.subtract(this.discountAmount);
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
