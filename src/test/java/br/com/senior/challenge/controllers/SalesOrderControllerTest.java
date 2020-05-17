package br.com.senior.challenge.controllers;

import br.com.senior.challenge.SeniorChallengeApplication;
import br.com.senior.challenge.builder.SalesOrderBuilder;
import br.com.senior.challenge.entities.SalesOrder;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SeniorChallengeApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SalesOrderControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper defaultMapper;

    @Test
    public void givenSalesOrder_whenList_thenReturnOk() throws Exception {
        MvcResult mvcResult = perform("", HttpMethod.POST, getDefaultSalesOrder(), status().isOk());
        SalesOrder salesOrderCreated = read(mvcResult, SalesOrder.class);

        mvcResult = perform("", HttpMethod.GET, salesOrderCreated, status().isOk());
        PagedModel pagedModel = read(mvcResult, PagedModel.class);

        assertThat(pagedModel.getMetadata()).isNotNull();
        assertThat(pagedModel.getMetadata().getTotalElements()).isEqualTo(1);
    }

    @Test
    public void givenSalesOrder_whenGet_thenReturnOk() throws Exception {
        MvcResult mvcResult = perform("", HttpMethod.POST, getDefaultSalesOrder(), status().isOk());
        SalesOrder salesOrderCreated = read(mvcResult, SalesOrder.class);

        mvcResult = perform("/" + salesOrderCreated.getId(), HttpMethod.GET, salesOrderCreated, status().isOk());
        SalesOrder salesOrderFound = read(mvcResult, SalesOrder.class);

        assertThat(salesOrderFound.getReference()).isEqualTo(salesOrderFound.getReference());
        assertThat(salesOrderFound.getComments()).isEqualTo(salesOrderFound.getComments());
        assertThat(salesOrderFound.getStatus()).isEqualTo(salesOrderFound.getStatus());
    }

    @Test
    public void givenSalesOrder_whenCreate_thenReturnOk() throws Exception {
        perform("", HttpMethod.POST, getDefaultSalesOrder(), status().isOk());
    }

    @Test
    public void givenWrongJson_whenCreate_thenThrow() throws Exception {
        perform("", HttpMethod.POST, "{\"id\":}", status().isBadRequest());
    }

    @Test
    public void givenSalesOrder_whenUpdate_thenReturnOk() throws Exception {

        MvcResult mvcResult = perform("", HttpMethod.POST, getDefaultSalesOrder(), status().isOk());
        SalesOrder salesOrderToUpdate = read(mvcResult, SalesOrder.class);
        salesOrderToUpdate.setReference("nova referencia");

        mvcResult = perform("/" + salesOrderToUpdate.getId(), HttpMethod.PUT, salesOrderToUpdate, status().isOk());
        SalesOrder salesOrderUpdated = read(mvcResult, SalesOrder.class);

        assertThat(salesOrderToUpdate.getId()).isEqualTo(salesOrderUpdated.getId());
        assertThat(salesOrderToUpdate.getReference()).isEqualTo(salesOrderUpdated.getReference());
    }

    @Test
    public void givenSalesOrder_whenDelete_thenReturnNotFound() throws Exception {

        MvcResult mvcResult = perform("", HttpMethod.POST, getDefaultSalesOrder(), status().isOk());
        SalesOrder salesOrderCreated = read(mvcResult, SalesOrder.class);

        mvcResult = perform("/" + salesOrderCreated.getId(), HttpMethod.DELETE, salesOrderCreated, status().isOk());
        SalesOrder SalesOrderDeleted = read(mvcResult, SalesOrder.class);

        assertThat(SalesOrderDeleted.getId()).isEqualTo(salesOrderCreated.getId());

        perform("/" + SalesOrderDeleted.getId(), HttpMethod.GET, SalesOrderDeleted, status().isNotFound());
    }

    @Test
    public void givenSalesOrder_whenFullfill_thenReturnOk() throws Exception {
        changeStatus("/fulfill");
    }

    @Test
    public void givenSalesOrder_whenCancel_thenReturnOk() throws Exception {
        changeStatus("/cancel");
    }

    private void changeStatus(String status) throws Exception {
        MvcResult mvcResult = perform("", HttpMethod.POST, getDefaultSalesOrder(), status().isOk());
        SalesOrder salesOrderCreated = read(mvcResult, SalesOrder.class);

        mvcResult = perform("/" + salesOrderCreated.getId() + "/" + status, HttpMethod.POST, salesOrderCreated, status().isOk());
        SalesOrder salesOrderFulfilled = read(mvcResult, SalesOrder.class);

        assertThat(salesOrderCreated.getStatus()).isNotEqualTo(salesOrderFulfilled.getStatus());
    }

    private SalesOrder getDefaultSalesOrder() {
        return SalesOrderBuilder.newSalesOrder()
                .withReference("referencia de teste")
                .withComments("comentário de teste")
                .build();
    }

    private MvcResult perform(String url, HttpMethod method, Object salesOrder, ResultMatcher expected) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                .request(method, "/sales-order" + url)
                .content(defaultMapper.writeValueAsString(salesOrder))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(expected)
                .andReturn();
    }

    private <T> T read(MvcResult mvcResult, Class<T> clazz) throws com.fasterxml.jackson.core.JsonProcessingException, UnsupportedEncodingException {
        return defaultMapper.readValue(mvcResult.getResponse().getContentAsString(), clazz);
    }
}
