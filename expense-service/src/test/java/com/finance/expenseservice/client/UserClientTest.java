package com.finance.expenseservice.client;


import com.finance.expenseservice.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserClientTest {

    @InjectMocks
    private UserClient userClient;

    @Mock
    private RestTemplate restTemplate;

    private UserDTO userDTO;
    private final Long userid = 1L;

    @BeforeEach
    void setUp() {

        userDTO = new UserDTO();
        userDTO.setUserId(userid);
        userDTO.setName("Shivangi Srivastava");

        userClient = new UserClient(this.restTemplate);
    }

    @Test
    void testGetUser_Success() {

        when(userClient.getUser(userid)).thenReturn(userDTO);
        UserDTO result = userClient.getUser(userid);

        assertNotNull(result);
        assertEquals(userid, result.getUserId());
        assertEquals("Shivangi Srivastava", result.getName());
    }

    @Test
    void testGetUser_NotFound() {

        when(userClient.getUser(userid)).thenReturn(null);
        UserDTO result = userClient.getUser(userid);

        assertNull(result);
    }

    @Test
    void testGetUser_InternalServerError() {

        when(userClient.getUser(userid)).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> userClient.getUser(userid));
    }
}
