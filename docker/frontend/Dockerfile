# Utilise l'image officielle Node.js version 16 pour la phase de build
FROM node:16 as build-stage

# Définit le répertoire de travail à /app dans le conteneur
WORKDIR /app

# Copie les fichiers package.json et package-lock.json (s'ils existent) dans le répertoire de travail
COPY package*.json ./

# Installe les dépendances listées dans package.json
RUN npm install

# Copie tous les fichiers et dossiers du répertoire courant (où se trouve le Dockerfile)
# dans le répertoire de travail du conteneur de manière récursive
COPY . .

# Exécute la commande pour construire l'application Angular en mode production
RUN npm run build --prod

# Utilise l'image officielle Nginx basée sur Alpine Linux pour la phase de runtime
FROM nginx:alpine

# Copie le fichier de configuration personnalisé nginx.conf dans le répertoire de configuration de Nginx
COPY nginx.conf /etc/nginx/nginx.conf

# Copie le résultat du build de l'application Angular (généré dans /app/dist/book-network-ui) vers le répertoire où Nginx sert les fichiers statiques
COPY --from=build-stage /app/dist/book-network-ui /usr/share/nginx/html

# Expose le port 80 pour que le conteneur puisse accepter des connexions HTTP
EXPOSE 80
