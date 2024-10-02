package com.davidmedina.webflux.controller;

import com.davidmedina.webflux.model.JsonApiBodyRequest;
import com.davidmedina.webflux.service.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class WebhookController {


    private final WebhookService webhookService;

    @Autowired
    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/webhook")
    public String handleWebhook(@RequestBody JsonApiBodyRequest requestBody) {
        return webhookService.processWebhook(requestBody);
    }

    /*public Mono<String> receiveWebhook(@RequestBody JsonApiBodyRequest requestBody) {
        System.out.println("\nRecibido el mensaje del orquestador: \n" + requestBody);
        return Mono.just("\nWebhook recibido correctamente\n");
    }

    public ResponseEntity<List<JsonApiBodyResponseSuccess>> getStep(@ApiParam(value = "request body get enigma step" ,required=true )  @Valid @RequestBody JsonApiBodyRequest body) {
        String accept = request.getHeader("Accept");
        JsonApiBodyResponseSuccess response = new JsonApiBodyResponseSuccess();
        GetEnigmaStepResponse pasoresponse = new GetEnigmaStepResponse();

        pasoresponse.answer("Open the refirgerator");
        pasoresponse.setHeader(body.getData().get(0).getHeader());
        response.addDataItem(pasoresponse);
        listResponse.add(response);
        return new ResponseEntity<>(listResponse, HttpStatus.OK);

    }*/
}
