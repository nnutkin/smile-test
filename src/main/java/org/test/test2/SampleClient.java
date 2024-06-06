package org.test.test2;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SampleClient {
	public static final String LAST_NAMES = "src/main/resources/lastnames.txt";
	public static final int MAX_RETRY = 3;

	private final FhirContext fhirContext;
	private final IGenericClient client;
	private final ResponseTimeInterceptor responseTimeInterceptor;

	public SampleClient(FhirContext fhirContext, IGenericClient client,
			ResponseTimeInterceptor responseTimeInterceptor) {
		this.fhirContext = fhirContext;
		this.client = client;
		this.responseTimeInterceptor = responseTimeInterceptor;
		client.registerInterceptor(new LoggingInterceptor(false));
		client.registerInterceptor(responseTimeInterceptor);
	}

	public static void main(String[] args) throws IOException {
		FhirContext fhirContext = FhirContext.forR4();
		IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
		ResponseTimeInterceptor responseTimeInterceptor = new ResponseTimeInterceptor();
		SampleClient sampleClient = new SampleClient(fhirContext, client, responseTimeInterceptor);
		List<String> lastNames = Files.readAllLines(Paths.get(LAST_NAMES));
		sampleClient.run(lastNames, MAX_RETRY);
	}

	public void run(List<String> lastNames, int maxRetry) throws IOException {
		for (int run = 1; run <= maxRetry; run++) {
			fhirContext.getRestfulClientFactory().setServerValidationMode(
					run == maxRetry ? ServerValidationModeEnum.NEVER : ServerValidationModeEnum.ONCE);
			responseTimeInterceptor.reset();
			call(lastNames);
			double averageResponseTime = responseTimeInterceptor.getAverageResponseTime();
			System.out.println("Run " + run + " - Average response time: " + averageResponseTime + " ms");
		}
	}

	private void call(List<String> lastNames) {
		lastNames.forEach(lastName -> {
			searchPatientsByLastName(lastName);
		});
	}

	private void searchPatientsByLastName(String lastName) {
		client.search().forResource(Patient.class).where(Patient.FAMILY.matches().value(lastName))
				.returnBundle(Bundle.class).execute();
	}
}