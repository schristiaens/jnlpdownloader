package com.gemserk.infraestructure.jnlpdownloader
import groovyx.net.http.ContentType;

import java.util.concurrent.Future;



import java.io.File;
import java.io.OutputStream;

import com.gemserk.infraestructure.jnlpdownloader.JNLPInfo 
import com.gemserk.infraestructure.jnlpdownloader.JNLPInfo.ResourcesInfo 

import groovy.io.FileType;
import groovyx.net.http.HTTPBuilder 
import groovyx.net.http.URIBuilder 

class Main {
	
	static def downloadFile(HTTPBuilder httpbuilder, def fileUri, File destFile){
		return httpbuilder.request(fileUri,groovyx.net.http.Method.GET,groovyx.net.http.ContentType.BINARY) { req ->
			headers.'User-Agent' = 'Mozilla/5.0'
			
			response.success = { resp, InputStream reader ->
				assert resp.statusLine.statusCode == 200
				println "My response handler got response: ${resp.statusLine}"
				long fileLength = Long.parseLong(resp.headers.'Content-Length')
				println "Response length: ${fileLength/(1024)}kB"
				
				destFile.withOutputStream {OutputStream outputStream -> 
					byte[] buffer = new byte[10000]
					int downloaded = 0 
					
					int chunkSize = 0
					boolean done = false
					int time = System.currentTimeMillis()
					int lastDownloaded = 0
					while((chunkSize = reader.read(buffer))!=-1){
						outputStream.write(buffer, 0, chunkSize)
						
						downloaded+=chunkSize
						def percent = (downloaded/fileLength)*100
						if(percent == 100){
							println "Done downloading ${destFile.getName()}"
						} else {
							int currentTime = System.currentTimeMillis() 
							int lastTimePeriod = currentTime - time
							if(lastTimePeriod > 1000){
								int downloadedInTimePeriod = (downloaded - lastDownloaded)/1024
								int speed = (downloadedInTimePeriod / lastTimePeriod)*1000
								println "Downloaded ${percent}% - speed: $speed kB/s - ${destFile.getName()}"
								lastDownloaded = downloaded
								time = currentTime
							}						
						}
						
					}
				}
			}
			
			// called only for a 401 (access denied) status code:
			response.'404' = { resp -> println 'Not found' }
		}
	}
	
	static def downloadFileHelper(HTTPBuilder httpbuilder, def url, File destDir){
		def fileUri = new URIBuilder(url)
		def fileNameComponents = fileUri.toString().split("/")
		def fileName = fileNameComponents[fileNameComponents.length -1]
		println "$fileName"
		def destFile = new File(destDir,fileName)
		return downloadFile(httpbuilder, fileUri, destFile)
	}
	
	static def downloadJNLP(URI jnlpurl, File destDir){
		JNLPInfoGenerator generator = new JNLPInfoGenerator()
		
		
		
		JNLPInfo jnlpinfo = generator.getJNLPInfo(jnlpurl)
		
		jnlpinfo = jnlpinfo.flatten()
		println jnlpinfo
		
		
		boolean keepnatives = System.getProperty("keepnatives") != null
		
		
		
		def nativeDir = new File(destDir,"natives/")
		nativeDir.mkdirs()
		
		def jarsDir = new File(destDir,"libs/")
		jarsDir.mkdirs()
		
		
		def  http = new  HTTPBuilder()
		
//				def  http = new  AsyncHTTPBuilder(
//						poolSize : 4,
//						contentType : ContentType.BINARY )
		
		def futures = []
		
		
		jnlpinfo.resources.each { os, ResourcesInfo resourceInfo ->
			def nativeDirByOs = new File(nativeDir,os)
			nativeDirByOs.mkdirs()
			resourceInfo.natives.each { natives ->
				futures << downloadFileHelper(http, natives, nativeDirByOs)
			}
			
			resourceInfo.jars.each { jar ->
				futures << downloadFileHelper(http, jar, jarsDir)
			}
		}
		
		def notDoneFutures = { futures.findAll { !it.done
			} }
		
		while(!futures.isEmpty() && (futures[0] instanceof Future) && !notDoneFutures().isEmpty()){
			def left = notDoneFutures().size
//			println "I am waiting for downloadddd.....$left left"
			Thread.sleep(1000)
		}
		
		
		http.shutdown()
		
		
		def unpacker = new JarUnpacker()
		nativeDir.eachFile { nativeDirByOs ->
			nativeDirByOs.eachFileMatch FileType.FILES, ~/.*\.jar/, { File nativeFile ->
				unpacker.copyJarContent(nativeFile,nativeDirByOs)
				if(!keepnatives)
					nativeFile.delete()
			}
		}
		
		
		def linuxCommandLine = "java -Djava.library.path='natives/Linux' -cp 'libs/*' $jnlpinfo.mainClass ${jnlpinfo.parameters.join(" ")}"
		println "LINUX: $linuxCommandLine"
		new File(destDir,"run-linux.sh").setText(linuxCommandLine)
		def windowsCommandLine =  "java -Djava.library.path=\"natives\\Windows\" -cp \"libs\\*\" $jnlpinfo.mainClass ${jnlpinfo.parameters.join(" ")}"
		println "WINDOWS: $windowsCommandLine"
		new File(destDir,"run-windows.bat").setText(windowsCommandLine)
		def macCommandLine =  "java -Djava.library.path='natives/Mac' -cp 'libs/*' $jnlpinfo.mainClass ${jnlpinfo.parameters.join(" ")}"
		println "Mac OS X: $macCommandLine"
		new File(destDir,"run-macosx.sh").setText(macCommandLine)
	}
	
	static main(args) {
		if(args.length != 2){
			System.out.println("param1: destinationDirectory, param2: jnlpurl");
			System.exit(-1);
		}
		
		String destDirPath = args[0];
		URI jnlpurl = new URIBuilder(args[1]).toURI();
		
		
		
		File destDir = new File(destDirPath);
		
		if(destDir.exists() && !destDir.isDirectory()){
			System.out.println(destDir + " should be a directory not a file");
			System.exit(-1);
		}
		
		destDir.mkdirs();
		
		
		
		downloadJNLP(jnlpurl,destDir)
		
	}
}
