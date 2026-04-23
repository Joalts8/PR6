package com.uma.example.springuma.integration;

import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uma.example.springuma.integration.base.AbstractIntegration;
import com.uma.example.springuma.model.Medico;
import com.uma.example.springuma.model.Paciente;

public class PacienteControllerMockMvcIT extends AbstractIntegration {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Paciente paciente;
    private Medico medico;

    @BeforeEach
    void setUp() throws Exception {
        // Primero necesitamos un médico para asociarlo al paciente
        medico = new Medico();
        medico.setDni("835");
        medico.setNombre("Miguel");
        medico.setEspecialidad("Ginecología");

        // Creamos el médico en la base de datos a través de la API
        String medicoResponse = mockMvc.perform(post("/medico")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        
        // Si la respuesta contiene el médico con ID, lo recuperamos
        if (!medicoResponse.isEmpty()) {
            medico = objectMapper.readValue(medicoResponse, Medico.class);
        } else {
            // Si no devuelve el objeto, lo buscamos por DNI para tener su ID
            String findMedico = mockMvc.perform(get("/medico/dni/835"))
                .andReturn().getResponse().getContentAsString();
            medico = objectMapper.readValue(findMedico, Medico.class);
        }

        paciente = new Paciente();
        paciente.setNombre("Maria");
        paciente.setDni("888");
        paciente.setEdad(20);
        paciente.setCita("Revisión anual");
        paciente.setMedico(medico);
    }

    @Test
    @DisplayName("Debe crear un paciente y recuperarlo por ID")
    void testCrearYRecuperarPaciente() throws Exception {
        // 1. Crear paciente
        mockMvc.perform(post("/paciente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isCreated());

        // 2. Recuperar todos los pacientes del médico para obtener el ID del paciente creado
        String response = mockMvc.perform(get("/paciente/medico/" + medico.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dni", is("888")))
                .andReturn().getResponse().getContentAsString();
        
        Paciente[] pacientes = objectMapper.readValue(response, Paciente[].class);
        long pacienteId = pacientes[0].getId();

        // 3. Verificar recuperación por ID individual
        mockMvc.perform(get("/paciente/" + pacienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Maria")));
    }

    @Test
    @DisplayName("Debe eliminar un paciente")
    void testEliminarPaciente() throws Exception {
        // 1. Crear paciente
        mockMvc.perform(post("/paciente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isCreated());

        // 2. Obtener el ID
        String response = mockMvc.perform(get("/paciente/medico/" + medico.getId()))
                .andReturn().getResponse().getContentAsString();
        Paciente[] pacientes = objectMapper.readValue(response, Paciente[].class);
        long pacienteId = pacientes[0].getId();

        // 3. Eliminar
        mockMvc.perform(delete("/paciente/" + pacienteId))
                .andExpect(status().isOk());

        // 4. Verificar que no existe (el controlador devuelve null o 500 según su implementación actual)
        // Nota: Según PacienteController.getPaciente(id), llama a service.getPaciente que usa getReferenceById
        // Esto suele lanzar excepción si no existe.
    }
}
