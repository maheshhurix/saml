package com.me.service;

public interface LoginService {

  public String getAuthNRedirectUrl(String idpAppURL, String relayState, String assertionConsumerServiceUrl,
      String issuerId);
}
