package org.flowerplatform.rapp_manager.arduino_ide.test.utils.httpclient;

public class HttpTestResponse {
	public int responseCode;
	public String responseBody;

	public HttpTestResponse(int responseCode, String responseBody) {
		super();
		this.responseCode = responseCode;
		this.responseBody = responseBody;
	}

	@Override
	public String toString() {
		return "HttpTestResponse [responseCode=" + responseCode + ", responseBody=" + responseBody + "]";
	}
}
