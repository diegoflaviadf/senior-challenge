package br.com.senior.challenge.controllers;

import br.com.senior.challenge.SeniorChallengeApplication;
import br.com.senior.challenge.builder.ItemBuilder;
import br.com.senior.challenge.entities.Item;
import br.com.senior.challenge.entities.enums.ItemType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
public class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper defaultMapper;

    @Test
    public void givenItem_whenList_thenReturnOk() throws Exception {
        MvcResult mvcResult = perform("", HttpMethod.POST, getDefaultItem(), status().isOk());
        Item itemCreated = read(mvcResult, Item.class);

        mvcResult = perform("", HttpMethod.GET, itemCreated, status().isOk());
        PagedModel pagedModel = read(mvcResult, PagedModel.class);

        assertThat(pagedModel.getMetadata()).isNotNull();
        assertThat(pagedModel.getMetadata().getTotalElements()).isEqualTo(1);
    }

    @Test
    public void givenItem_whenGet_thenReturnOk() throws Exception {
        MvcResult mvcResult = perform("", HttpMethod.POST, getDefaultItem(), status().isOk());
        Item itemCreated = read(mvcResult, Item.class);

        mvcResult = perform("/" + itemCreated.getId(), HttpMethod.GET, itemCreated, status().isOk());
        Item itemFound = read(mvcResult, Item.class);

        assertThat(itemCreated.getName()).isEqualTo(itemFound.getName());
        assertThat(itemCreated.getType()).isEqualTo(itemFound.getType());
        assertThat(itemCreated.getDescription()).isEqualTo(itemFound.getDescription());
    }

    @Test
    public void givenItem_whenCreate_thenReturnOk() throws Exception {
        perform("", HttpMethod.POST, getDefaultItem(), status().isOk());
    }

    @Test
    public void givenWrongJson_whenCreate_thenThrow() throws Exception {
        perform("", HttpMethod.POST, "{\"id\":}", status().isBadRequest());
    }

    @Test
    public void givenItem_whenUpdate_thenReturnOk() throws Exception {

        MvcResult mvcResult = perform("", HttpMethod.POST, getDefaultItem(), status().isOk());

        Item itemToUpdate = read(mvcResult, Item.class);
        itemToUpdate.setName("nome do produto para teste alterado");

        mvcResult = perform("/"+itemToUpdate.getId(), HttpMethod.PUT, itemToUpdate, status().isOk());

        Item itemUpdated = read(mvcResult, Item.class);
        assertThat(itemUpdated.getId()).isEqualTo(itemToUpdate.getId());
        assertThat(itemUpdated.getName()).isNotEqualTo(itemToUpdate.getId());
    }

    @Test
    public void givenItem_whenDelete_thenReturnNotFound() throws Exception {

        MvcResult mvcResult = perform("", HttpMethod.POST, getDefaultItem(), status().isOk());

        Item itemToDelete = read(mvcResult, Item.class);

        mvcResult = perform("/"+itemToDelete.getId(), HttpMethod.DELETE, itemToDelete, status().isOk());

        Item itemDeleted = read(mvcResult, Item.class);
        assertThat(itemDeleted.getId()).isEqualTo(itemToDelete.getId());

        perform("/"+itemDeleted.getId(), HttpMethod.GET, itemDeleted, status().isNotFound());
    }

    @Test
    public void givenItem_whenDeactivateAndActivate_thenReturnOk() throws Exception {

        MvcResult mvcResult = perform("", HttpMethod.POST, getDefaultItem(), status().isOk());

        Item itemToDeactivate = read(mvcResult, Item.class);

        mvcResult = perform("/"+itemToDeactivate.getId()+"/deactivate", HttpMethod.POST, itemToDeactivate, status().isOk());

        Item itemDeactivated = read(mvcResult, Item.class);
        assertThat(itemDeactivated.getActive()).isEqualTo(itemDeactivated.getActive());

        mvcResult = perform("/"+itemToDeactivate.getId()+"/activate", HttpMethod.POST, itemToDeactivate, status().isOk());

        Item itemActivated = read(mvcResult, Item.class);
        assertThat(itemActivated.getActive()).isEqualTo(itemActivated.getActive());
    }

    @Test
    public void givenItem_whenActivate_thenReturnNotProcessed() throws Exception {

        MvcResult mvcResult = perform("", HttpMethod.POST, getDefaultItem(), status().isOk());

        Item itemToDeactivate = read(mvcResult, Item.class);

        perform("/"+itemToDeactivate.getId()+"/activate", HttpMethod.POST, itemToDeactivate, status().isUnprocessableEntity());
    }

    @Test
    public void givenItem_whenDeactivateAndDeactivate_thenReturnNotProcessed() throws Exception {

        MvcResult mvcResult = perform("", HttpMethod.POST, getDefaultItem(), status().isOk());

        Item itemToDeactivate = read(mvcResult, Item.class);

        mvcResult = perform("/"+itemToDeactivate.getId()+"/deactivate", HttpMethod.POST, itemToDeactivate, status().isOk());

        Item itemDeactivated = read(mvcResult, Item.class);
        assertThat(itemDeactivated.getActive()).isEqualTo(itemDeactivated.getActive());

        perform("/"+itemToDeactivate.getId()+"/deactivate", HttpMethod.POST, itemToDeactivate, status().isUnprocessableEntity());
    }

    private Item getDefaultItem() {
        return ItemBuilder.newItem()
                .withName("nome do produto para teste")
                .withType(ItemType.PRODUCT)
                .withDescription("descrição do produto para teste")
                .build();
    }

    private MvcResult perform(String url, HttpMethod method, Object item, ResultMatcher expected) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                .request(method, "/item" + url)
                .content(defaultMapper.writeValueAsString(item))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(expected)
                .andReturn();
    }

    private <T> T read(MvcResult mvcResult, Class<T> clazz) throws com.fasterxml.jackson.core.JsonProcessingException, UnsupportedEncodingException {
        return defaultMapper.readValue(mvcResult.getResponse().getContentAsString(), clazz);
    }

}
