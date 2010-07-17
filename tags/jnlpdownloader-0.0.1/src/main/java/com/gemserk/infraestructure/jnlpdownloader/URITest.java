package com.gemserk.infraestructure.jnlpdownloader;

import java.net.URI;

public class URITest {

	public static void main(String[] args) {
		URI codebase = URI.create("http://www.example.com/");
		
		URI withRelative = codebase.resolve("test1.html");
		System.out.println(withRelative);
		
		URI codebaseWithDirNoSlash = codebase.resolve("dir1");
		System.out.println(codebaseWithDirNoSlash);

		URI withRelativeAfterNoSlash = codebaseWithDirNoSlash.resolve("dir1b");
		System.out.println(withRelativeAfterNoSlash);
		
		URI codebaseWithDirWithSlash = codebase.resolve("dir1/");
		System.out.println(codebaseWithDirWithSlash);

		URI withRelativeAfterSlash = codebaseWithDirWithSlash.resolve("dir1b");
		System.out.println(withRelativeAfterSlash);
		
		URI withAbsoluteSameHost = codebaseWithDirWithSlash.resolve("/dir2");
		System.out.println(withAbsoluteSameHost);
		
		
		URI withAnotherFullURI = codebaseWithDirWithSlash.resolve("http://www.anotherexample.com/dir/launch.jnlp");
		System.out.println(withAnotherFullURI);
	}
	
}
