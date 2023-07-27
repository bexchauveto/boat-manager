package com.bexchauvet.boatmanager.rest;

import com.bexchauvet.boatmanager.BoatManagerApplication;
import com.bexchauvet.boatmanager.domain.Boat;
import com.bexchauvet.boatmanager.repository.BoatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = BoatManagerApplication.class)
@AutoConfigureMockMvc
public class BoatControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;


    @BeforeEach
    void init(@Autowired BoatRepository boatRepository) {
        boatRepository.saveAll(List.of(new Boat(1L, "QUEEN MARY 2", "Passenger Ship", false),
                new Boat(2L, "QUEEN ELIZABETH", "Combat Vessel", false)));
    }

    @Test
    void No_Token() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/boats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        mvc.perform(MockMvcRequestBuilders.get("/boats/filter")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        mvc.perform(MockMvcRequestBuilders.get("/boats/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        mvc.perform(MockMvcRequestBuilders.post("/boats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"CHARLES DE GAULLE\", \"description\":\"Combat Vessel\"}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(MockMvcRequestBuilders.put("/boats/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"QUEEN MARY 3\"}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(MockMvcRequestBuilders.delete("/boats/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        mvc.perform(MockMvcRequestBuilders.post("/boats/1/image")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        mvc.perform(MockMvcRequestBuilders.get("/boats/1/image")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

    }


}
