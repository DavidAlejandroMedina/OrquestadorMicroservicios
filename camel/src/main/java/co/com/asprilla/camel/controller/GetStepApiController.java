package co.com.asprilla.camel.controller;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/steps")
public class GetStepApiController {

    @Autowired
    private ProducerTemplate producerTemplate;

    @PostMapping("/step-one")
    public ResponseEntity<String> getStepOne(@RequestBody String requestBody) {
        // Envía el request body a la ruta Camel y devuelve la respuesta final
        String response = producerTemplate.requestBody("direct:resolve-enigma-api-uno", requestBody, String.class);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/step-two")
    public ResponseEntity<String> getStepTwo(@RequestBody String requestBody) {
        // Envía el request body a la ruta Camel y devuelve la respuesta final
        String response = producerTemplate.requestBody("direct:resolve-enigma-api-dos", requestBody, String.class);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/step-three")
    public ResponseEntity<String> getStepThree(@RequestBody String requestBody) {
        // Envía el request body a la ruta Camel y devuelve la respuesta final
        String response = producerTemplate.requestBody("direct:resolve-enigma-api-tres", requestBody, String.class);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/orchestration")
    public ResponseEntity<String> startOrchestration(@RequestBody String requestBody) {
        // Envía el request body a la ruta Camel de orquestación y devuelve la respuesta final
        String response = producerTemplate.requestBody("direct:startOrchestration", requestBody, String.class);
        return ResponseEntity.ok(response);
    }
}
