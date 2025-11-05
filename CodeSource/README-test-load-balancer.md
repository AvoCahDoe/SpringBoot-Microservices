# Tester le Load Balancer via API Gateway

Ce guide montre comment démarrer plusieurs instances des services et tester la distribution de charge via l’API Gateway avec des appels `lb://`.

## Prérequis
- Java 17 et Maven Wrapper (`mvnw.cmd`).
- `discovery-server` (Eureka) lancé sur `http://localhost:8761/`.
- `api-gateway` lancé sur `http://localhost:8085/`.
- Routes Gateway configurées vers `lb://product-service`, `lb://review-service`, `lb://recommendation-service` (déjà présentes dans `api-gateway/src/main/resources/application.yml`).

## Démarrer plusieurs instances
Dans des terminaux séparés, lancez les instances par défaut puis une seconde instance avec un port différent (Windows):

```
# Instances par défaut
product-service\product-service> mvnw.cmd spring-boot:run
review-service\review-service> mvnw.cmd spring-boot:run
recommendation-service\recommendation-service> mvnw.cmd spring-boot:run

# Instances supplémentaires (ports alternatifs)
product-service\product-service> mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--server.port=8181
review-service\review-service> mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--server.port=8182
recommendation-service\recommendation-service> mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--server.port=8183
```

Vérifiez dans Eureka (`http://localhost:8761/`) que chaque service affiche 2 instances.

## En-têtes requis (Gateway Auth)
Le Gateway applique un filtre d’authentification global. Ajoutez ces en-têtes à chaque requête:
- `username: admin`
- `password: admin`
- `role: ADMIN`

## Appels via Gateway (curl)
Effectuez plusieurs appels (au moins 10) pour observer la répartition Round-Robin:

```
# Produits
curl.exe -H "username: admin" -H "password: admin" -H "role: ADMIN" http://localhost:8085/products

# Avis
curl.exe -H "username: admin" -H "password: admin" -H "role: ADMIN" http://localhost:8085/reviews

# Recommandations
curl.exe -H "username: admin" -H "password: admin" -H "role: ADMIN" http://localhost:8085/recommendations
```

Répétez ces commandes plusieurs fois et observez les logs dans les terminaux des services: les requêtes doivent alterner entre les ports (ex. `8081` et `8181` pour `product-service`).

## Appels d’ID spécifique (optionnel)
Vous pouvez cibler des endpoints individuels pour rendre les réponses comparables:

```
curl.exe -H "username: admin" -H "password: admin" -H "role: ADMIN" http://localhost:8085/products/1
curl.exe -H "username: admin" -H "password: admin" -H "role: ADMIN" http://localhost:8085/reviews?productId=1
curl.exe -H "username: admin" -H "password: admin" -H "role: ADMIN" http://localhost:8085/recommendations?productId=1
```

## Dépannage
- 401 Unauthorized: assurez-vous d’envoyer les en-têtes `username/password/role`.
- Pas de distribution: démarrez au moins 2 instances par service et vérifiez l’inscription à Eureka.
- Routes introuvables: confirmez le fichier `api-gateway/src/main/resources/application.yml` et le statut du Gateway dans Eureka.

## Notes
- Le Gateway utilise `spring.cloud.gateway.discovery.locator.enabled=true` et des routes statiques `lb://`.
- Le load balancing utilise Spring Cloud LoadBalancer (Round-Robin par défaut).
- Vous pouvez ajouter d’autres instances avec d’autres ports (`--server.port=8281`, etc.).

