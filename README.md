# Compose multiple upload files and download a file that zip those files without storing them in the server

## The solution

Provide only one endpoint where the request method, headers and parameters are same with all upload endpoint, what is 
different here is the response. Since we don't have a separate download endpoint, we have to move what is for a download
response entity to the upload endpoint. So the upload endpoint doesn't response with text message as usual, instead, it 
responds with a response entity with download headers, and an input stream as response body. The service method simply
read upload files block by block, and stream each block to the download client. 

## The solution for large files

### If only one endpoint is allowed

Develop the service with Spring Boot Webflux. The upload multiple files endpoint will accept the files as input stream.
```
    @PostMapping(value = "/api/v1/download/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Message> streamMessages(@RequestParam("files") MultipartFile[] files) {
        //process the upload files stream,
        for ( MultipartFile file : files) 
         {
             while ( not finished )
             {
                read bytes block and encode the block with base64
                emit the block as a message
             }
          }
    }
```

the client side receive messages with base64 encoded payloads, it then decodes the message byte stream and save to the local file system
by using techniques of "programmatic file downloads in the browser"

### The common practise

Provide an endpoint to uploads multiples file, and an endpoint to download the zip file of those uploaded files, and an endpoint to query header in order to know any pause point of the download.
The download service provide resume capability with Range header.

## Package the spring boot application in docker image

First of all, we need to install Docker in our operation system box.

Run below command to package the app docker as an image
```
mvn spring-boot:build-image
```

## Run the applicaton in a docker container

The above image build results in an image as 'docker.io/library/remote-zipper:0.0.1-SNAPSHOT'

so run the image like below:
```
docker run docker.io/library/remote-zipper:0.0.1-SNAPSHOT
```

## Steps to consider when defining a SDLC for a SaaS application

SDLC is for Software Development Life Cycle

### step 1 - choose project management tool

Choose a project management tool for agile development, for example, use JIRA service to plan development spint, features or bugs
in the sprints.

### step 2 - choose source code control tool

Choose a source code control tool, e.g. github, Bitbucket for source code management. publish the project to remote repository.

### Step 3 - create project

Create the project from some framework, e.g. Spring boot, Spring webflux, for the development of micro-services with some 
IDE such as InteliJ, VS-code. In the project structure, there is configuration file for build tool, e.g. pom.xml for maven,
Dockerfile for docker image. There is test directory that contains a number of tests. 

### step 4 - feature development and bug fix

Choose a project management and build tool to do the feature development and bug fix. features and bugs in JIRA will be
done sprint by sprint, issue by issue.

### Step 5 Testing
1. Unit Test
2. Cucumber End to End Test
3.  Mutation Testing

### Step 6 Code Analysis and Quality

1. Checkstyle
2. Jacoco

### Step 7 Swagger API Documentation

configure swagger API document generator, and write API document in the API code

### Step 8 DevSecOps

1. Dependency Vulnerability Check
2. Docker Image Vulnerability Check
3. Penetration Test

### Step 9 Continuous Integration, Delivery and Deployment
1. Docker Containerization
2. CI and CD Pipeline Tools
    1. CircleCI
    2. Jenkins
    3. Google Cloud Build

### Step 10 Platforms
   7.1 Kubernetes
   7.2 Google Cloud Run
   7.3 Cloud Foundry

## Run the App in IntelliJ

open the project as a maven project. ( select pom.xml as project file )
1. right click the class source file 'RemoteZipperApplication.java'.
2. Choose to click "run 'RemoteZipper....main()'"

## Run the app with command line:

You need to have maven installed in your local environment.

1. open a terminal
2. run "cd 'path_to_project_directory'"
3. run "mvn spring-boot:run"


## Package the app for deployment

If you're on Windows, run below in the command line window (terminal) :
```javascript
mvn -D"maven.test.skip"="true" package
```
Note here, we skip the test, as the test will failed without the service is running. 

If you'are on linux or IOS, run below in the terminal:
```javascript
mvn -Dmaven.test.skip=true package

```
## Run Test in IntelliJ

it is quite straightforward, right click the test file, then click 'Run xxxxxx'. Another way is to
create a JUnit test launcher, omitted here as it is very similar to create a java application launcher as described 
above)


