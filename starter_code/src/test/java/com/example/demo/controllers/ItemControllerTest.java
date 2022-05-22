package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    private Item mItem0;
    private Item mItem1;

    @Before()
    public void startUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItemById() {
        setItems();

        when(itemRepository.findById(mItem0.getId())).thenReturn(Optional.of(mItem0));

        final ResponseEntity<Item> response = itemController.getItemById(mItem0.getId());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        final Item responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(mItem0.getId(), responseBody.getId());
        assertEquals(mItem0.getName(), responseBody.getName());
        assertEquals(mItem0.getPrice(), responseBody.getPrice());
        assertEquals(mItem0.getDescription(), responseBody.getDescription());
    }

    @Test
    public void getItemByName() {
        setItems();

        List<Item> itemList = Arrays.asList(mItem0);
        when(itemRepository.findByName(mItem0.getName())).thenReturn(itemList);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName(mItem0.getName());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        final Item responseBody = response.getBody().get(0);
        assertNotNull(responseBody);
        assertEquals(mItem0.getId(), responseBody.getId());
        assertEquals(mItem0.getName(), responseBody.getName());
        assertEquals(mItem0.getPrice(), responseBody.getPrice());
        assertEquals(mItem0.getDescription(), responseBody.getDescription());
    }

    @Test
    public void getItems() {
        setItems();
        List<Item> itemList = Arrays.asList(mItem0);
        when(itemRepository.findAll()).thenReturn(itemList);

        final ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        final List<Item> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(itemList,responseBody);
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
}