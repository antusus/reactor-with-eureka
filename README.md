# reactor-with-eureka
Playing with project reactor and Netflix Eureka with Spring Cloud.

Requires Java 16 to run.

Using examples from "Reactive Spring" by Josh Long

## Eureka server
Run it before other examples.

Server running on default port 8761.

To run call `./gradlew bootRun`.

## Customer Service
Returns list of customers and can add delay to response. Application starts on random port and registers in Eureka service.

Sample usage:
```
GET http://localhost:54904/customers?ids=1,3,5&shouldDelay=true
```
Gets three customers with delay:
```json
[
  {
    "id": 1,
    "name": "Jane"
  },
  {
    "id": 3,
    "name": "Leroy"
  },
  {
    "id": 5,
    "name": "Zhen"
  }
]
```
