Hospital Management System - Microservices Architecture
Un système de gestion hospitalière moderne basé sur une architecture microservices avec Spring Boot et Keycloak pour l'authentification.

> Projet de fin d’année de quatrième année, réalisé en équipe. Ce projet académique est destiné à l’apprentissage et à la démonstration ; il n’est utilisé par aucune clinique réelle.

📋 Table des matières

Vue d'ensemble
Architecture
Services
Prérequis
Installation
Configuration
Utilisation
Ports et Accès
Base de données
Sécurité

🎯 Vue d'ensemble
Ce projet est un système de gestion hospitalière complet qui utilise une architecture microservices pour gérer différents aspects d'un hôpital : patients, personnel, rendez-vous, consultations et rapports médicaux.
🏗️ Architecture
Le système utilise :

Spring Boot pour les microservices
PostgreSQL comme base de données pour chaque service
Keycloak pour l'authentification et l'autorisation
Docker & Docker Compose pour l'orchestration des conteneurs
Architecture microservices avec isolation des données

🔧 Services
1. Patient Service (Port 9090)
   Gestion des informations des patients.

Base de données : patientdb (Port 5432)

2. Staff Service (Port 9091)
   Gestion du personnel médical et administratif.

Base de données : staffdb (Port 5433)

3. RDV Service (Port 9092)
   Gestion des rendez-vous médicaux.

Base de données : rdvdb (Port 5434)

4. Consultation Service (Port 9093)
   Gestion des consultations médicales.

Base de données : consultationdb (Port 5435)

5. Rapport Service (Port 9094)
   Gestion des rapports médicaux.

Base de données : rapportdb (Port 5436)

6. Auth Service (Port 9095)
   Service d'authentification intégré avec Keycloak.
7. Keycloak (Port 9089)
   Serveur d'authentification et d'autorisation.

Base de données : keycloakdb (Port 5438)

📦 Prérequis

Docker (version 20.10 ou supérieure)
Docker Compose (version 2.0 ou supérieure)
4 Go de RAM minimum
10 Go d'espace disque disponible

🚀 Installation
1. Cloner le projet
   bashgit clone <url-du-projet>
   cd hospital-management-system
```

### 2. Structure des répertoires

Assurez-vous que la structure suivante existe :
```
.
├── docker-compose.yml
└── services/
├── patient/
│   └── Dockerfile
├── staff/
│   └── Dockerfile
├── rdv/
│   └── Dockerfile
├── consultation/
│   └── Dockerfile
├── rapport/
│   └── Dockerfile
└── auth/
└── Dockerfile
3. Lancer l'application
   bash# Construire et démarrer tous les services
   docker-compose up -d

# Voir les logs
docker-compose logs -f

# Vérifier le statut des services
docker-compose ps
4. Arrêter l'application
   bash# Arrêter tous les services
   docker-compose down

# Arrêter et supprimer les volumes (ATTENTION : supprime les données)
docker-compose down -v
⚙️ Configuration
Keycloak - Configuration initiale

Accéder à Keycloak : http://localhost:9089
Se connecter avec :

Username: admin
Password: <set locally>


Créer un realm nommé hospital-realm
Créer un client auth-service-client
Configurer le client secret via la variable KEYCLOAK_CLIENT_SECRET de votre environnement local.

Variables d'environnement
Chaque service utilise des variables d'environnement pour sa configuration. Pour personnaliser :
yamlenvironment:
SPRING_DATASOURCE_URL: jdbc:postgresql://nom-db:5432/nomdb
SPRING_DATASOURCE_USERNAME: username
SPRING_DATASOURCE_PASSWORD: ${DEMO_DB_PASSWORD:?Set DEMO_DB_PASSWORD in your local environment}
🌐 Ports et Accès
ServicePortURL d'accèsPatient Service9090http://localhost:9090Staff Service9091http://localhost:9091RDV Service9092http://localhost:9092Consultation Service9093http://localhost:9093Rapport Service9094http://localhost:9094Auth Service9095http://localhost:9095Keycloak9089http://localhost:9089
Bases de données PostgreSQL
Base de donnéesPort externePort internepatient-db54325432staff-db54335432rdv-db54345432consultation-db54355432rapport-db54365432keycloak-db54385432
💾 Base de données
Connexion aux bases de données
bash# Exemple pour patient-db
docker exec -it patient-db psql -U patientuser -d patientdb

# Exemple pour staff-db
docker exec -it staff-db psql -U staffuser -d staffdb
Health checks
Chaque base de données dispose d'un health check qui vérifie sa disponibilité toutes les 10 secondes.
🔒 Sécurité
Credentials par défaut (À MODIFIER EN PRODUCTION)
Keycloak Admin:

Username: admin
Password: <set locally>

Bases de données:

Voir la section environment de chaque service dans docker-compose.yml

⚠️ IMPORTANT : Changez tous les mots de passe par défaut avant de déployer en production.
🐛 Dépannage
Les services ne démarrent pas
bash# Vérifier les logs
docker-compose logs <nom-du-service>

# Redémarrer un service spécifique
docker-compose restart <nom-du-service>
Problèmes de base de données
bash# Vérifier le statut du health check
docker-compose ps

# Se connecter à la base de données
docker exec -it <nom-db> psql -U <user> -d <database>
Nettoyer et redémarrer
bash# Arrêter tous les services
docker-compose down

# Supprimer les volumes (ATTENTION : perte de données)
docker-compose down -v

# Reconstruire les images
docker-compose build --no-cache

# Redémarrer
docker-compose up -d
📝 Développement
Ajouter un nouveau service

Créer le répertoire services/<nom-service>
Ajouter le Dockerfile
Mettre à jour docker-compose.yml avec le nouveau service et sa base de données
Redémarrer : docker-compose up -d --build



## Configuration locale après nettoyage des identifiants

Les mots de passe et secrets sont fournis par des variables d'environnement. Aucun identifiant réel ne doit être ajouté au dépôt.

1. Copier `.env.example` vers `.env` à la racine et remplacer toutes les valeurs `replace-with-...` par des valeurs locales.
2. Créer le client Keycloak `auth-service-client`, puis reporter son secret local dans `KEYCLOAK_CLIENT_SECRET`.
3. Lancer Docker Compose depuis la racine : `docker compose --env-file .env up -d`.
4. Pour lancer les services Spring Boot hors Docker, exporter aussi ces variables dans leur environnement ; Spring Boot ne charge pas automatiquement le fichier `.env`.
5. Pour le service Python, copier `AI-radio/.env.example` vers `AI-radio/.env` et renseigner les mêmes identifiants MinIO. `MINIO_ACCESS_KEY` doit correspondre à `MINIO_ROOT_USER`, et `MINIO_SECRET_KEY` au mot de passe MinIO.

Les fichiers `.env` locaux sont ignorés par Git. Les modèles d'IA ne sont pas inclus ; leur configuration reste nécessaire pour exécuter ce service.
