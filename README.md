# Taverna Java Client

A Java Client for TavernaServer 2.5.x REST API

## Setup

```java
TavernaClient client = new TavernaClient();
client.setBaseUri("http://localhost:8080/TavernaServer-2.5.4/rest");
client.setAuthorization("taverna", "taverna");
```

## Usage

### Create a new run

```java
String uuid = client.create("/Users/vitorfs/Documents/Web_Service_example.t2flow");
```

### Get run status

```java
String status = client.getStatus(uuid);
```

### Get expected inputs

```java
ArrayList<TavernaInput> inputs = client.getExpectedInputs(uuid);
```

### Set input value

```java
client.setInputValue(uuid, "Country", "Brazil");
client.setInputValue(uuid, "City", "Juiz de Fora");
```

### Get workflow output
```java
ArrayList<TavernaOutput> tavernaOutput = client.getOutput(uuid);
```

### Set status to a run

```java
client.setStatus(uuid, TavernaServerStatus.OPERATING);
client.setStatus(uuid, TavernaServerStatus.FINISHED);
```

### Start a run 

Shortcut to setStatus with Operating parameter

```java
client.start(uuid);
```

### Cancel a run 

Shortcut to setStatus with Finished parameter

```java
client.cancel(uuid);
```

### Destroy a run and all related files

```java
client.destroy(uuid);
```
