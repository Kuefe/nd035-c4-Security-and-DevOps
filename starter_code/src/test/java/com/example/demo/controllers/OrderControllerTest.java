package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    private OrderController orderController;
    private OrderController orderControllerSpy;

    private OrderRepository orderRepository = mock(OrderRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    private User mUser;
    private Item mItem0;
    private Item mItem1;
    private Cart mCart;
    private UserOrder mUserOrder;

    private List<Item> itemList;

    @Before()
    public void startUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        orderControllerSpy = Mockito.spy(orderController); // Spying on that real instance

        setItems();
        itemList = Arrays.asList(mItem0, mItem1);
        setUser();
        setCart();
        mUser.setCart(mCart);
        setUserOrder();

        when(userRepository.findByUsername(mUser.getUsername())).thenReturn(mUser);
        when(orderRepository.findByUser(mUser)).thenReturn(List.of(mUserOrder));
    }

    @Test
    public void submit() {
        doReturn(mUserOrder).when(orderControllerSpy).createFromCartWrapper(mUser.getCart());
        final ResponseEntity<UserOrder> response = orderController.submit(mUser.getUsername());

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(mUserOrder.getUser(), responseBody.getUser());
        assertEquals(mUserOrder.getItems(), responseBody.getItems());
        assertEquals(mUserOrder.getTotal(), responseBody.getTotal());
    }

    @Test
    public void getOrdersForUser() {
        final ResponseEntity<List<UserOrder>> response =
                orderController.getOrdersForUser(mUser.getUsername());

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(mUserOrder.getUser(), responseBody.get(0).getUser());
        assertEquals(mUserOrder.getItems(), responseBody.get(0).getItems());
        assertEquals(mUserOrder.getTotal(), responseBody.get(0).getTotal());
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
        mCart.setItems(itemList);
        mCart.setTotal(BigDecimal.valueOf(4.98));
    }

    private void setUserOrder() {
        mUserOrder = new UserOrder();
        mUserOrder.setId(0L);
        mUserOrder.setUser(mUser);
        mUserOrder.setItems(itemList);
        mUserOrder.setTotal(BigDecimal.valueOf(4.98));
    }
}