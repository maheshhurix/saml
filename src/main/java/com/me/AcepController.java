package com.me;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLException;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.me.service.LoginService;

@RestController
public class AcepController {
	
	@Autowired
	private Environment environment;
	@Autowired
	private LoginService loginService;
	
	private static boolean bootStrapped = false;
	public static final String APPM_MGT_SAML2_RESPONSE = "AppMgtSAML2Response";

	
	@ResponseBody
	@PostMapping(value = "/ssorequest/idp/consumer")
	public Response redi(HttpServletRequest request) {

		Map<String, String[]> parameterMap = request.getParameterMap();

		String samlResponseHeader = request.getParameter("SAMLResponse");
		byte[] base64DecodedResponse = Base64.getDecoder().decode(samlResponseHeader);

		// Reading AppMgtSAML2Response header value from the request

		// Decoding the extracted encoded SAML Response
		Response response = null;

		// Initializing Open SAML Library
		try {
			doBootstrap();
		} catch (SAMLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		try {
			// Converting the decoded SAML Response string into DOM object
			ByteArrayInputStream inputStreams = new ByteArrayInputStream(base64DecodedResponse);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = null;
			try {
				document = docBuilder.parse(inputStreams);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Element element = document.getDocumentElement();

			// Unmarshalling the element
			UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
			Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
			XMLObject responseXmlObj = unmarshaller.unmarshall(element);
			response = (Response) responseXmlObj;

		} catch (ParserConfigurationException e) {
			try {
				throw new SAMLException("Error while parsing the decoded SAML Response", e);
			} catch (SAMLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (UnmarshallingException e) {
			try {
				throw new SAMLException("Error while unmarshalling the decoded SAML Response", e);
			} catch (SAMLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		Status status = response.getStatus();

		StatusCode statusCode = status.getStatusCode();
		String value = statusCode.getValue();

		return response;

	}

	public static void doBootstrap() throws SAMLException {
		/* Initializing the OpenSAML library */
		if (!bootStrapped) {
			try {
				DefaultBootstrap.bootstrap();
				bootStrapped = true;
			} catch (ConfigurationException e) {
				throw new SAMLException("Error while bootstrapping OpenSAML library", e);
			}
		}
	}
	
	 
	private String getSSOUrl() {
		String redirectUrl, redirectString = null;

		String idpAppURL = environment.getProperty("IDP_SSO_URL");
		String relayState = environment.getProperty("RELAYSTATE_BASE_URL") + "?articleId=1234"; // Relaystate can be
		// dynamic
		String assertionConsumerServiceUrl = environment.getProperty("ACS_URL");
		String issuerId = environment.getProperty("IDP_ISSUER_ID");
		return redirectUrl = loginService.getAuthNRedirectUrl(idpAppURL, relayState, assertionConsumerServiceUrl, issuerId);
		
	}
	
	
	public Response r(String url) {
		 
		byte[] base64DecodedResponse = Base64.getDecoder().decode(url);

		// Reading AppMgtSAML2Response header value from the request

		// Decoding the extracted encoded SAML Response
		Response response = null;

		// Initializing Open SAML Library
		try {
			doBootstrap();
		} catch (SAMLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		try {
			// Converting the decoded SAML Response string into DOM object
			ByteArrayInputStream inputStreams = new ByteArrayInputStream(base64DecodedResponse);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = null;
			try {
				document = docBuilder.parse(inputStreams);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Element element = document.getDocumentElement();

			// Unmarshalling the element
			UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
			Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
			XMLObject responseXmlObj = unmarshaller.unmarshall(element);
			response = (Response) responseXmlObj;

		} catch (ParserConfigurationException e) {
			try {
				throw new SAMLException("Error while parsing the decoded SAML Response", e);
			} catch (SAMLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (UnmarshallingException e) {
			try {
				throw new SAMLException("Error while unmarshalling the decoded SAML Response", e);
			} catch (SAMLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		Status status = response.getStatus();

		StatusCode statusCode = status.getStatusCode();
		String value = statusCode.getValue();

		return response;

	
	}
	 

}
