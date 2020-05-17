package br.com.senior.challenge.controllers;

import br.com.senior.challenge.SeniorChallengeApplication;
import br.com.senior.challenge.builder.ItemBuilder;
import br.com.senior.challenge.builder.SalesOrderBuilder;
import br.com.senior.challenge.builder.SalesOrderItemBuilder;
import br.com.senior.challenge.entities.Item;
import br.com.senior.challenge.entities.SalesOrder;
import br.com.senior.challenge.entities.SalesOrderItem;
import br.com.senior.challenge.entities.enums.ItemType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SeniorChallengeApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SalesOrderItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper defaultMapper;

    @Test
    public void givenSalesOrderItem_whenList_thenReturnOk() throws Exception {
        SalesOrder salesOrderCreated = getDefaultSalesOrder();
        Item itemCreated = getDefaultItem("", ItemType.PRODUCT);

        MvcResult mvcResult = performOrder("/" + salesOrderCreated.getId() + "/item", HttpMethod.POST, getDefaultSalesOrderItem(salesOrderCreated, itemCreated), status().isOk());
        SalesOrderItem salesOrderItemCreated = read(mvcResult, SalesOrderItem.class);

        mvcResult = performOrder("/" + salesOrderCreated.getId() + "/item", HttpMethod.GET, salesOrderItemCreated, status().isOk());
        PagedModel pagedModel = read(mvcResult, PagedModel.class);

        assertThat(pagedModel.getMetadata()).isNotNull();
        assertThat(pagedModel.getMetadata().getTotalElements()).isEqualTo(1);
    }

    @Test
    public void givenSalesOrderItem_whenGet_thenReturnOk() throws Exception {
        SalesOrder salesOrderCreated = getDefaultSalesOrder();
        Item itemCreated = getDefaultItem("", ItemType.PRODUCT);

        MvcResult mvcResult = performOrder("/" + salesOrderCreated.getId() + "/item", HttpMethod.POST, getDefaultSalesOrderItem(salesOrderCreated, itemCreated), status().isOk());
        SalesOrderItem salesOrderItemCreated = read(mvcResult, SalesOrderItem.class);

        mvcResult = performOrder("/" + salesOrderCreated.getId() + "/item", HttpMethod.GET, salesOrderItemCreated, status().isOk());
        SalesOrderItem salesOrderItemFound = read(mvcResult, SalesOrderItem.class);

        assertThat(salesOrderItemFound).isNotNull();
    }

    @Test
    public void givenSalesOrderItem_whenCreate_thenReturnOk() throws Exception {
        performOrder("", HttpMethod.POST, getDefaultSalesOrder(), status().isOk());
    }

    @Test
    public void givenWrongJson_whenCreate_thenThrow() throws Exception {
        performOrder("", HttpMethod.POST, "{\"id\":}", status().isBadRequest());
    }

    @Test
    public void givenSalesOrderItem_whenUpdate_thenReturnOk() throws Exception {

        SalesOrder salesOrderCreated = getDefaultSalesOrder();
        Item itemCreated = getDefaultItem("", ItemType.PRODUCT);

        MvcResult mvcResult = performOrder("/" + salesOrderCreated.getId() + "/item", HttpMethod.POST, getDefaultSalesOrderItem(salesOrderCreated, itemCreated), status().isOk());
        SalesOrderItem salesOrderItemToUpdate = read(mvcResult, SalesOrderItem.class);
        salesOrderItemToUpdate.setQuantity(BigDecimal.valueOf(20));

        mvcResult = performOrder("/" + salesOrderCreated.getId() + "/item/" + salesOrderItemToUpdate.getId(), HttpMethod.PUT, salesOrderItemToUpdate, status().isOk());

        SalesOrderItem salesOrderItemUpdated = read(mvcResult, SalesOrderItem.class);

        assertThat(salesOrderItemToUpdate.getId()).isEqualTo(salesOrderItemUpdated.getId());
        assertThat(salesOrderItemToUpdate.getQuantity()).isEqualTo(salesOrderItemUpdated.getQuantity());
    }

    @Test
    public void givenSalesOrderItem_whenDelete_thenReturnNotFound() throws Exception {

        SalesOrder salesOrderCreated = getDefaultSalesOrder();
        Item itemCreated = getDefaultItem("", ItemType.PRODUCT);

        MvcResult mvcResult = performOrder("/" + salesOrderCreated.getId() + "/item", HttpMethod.POST, getDefaultSalesOrderItem(salesOrderCreated, itemCreated), status().isOk());
        SalesOrderItem salesOrderItemToDelete = read(mvcResult, SalesOrderItem.class);

        mvcResult = performOrder("/" + salesOrderCreated.getId() + "/item/" + salesOrderItemToDelete.getId(), HttpMethod.DELETE, salesOrderCreated, status().isOk());

        SalesOrderItem salesOrderItemDeleted = read(mvcResult, SalesOrderItem.class);
        assertThat(salesOrderItemDeleted.getId()).isEqualTo(salesOrderItemToDelete.getId());

        performOrder("/" + salesOrderCreated.getId() + "/item/" + salesOrderItemDeleted.getId(), HttpMethod.GET, salesOrderItemDeleted, status().isNotFound());
    }

    @Test
    public void givenSalesOrderItem_whenItemDeactive_thenReturnOk() throws Exception {

        SalesOrder salesOrderCreated = getDefaultSalesOrder();
        Item itemCreated = getDefaultItem("", ItemType.PRODUCT, false);

        performOrder("/" + salesOrderCreated.getId() + "/item", HttpMethod.POST, getDefaultSalesOrderItem(salesOrderCreated, itemCreated), status().isUnprocessableEntity());
    }

    private SalesOrder getDefaultSalesOrder() throws Exception {
        SalesOrder salesOrder = SalesOrderBuilder.newSalesOrder()
                .withReference("referencia de teste")
                .withComments("comentário de teste")
                .build();
        MvcResult mvcResult = performOrder("", HttpMethod.POST, salesOrder, status().isOk());
        return read(mvcResult, SalesOrder.class);
    }

    private Item getDefaultItem(String prefix, ItemType type, boolean active) throws Exception {
        Item item = ItemBuilder.newItem()
                .withName(prefix + " nome do produto para teste")
                .withType(type)
                .withDescription(prefix + "descrição do produto para teste")
                .build();
        MvcResult mvcResult = performItem("", HttpMethod.POST, item, status().isOk());
        item = read(mvcResult, Item.class);
        if (!active){
            mvcResult = performItem("/" + item.getId() +  "/deactivate", HttpMethod.POST, item, status().isOk());
            return read(mvcResult, Item.class);
        }else {
            return item;
        }
    }

    private Item getDefaultItem(String prefix, ItemType type) throws Exception {
        return getDefaultItem(prefix, type, true);
    }

    private SalesOrderItem getDefaultSalesOrderItem(SalesOrder order, Item item) {
        return SalesOrderItemBuilder.newSalesOrder()
                .withSalesOrder(order)
                .withItem(item)
                .withPrice(BigDecimal.valueOf(19.99))
                .withQuantity(BigDecimal.valueOf(10))
                .build();
    }

    private MvcResult performOrder(String url, HttpMethod method, Object obj, ResultMatcher expected) throws Exception {
        return perform("/sales-order" + url, method, obj, expected);
    }

    private MvcResult performItem(String url, HttpMethod method, Object obj, ResultMatcher expected) throws Exception {
        return perform("/item" + url, method, obj, expected);
    }

    private MvcResult perform(String url, HttpMethod method, Object obj, ResultMatcher expected) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                .request(method, url)
                .content(defaultMapper.writeValueAsString(obj))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(expected)
                .andReturn();
    }

    private <T> T read(MvcResult mvcResult, Class<T> clazz) throws com.fasterxml.jackson.core.JsonProcessingException, UnsupportedEncodingException {
        return defaultMapper.readValue(mvcResult.getResponse().getContentAsString(), clazz);
    }

}
