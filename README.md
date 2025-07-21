# Period Tracker Backend (Spring Boot)

A secure, modern backend for a period tracking app, featuring Google Sign-In (Firebase Auth), PostgreSQL, and robust REST APIs.

## 🚀 Features
- Google Account authentication (Firebase ID token)
- Secure session handling (token verified on every request)
- Track period cycles (start date, duration)
- View paginated cycle history
- Predict next period using cycle averages
- PostgreSQL database
- CORS enabled for frontend integration
- Modular, maintainable codebase

## 🛠️ Tech Stack
- Java 17, Spring Boot 3
- Spring Security
- Firebase Admin SDK
- PostgreSQL (JPA/Hibernate)

## 🔑 Firebase Auth Integration
- All endpoints require a valid Google ID token in the `Authorization: Bearer <token>` header.
- Backend verifies the token and uses the user's email for all data.
- New users are auto-created with their Google name/email on first login.

## 🌐 CORS
- CORS is enabled for `http://localhost:5173` (React frontend dev server).

## 📚 API Endpoints
- `POST /api/cycles/track?start=YYYY-MM-DD&duration=N` — Track a new period
- `GET /api/cycles/recent?page=0&size=10` — Get paginated cycle history
- `GET /api/cycles/predict` — Get next period prediction

All endpoints require the `Authorization` header with a valid Firebase ID token.