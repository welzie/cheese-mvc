package org.launchcode.controllers.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.launchcode.controllers.IntegrationTestConfig;
import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.data.CategoryDao;
import org.launchcode.models.data.CheeseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@IntegrationTestConfig
public class CheeseRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private CategoryDao categoryDao;

    private List<Cheese> testCheeses = new ArrayList<>();

    @Before
    /**
     *  Put KNOWN data in the test database.
     *  Also keep a list of the objects that should be in the database and use those to test results.
     *
     *  This will run before EACH TEST, so that each test has clean, known data to use. This prevents
     *  data pollution from test to test.
     */
    public void before() {
        Category classic = categoryDao.save(new Category("classic"));
        this.testCheeses.add(cheeseDao.save(new Cheese("Mild Cheddar", "orange", classic)));
        this.testCheeses.add(cheeseDao.save(new Cheese("Cheddar", "orange", classic)));
        this.testCheeses.add(cheeseDao.save(new Cheese("Sharp Cheddar", "orange", classic)));
    }

    @Test
    public void getAllCheeses() throws Exception {
        Cheese firstCheeseByName = testCheeses.get(1);
        mockMvc.perform(get("/api/cheeses"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(new MediaType(MediaType.APPLICATION_JSON, Charset.forName("utf8"))))
                .andExpect(jsonPath("$", hasSize(this.testCheeses.size())))
                .andExpect(jsonPath("$[0].name", is(firstCheeseByName.getName())))
                .andExpect(jsonPath("$[0].id", is(firstCheeseByName.getId())))
                .andExpect(jsonPath("$[0].description", is(firstCheeseByName.getDescription())));
    }

    @Test
    public void getCheeseById() throws Exception {
        Cheese cheese = testCheeses.get(0);
        mockMvc.perform(get("/api/cheeses/" + cheese.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(new MediaType(MediaType.APPLICATION_JSON, Charset.forName("utf8"))))
                .andExpect(jsonPath("$.name", is(cheese.getName())))
                .andExpect(jsonPath("$.id", is(cheese.getId())))
                .andExpect(jsonPath("$.description", is(cheese.getDescription())))
                .andExpect(jsonPath("$.category.id", is(cheese.getCategory().getId())))
                .andExpect(jsonPath("$.category.name", is(cheese.getCategory().getName())));
    }
}
