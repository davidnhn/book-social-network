# Build stage
FROM maven:3.8.7-openjdk-18 AS build
WORKDIR /build
# Copie le fichier pom.xml dans le répertoire de travail /build
COPY pom.xml .
# Télécharge toutes les dépendances du projet pour les mettre en cache
RUN mvn dependency:go-offline
# Copie tout le contenu du dossier src dans le répertoire de travail /build/src
COPY src ./src
# cette commande build un dossier target avec le projet compilé
# Compile le projet et génère un fichier JAR dans le dossier target, sans exécuter les tests
RUN mvn clean package -DskipTests

# Runtime stage
# Utilise une image Amazon Corretto avec JDK 17 pour exécuter l'application
FROM amazoncorretto:17
# define few thing (on indique les valeur pas defaut)
# Définit des arguments de build par défaut pour le profil et la version de l'application
ARG PROFILE=dev
ARG APP_VERSION=1.0.0

# Définit le répertoire de travail à /app dans l'image
WORKDIR /app
# Copie le fichier JAR généré lors de la phase de build vers le répertoire de travail /app
COPY --from=build /build/target/book-network-*.jar /app/
# cd C:\Users\david\Documents\spring-boot\ali-bouali\book-social-network\book-netwok
  #.\mvnw.cmd clean package -DskipTests
  #javac ficher jar
# Indique que le conteneur écoute sur le port 8088
EXPOSE 8088

ENV DB_URL=jdbc:postgresql://postgres-sql-bsn:5432/book_social_network
ENV ACTIVE_PROFILE=${PROFILE}
ENV JAR_VERSION=${APP_VERSION}
ENV JAR_FILE="book-network-${JAR_VERSION}.jar"
RUN echo "PROFILE=${PROFILE}, APP_VERSION=${APP_VERSION}"
# Utilise le format JSON pour éviter les problèmes liés aux signaux OS
CMD ["sh", "-c", "java -jar -Dspring.profiles.active=${ACTIVE_PROFILE} -Dspring.datasource.url=${DB_URL} /app/${JAR_FILE}"]
# \Documents\spring-boot\ali-bouali\book-social-network\book-network> docker build -t bsn/bsn:1.0.0 -f ../docker/backend/Dockerfile
# et avec argument : docker build --build-arg APP_VERSION=1.0.1 -t bsn/bsn:1.0.1 -f ../docker/backend/Dockerfile .
# Commande de construction Docker à exécuter depuis le répertoire book-network, spécifiant le fichier Dockerfile et le tag de l'image