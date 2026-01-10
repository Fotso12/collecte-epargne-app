# Guide de lancement du backend Spring Boot

## Prérequis

1. **Java 21** (JDK 21) installé
   - Vérifier : `java -version`
   - Télécharger : https://www.oracle.com/java/technologies/downloads/#java21

2. **Maven** installé (ou utiliser le wrapper Maven inclus)
   - Vérifier : `mvn -version`
   - Le projet inclut `mvnw` (Maven Wrapper) pour Windows

3. **MySQL** installé et démarré
   - Base de données : `collecte-epargne`
   - Port : `3306`
   - Utilisateur : `root` (mot de passe vide par défaut)
   - Créer la base si elle n'existe pas : `CREATE DATABASE collecte-epargne;`

## Configuration

Vérifier/modifier `src/main/resources/application.properties` :
- `server.port=8082` (port du backend)
- `spring.datasource.url=jdbc:mysql://localhost:3306/collecte-epargne`
- `spring.datasource.username=root`
- `spring.datasource.password=` (vide par défaut)

## Méthode 1 : Avec Maven Wrapper (Recommandé)

### Sur Windows :
```bash
cd collecte-epargne-app/backend/collecte-epargne-backend
.\mvnw.cmd spring-boot:run
```

### Sur Linux/Mac :
```bash
cd collecte-epargne-app/backend/collecte-epargne-backend
./mvnw spring-boot:run
```

## Méthode 2 : Avec Maven installé

```bash
cd collecte-epargne-app/backend/collecte-epargne-backend
mvn spring-boot:run
```

## Méthode 3 : Depuis un IDE (IntelliJ IDEA, Eclipse, VS Code)

1. Ouvrir le projet dans l'IDE
2. Localiser `CollecteEpargneApplication.java`
3. Clic droit → Run 'CollecteEpargneApplication'

## Vérification

Une fois lancé, le backend devrait être accessible sur :
- **API** : http://localhost:8082
- **Swagger UI** : http://localhost:8082/swagger-ui.html
- **Test endpoint** : http://localhost:8082/api/roles (doit retourner du JSON)

## Résolution de problèmes

### Erreur : Port déjà utilisé
- Changer le port dans `application.properties` : `server.port=8083`
- Ou arrêter l'application qui utilise le port 8082

### Erreur : Connexion MySQL impossible
- Vérifier que MySQL est démarré
- Vérifier les identifiants dans `application.properties`
- Créer la base de données : `CREATE DATABASE collecte-epargne;`

### Erreur : Java version incorrecte
- Installer Java 21
- Vérifier `JAVA_HOME` pointe vers Java 21

## Commandes utiles

### Compiler sans lancer :
```bash
.\mvnw.cmd clean install
```

### Lancer avec un profil spécifique :
```bash
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

### Voir les logs détaillés :
Les logs SQL sont activés par défaut (`spring.jpa.show-sql=true`)

