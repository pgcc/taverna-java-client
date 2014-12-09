# Taverna Java Client

A Java Client for TavernaServer 2.5.x REST API

## Setup

```java
TavernaClient client = new TavernaClient();
client.setBaseUri("https://localhost:8080/TavernaServer-2.5.4/rest");
client.setAuthorization("taverna", "taverna");
```

## Usage

### Create a new Workflow Run

```java
String uuid = client.create("/Users/vitorfs/Documents/Web_Service_example.t2flow");
```
