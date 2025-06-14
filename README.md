# 🧠 SkillTrack – Developer Progress Tracker

**SkillTrack** is a developer productivity tracker that helps you stay consistent, set goals, log daily coding practice, and monitor progress — all in one place.

Built using **Spring Boot**, **PostgreSQL**, **Redis**, and **JWT-based Auth**, SkillTrack is designed to be scalable, secure, and developer-friendly.

---

## 🚀 Features

### 🔐 Authentication
- Sign up / Log in with JWT
- Forgot Username & Forgot Password (secure, token-based reset)
- Role-based users (User/Admin)

### 📘 Skill Log
- Add daily logs manually (DSA, Backend, etc.)
- Auto-sync GitHub/LeetCode (coming soon)
- Tag logs (e.g., Graph, Spring, Arrays)

### 🎯 Goals (Upcoming)
- Set daily/weekly targets
- Track live progress
- Get deadline reminders

### 📊 Analytics (Upcoming)
- Weekly/monthly coding graphs
- Tag-based skill breakdown (Pie/Bar)

### 🏆 Leaderboard (Upcoming)
- Global + friends leaderboard
- Composite score (consistency + count)

---

## 🧰 Tech Stack

| Layer      | Tech                         |
|------------|------------------------------|
| Backend    | Spring Boot 3.2+, Java 17     |
| Database   | PostgreSQL                   |
| Caching    | Redis                        |
| Auth       | JWT (JJWT) + Spring Security |
| Validation | Hibernate Validator (JSR-380) |
| Docs       | OpenAPI + Swagger UI         |

---

## ⚙️ Getting Started

### ✅ Prerequisites
- Java 17+
- Docker (for Redis/Postgres)
- PostgreSQL running on `localhost:5432`
- Redis running on `localhost:6379`

### 🔧 Run App

```bash
./gradlew bootRun
```

## 🔑 API Endpoints (Auth)

| Method | Endpoint                          | Description                  |
|--------|-----------------------------------|------------------------------|
| POST   | `/api/auth/signup`               | Register a new user         |
| POST   | `/api/auth/login`                | Log in and get JWT          |
| POST   | `/api/auth/forgot-username`      | Get username from email     |
| POST   | `/api/auth/forgot-password-request` | Get reset token         |
| POST   | `/api/auth/reset-password`       | Reset password via token    |

---

## 📚 Contribution

Feel free to fork the repo and raise PRs with:

- 🧩 New feature modules  
- ✅ Test cases  
- 🧼 Refactor improvements  

**Developer:** [@Aayush](#)  
**Mentor:** Madhav 🤖

---

## 📜 License

MIT License © 2025

---

> _Stay consistent. Stay sharp. Track like a pro._ 🚀

