# Hotelbeds - hotel-api SDK for Java
##Introduction
------------
Hotelbeds - hotel-api SDK for Java is a set of utilities whose main goal is to help
in the development of Java applications that use [APItude Booking](https://developer.hotelbeds.com), the Hotelbeds API.

##Release notes
------------------------------------------
See [Release notes.md](https://github.com/hotelbeds-sdk/hotel-api-sdk-java/blob/master/RELEASE_NOTES.md)

##JavaDocs
* [HotelAPI Model 0.8-SNAPSHOT Javadocs](http://hotelbeds-sdk.github.io/hotel-api-sdk-java/hotel-api-model/0.8-SNAPSHOT/)
* [HotelAPI SDK 0.8-SNAPSHOT Javadocs](http://hotelbeds-sdk.github.io/hotel-api-sdk-java/hotel-api-sdk/0.8-SNAPSHOT/)

##License
-------
This softwared is licensed under the LGPL v2.1 license. Please refer to the file LICENSE for specific details and more license and copyright information.

##Modules
-------------------
The SDK for Java is composed of various modules:

* **Hotel API Model**: A set of basic classes used in the SDK, deployed as a separate module so it can be used without the SDK
* **Hotel API SDK**: The SDK itself, with the classes required to connect to the remote API and return the results as model objects. 
* **Hotel API Demo**: A sample application that makes use of the SDK, to be used as starting point.

##Using the SDK
-------------------
###Getting the SDK

Include the dependency in your pom.xml with the last released version

    <dependency>
      <groupId>com.hotelbeds.hotel-api-sdk-java</groupId>
      <artifactId>hotel-api-sdk</artifactId>
      <version>0.6</version>
    </dependency>

Include also the Sonatype repository to your repositories:

    	<!-- You can set up the repository in your setting.xml file -->
    	<repositories>
    		<repository>
    			<id>ossrh</id>
    			<url>https://oss.sonatype.org/content/groups/staging</url>
    		</repository>
    	</repositories>

Here you have a complete pom.xml file with the optional dependencies for logging

    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    	<modelVersion>4.0.0</modelVersion>
    	<groupId>your.groupId</groupId>
    	<artifactId>your-artifactId</artifactId>
    	<version>0.0.1-SNAPSHOT</version>
    	<!-- You can set up the repository in your setting.xml file -->
    	<repositories>
    		<repository>
    			<id>ossrh</id>
    			<url>https://oss.sonatype.org/content/groups/staging</url>
    		</repository>
    	</repositories>
    	<dependencies>
    		<dependency>
    			<groupId>com.hotelbeds.hotel-api-sdk-java</groupId>
    			<artifactId>hotel-api-sdk</artifactId>
    			<version>0.6</version>
    		</dependency>
    		<!-- Optionally adding logging dependencies -->
    		<dependency>
    			<groupId>org.slf4j</groupId>
    			<artifactId>slf4j-api</artifactId>
    			<version>1.6.6</version>
    		</dependency>
    		<dependency>
    			<groupId>org.slf4j</groupId>
    			<artifactId>jcl-over-slf4j</artifactId>
    			<scope>optional</scope>
    			<version>1.6.6</version>
    		</dependency>
    		<dependency>
    			<groupId>org.slf4j</groupId>
    			<artifactId>slf4j-log4j12</artifactId>
    			<scope>optional</scope>
    			<version>1.6.6</version>
    		</dependency>
    		<dependency>
    			<groupId>log4j</groupId>
    			<artifactId>log4j</artifactId>
    			<scope>optional</scope>
    			<version>1.2.17</version>
    		</dependency>
    	</dependencies>
    </project>

###Calling the SDK
Here you can find an example calling the /status resource.

    public class Demo {
        public static void main(String[] args) throws HotelSDKException {
            HotelApiClient apiClient = new HotelApiClient("yourApiKey", "yourSharedSecret");
            apiClient.setReadTimeout(40000);
            apiClient.init();
            log.info("Requesting status...");
            StatusRS statusRS = apiClient.status();
            log.info("StatusRS: {}", LoggingRequestInterceptor.writeJSON(statusRS, true));
        }
    }

You can find several examples in the [hotel-api-sdk-demo](https://github.com/hotelbeds-sdk/hotel-api-sdk-java/blob/master/hotel-api-sdk-demo/src/main/java/tst/HotelAPIClientDemo.java) module

##Building the SDK
----------------------
In order to build the SDK you must have permission.

Include the repository information in your Maven settings.xml

        <repository>
          <releases>
            <updatePolicy>always</updatePolicy>
          </releases>
          <snapshots>
            <updatePolicy>always</updatePolicy>
          </snapshots>
          <id>oss-sonatype</id>
          <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
        </repository>
        <repository>
          <releases>
            <updatePolicy>always</updatePolicy>
          </releases>
          <snapshots>
            <updatePolicy>always</updatePolicy>
          </snapshots>
          <id>sonatype-nexus-staging</id>
          <url>https://oss.sonatype.org/service/local/staging/deploy/maven2/</url>
        </repository>

First of all clean all previous release information:

    mvn release:clean

Then prepare the release:

    mvn release:prepare -Dusername=yourUserName -Dpassword=mypassword

The last action is to perform the relase and deploy the artifacts in the repository:

    mvn release:perform
