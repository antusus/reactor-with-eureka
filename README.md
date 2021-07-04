# reactor-with-eureka
Playing with project reactor and Netflix Eureka with Spring Cloud.

Requires Java 16 to run.
To run each service call 
```bash
./gradlew bootRun
```

Using examples from "Reactive Spring" by Josh Long

# Eureka server
Run it before other examples.

Server running on default port 8761.

# Services

Each service starts on random port and registers itself in Eureka service.


## Customer Service
Returns list of customers and can add delay to response.

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
Returns list of orders for customers.

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

## Profiles Service
Returns profile of a customer. 

Sample usage:
```
GET http://localhost:55616/profiles/10
```
Gets three customers with delay:
```json
{
  "id": 10,
  "username": "richard",
  "password": "10c363cc-50f8-459e-a595-31298fc82835"
}
```

## Error Service
### Returns `500` error always
```
GET http://localhost:55617/cb
```
### Counts attempts to get data
```
GET http://localhost:55950/ok?uid=1e457fc3-74b1-4dff-a715-828d3835e7f6
```

Response example:
```json
{
  "greeting": "greeting attempt 4 on port 55950"
}
```

### Counts attemts and returns 500 error if number of attempts is less than 3 or returns response
```
GET http://localhost:55950/retry?uid=1e457fc3-74b1-4dff-a715-828d3835e7f6
```
Response example:
```json
{
  "greeting": "greeting attempt 4 on port 55950"
}
```

## Slow Service
Returns deleayed greeting response
```
GET http://localhost:56441/greetings/?name=John
```
Response example:
```json
{
  "message": "Hello, John! (from 56441 took 2(s))"
}
```
You can change delay by running:
```bash
export service_delay=3 && ./gradlew bootRun
```
or editing value in `application.properties`.

# Client
The client project contain sample Client application.
**WebClientAutoConfiguration** overrides default load balancer settings and switches DNS lookup for Eureka service lookup. This way we can use `http://order-service` URI to get current location of Order Service.

 - **OrdersClient** gets orders
 - **RetryClient** uses ressilience4j and specifies Retry policy when requests fails
 - **CircuitBreakerClient** uses Circuit Breaker to fail fast. Retries 10 times to get greeting. 

