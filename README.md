### ArchiveManager Server
AM Server is a Spring Boot application that provides the following functionality:
* Data Dictionary based data schema including GUI editor
* Content classification including people corporations and subjects
* Customized search & navigation

Version 1 of the library uses Spring Boot 2.1, Neo4J graph datastore and ElasticSearch.

### Building and testing
The project can be built and tested by running Maven command:
~~~
mvn clean install
~~~

### Artifacts
The artifacts can be obtained by:
* downloading from [Heed Technology repository](https://artifacts.heedtechnology.com/nexus/content/groups/public)
* getting as Maven dependency by adding the dependency to your pom file:
~~~
<dependency>
  <groupId>org.archivemanager</groupId>
  <artifactId>server</artifactId>
  <version>version</version>
</dependency>
~~~
and Alfresco repository:
~~~
<repository>
  <id>heedtechnology-maven-repo</id>
  <url>https://artifacts.heedtechnology.com/nexus/content/groups/public</url>
</repository>
