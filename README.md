# ScrumCat

ScrumCat is a lightweight Scrum process management platform for course projects and personal agile practice.

Current stage: Step 0, project scaffold creation.

The project has not implemented login/register, user stories, Sprint planning, boards, burndown charts, retrospectives, issue tracking, or other business features yet.

## Tech Stack

- Frontend: Vue 3, Vite, Ant Design Vue, Vue Router, Pinia, Axios, ECharts, VueDraggableNext
- Backend: Spring Boot 3.x, Java 17, Maven, MyBatis-Plus, MySQL, JWT
- Database: MySQL 8.x
- API style: RESTful API

## Project Structure

```text
ScrumCat/
├── frontend/        # Vue 3 frontend project
├── backend/         # Spring Boot backend project
├── database/        # Database scripts
├── docs/            # Project documents
├── api-tests/       # Postman or Apifox API collections
├── README.md
└── .gitignore
```

## Frontend

```bash
cd frontend
npm install
npm run dev
```

The default Vite development server runs on port `5173`.

## Backend

Before starting the backend, make sure Maven uses JDK 17 or newer.

```bash
cd backend
mvn spring-boot:run
```

The backend service runs on port `8080`.

Database credentials in committed configuration use placeholders only. Do not commit real database passwords. For local development, adjust local configuration manually and keep secrets out of Git.

## Database Initialization

Run the script in `database/init.sql` to create the database:

```sql
CREATE DATABASE IF NOT EXISTS scrumcat
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

Current scaffold does not create business tables.

## Git Strategy

The recommended branch strategy is:

- `main`: stable branch for milestone builds and final delivery
- `dev`: daily development branch

Recommended first commit:

```bash
git add .
git commit -m "init: create ScrumCat project scaffold"
```

Then create the development branch:

```bash
git checkout -b dev
```
