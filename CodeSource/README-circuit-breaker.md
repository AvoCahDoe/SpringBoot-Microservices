# Circuit Breaker + Fallback (Product Composite)

Ce guide explique comment tester le Circuit Breaker (Resilience4j) et le fallback implémentés sur l’endpoint `GET /product-composite/{productId}`.

## Prérequis
- Java 17 et Maven Wrapper (`mvnw.cmd`).
- Services démarrables localement.
- Eureka (discovery-server) démarré.
- Optionnel: Zipkin (`http://localhost:9411/`) pour observer les traces.

## Démarrage des services
Ouvrez plusieurs terminaux (Windows) et lancez chaque service avec la commande ci-dessous dans le dossier du module:

```
discovery-server\discovery-server> mvnw.cmd spring-boot:run
product-service\product-service> mvnw.cmd spring-boot:run
review-service\review-service> mvnw.cmd spring-boot:run
recommendation-service\recommendation-service> mvnw.cmd spring-boot:run
product-composite-service\product-composite-service> mvnw.cmd spring-boot:run
```

Ports par défaut:
- discovery-server: `8761`
- product-service: `8081`
- review-service: `8082`
- recommendation-service: `8083`
- product-composite-service: `8086`

## Comportement normal
Appelez l’agrégat:

```
GET http://localhost:8086/product-composite/1
```

Résultat attendu: un agrégat complet contenant:
- `product` (id, name, weight)
- `reviews` (liste)
- `recommendations` (liste)

## Déclencher le fallback
Arrêtez un service en aval (par ex. `review-service` → Ctrl+C dans son terminal), puis appelez à nouveau:

```
GET http://localhost:8086/product-composite/1
```

Résultat attendu: agrégat minimal du fallback:
- `product`: `{ id: 1, name: "Unavailable", weight: null }`
- `reviews`: `[]`
- `recommendations`: `[]`

## Observer l’état du circuit
- Le Circuit Breaker configuré s’appelle `composite`.
- Paramètres dans `product-composite-service/src/main/resources/application.properties`:
  - `resilience4j.circuitbreaker.instances.composite.slidingWindowType=COUNT_BASED`
  - `resilience4j.circuitbreaker.instances.composite.slidingWindowSize=10`
  - `resilience4j.circuitbreaker.instances.composite.failureRateThreshold=50`
  - `resilience4j.circuitbreaker.instances.composite.waitDurationInOpenState=10s`
  - `resilience4j.circuitbreaker.instances.composite.registerHealthIndicator=true`

Vérifiez la santé (inclut les indicateurs si exposés):

```
GET http://localhost:8086/actuator/health
```

Rétablissement: relancez le service arrêté (`review-service`), puis refaites des appels. Le circuit passera en half-open puis se fermera si les appels réussissent.

## Traces Zipkin (optionnel)
Si Zipkin est actif, des traces sont exportées (sampling 100%). Ouvrez `http://localhost:9411/` et filtrez par `serviceName` (`product-composite-service`, `product-service`, etc.).

## Dépannage
- `javax.validation` non résolu: utilisez `jakarta.validation.*` (Spring Boot 3).
- Services non découverts: vérifiez l’inscription à Eureka (`application.properties` et ports).
- Pas d’agrégat: assurez-vous que `product-service`, `review-service`, `recommendation-service` sont démarrés.

