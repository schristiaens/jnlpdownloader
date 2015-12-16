Given a url of a jnlp file, JNLPDownloader downloads every jar declared in the main jnlp or extension, every native library, and stores them in a directory with scripts to run the application in Linux, Windows or Mac.

Usage:

```
java -jar jnlpdownloader.jar example http://www.example.com/application.jnlp
```

That will download the jnlp from `http://www.example.com/application.jnlp` and save it to the `example` directory.

As a simplified example, for a jnlp file:

```
<?xml version="1.0" encoding="utf-8"?>
<jnlp spec="1.0+" codebase="http://www.example.com/" href="application.jnlp">

	<information>
		<title>Some Title</title>
		<vendor>Some Vendor</vendor>
		<description>Some Description</description>
	</information>

	<resources>
		<jar href="slf4j-api-1.5.8.jar" />
		<jar href="google-collections-1.0.jar" />
		<jar href="lwjgl-2.4.2.jar" />
	</resources>

	<resources os="Windows">
		<nativelib href="lwjgl-2.4.2-natives-win.jar" />
	</resources>

	<resources os="Linux">
		<nativelib href="lwjgl-2.4.2-natives-linux.jar" />
	</resources>

	<resources os="Mac">
		<nativelib href="lwjgl-2.4.2-natives-mac.jar" />
	</resources>
  	
	<application-desc main-class="Main" />
</jnlp>
```

The output will be the next folder structure with the native libs uncompressed in the natives directory:

```
./example
./example/natives
./example/natives/Linux
./example/natives/Linux/liblwjgl.so
./example/natives/Windows
./example/natives/Windows/lwjgl.dll
./example/natives/Mac
./example/natives/Mac/liblwjgl.jnilib
./example/libs
./example/libs/google-collections-1.0.jar
./example/libs/slf4j-api-1.5.8.jar
./example/libs/lwjgl-2.4.2.jar
./example/run-windows.bat
./example/run-macosx.sh
./example/run-linux.sh
```