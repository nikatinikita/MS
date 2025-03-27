package com.itm.space.backendresources.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itm.space.backendresources.AppTestConfig;
import com.itm.space.backendresources.api.request.UserRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.ws.rs.core.Response;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppTestConfig.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UsersResource mockUsersResource;

    @MockBean
    private Keycloak keycloakClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private RealmResource mockRealmResource;

    @Test
    @WithMockUser(roles = "ROLE_MODERATOR")
    public void create_whenIsOk_thenIsOk() throws Exception {

        UserRequest userRequest = new UserRequest("testuser", "test@example.com", "password", "John", "Doe");
        String userRequestJson = objectMapper.writeValueAsString(userRequest);


        when(keycloakClient.realm(anyString())).thenReturn(mockRealmResource);
        when(mockRealmResource.users()).thenReturn(mockUsersResource);
        when(mockUsersResource.create(any(UserRepresentation.class))).thenReturn(Response.ok().build());


        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequestJson))
                .andExpect(status().isOk());


        verify(keycloakClient).realm(anyString());
        verify(mockRealmResource).users();
        verify(mockUsersResource).create(any(UserRepresentation.class));
    }

}
