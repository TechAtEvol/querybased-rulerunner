# Query Based Ruled runner

## What it does
It simulates a system where a consuming system sends an organisational id and gets a JSOn-object in return that summarises what rules were applied and if were any legal problems or economical risks attached to the organisation.

## What it is
This is proof of concept exploring if a service with rules defined as a query, running against Json-documents can be a performant and easy to maintain alternative to existing rule executing products at a government agency.

The existing products to compete with are: 
- One performant, but complex Java-service
- A low-code platform, meant to replace the first alternative

### The Java-legacy project
The Java-service created concerns for the business owners where each added rule is taking longer and longer to write, test and deliver.
It was also meant to replace a similar product, but it could never reach the needed delivery velocity

### The Low-code platform
In the Low-code platform we found out the hard way that complex problems actually takes the same time as Java to write and deliver, but now the platform have added 100 times to execution time and licensing fees based on each consumer call

## What to proof
A successful proof of concept would execute as fast the old java-service, while proving that queries can replace large parts of the Java-code that slowed down business progress. Adding a new rule will require no new code, speeding the delivery time up by quite a lot. 
A successful proof would also show that this PoC is also a performant and cost saving alternative for running monthly checks, where instead of running one rule at a time for roughly 200 000 companies, you would ask for the ide of all companies that matches the rule.
Today's monthly checks takes roughly 8 hours with server CPU maxed out at 100% and the alarms turned off. There is money to be saved if we could scale down those servers.

## How to test this as a developer
You want a local database for now, so do
```
docker-compose up
```
The start the service
```shell script
./gradlew quarkusDev
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
