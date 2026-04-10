# Test Technique — Full Stack Engineer — Tendanz Group

## Moteur de Tarification Assurance

### Contexte

Vous rejoignez une équipe projet chez Tendanz Group pour développer un **moteur de tarification d'assurance**. Le système doit permettre de calculer le prix d'une couverture en fonction du profil client, du produit choisi et de la zone géographique.

### Structure du projet

```
├── backend/          # Spring Boot 3.2 — Java 17
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/tendanz/pricing/
│       │   ├── controller/
│       │   │   ├── ProductController.java  # Fourni (GET /api/products)
│       │   │   └── QuoteController.java    # → À implémenter (TODO)
│       │   ├── service/
│       │   │   └── PricingService.java     # → À implémenter (TODO)
│       │   ├── repository/
│       │   │   └── QuoteRepository.java    # → À compléter (TODO)
│       │   ├── entity/        # Entités JPA (fournies)
│       │   ├── dto/           # DTOs (fournis)
│       │   ├── exception/
│       │   │   └── GlobalExceptionHandler  # → À implémenter (TODO)
│       │   └── enums/         # AgeCategory (fourni)
│       └── main/resources/
│           ├── schema.sql     # DDL (fourni)
│           ├── data.sql       # Données initiales (fourni)
│           └── application.yml
│
└── frontend/         # Angular 17 — Standalone Components
    ├── package.json
    └── src/app/
        ├── services/      # → À implémenter (TODO)
        ├── pages/         # → À implémenter (TODO)
        └── models/        # Interfaces TypeScript (fournies)
```

### Démarrage rapide

**Backend :**
```bash
cd backend
mvn spring-boot:run
# API disponible sur http://localhost:8080
# Console H2 : http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:testdb)
```

**Frontend :**
```bash
cd frontend
npm install
ng serve
# App disponible sur http://localhost:4200
```

### Formule de tarification

```
Prix Final = Taux de Base × Facteur Âge × Coefficient Zone
```

| Tranche d'âge | Catégorie | Facteur |
|---------------|-----------|---------|
| 18 - 24 ans   | YOUNG     | 1.30    |
| 25 - 45 ans   | ADULT     | 1.00    |
| 46 - 65 ans   | SENIOR    | 1.20    |
| 66 - 99 ans   | ELDERLY   | 1.50    |

| Zone        | Code | Coefficient |
|-------------|------|-------------|
| Grand Tunis | TUN  | 1.20        |
| Sfax        | SFX  | 1.00        |
| Sousse      | SOU  | 1.10        |

| Produit              | Code    | Taux de Base (TND) |
|----------------------|---------|---------------------|
| Assurance Auto       | AUTO    | 500.00              |
| Assurance Habitation | HABITAT | 300.00              |
| Assurance Santé      | SANTE   | 800.00              |

**Exemple :** Client de 30 ans, zone Tunis, Assurance Auto = 500 × 1.00 × 1.20 = **600.00 TND**

### Livrable attendu

- Code complété (tous les fichiers `TODO`)
- Minimum 5 tests unitaires backend
- README mis à jour avec vos choix techniques
- Commits Git progressifs et clairs

<<<<<<< HEAD
Bonne chance !
=======
### Deadline

**Samedi 11 avril 2026 à 23h59**

Envoyez le lien de votre repository à : **recrutement.tn@tendanz.com**
>>>>>>> df8eb3a (Fix skeleton: corrected data.sql, real TODOs, frontend models, added ProductController)
