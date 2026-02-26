# Intelligent Quiz Management System (Java + Swing + MySQL)

A role-based desktop quiz application built using **Java Swing** and **MySQL**, featuring a **timer-based quiz engine**, **performance analytics (weak topic detection + recommendations)**, **result history**, **leaderboard**, and a **full Admin CRUD panel** for managing the question bank.



## ✨ Key Features

### 👤 User Module
- Register / Login (DB-backed)
- Timer-based quiz (**15 seconds per question**)
- Auto score + accuracy calculation
- Topic-wise performance tracking
- Weak topic detection + recommendation
- Result history (attempt-wise)
- Leaderboard (Top 5 users)

### 👩‍💼 Admin Module
- Admin login
- Manage Questions (Full CRUD)
  - Add / Update / Delete
  - JTable listing of all questions
  - Auto-fill fields on row selection
- Topic & difficulty management (Easy/Medium/Hard)

### 📊 Analytics
- Text-based performance report after quiz:
  - Score, Accuracy %, Time taken
  - Topic-wise accuracy
  - Weak area + recommendation
- (Optional) Graphical dashboard using **JFreeChart**
  - Bar chart: Topic-wise accuracy
  - Pie chart: Correct vs Incorrect



## 🧠 Why “Intelligent”?
This system goes beyond simple scoring by performing **topic-wise analysis**, identifying **weak areas** using accuracy metrics, tracking **time-based performance**, and generating **recommendations** for improvement.

## Implemented Features

1️⃣ Project Setup

Configured Java (JDK 17)

Integrated MySQL Database

Connected project using MySQL JDBC Connector

Structured project using packages:

ui

models

database

2️⃣ Authentication System

✅ User Registration (Database integrated)

✅ Login System with credential verification

✅ Role-based login (Admin / User)

✅ Dashboard redirection after login

3️⃣ User Dashboard

Start Quiz button

View Results (UI ready)

Leaderboard access

Logout functionality

4️⃣ Quiz Engine (Core Feature)

Questions fetched dynamically from database

Multiple choice options using radio buttons

One question at a time

Next button navigation

Auto score calculation

5️⃣ Professional Timer System

⏱ 15 seconds per question

Live countdown display

Auto move to next question when time ends

Total time tracking

6️⃣ Performance Analytics (Intelligent Feature)

Accuracy percentage calculation

Topic-wise performance tracking

Weak topic detection

Personalized recommendation generation

Structured performance report

7️⃣ Result Storage

Score saved in database

Accuracy saved

Time taken saved

User-linked results using foreign key

8️⃣ Leaderboard System

Displays Top 5 users

Sorted by highest score

Shows:

Rank

Name

Score

Accuracy %
