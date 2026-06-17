# DailyMate 📝📍

**DailyMate** is a feature-rich, native Android To-Do and Task Management application built with Java and SQLite. It goes beyond a simple checklist by integrating location tagging, real-time search, and system notifications to help users organize their daily lives efficiently.

---

## 🌟 Features

* **User Authentication:** Secure local registration, login, and password management.
* **Task Management:** Create, view, complete, and permanently delete tasks with specific deadlines.
* **Location Integration (GPS):** * Seamlessly capture your current real-world location when adding a task using device GPS (Google Fused Location Provider).
  * Interactive map preview (Google Maps SDK) to view where a task needs to be completed.
* **Real-Time Search:** Instantly filter and find specific tasks by title using an intuitive search interface.
* **System Notifications:** Receive native Android notifications when tasks are added, completed, or permanently removed.
* **Account Dashboard:** View profile details, update account names/usernames, and change passwords securely.
* **Seamless Navigation:** Smooth bottom navigation bar to switch between Pending Tasks, Completed Tasks, and Account Settings.
* **Personalized UI:** Dynamic greetings on the home screen and clean, Material-inspired XML layouts.

---

## 🛠️ Tech Stack

* **Language:** Java
* **UI/UX:** XML, Material Design Components
* **Local Database:** SQLite (`SQLiteOpenHelper`)
* **APIs & Services:** * Google Maps SDK (`play-services-maps`)
  * Google Location Services (`play-services-location`)
* **Architecture:** Activity-based Architecture with RecyclerViews and custom Adapters.

---

## 🚀 Installation & Setup

To run this project locally on your emulator or Android device:

**1. Clone the repository:**
```bash
git clone [https://github.com/supunShashika/DailyMate.git]
