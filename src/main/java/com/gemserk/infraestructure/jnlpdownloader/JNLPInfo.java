package com.gemserk.infraestructure.jnlpdownloader;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class JNLPInfo {

	public static final String COMMON_OS = "";
	
	public static class ResourcesInfo {
		public Set<URI> jars = new HashSet<URI>();
		public Set<URI> natives = new HashSet<URI>();
		public Map<String, JNLPInfo> extensions = new HashMap<String, JNLPInfo>();
		
		public void addAllFirstLevel(ResourcesInfo ri){
			jars.addAll(ri.jars);
			natives.addAll(ri.natives);
		}
		
		
		@Override
		public String toString() {
			return "ResourcesInfo [extensions=" + extensions + ", jars=" + jars + ", natives=" + natives + "]";
		}
		
		
	}
	
	public Map<String, ResourcesInfo> resources = new HashMap<String, ResourcesInfo>();
	public String mainClass;
	public List<String> parameters = new ArrayList<String>();
	
	
	
	public ResourcesInfo getResourcesInfo(String OS){
		ResourcesInfo resourcesInfo = resources.get(OS);
		if(resourcesInfo==null){
			resourcesInfo = new ResourcesInfo();
			resources.put(OS, resourcesInfo);
		}
		return resourcesInfo;
	}



	@Override
	public String toString() {
		return "JNLPInfo [resources=" + resources + ", mainClass=" + mainClass + ", parameters=" + parameters + "]";
	}
	
	
	public void addAllResources(JNLPInfo finalJnlpInfo){
		for (Entry<String, ResourcesInfo> entry : resources.entrySet()) {
			ResourcesInfo finalResourceInfo = finalJnlpInfo.getResourcesInfo(entry.getKey());
			ResourcesInfo currentResourceInfo = entry.getValue();
			finalResourceInfo.addAllFirstLevel(currentResourceInfo);
			for (JNLPInfo extension : currentResourceInfo.extensions.values()) {
				extension.addAllResources(finalJnlpInfo);
			}
		}
	}
	
	
	public JNLPInfo flatten(){
		JNLPInfo jnlpInfo = new JNLPInfo();
		jnlpInfo.mainClass = this.mainClass;
		jnlpInfo.parameters = new ArrayList<String>(this.parameters);
		
		addAllResources(jnlpInfo);
		
		
		return jnlpInfo;
	}
	
}


