# Query Based Ruled runner

## What it does
It simulates a validation flow that happens before new contracts, where a consuming system sends a single organisational ID and gets a JSON-object in return.
A response that summarises what rules were applied and if there were any legal problems or economical risks attached to the organisation.

## What it is
This is a proof of concept exploring if a service with rules defined as a document queries, running against JSON-documents, can be a performant and easy to maintain alternative to existing rule executing products at a government agency.

The existing products to compete with are: 
- One performant, but complex Java-service that is hard to maintain
- A low-code platform, meant to replace the first alternative, but unfortunately created new problems

### The Java-legacy project
The Java-service created concerns for the business owners where each added rule took longer and longer to write, test and deliver.
It was also meant to scale its responsibilities and replace a similar product, but it could never reach the needed feature delivery velocity,

### The Low-code platform
In the Low-code platform we found out that complex problems, also gives complex low-code and that it roughly takes the same time as Java to write and deliver new features, but now the platform have added a factor times 100 to the execution time and licensing fees based on each consumer call.

## What to proof
A successful proof of concept would execute as fast the old java-service, while proving that queries can replace large parts of the Java-code that slowed down business progress. 
Adding a new business rule should not require any new code, speeding up the delivery time by quite a lot. 

A successful proof would also show that this PoC is also a performant and cost saving alternative for the legacy system that are running monthly checks on a all. 
This legacy system is also running one rule at a time for each of the roughly 200 000 companies.
Those monthly checks takes roughly 8 hours with server CPU maxed out at 100% and the alarms turned off, too not upset Ops. 
There is money to be saved if we could scale down those servers.

So if those thousands of HTTP-calls could be aggregated into one query call for each rule, then success is there for the taking.

## How to test this as a developer
You want a local database for now, so do
```
docker-compose up
```
To start the service
```shell script
./gradlew quarkusRun
```
Without thinking too much yet, look up in test/resources/rules.http and POST the two rules

After that, go to test/resources/controls.http do a GET for each company type

Now that you have some insights, familiarise your self with the rest of the data in the test-folders and the src code


## Stack
This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

You also need Docker right now to run the tests, remember to do 
```
docker-compose up
```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./gradlew build
```

It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./gradlew build -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./gradlew build -Dquarkus.native.enabled=true
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/organisational-controller-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/gradle-tooling>.

## Related Guides

- MongoDB with Panache ([guide](https://quarkus.io/guides/mongodb-panache)): Simplify your persistence code for MongoDB
  via the active record or the repository pattern
- REST resources for MongoDB with Panache ([guide](https://quarkus.io/guides/rest-data-panache)): Generate Jakarta REST
  resources for your MongoDB entities and repositories
- RESTEasy Classic's REST Client Jackson ([guide](https://quarkus.io/guides/resteasy-client)): Jackson serialization
  support for the REST Client
