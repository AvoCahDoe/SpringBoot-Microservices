# Actuator Metrics Test (product-composite-service)

This guide shows how to verify the two Micrometer counters in `product-composite-service`:
- `product_composite.requests.get` for `GET` requests
- `product_composite.requests.write` for `POST/PUT` requests

## Prerequisites
- Java 17 and Maven Wrapper (`mvnw`)
- Actuator/Micrometer dependencies are present (already configured)
- Actuator endpoints exposure: `management.endpoints.web.exposure.include=health,info,metrics` (configured locally and in Config Server)

## Start the service
Option A — easiest (dev/default profile, port `8086`):
- From `product-composite-service/product-composite-service` run:
  - Windows PowerShell: `./mvnw spring-boot:run`
- Health check: `GET http://localhost:8086/actuator/health` should return `UP`

Option B — prod profile (Config Server, port `9080`):
- Ensure Config Server is running at `http://localhost:8888`
- Run: `./mvnw spring-boot:run -Dspring-boot.run.profiles=prod`
- Health check: `GET http://localhost:9080/actuator/health` should return `UP`

## List available metrics
- `GET http://localhost:<PORT>/actuator/metrics`
- You should see:
  - `product_composite.requests.get`
  - `product_composite.requests.write`

## Test GET counter
1. Call aggregate:
   - `GET http://localhost:<PORT>/product-composite/1`
2. Inspect metric:
   - `GET http://localhost:<PORT>/actuator/metrics/product_composite.requests.get`
   - `measurements[0].value` should be `>= 1`

## Test POST/PUT counter
1. Call POST (no-op for instrumentation):
   - `POST http://localhost:<PORT>/product-composite/1`
   - Expected: `202 Accepted`
2. Or call PUT:
   - `PUT http://localhost:<PORT>/product-composite/1`
   - Expected: `202 Accepted`
3. Inspect metric:
   - `GET http://localhost:<PORT>/actuator/metrics/product_composite.requests.write`
   - The value increases after each POST/PUT

## Troubleshooting
- If `/actuator/metrics` is not accessible:
  - Ensure `management.endpoints.web.exposure.include=health,info,metrics` is present locally or via Config Server (`github-config-repo`).
  - Restart the service after changes.
- If the service fails in `prod` profile:
  - Start Config Server, or use the default profile (Option A) to avoid DB requirements.
  - Verify `http://localhost:8888/product-composite-service/prod` shows the served configuration.

## Notes
- The POST/PUT endpoints are no-op, created solely to demonstrate metrics.
- Counter names:
  - `product_composite.requests.get`
  - `product_composite.requests.write`
