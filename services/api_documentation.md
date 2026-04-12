# Projet Clinique - Documentation API

Ce document regroupe les contrôleurs, les chemins, les accès et les DTOs par service.

## Architecture Globale
- **Passerelle (ApiGateway)** : Gère les accès CORS et la validation JWT via Keycloak.
- **Sécurité** :
    - Routes publiques : `/auth/login`, `/auth/refresh`.
    - Routes privées : Tout le reste (JWT requis).
    - Contrôle granulaire : Utilisation de `X-User-Roles` (injecté par la passerelle) et validé dans les services via `SecurityUtils`.

---

## 1. Service d'Authentification (Auth Service)

| Contrôleur | Chemin de base | Méthode | Endpoint | Accès | DTO Requête | DTO Réponse |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `AuthController` | `/auth` | POST | `/login` | Public | `LoginRequest` | `LoginResponse` |
| | | POST | `/refresh` | Public | `RefreshRequest` | `LoginResponse` |
| `AuthMedecinController` | `/auth/medecin` | POST | `/register` | Authentifié | `MedecinRegisterRequest` | `MedecinResponse` |
| `AuthPatientController` | `/auth/patient` | POST | `/register` | Authentifié | `PatientRegisterRequest` | `PatientResponse` |
| `AuthStaffController` | `/auth/staff` | POST | `/register` | Authentifié | `StaffRegisterRequest` | `StaffResponse` |

---

## 2. Service Patient (Patient Service)

| Méthode | Endpoint | Accès (Rôle) | DTO Requête | DTO Réponse |
| :--- | :--- | :--- | :--- | :--- |
| POST | `/patient/create` | STAFF | `PatientCreateRequeste` | `PatientResponse` |
| GET | `/patient/` | STAFF | - | `List<PatientResponse>` |
| GET | `/patient/{id}` | STAFF | - | `PatientResponse` |
| PUT | `/patient/update/{id}`| Authentifié | `PatientUpdateRequeste` | `PatientResponse` |
| DELETE| `/patient/delete/{id}`| STAFF | - | `String` |
| GET | `/patient/me` | PATIENT | - | `PatientResponse` |

---

## 3. Service Staff (Staff Service)

### MedecinController (`/medecin`)
| Méthode | Endpoint | Accès (Rôle) | DTO Requête | DTO Réponse |
| :--- | :--- | :--- | :--- | :--- |
| POST | `/medecin/create` | Authentifié | `MedecinCreateRequestDto`| `MedecinResponseDto` |
| GET | `/medecin/` | Authentifié | - | `List<MedecinResponseDto>` |
| GET | `/medecin/{id}` | Authentifié | - | `MedecinResponseDto` |
| PUT | `/medecin/update/{id}`| Authentifié | `MedecinUpdateRequestDto`| `MedecinResponseDto` |
| DELETE| `/medecin/delete/{id}`| Authentifié | - | `String` |
| GET | `/medecin/me` | MEDECIN | - | `MedecinResponseDto` |

### StaffController (`/staff`)
| Méthode | Endpoint | Accès (Rôle) | DTO Requête | DTO Réponse |
| :--- | :--- | :--- | :--- | :--- |
| POST | `/staff/create` | Authentifié | `StaffCreateRequest` | `StaffResponse` |
| GET | `/staff/` | Authentifié | - | `List<StaffResponse>` |
| GET | `/staff/{id}` | Authentifié | - | `StaffResponse` |

---

## 4. Service Rendez-vous (RDV Service)

| Méthode | Endpoint | Accès | DTO Requête | DTO Réponse |
| :--- | :--- | :--- | :--- | :--- |
| POST | `/rdv/create` | Authentifié | `RdvCreateRequest` | `RdvResponse` |
| GET | `/rdv/` | Authentifié | - | `List<RdvResponse>` |
| GET | `/rdv/{id}` | Authentifié | - | `RdvResponse` |

---

## 5. Service Consultation (Consultation Service)

| Méthode | Endpoint | Accès | DTO Requête | DTO Réponse |
| :--- | :--- | :--- | :--- | :--- |
| POST | `/consultation/create` | Authentifié | `ConsultationRequest` | `ConsultationResponse` |
| GET | `/consultation/` | Authentifié | - | `List<ConsultationResponse>` |

---

## 6. Service Analyses (Analyses Service)

| Méthode | Endpoint | Accès | DTO Requête | DTO Réponse |
| :--- | :--- | :--- | :--- | :--- |
| POST | `/analyse/create` | Authentifié | `AnalyseCreateRequest` | `AnalyseResponse` |
| GET | `/analyse/` | Authentifié | - | `List<AnalyseResponse>` |
| PUT | `/analyse/update/{id}` | Authentifié | `AnalyseUpdateRequest` | `AnalyseResponse` |

---

## 7. Service Radio (Radio Service)

| Méthode | Endpoint | Accès | DTO Requête | DTO Réponse |
| :--- | :--- | :--- | :--- | :--- |
| POST | `/Radio/create` | Authentifié | `RadioRequest` | `RadioResponse` |
| GET | `/Radio/` | Authentifié | - | `List<RadioResponse>` |

---

## 8. Service Rapport (Rapport Service)

| Méthode | Endpoint | Accès | DTO Requête | DTO Réponse |
| :--- | :--- | :--- | :--- | :--- |
| POST | `/rapport/create` | Authentifié | `RapportRequest` | `RapportResponse` |
| GET | `/rapport/` | Authentifié | - | `List<RapportResponse>` |

---

---

## Détails des Contenus des DTOs (Champs et Types)

Cette section détaille les champs pour les principaux objets d'échange (Request / Response).

### 1. Authentification

#### `LoginRequest`
- `username` (String) : Identifiant de l'utilisateur.
- `password` (String) : Mot de passe.

#### `LoginResponse`
- `accessToken` (String) : Token JWT pour l'authentification.
- `refreshToken` (String) : Token pour renouveler le JWT.
- `expiresIn` (long) : Durée de validité du token.

#### `MedecinRegistrationRequest`
- `nom`, `prenom`, `tel`, `email` (String)
- `dateNaissance` (LocalDate)
- `specialite` (Enum: Specialite)
- `password` (String)

---

### 2. Service Patient

#### `PatientCreateRequeste` / `PatientUpdateRequeste`
- `nom`, `prenom`, `tel`, `email` (String)
- `dateNaissance` (LocalDate)

#### `PatientResponse`
- `id` (Long)
- `nom`, `prenom`, `email`, `tel` (String)
- `dateNaissance` (LocalDate)

---

### 3. Service Staff

#### `MedecinResponseDto`
- `id`, `staffId` (Long)
- `nom`, `prenom`, `email`, `tel` (String)
- `dateNaissance` (LocalDate)
- `role` (Enum: Role)
- `specialite` (Enum: Specialite)

---

### 4. Service Rendez-vous

#### `RdvCreateRequestDto`
- `idPatient` (Long)
- `idMedecin` (Long)
- `dateRdv` (LocalDateTime)

#### `RdvResponseDto`
- `id`, `idPatient`, `idMedecin` (Long)
- `dateRdv` (LocalDateTime)

---

### 5. Service Consultation

#### `ConsultationResponse`
- `id`, `idPatient`, `idMedecin`, `idRdv` (Long)
- `type` (Enum: ConsultationType)
- `typeMaladie`, `riskLevel`, `observation`, `traitement` (String)
- `confidence` (Double)
- `dateConsultation` (LocalDateTime)

---

### 6. Service Analyses

#### `AnalyseResponse`
- `id`, `patientId` (Long)
- `age` (Integer)
- `sexe` (Enum: Sexe)
- `glycemieJeun`, `hemoglobine`, `globulesBlancs`, `cholesterolTotal`, `creatinine`, `tensionSystolique`, `tensionDiastolique`, `frequenceCardiaque`, `temperature`, `imc` (Double)
- `dateAnalyse` (LocalDateTime)

---

### 7. Service Radio

#### `RadioResponse`
- `id`, `patientId`, `radiologueId` (Long)
- `patientNom`, `patientPrenom`, `notes`, `status`, `typeMaladie`, `riskLevel`, `notesRadiologue` (String)
- `fichierRadioUrl`, `heatmapUrl` (String)
- `confidence` (Double)
- `dateRadio`, `createdAt`, `updatedAt` (LocalDateTime)

---

### 8. Service Rapport

#### `RapportResponse`
- `id`, `patientId`, `medecinId`, `consultationId` (Long)
- `patientNom`, `patientPrenom`, `patientEmail`, `patientCin` (String)
- `medecinNom`, `medecinPrenom`, `medecinSpecialite` (String)
- `typeRapport`, `observations`, `traitement`, `diagnostic`, `pdfFileUrl` (String)
- `dateRapport`, `createdAt`, `updatedAt` (LocalDateTime)

---

## Liste des principaux DTOs par package (exemples)

| Service | Package DTO | Classes Clés |
| :--- | :--- | :--- |
| **Auth** | `com.ehei.auth.DTOS.login` | `LoginRequest`, `LoginResponse`, `RefreshRequest` |
| **Patient** | `com.ehei.patient.dto` | `PatientCreateRequeste`, `PatientResponse`, `PatientUpdateRequeste` |
| **Staff** | `com.ehei.staff.DTOS.medecin`| `MedecinCreateRequestDto`, `MedecinResponseDto` |
| **Consultation**| `com.ehei.consultation.dtos` | `ConsultationRequest`, `ConsultationResponse` |
| **Analyse** | `com.ehei.analyses.dto` | `AnalyseCreateRequest`, `AnalyseResponse`, `AnalyseUpdateRequest` |
