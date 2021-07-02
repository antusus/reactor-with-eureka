# reactor-with-eureka
Playing with project reactor and Netflix Eureka with Spring Cloud.

Requires Java 16 to run.
To run each service call 
```bash
./gradlew bootRun
```

Using examples from "Reactive Spring" by Josh Long

## Eureka server
Run it before other examples.

Server running on default port 8761.

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

## Orders Service
Returns list of cusorders for customers. Application starts on random port and registers in Eureka service.

Sample usage:
```
GET http://localhost:54905/orders?ids=10
```
Gets three customers with delay:
```json
[
  {
    "id": "1ccd6836-cfcc-4179-a23a-b48126beb75b",
    "customerId": 10
  },
  {
    "id": "bcb67ee1-35e5-4381-9df9-2868810fc057",
    "customerId": 10
  }
]
```