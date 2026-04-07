package com.carbo.job.services;

import com.carbo.job.config.WebClientConfig;
import com.carbo.job.model.User;
import com.carbo.job.repository.UserMongoDbRepository;
import com.carbo.job.utils.WebClientUtils;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final UserMongoDbRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private WebClient.Builder webClientBuilder ;

    @Autowired
    private SslContext sslContext;
    private WebClient webClient = null;

    @Autowired
    WebClientConfig webClientConfig;

    @Autowired
    public UserService(UserMongoDbRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public List<User> getByOrganizationId(String organizationId) {
        return userRepository.findByOrganizationId(organizationId);
    }

    public Optional<User> getUser(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUserName(HttpServletRequest request, String userName) {
        //return userRepository.findByUserName(userName);
        return findByUserNameFromService(request,userName);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(String emailGroupId) {
        userRepository.deleteById(emailGroupId);
    }
    public Optional<User> findByUserNameFromService(HttpServletRequest request, String userName) {

        logger.info("<-------------Invoked UserService :: findByUserNameFromService ------------->");
        logger.info("userName  : "+userName);
        //Getting all headers into map
        Map<String, String> headerValueMap = WebClientUtils.getHeadersInfo(request);
        String authToken = (String)headerValueMap.get("authorization");

        Optional<User> results = Optional.ofNullable(new User());
        try {
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            if(webClient==null)
            webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(webClientConfig.getBaseUrl()+"/"+WebClientUtils.AUTHENTICATION+"/")
                    .build();

            results = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.AUTHENTICATION_FIND_BY_USER_NAME)
                            .queryParam("userName", userName)
                            .build())
                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(User.class)
                    .blockOptional();
        }catch (Exception e){
            logger.error("Error in UserService :: findByUserNameFromService "+e.getMessage());
            results = userRepository.findByUserNameIgnoreCase(userName);
        }
        return results;
    }

}
