# REST Client (Spring Boot + RestTemplate)

This separate client project calls the API Gateway endpoint `GET /product-composite/{id}`.

## Project location
- `rest-client/rest-client`

## Run the client
- From `rest-client/rest-client`:
  - Windows PowerShell: `./mvnw spring-boot:run` (if Maven Wrapper present), otherwise `mvn spring-boot:run`
- The client starts on `http://localhost:8099`

## Call through the client
- Endpoint:
  - `GET http://localhost:8099/client/product-composite/1`
- The client will forward the call to the Gateway:
  - Default gateway base URL: `http://localhost:8085`
  - To use `http://localhost:8080`, set: `client.gatewayBaseUrl=http://localhost:8080`
    - Example: `mvn spring-boot:run -Dspring-boot.run.arguments="--client.gatewayBaseUrl=http://localhost:8080"`

## Optional auth headers (when gateway+auth is ready)
- Enable and set the headers expected by the gateway filter (`username`, `password`, `role`):
  - `client.auth.enabled=true`
  - `client.auth.username=user`
  - `client.auth.password=user`
  - `client.auth.role=user`
- Example run:
  - `mvn spring-boot:run -Dspring-boot.run.arguments="--client.auth.enabled=true --client.auth.username=user --client.auth.password=user --client.auth.role=user"`

## Prerequisites
- API Gateway running (`api-gateway`) and registered services (Eureka)
- `product-composite-service` up and reachable via gateway route `/product-composite/**`

## Notes
- The client simply proxies the response body as `String`.
- Adjust the `client.gatewayBaseUrl` to match the gateway port configured in your environment (`8085` by default in this repo).
