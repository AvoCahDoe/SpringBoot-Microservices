# Config Server avec dépôt GitHub (properties uniquement)

Ce guide explique comment centraliser la configuration via Spring Cloud Config Server en utilisant un dépôt GitHub et uniquement des fichiers `.properties`.

## Préparation
- Dépôt GitHub: `https://github.com/AvoCahDoe/microservices-config.git`
- Dossier local: `github-config-repo/` contient des exemples:
  - `application.properties` (commun)
  - `api-gateway.properties`, `authorization-service.properties`, `product-service.properties`, `review-service.properties`, `recommendation-service.properties`, `product-composite-service.properties`
- Assurez-vous que `spring.application.name` de chaque service correspond à son fichier (ex: `product-service` ↔ `product-service.properties`).

## Pousser la configuration vers GitHub
Dans PowerShell, depuis `c:\Users\wombi\Desktop\TP_spring\github-config-repo`:

```
# Si dépôt public, identifiants non requis
# Si dépôt privé, définir des variables d'environnement
$env:GITHUB_USERNAME = "<votre_username>"
$env:GITHUB_TOKEN = "<votre_token_pat>"

# Initialiser si nécessaire (déjà initialisé dans ton cas)
# git init
# git remote add origin https://github.com/AvoCahDoe/microservices-config.git

# Ajouter et pousser
git add .
git commit -m "Add config properties for microservices"
git push origin main
```

## Configurer le Config Server
- Le module `config-server` est déjà annoté `@EnableConfigServer`.
- Fichier: `config-server/config-server/src/main/resources/application.properties`:
  - `spring.cloud.config.server.git.uri=https://github.com/AvoCahDoe/microservices-config.git`
  - `spring.cloud.config.server.git.default-label=main`
  - `spring.cloud.config.server.git.clone-on-start=true`
  - `spring.cloud.config.server.git.username=${GITHUB_USERNAME}` (optionnel si privé)
  - `spring.cloud.config.server.git.password=${GITHUB_TOKEN}` (optionnel si privé)

Démarrer le serveur:

```
config-server\config-server> mvnw.cmd spring-boot:run
```

Vérifier l’accès aux configurations:

```
# Fichier commun
GET http://localhost:8888/application/default

# Par service
GET http://localhost:8888/api-gateway/default
GET http://localhost:8888/authorization-service/default
GET http://localhost:8888/product-service/default
GET http://localhost:8888/review-service/default
GET http://localhost:8888/recommendation-service/default
GET http://localhost:8888/product-composite-service/default
```

## Configurer les clients (services)
Chaque service inclut désormais dans `application.properties`:

```
spring.config.import=optional:configserver:
spring.cloud.config.uri=http://localhost:8888
spring.cloud.config.label=main
```

Démarrer les services après le Config Server:

```
api-gateway\api-gateway> mvnw.cmd spring-boot:run
authorization-service\authorization-service> mvnw.cmd spring-boot:run
product-service\product-service> mvnw.cmd spring-boot:run
review-service\review-service> mvnw.cmd spring-boot:run
recommendation-service\recommendation-service> mvnw.cmd spring-boot:run
product-composite-service\product-composite-service> mvnw.cmd spring-boot:run
```

## Tester que ça fonctionne
- Appeler l’endpoint `env` (exposé via config commune) pour voir les sources et marqueurs:

```
GET http://localhost:8081/actuator/env        # product-service
GET http://localhost:8085/actuator/env        # api-gateway
```

- Recherchez `app.config.source` dans la sortie: la valeur doit être `github` (provenant de `*-service.properties` du dépôt).
- Vérifiez que `management.endpoints.web.exposure.include` inclut `env` et provient de la source `configserver`.

## Mettre à jour la configuration
- Modifier un fichier `*.properties` dans le dépôt GitHub (ex: changer `app.config.source=github-v2`).
- `git commit` + `git push` sur `main`.
- Redémarrer le service pour appliquer la nouvelle configuration.

## Dépannage
- 404 sur `http://localhost:8888/<service>/default`: vérifier que le fichier `<service>.properties` existe sur la branche `main`.
- Erreur d’authentification: si le dépôt est privé, définir `GITHUB_USERNAME` et `GITHUB_TOKEN` côté Config Server (variables d’environnement) puis redémarrer.
- Le service ne charge pas la config: vérifier la présence des lignes `spring.config.import=optional:configserver:` et l’URI `http://localhost:8888`.
- YAML vs properties: uniquement des fichiers `.properties` sont utilisés ici; pas de `.yml`.

## Bonnes pratiques
- Centraliser les propriétés communes dans `application.properties` du dépôt.
- Éviter de stocker des secrets en clair; utiliser variables d’environnement ou chiffrement.
- Garder la correspondance stricte entre `spring.application.name` et le nom de fichier de config.
