package com.gemserk.infraestructure.jnlpdownloader

import com.gemserk.infraestructure.jnlpdownloader.JNLPInfo 
import com.gemserk.infraestructure.jnlpdownloader.JNLPInfo.ResourcesInfo 
import groovyx.net.http.HTTPBuilder 
import java.net.URI;


class JNLPInfoGenerator {
	
	JNLPInfo getJNLPInfo(URI jnlpurl){
		JNLPInfo jnlpinfo = new JNLPInfo()
		
		def http = new HTTPBuilder(jnlpurl)
		def jnlp = http.get( contentType:groovyx.net.http.ContentType.XML)
		String codebaseText = jnlp.@codebase.text()
		if(!codebaseText.endsWith("/"))
			codebaseText = codebaseText + "/"
		
		URI codebase = URI.create(codebaseText)
		
		def allResources = jnlp.resources
		allResources.each { resources ->
			def os = resources.@os.text().split(" ")[0]
			ResourcesInfo resourcesInfo = jnlpinfo.getResourcesInfo(os)
			println "resources - $os"
			def allJars = resources.jar
			allJars.each { jar ->
				def href = jar.@href.text()
				def uri = codebase.resolve(href)
				resourcesInfo.jars.add(uri)
				println "\tJAR: $uri"
			}
			
			def allNatives = resources.nativelib
			allNatives.each { natives ->
				def href = natives.@href.text()
				def uri = codebase.resolve(href)
				resourcesInfo.natives.add(uri)
				println "\tNATIVE: $uri"
			}
			
			resources.extension.each { extension ->
				def name = extension.@name.text()
				def extensionUri = codebase.resolve(extension.@href.text())
				println "\tBEGIN - EXTENSION: $name - $extensionUri"
				def extensionjnlpinfo = getJNLPInfo(extensionUri)
				resourcesInfo.extensions[(name)] = extensionjnlpinfo
				println "\tEND - EXTENSION: $name - $extensionUri"
			}
		}
		
		def applicationDesc = jnlp."application-desc"
		def mainClass = applicationDesc.@"main-class".text()
		
		jnlpinfo.mainClass = mainClass
		
		applicationDesc.parameters.each { parameter ->
			jnlpinfo.parameters.add(parameter)
		}
		
		return jnlpinfo
	}
}	