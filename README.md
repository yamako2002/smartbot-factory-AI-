# 🤖 SmartBot Factory

### 🧩 Contexte académique
Projet universitaire d’intégration d’intelligence artificielle dans une architecture **Spring Boot + Angular**, visant à permettre la **création, la gestion et la recommandation intelligente de chatbots**.  
SmartBot Factory combine des technologies de **RAG (Retrieval-Augmented Generation)**, **Azure OpenAI**, et **OpenRouter AI** pour offrir des fonctionnalités avancées d’assistance conversationnelle.

---

## ⚙️ Stack technique

| Composant | Technologie |
|------------|-------------|
| **Backend** | Spring Boot 3 (Java 17) |
| **Frontend** | Angular 17 |
| **Base de données** | MySQL / PostgreSQL |
| **IA** | Azure OpenAI (Embeddings) + OpenRouter API |
| **Déploiement** | Docker / cPanel |
| **IDE** | IntelliJ IDEA & VS Code |

---

## 🚀 Fonctionnalités principales

- 🧠 **Création de chatbots intelligents**
  - Ajout de sources (URLs, documents)
  - Indexation automatique
  - Gestion via tableau de bord Angular

- 🤝 **Système de recommandation IA**
  - Suggestions internes (basées sur les chatbots créés)
  - Suggestions externes (via OpenRouter AI)
  - Calcul de similarité par embeddings Azure OpenAI

- 💬 **Intégration RAG (Retrieval-Augmented Generation)**
  - Génération de réponses contextuelles
  - Vectorisation via Qdrant / Azure Embeddings

- 🧾 **Historique et journalisation**
  - Tracking des modifications (ModificationLog)
  - Suivi des événements (notifications temps réel)

- 🔐 **Authentification & Sécurité**
  - Gestion des utilisateurs (JWT)
  - Dashboard sécurisé

---

## 🧰 Installation et exécution

### 1️⃣ Backend — Spring Boot

```bash
cd backend/
mvn clean install
mvn spring-boot:run
```

Le backend tourne par défaut sur **http://localhost:8080**.

> ⚠️ Vérifiez que vos clés API Azure et OpenRouter sont bien configurées dans `application.properties`.

### 2️⃣ Frontend — Angular

```bash
cd frontend/
npm install
ng serve
```

Le frontend tourne sur **http://localhost:4200**.

---

## 🌍 Configuration IA (application.properties)

```properties
rag.azure.endpoint=https://openia-oussema.openai.azure.com/
rag.azure.api-key=YOUR_AZURE_API_KEY
rag.azure.embedding-deployment=text-embedding-3-small
rag.azure.embedding-api-version=2023-05-15

ai.recommendation.engine=OPENROUTER
ai.recommendation.topk=5
ai.recommendation.threshold=0.05
```

---

## 🧠 Module IA : Recommandation intelligente

Le système compare les chatbots internes et les modèles externes OpenRouter à partir d’un texte de requête.

### 🔹 Exemple d’appel API

```
GET /api/recommend?query=travel assistant
```

### 🔹 Exemple de réponse
```json
[
  {
    "name": "mistralai/mistral-7b-instruct",
    "provider": "MistralAI",
    "source": "OpenRouter",
    "score": 0.82
  },
  {
    "name": "SupportAI",
    "domain": "support.example.com",
    "source": "SmartBotFactory",
    "score": 0.77
  }
]
```

---

## 🧩 Architecture globale

```
Frontend (Angular) 
   ↓ REST API
Backend (Spring Boot)
   ↓
Azure OpenAI  ←→  OpenRouter API
   ↓
Qdrant (Vector DB)
   ↓
MySQL / PostgreSQL
```

---

## 👥 Équipe du projet

| Nom | Rôle | Responsabilités |
|------|------|-----------------|
| **Oussama Hannachi** | Chef de projet / Backend AI Engineer | Intégration IA, services backend, API Azure/OpenRouter |
| **Maryem Rahmouni** | Frontend Developer | UI/UX Angular, composant Recommandation |
| **Mounir Gandouli** | DevOps / Database | Docker, configuration réseau, CI/CD |
| **Tarek Gadhgadhi** | Data Scientist / AI Engineer | Conception du système de recommandation |
| **Yassine Cherni** | QA / Documentation | Tests, validation académique, documentation technique |

---

## 🧠 Prompts IA utilisés

Les prompts sont documentés dans [`PROMPTS.md`](./PROMPTS.md).  
Ils couvrent :
- Recommandation de chatbots
- Génération automatique de code
- Refactoring intelligent
- Évaluation IA et documentation automatique

---

## 🧪 Tests & validation

- ✅ Tests Postman (API backend)
- ✅ Tests unitaires Angular (Jasmine)
- ✅ Tests IA sur OpenRouter (résultats cohérents dès threshold 0.05)

---

## 📚 Liens associés

- [TEAM.md](./TEAM.md)
- [PROMPTS.md](./PROMPTS.md)

---

## 🏁 Licence
Projet académique — © 2025 *SmartBot Factory Team*
