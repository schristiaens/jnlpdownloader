package com.gemserk.infraestructure.jnlpdownloader;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.xml.sax.SAXException;

import groovy.util.XmlSlurper;
import groovy.util.slurpersupport.GPathResult;
import groovyx.net.http.ParserRegistry;

public class ParserRegistryWithoutValidation extends ParserRegistry{

	@Override
	public GPathResult parseXML(HttpResponse resp) throws IOException, SAXException, ParserConfigurationException {
		XmlSlurper xml = new XmlSlurper();
		xml.setFeature("http://xml.org/sax/features/external-general-entities", false);
		xml.setFeature("http://xml.org/sax/features/external-parameter-entities", false); 
		xml.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		xml.setEntityResolver( catalogResolver );
		return xml.parse( parseText( resp ) );
	}
	
}
