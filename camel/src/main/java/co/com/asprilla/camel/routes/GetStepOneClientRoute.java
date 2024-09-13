package co.com.asprilla.camel.routes;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class GetStepOneClientRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Ruta para el primer microservicio
        from("direct:resolve-enigma-api-uno")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to("freemarker:templates/GetStepOneClientTemplate.ftl")
                .to("http://localhost:8080/getStep")
                .log("Step 1 response: ${body}")
                .setProperty("step1Response", body())
                .end();

        // Ruta para el segundo microservicio
        from("direct:resolve-enigma-api-dos")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody().simple("{\"headerId\": \"12345\"}")
                .to("freemarker:templates/GetStepTwoClientTemplate.ftl")
                .to("http://localhost:8081/getStep")
                .log("Step 2 response: ${body}")
                .setProperty("step2Response", body())
                .end();

        // Ruta para el tercer microservicio
        from("direct:resolve-enigma-api-tres")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody().simple("{\"headerId\": \"12345\"}")
                .to("freemarker:templates/GetStepThreeClientTemplate.ftl")
                .to("http://localhost:8082/getStep")
                .log("Step 3 response: ${body}")
                .setProperty("step3Response", body())
                .end();

        // Orquestación
        from("direct:startOrchestration")
                .log("Starting orchestration...")
                .doTry()
                .to("direct:resolve-enigma-api-uno")
                .process(exchange -> {
                    String step1Response = exchange.getIn().getBody(String.class);
                    String step1Answer = extractAnswer(step1Response);
                    exchange.setProperty("step1Answer", step1Answer);
                })
                .to("direct:resolve-enigma-api-dos")
                .process(exchange -> {
                    String step2Response = exchange.getIn().getBody(String.class);
                    String step2Answer = extractAnswer(step2Response);
                    exchange.setProperty("step2Answer", step2Answer);
                })
                .to("direct:resolve-enigma-api-tres")
                .process(exchange -> {
                    String step3Response = exchange.getIn().getBody(String.class);
                    String step3Answer = extractAnswer(step3Response);
                    exchange.setProperty("step3Answer", step3Answer);
                })
                .process(exchange -> {
                    String step1Answer = exchange.getProperty("step1Answer", String.class);
                    String step2Answer = exchange.getProperty("step2Answer", String.class);
                    String step3Answer = exchange.getProperty("step3Answer", String.class);

                    String finalAnswer = String.format(
                            "{\"data\": [{\"header\": {\"id\": \"12345\", \"type\": \"TestGiraffeRefrigerator\"}, \"answer\": \"Step1: %s - Step2: %s - Step3: %s\"}]}",
                            step1Answer, step2Answer, step3Answer
                    );

                    exchange.getIn().setBody(finalAnswer);
                })
                .log("Orchestration completed with answer: ${body}")
                .doCatch(Exception.class)
                .log("An error occurred: ${exception.message}")
                .setBody(constant("{ \"errors\": [{ \"code\": \"ERR001\", \"detail\": \"Failed to process one of the steps\", \"id\": \"12345\", \"source\": \"orchestrator\", \"status\": \"500\", \"title\": \"Internal Error\" }] }"))
                .end();
    }

    // Función para extraer el valor de "answer" del body de la respuesta JSON
    private String extractAnswer(String jsonResponse) {
        return jsonResponse.replaceAll(".*\"answer\":\"([^\"]+)\".*", "$1");
    }
}
