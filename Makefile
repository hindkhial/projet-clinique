# ============================================================
# Variables
# ============================================================
NETWORK   = -f compose/network/network.yml
INFRA     = $(NETWORK) -f compose/infra/kafka.yml -f compose/infra/minio.yml -f compose/infra/mail.yml

# Databases
DB_AUTH   = -f compose/databases/auth-db.yml
DB_PAT    = -f compose/databases/patient-db.yml
DB_STAFF  = -f compose/databases/staff-db.yml
DB_RDV    = -f compose/databases/rdv-db.yml
DB_CONS   = -f compose/databases/consultation-db.yml
DB_RAP    = -f compose/databases/rapport-db.yml
DB_ANA    = -f compose/databases/analyses-db.yml
DB_RAD    = -f compose/databases/radio-db.yml

DB_ALL    = $(DB_AUTH) $(DB_PAT) $(DB_STAFF) $(DB_RDV) $(DB_CONS) $(DB_RAP) $(DB_ANA) $(DB_RAD)

# Services
SVC_AUTH  = -f compose/services/auth.yml
SVC_PAT   = -f compose/services/patient.yml
SVC_STAFF = -f compose/services/staff.yml
SVC_RDV   = -f compose/services/rdv.yml
SVC_CONS  = -f compose/services/consultation.yml
SVC_RAP   = -f compose/services/rapport.yml
SVC_ANA   = -f compose/services/analyses.yml
SVC_RAD   = -f compose/services/radio.yml
SVC_NOTIF = -f compose/services/notification.yml

SVC_ALL   = $(SVC_AUTH) $(SVC_PAT) $(SVC_STAFF) $(SVC_RDV) $(SVC_CONS) $(SVC_RAP) $(SVC_ANA) $(SVC_RAD) $(SVC_NOTIF)

UI        = -f compose/ui/angular.yml

AI-RADIO = -f compose/AI/AI-radio.yml

# ============================================================
# Tout démarrer / arrêter
# ============================================================
up:
	docker compose up -d

down:
	docker compose down

down-volumes:
	docker compose down -v

restart: down up

# ============================================================
# Infra
# ============================================================
infra-up:
	docker compose $(INFRA) up -d

infra-down:
	docker compose $(INFRA) down


# ============================================================
# Toutes les bases de données
# ============================================================
db-up:
	docker compose $(NETWORK) $(DB_ALL) up -d

db-down:
	docker compose $(NETWORK) $(DB_ALL) down

# ============================================================
# Démarrer les dépendances d'un service spécifique (demarer les services localement)
# ============================================================

patient-up:
	docker compose $(INFRA) $(DB_PAT) up -d

staff-up:
	docker compose $(INFRA) $(DB_STAFF) up -d

rdv-up:
	docker compose $(INFRA) $(DB_RDV) $(DB_STAFF) $(DB_PAT) up -d

consultation-up:
	docker compose $(INFRA) $(DB_CONS) up -d

rapport-up:
	docker compose $(INFRA) $(DB_RAP) up -d

analyses-up:
	docker compose $(INFRA) $(DB_ANA) up -d

radio-up:
	docker compose $(INFRA) $(DB_RAD) up -d

notification-up:
	docker compose $(INFRA) up -d

auth-up:
	docker compose $(INFRA) $(DB_AUTH) $(DB_PAT) $(DB_STAFF) up -d

ui-up:
	docker compose $(NETWORK) $(UI) up -d