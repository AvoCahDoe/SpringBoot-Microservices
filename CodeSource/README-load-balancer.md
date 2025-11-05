# Test du Load Balancer via API Gateway

Ce guide explique comment démarrer plusieurs instances des services `product`, `review`, `recommendation`, et tester la distribution de charge via l’API Gateway utilisant `lb://`.

## Prérequis
- Java 17 et Maven Wrapper (`mvnw.cmd`).
- `discovery-server` (Eureka) démarré sur `http://localhost:8761/`.
- `api-gateway` démarré (port par défaut `8085`).
- Les routes Gateway vers `lb://product-service`, `lb://review-service`, `lb://recommendation-service` sont configurées (déjà présentes dans `api-gateway/src/main/resources/application.yml`).

## Démarrer plusieurs instances
Chaque service a un port par défaut (product: `8081`, review: `8082`, recommendation: `8083`). Lancez une seconde instance en surchargeant `server.port`.

Exemples de commandes (exécutez-les dans les dossiers des modules):

```
# Instance 1 (par défaut)
product-service\product-service> mvnw.cmd spring-boot:run
review-service\review-service> mvnw.cmd spring-boot:run
recommendation-service\recommendation-service> mvnw.cmd spring-boot:run

# Instance 2 (port alternatif)
product-service\product-service> mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--server.port=8181
review-service\review-service> mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--server.port=8182
recommendation-service\recommendation-service> mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--server.port=8183
```

Vérifiez dans l’interface Eureka (`http://localhost:8761/`) que chaque service a 2 instances enregistrées.

## Appeler via le Gateway (lb://)
Les routes configurées exposent:
- `GET http://localhost:8085/products` → `lb://product-service`
- `GET http://localhost:8085/reviews` → `lb://review-service`
- `GET http://localhost:8085/recommendations` → `lb://recommendation-service`

Le Gateway applique un filtre d’authentification global. Ajoutez ces en-têtes (exemple):
- `username: admin`
- `password: admin`
- `role: ADMIN`

Exemples d’appels (répétez plusieurs fois pour observer la répartition):
```
GET http://localhost:8085/products
GET http://localhost:8085/reviews
GET http://localhost:8085/recommendations
```

## Observer la distribution
- Répétez les appels ci-dessus et observez les logs des services (`product-service`, `review-service`, `recommendation-service`).
- Les requêtes doivent alterner entre les instances (par défaut, Round-Robin).
- Vous pouvez aussi appeler des endpoints spécifiques, ex. `GET http://localhost:8085/products/1`, et vérifier quelle instance logge la requête.

## Dépannage
- Aucune distribution: assurez-vous d’avoir au moins 2 instances par service et que Eureka est en ligne.
- Erreur 401: ajoutez les en-têtes `username/password/role` requis par le Gateway.
- Routes indisponibles: confirmez la présence des routes dans `api-gateway/src/main/resources/application.yml` et que Gateway est inscrit à Eureka.

## Verify which port the Gateway forwards to

- Start at least two instances of a service (e.g., `product-composite-service`) so Eureka can register multiple instances.
- Ensure `api-gateway` is running on `:8085` and `discovery-server` is up on `:8761`.
- Every request through Gateway will log the chosen downstream host and port.

Example:

```
GET http://localhost:8085/product-composite/1

Log (api-gateway):
Gateway forwarded 'http://localhost:8085/product-composite/1' via route 'product-composite' to http://192.168.1.10:8080/product-composite/1
```

- The important part is `to http://<host>:<port>/...` which shows which instance received the request.
- Make several calls and confirm the port alternates between your instances (e.g., `8080`, `8081`) to validate load balancing.

Notes:
- Logging is enabled at `INFO` for `com.example.api_gateway.filter`. You can adjust levels in `api-gateway/src/main/resources/application.properties`.
- If you don’t see logs, verify `api-gateway` includes the `LoadBalancerLoggingFilter` under `com.example.api_gateway.filter` and that Eureka shows multiple instances for the target service.

## Notes
- Le Gateway est configuré avec `spring.cloud.gateway.discovery.locator.enabled=true` et des routes statiques via `lb://`.
- Le load balancing est assuré par Spring Cloud LoadBalancer (Round-Robin par défaut).
- Vous pouvez démarrer plus d’instances en ajoutant d’autres ports (`--server.port=8281`, etc.).
