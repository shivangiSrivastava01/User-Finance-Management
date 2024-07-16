package com.finance.expenseservice.client;

import com.finance.expenseservice.dto.UserDTO;
import com.finance.expenseservice.exception.ExpenseCustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class UserClient {
    private final RestTemplate restTemplate;

    @Autowired
    public UserClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserDTO getUser(Long userId) {

        try{
            log.info("call User service to fetch the userData::");
            String userServiceUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8083/financeManagement/userById")
                    .queryParam("userId", userId)
                    .toUriString();

            return restTemplate.getForObject(userServiceUrl, UserDTO.class);
        }catch (Exception e){
            throw new ExpenseCustomException("User for the requested User Id and category does not exists");
        }
    }
}
