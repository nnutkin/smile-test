package org.test.test1;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;

public class SampleClient {

    public static void main(String[] theArgs) {

        // Create a FHIR client
        FhirContext fhirContext = FhirContext.forR4();
        IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
        client.registerInterceptor(new LoggingInterceptor(false));

        // Search for Patient resources
        Bundle response = client
                .search()
                .forResource("Patient")
                .where(Patient.FAMILY.matches().value("SMITH"))
                .returnBundle(Bundle.class)
                .execute();
        
        List<Patient> patients = response.getEntry().stream()
                .map(BundleEntryComponent::getResource)
                .filter(resource -> resource instanceof Patient)
                .map(resource -> (Patient) resource)
                .collect(Collectors.toList());

        // Sort and print patient details
        patients.stream()
                .sorted((p1, p2) -> {
                    String firstName1 = p1.getNameFirstRep().getGivenAsSingleString();
                    String firstName2 = p2.getNameFirstRep().getGivenAsSingleString();
                    return firstName1.compareTo(firstName2);
                })
                .forEach(patient -> {
                    HumanName name = patient.getNameFirstRep();
                    String firstName = name.getGivenAsSingleString();
                    String lastName = name.getFamily();
                    String birthDate = patient.getBirthDate() != null 
                            ? LocalDate.ofInstant(patient.getBirthDate().toInstant(), ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
                            : "N/A";

                    System.out.println("Patient: " + firstName + " " + lastName + ", DOB: " + birthDate);
                });
    


    }

}
