package org.test.test2;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;

public class ResponseTimeInterceptor implements IClientInterceptor {
	private long totalResponseTime = 0;
	private int requestCount = 0;

	@Override
	public void interceptRequest(IHttpRequest theRequest) {
		// Not required
	}

	@Override
	public void interceptResponse(IHttpResponse response) {
		totalResponseTime += response.getRequestStopWatch().getMillis();
		requestCount++;
	}

	public void reset() {
		totalResponseTime = 0;
		requestCount = 0;
	}

	public double getAverageResponseTime() {
		return requestCount == 0 ? 0 : (double) totalResponseTime / requestCount;
	}

}