package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private CartRepository cartRepository = mock(CartRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    private User mUser;
    private Item mItem0;
    private Item mItem1;
    private Cart mCart;

    @Before()
    public void startUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

        setUser();
        when(userRepository.findByUsername(mUser.getUsername())).thenReturn(mUser);

        setCart();
        mUser.setCart(mCart);

        setItems();
        when(itemRepository.findById(mItem0.getId())).thenReturn(Optional.of(mItem0));
        when(itemRepository.findById(mItem1.getId())).thenReturn(Optional.of(mItem1));
    }

    @Test
    public void addTocart() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(mUser.getUsername());
        modifyCartRequest.setItemId(mItem0.getId());
        modifyCartRequest.setQuantity(2);

        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> itemList = Arrays.asList(mItem0, mItem1);

        Cart responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(mCart.getId(),responseBody.getId());
        assertEquals(itemList, responseBody.getItems());
        assertEquals(mUser, responseBody.getUser());
        assertEquals(mCart.getTotal(), BigDecimal.valueOf(3.98));
    }

    @Test
    public void addTocartUserIsNull() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("test1");
        modifyCartRequest.setItemId(mItem0.getId());
        modifyCartRequest.setQuantity(2);

        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void addTocartItemNotPresent() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(mUser.getUsername());
        modifyCartRequest.setItemId(100);
        modifyCartRequest.setQuantity(2);

        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void removeFromcart() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(mUser.getUsername());
        modifyCartRequest.setItemId(mItem0.getId());
        modifyCartRequest.setQuantity(2);
        cartController.addTocart(modifyCartRequest);

        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(mCart.getId(), responseBody.getId());
        assertEquals(0, responseBody.getItems().size());
        assertEquals(mUser,responseBody.getUser());
        assertEquals(BigDecimal.valueOf(0L, 2), mCart.getTotal());
    }

    @Test
    public void removeFromcartUserIsNull() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(mUser.getUsername());
        modifyCartRequest.setItemId(mItem0.getId());
        modifyCartRequest.setQuantity(2);
        cartController.addTocart(modifyCartRequest);

        modifyCartRequest.setUsername("test1");
        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void removeFromcartItemNotPresent() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(mUser.getUsername());
        modifyCartRequest.setItemId(mItem0.getId());
        modifyCartRequest.setQuantity(2);
        cartController.addTocart(modifyCartRequest);

        modifyCartRequest.setItemId(100);
        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    private void setUser() {
        mUser = new User();
        mUser.setId(0);
        mUser.setUsername("test");
        mUser.setPassword("somepassword");
    }

    private void setItems() {
        mItem0 = new Item();
        mItem0.setId(0L);
        mItem0.setName("Round Widget");
        mItem0.setPrice(BigDecimal.valueOf(2.99));
        mItem0.setDescription("A widget that is round");

        mItem1 = new Item();
        mItem1.setId(0L);
        mItem1.setName("Square Widget");
        mItem1.setPrice(BigDecimal.valueOf(1.99));
        mItem1.setDescription("A widget that is square");
    }

    private void setCart() {
        mCart = new Cart();
        mCart.setId(0L);
        mCart.setUser(mUser);
    }
}