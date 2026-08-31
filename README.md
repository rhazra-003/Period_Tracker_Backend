# Period Tracker Backend

Spring Boot backend for the period tracker app. It uses Firebase Authentication for user identity, PostgreSQL for persistence, and REST APIs for cycle tracking, prediction, and history.

## Features
- Firebase Google Sign-In token verification
- Auto user creation on first authenticated request
- Period tracking by start date and duration
- Recent cycle history with pagination
- Next period prediction based on cycle history
- Delete a tracked cycle entry with ownership validation
- CORS support for local and deployed frontend origins
- Environment-based configuration for local and cloud deployment

## Tech Stack
- Java 17
- Spring Boot 3
- Spring Security
- Spring Data JPA
- PostgreSQL
- Firebase Admin SDK
- Lombok

## Project Structure
- `src/main/java/com/project/periodtracker/controller` — REST controllers
- `src/main/java/com/project/periodtracker/service` — business logic
- `src/main/java/com/project/periodtracker/repository` — database access
- `src/main/java/com/project/periodtracker/model` — JPA entities
- `src/main/java/com/project/periodtracker/config` — security and Firebase setup
- `src/main/resources/application.properties` — runtime configuration

## Required Runtime Configuration
The app reads configuration from environment variables with local fallbacks.

Example values:
- `DB_URL=jdbc:postgresql://localhost:5432/period_tracker?sslmode=disable`
- `DB_USERNAME=postgres`
- `DB_PASSWORD=admin`
- `PORT=8081`
- `CORS_ALLOWED_ORIGINS=http://localhost:5173,https://your-frontend.vercel.app`
- `FIREBASE_CREDENTIALS_PATH=/path/to/firebase-service-account.json`

The main config is in:
- `src/main/resources/application.properties`

### Database setup
Create a PostgreSQL database, for example:
- `period_tracker`

Then point `DB_URL` to it. JPA is configured with:
- `spring.jpa.hibernate.ddl-auto=update`

This means the tables will be auto-created/updated on startup.

## Firebase setup
This app expects a Firebase Admin service account JSON file for backend verification.

Important:
- Keep the service-account JSON out of GitHub
- Pass the path via `FIREBASE_CREDENTIALS_PATH`
- Example local path:
  - `src/main/resources/firebase-service-account.json`

The backend loads Firebase credentials at startup and verifies the `Authorization: Bearer <token>` header for protected API requests.

## Authentication flow
All protected APIs require a valid Firebase ID token in the Authorization header.

Example:
- `Authorization: Bearer <FIREBASE_ID_TOKEN>`

The backend verifies the token and uses the authenticated user email as the user identity.

## API endpoints
All endpoints below require a valid Firebase token in the Authorization header unless otherwise noted.

### Track a new cycle
- `POST /api/cycles/track?start=YYYY-MM-DD&duration=N`
- Example:
  - `POST /api/cycles/track?start=2026-09-01&duration=5`

### Get recent cycle history
- `GET /api/cycles/recent?page=0&size=10`
- Returns the recent cycles for the logged-in user

### Get next period prediction
- `GET /api/cycles/predict`
- Returns the next predicted cycle date

### Delete a tracked cycle entry
- `DELETE /api/cycles/{id}`
- Deletes the cycle record only if it belongs to the authenticated user

### Success/error response style
The backend uses consistent JSON responses like:
- success: `{"message":"Cycle entry deleted successfully!"}`
- error: `{"error":"Some validation message"}`

## Local development
1. Install Java 17+
2. Start PostgreSQL locally
3. Create a database named `period_tracker`
4. Set environment variables in your shell or IDE
5. Run:
   - `./mvnw spring-boot:run`

or on Windows:
- `mvnw.cmd spring-boot:run`

## Deployment guidance
This project is designed for free-tier hosting:
- Frontend: Vercel
- Backend: Render
- Database: Neon or Supabase PostgreSQL
- Auth: Firebase

### Render notes
- Use a Docker-based deployment if Render is not happy with the plain Java jar startup
- A Dockerfile is included in the backend repo
- Set the environment variable values in Render secrets
- Ensure the Firebase service account JSON is available at runtime via a secret file or mounted path

## CORS configuration
The app reads allowed frontend origins from:
- `CORS_ALLOWED_ORIGINS`

Example:
- `http://localhost:5173,https://your-app.vercel.app`

This is used in the backend security configuration so deployed frontend domains can call the API without CORS errors.

## Credits
- Made with ❤️ by Ridam with Cursor & GitHub Copilot
- Use the App here - https://period-tracker-ashen.vercel.app/