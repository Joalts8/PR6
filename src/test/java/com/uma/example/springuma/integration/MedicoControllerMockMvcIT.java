package com.uma.example.springuma.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uma.example.springuma.integration.base.AbstractIntegration;
import com.uma.example.springuma.model.Medico;

public class MedicoControllerMockMvcIT extends AbstractIntegration {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Medico medico;

    @BeforeEach
    void setUp() {
        medico = new Medico();
        medico.setDni("12345678Z");
        medico.setNombre("Dr. Gregory House");
        medico.setEspecialidad("Diagnóstico");
    }

    @Test
    @DisplayName("Debe crear un médico correctamente y devolver 201 Created")
    void testCrearMedico() throws Exception {
        mockMvc.perform(post("/medico")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/medico/dni/" + medico.getDni()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Dr. Gregory House")))
                .andExpect(jsonPath("$.especialidad", is("Diagnóstico")));
    }

    @Test
    @DisplayName("Debe eliminar un médico existente")
    void testEliminarMedico() throws Exception {
        // creacion del med
        mockMvc.perform(post("/medico")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isCreated());

        // Lo recuperamos para saber qué ID asignada
        String response = mockMvc.perform(get("/medico/dni/" + medico.getDni()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        Medico creado = objectMapper.readValue(response, Medico.class);
        long id = creado.getId();

        // Lo eliminamos por ID
        mockMvc.perform(delete("/medico/" + id))
                .andExpect(status().isOk());

        // Verificamos que ya no existe por DNI (debería dar 404)
        mockMvc.perform(get("/medico/dni/" + medico.getDni()))
                .andExpect(status().isNotFound());
    }
}
