package com.carbo.job.controllers.external;

import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.carbo.job.model.BolResponse;
import com.carbo.job.model.UserToken;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RestCalls {

    private static final String URI = "https://ea02yu4qdc.execute-api.us-east-1.amazonaws.com/production";

    private static final Logger logger = LoggerFactory.getLogger(ExternalJobServiceController.class);

    protected UserToken getToken(Map<String, String> requestBody, Client client) {
        String tokenURI = URI + "/auth";
        logger.info("Call to AI at URL " + tokenURI);

        WebResource webResource = client.resource(tokenURI);
        ClientResponse response = webResource.accept("application/json")
                                             .header("Content-Type", "application/json")
                                             .post(ClientResponse.class, new Gson().toJson(requestBody)); // Include request body in post method

        // check response status code
        if (response.getStatus() == 200) {
            response.bufferEntity();
            String json = response.getEntity(String.class);

            return new Gson().fromJson(json, UserToken.class);

        } else {
            logger.error("Error calling: " + tokenURI + ". Error: " +
                    response.getStatusInfo().getReasonPhrase());
        }

        return new UserToken();
    }

    protected BolResponse getBolRecord(String secret, int id, String token, Client client) {
        String tokenURI = URI + "/bol?secret=" + secret + "&id=" + id;
        logger.info("Call to AI at URL " + tokenURI);

        WebResource webResource = client.resource(tokenURI);
        ClientResponse response = webResource.accept("application/json")
                                             .header("Content-Type", "application/json")
                                             .header("Authorization", token)
                                             .get(ClientResponse.class);

        // check response status code
        if (response.getStatus() == 200) {
            response.bufferEntity();
            String json = response.getEntity(String.class);

            return new Gson().fromJson(json, BolResponse.class);

        } else {
            logger.error("Error calling: " + tokenURI + ". Error: " +
                    response.getStatusInfo().getReasonPhrase());
        }

        return new BolResponse();
    }
}
