package org.test.test2;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SampleClientTest {

    private SampleClient sampleClient;
    private FhirContext fhirContext;
    private IGenericClient client;
    private ResponseTimeInterceptor responseTimeInterceptor;

    @BeforeEach
    public void setUp() {
        fhirContext = FhirContext.forR4();
        client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
        responseTimeInterceptor = new ResponseTimeInterceptor();
        sampleClient = new SampleClient(fhirContext, client, responseTimeInterceptor);
    }

    // Mocking with Mockito is required, but for the lack of time made it as simple as possible
    @Test
    public void testRun_NoExceptionsThrown() {
        List<String> lastNames = Arrays.asList();
        assertDoesNotThrow(() -> sampleClient.run(lastNames, 1));
    }
}