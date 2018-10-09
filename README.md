# QCeda Blog

This is a blogging demo app that uses a React in the frontend, Spring Boot for the API, and Neo4J for the database.  By default it is using an embedded instance of Neo4J so no Neo4J server is needed with each start of the application having a completely clean DB.  These settings can be changed by editing the apps.properties file (recommended for development only) or through an environment file (for production deployment).

This app also make use of token-based authentication using JWT (Json Web Token) for session management.  There is no server session state.  Session is maintain through prolongation of a given auth token that is updated with a new value at specific time intervals.  This allows the server to be completely stateless yet support session management.

NOTE:  If the tmp folder is not in your development environment root path, please edit the default Neo4J file URI to point to a valid folder that exists.

Thanks to this [article](https://medium.com/@pietroghezzi/spring-and-react-js-the-easy-way-5abe8a529058) for getting me started with a ReactJS app working within Spring Boot server.

## Requirements
- Java SDK v1.8 or higher
- Apache Maven 3.5.4 or above
- Node v8.x
- NPM v6.4.x

## Installation
```shell
cd /apps/web-ui
npm install
npm run build
cd ../../
mvn package
mvn spring-boot:run
```

visit localhost:8080

## To run front-end UI without spring on webpack-dev-server
```shell
cd /apps/web-ui
npm install
npm start
```

visit localhost:3000

## Customizing default settings

```shell
vi /src/main/java/com/qceda/module/blog/app.properties

## Secret key for JWT auth token generation
jwt.secret=thisismysecretkey

## Non-development Neo4J
#neo4j.URI=bolt://neo4j:password@localhost:7687

## Development Embedded Neo4J
neo4j.URI=file:///tmp/graphdb

## Start out with a clean DB (only for Embedded Neo4j)
neo4j.clean=true

## Auth token valid duration in minutes
token.ttl=30
```


## Customizing settings by environment properties file or startup property
This is the preferred mechanism for production environments or running in docker since the file can be mapped.  The settings are the same as the section above.  If the environment file does not have a needed setting, the default one will be used.  The following example show how to change to a separate Neo4J DB:

```shell
vi /tmp/app.props

neo4j.URI=bolt://neo4j:mypassword@localhost:7687

```

Now run with the following:

```
java -Denv.props.file=/tmp/app.props -jar JAR_ARTIFACT.jar

```

You can also specify the properties directly like the following:

```
java -Dneo4j.URI=bolt://neo4j:mypassword@localhost:7687 -jar JAR_ARTIFACT.jar

```
