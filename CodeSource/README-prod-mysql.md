# Profil production + MySQL (préparation)

Ce document prépare l’environnement avant l’implémentation des profils `prod`.

## 1) Créer bases et utilisateurs MySQL
Un script est fourni: `scripts/create_mysql_dbs.sql`.

Exécuter depuis PowerShell (adapter le mot de passe root si nécessaire):

```
# Option A: si root n’a pas de mot de passe
mysql -u root < scripts/create_mysql_dbs.sql

# Option B: avec mot de passe via variable d’environnement
$env:MYSQL_ROOT_PASSWORD = "<votre_mot_de_passe_root>"
mysql --user=root --password=$env:MYSQL_ROOT_PASSWORD < scripts/create_mysql_dbs.sql

# Option C: prompt interactif
mysql -u root -p < scripts/create_mysql_dbs.sql
```

Bases créées: `product_db`, `review_db`, `recommendation_db`, `composite_db`, `auth_db`.
Utilisateurs: `product_user`, `review_user`, `recommendation_user`, `composite_user`, `auth_user` (modifiez les mots de passe dans le script si besoin).

## 2) Config centralisée (GitHub)
Des fichiers `*-prod.properties` ont été ajoutés dans `github-config-repo/`:
- `application-prod.properties`: paramètres communs prod (Actuator santé/info, Eureka, tracing 0.1).
- `<service>-prod.properties`: datasource MySQL, JPA (validate), ports distincts, marqueur `app.config.source=github-prod`.

Exemples de propriétés (product-service):
```
server.port=9081
spring.datasource.url=jdbc:mysql://localhost:3306/product_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=${PRODUCT_DB_USERNAME:product_user}
spring.datasource.password=${PRODUCT_DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false
spring.jpa.show-sql=false
```

Push vers GitHub:
```
cd github-config-repo
git add .
git commit -m "Add prod properties for MySQL"
git push origin main
```

## 3) Dépendances
- Les services Product/Review/Recommendation ont déjà `mysql-connector-j`.
- Ajouté `mysql-connector-j` (scope runtime) à `authorization-service` et `product-composite-service` pour éviter l’échec d’initialisation quand le profil prod définit une datasource.

## 4) Test minimal
- Vérifier que MySQL répond: `mysql -u root -e "SHOW DATABASES;"`
- Démarrer `config-server` et vérifier `http://localhost:8888/<service>/prod` et `http://localhost:8888/application/prod`.

## 5) Lancement (quand vous serez prêt à activer le profil)
- Exemple: `mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"`
- Les services utiliseront les ports définis dans `*-prod.properties`.

## 6) Sécurité
- Ne stockez pas de mots de passe en clair dans le dépôt.
- Utilisez les placeholders `${...}` résolus par variables d’environnement (ex: `PRODUCT_DB_PASSWORD`).
- Activer uniquement `health,info` sur Actuator en prod.

## 7) Dépannage
- Erreur de driver: assurez-vous que `mysql-connector-j` est présent dans le POM du service.
- 404 côté Config Server: vérifier le fichier `<service>-prod.properties` sur branche `main`.
- Connexion refusée: vérifier le port `3306`, firewall Windows, ou mot de passe root.
