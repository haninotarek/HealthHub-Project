<div align="center">

# 🏥 HealthHub Clinic

### *A Modern Desktop Clinic Management System*

**Your health, our priority**

[![Java](https://img.shields.io/badge/Java-11-orange?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![Swing](https://img.shields.io/badge/UI-Java%20Swing-007396?style=for-the-badge&logo=java&logoColor=white)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![SQL Server](https://img.shields.io/badge/Database-SQL%20Server-CC2927?style=for-the-badge&logo=microsoftsqlserver&logoColor=white)](https://www.microsoft.com/sql-server)
[![Maven](https://img.shields.io/badge/Build-Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![FlatLaf](https://img.shields.io/badge/Theme-FlatLaf-4CAF50?style=for-the-badge)](https://www.formdev.com/flatlaf/)
[![IntelliJ](https://img.shields.io/badge/IDE-IntelliJ%20IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white)](https://www.jetbrains.com/idea/)

[![Status](https://img.shields.io/badge/Status-Completed-success?style=flat-square)]()
[![Version](https://img.shields.io/badge/Version-1.0.0-blue?style=flat-square)]()
[![Platform](https://img.shields.io/badge/Platform-Windows-lightgrey?style=flat-square)]()
[![University](https://img.shields.io/badge/University-EPNU-blueviolet?style=flat-square)]()

</div>

---

## 📑 Table of Contents

- [📖 About The Project](#-about-the-project)
- [✨ Key Features](#-key-features)
- [🛠️ Tech Stack](#️-tech-stack)
- [🏗️ System Architecture](#️-system-architecture)
- [📸 Application Screenshots](#-application-screenshots)
- [🗄️ Database Design](#️-database-design)
- [🚀 Getting Started](#-getting-started)
- [📂 Project Structure](#-project-structure)
- [🎯 Project Scope](#-project-scope)
- [🔧 Configuration Details](#-configuration-details)
- [🐛 Troubleshooting](#-troubleshooting)
- [👥 Team](#-team)
- [🎓 Acknowledgments](#-acknowledgments)
- [📝 License](#-license)

---

## 📖 About The Project

**HealthHub Clinic** is a comprehensive desktop **Clinic Management System** designed to streamline the daily operations of small to medium-sized healthcare facilities. Built using **Java Swing** with a modern **FlatLaf** theme, the system provides an intuitive and professional interface for managing patients, doctors, and appointments.

The project was developed as part of the **second-year Computer Science curriculum** at the **Egyptian-Polish Nile University (EPNU)**. It demonstrates the practical application of object-oriented programming principles, database integration, GUI development, and software engineering best practices in a real-world context.

### 🎯 Project Goals

- 📌 Deliver a **fully functional** clinic management solution with clean, maintainable code
- 📌 Demonstrate proficiency in **Java Swing GUI development** and **JDBC database integration**
- 📌 Implement a clean **MVC-like architecture** with proper separation of concerns
- 📌 Provide a **modern user experience** through professional UI theming
- 📌 Apply industry-standard **CRUD operations** across all entities

---

## ✨ Key Features

### 🔐 Authentication & Security
- Secure admin login with credential validation through a dedicated `AuthService` layer
- Session management with proper logout functionality
- Password masking and input validation

### 🏥 Branded Welcome Experience
- Professional splash screen with the official EPNU HealthHub logo
- Smooth transition flow: Welcome → Login → Dashboard
- Clean split-screen design that establishes the brand identity

### 📊 Interactive Dashboard with Analytics
- **4 real-time statistics cards** showing key clinic metrics
- **Recent Appointments table** with the latest bookings
- **Interactive Bar Chart** visualizing appointment distribution across medical specializations
- Personalized user badge displaying the logged-in admin

### 👥 Comprehensive Patient Management
- **Full CRUD operations** (Create, Read, Update, Delete)
- **Real-time search** functionality to filter patients instantly
- Form validation for required fields
- Gender dropdown selection (Male / Female)
- Persistent storage in SQL Server database

### 🩺 Doctor Management System
- Complete doctor profile management
- **Predefined specialization dropdown** with 7+ medical specialties
- Contact information storage (phone & email)
- Searchable doctor directory

### 📅 Smart Appointment Scheduling
- Book appointments by linking patients with doctors
- **Date and time** management with format validation
- **Three status states:** Scheduled / Completed / Cancelled
- **Color-coded status badges** for at-a-glance recognition
- Optional notes field for additional appointment context
- Update appointment status without recreating the booking

### 🎨 Modern UI/UX
- **FlatLaf theme** for a professional, modern appearance
- Consistent color palette across all screens
- Responsive layout using `CardLayout` for smooth navigation
- Semantic button colors (green for add, blue for update, red for delete)
- Clean typography and spacing throughout the application

---

## 🛠️ Tech Stack

<div align="center">

| Layer | Technology | Version |
|:-----:|:----------:|:-------:|
| **Language** | Java | 11 |
| **GUI Framework** | Java Swing + AWT | Built-in |
| **UI Theme** | FlatLaf | 3.x |
| **Build Tool** | Apache Maven | 3.6+ |
| **Database** | Microsoft SQL Server Express | 2019+ |
| **JDBC Driver** | mssql-jdbc | 12.4.2.jre11 |
| **IDE** | IntelliJ IDEA | 2023+ |
| **DB Management** | SQL Server Management Studio (SSMS) | Latest |
| **Version Control** | Git + GitHub | - |

</div>

---

## 🏗️ System Architecture

The project follows a clean **layered MVC-like architecture** that promotes separation of concerns, maintainability, and testability.

### 📦 Architectural Layers

```
┌─────────────────────────────────────────────────┐
│           🖼️  Presentation Layer                │
│         (views/ — Swing Frames & Panels)        │
├─────────────────────────────────────────────────┤
│           ⚙️  Service Layer                      │
│        (services/ — Business Logic)             │
├─────────────────────────────────────────────────┤
│           💾  Data Access Layer                  │
│         (dao/ — Database Operations)            │
├─────────────────────────────────────────────────┤
│           📦  Model Layer                        │
│           (models/ — POJOs)                     │
├─────────────────────────────────────────────────┤
│           🔧  Utility Layer                      │
│      (utils/ — Helpers & Configuration)         │
└─────────────────────────────────────────────────┘
                       │
                       ▼
              ┌────────────────┐
              │  SQL Server DB │
              └────────────────┘
```

### 🔄 Architectural Highlights

- **Views** handle UI rendering and user interaction only — no business logic
- **Services** contain the business rules (e.g., authentication validation)
- **DAOs** isolate all database queries from the rest of the application
- **Models** are plain Java objects representing the domain entities
- **Utils** provide cross-cutting concerns (DB connection, UI helpers, colors)
- **Navigation** is managed via `CardLayout` for smooth screen transitions

---

## 📸 Application Screenshots

### 🏥 Welcome Screen

![Welcome Screen](screenshots/welcome.jpeg)

The application launches with an elegant **split-screen welcome page** that establishes the HealthHub brand identity from the very first moment.

**Left Panel — Brand Identity:**
- Official HealthHub Clinic circular logo featuring a stethoscope and laptop with the **EPNU** badge
- Application title: **"HealthHub Clinic"**
- Subtitle: *"EPNU · Clinic Management System"*
- Deep blue background creating a professional medical atmosphere

**Right Panel — Call to Action:**
- Bold **"Welcome"** heading
- Mission statement: *"Your health, our priority"*
- Description: *"Manage your clinic with ease..."*
- Prominent **Get Started →** button that navigates to the login screen

This screen serves as both a splash screen and a brand introduction, making the application feel polished and intentional rather than launching directly into a login form.

---

### 🔐 Login Screen

![Login Screen](screenshots/login.jpeg)

The **Secure Login** screen maintains visual continuity with the welcome screen through the same split-screen design, creating a cohesive user experience.

**Authentication Form:**
- 👤 **Username field** — Accepts admin credentials
- 🔒 **Password field** — Masked input for security
- 🔵 **Login button** — Triggers authentication via `AuthService`

**Authentication Flow:**

```
User Input → AuthService.login() → UserDAO.findByCredentials()
    → SQL Server Query → Returns User Object → Open Dashboard
```

> 🔑 **Default Credentials:**
> - **Username:** `admin`
> - **Password:** `admin123`

The login process validates credentials against the `users` table in SQL Server. Failed attempts display appropriate error messages, while successful logins seamlessly transition to the main dashboard.

---

### 📊 Dashboard

![Dashboard](screenshots/dashboard.jpeg)

The **main dashboard** is the operational heart of the application, providing administrators with a comprehensive overview of clinic activities at a single glance.

#### Top Statistics Row — 4 Live Stat Cards

| Card | Description |
|:----:|:-----------|
| 📊 **Total Patients** | Count of all registered patients in the system |
| 🩺 **Total Doctors** | Count of all active doctors |
| 📅 **Appointments** | Total appointments ever recorded |
| ✅ **Scheduled** | Currently active (non-completed/cancelled) appointments |

#### User Identification
- Logged-in admin username displayed in the top-right corner
- Avatar badge with the user's initial for quick visual identification

#### Main Content — Two-Column Layout

**Left: Recent Appointments Table**
- Displays the most recent appointments
- Columns: `#`, `Patient`, `Doctor`, `Date`, `Time`, `Status`
- Shows status as text (Scheduled / Completed / Cancelled)
- Provides quick context on current clinic activity

**Right: Appointment Distribution Bar Chart** ⭐
- **Data Visualization & Analytics feature**
- Bar chart showing appointment counts grouped by medical specialization
- Each specialty has its own distinct color:
  - 🔵 **Cardiology** — Blue
  - 🟢 **Dentistry** — Green
  - 🟡 **Dermatology** — Yellow
  - 🟣 **General Medicine** — Purple
  - 🔴 **Neurology** — Red
- Helps administrators identify which specialties have the highest demand

This dashboard transforms raw database records into actionable business intelligence.

---

### 👥 Patients Page

![Patients Page](screenshots/patients.jpeg)

The **Patient Management** page provides complete control over patient records with an intuitive form-and-table layout.

#### Patient Form (Top Section)

| Field | Type | Description |
|:------|:----:|:------------|
| **Name** | Text input | Full name of the patient |
| **Phone** | Text input | Contact phone number |
| **Age** | Numeric input | Patient's age in years |
| **Gender** | Dropdown | Male / Female selection |

#### Action Buttons (Semantic Colors)

- 🟢 **Add Patient** *(Green)* — Inserts a new patient record into the database
- 🔵 **Update** *(Blue)* — Modifies the currently selected patient
- 🔴 **Delete** *(Red)* — Removes the selected patient from the system

#### Search & Data Table
- 🔍 **Live search bar** that filters the table in real-time as you type
- **JTable** displaying all patients with sortable columns: `ID`, `Name`, `Phone`, `Age`, `Gender`
- Row selection automatically populates the form fields for quick editing
- All changes persist immediately to the SQL Server database via `PatientDAO`

---

### 🩺 Doctors Page

![Doctors Page](screenshots/doctors.jpeg)

The **Doctor Management** page mirrors the patient page's structure, ensuring a consistent and predictable user experience throughout the application.

#### Doctor Form (Top Section)

| Field | Type | Description |
|:------|:----:|:------------|
| **Doctor Name** | Text input | Full name of the physician |
| **Specialization** | Dropdown | Medical specialty selection |
| **Phone** | Text input | Contact number |
| **Email** | Text input | Professional email address |

#### Supported Specializations
- 🫀 Cardiology
- 🦷 Dentistry
- 🌟 Dermatology
- 👶 Pediatrics
- 🩺 General Medicine
- 🦴 Orthopedics
- 🧠 Neurology

#### Action Buttons
- 🟢 **Add Doctor** — Register a new physician
- 🔵 **Update** — Edit doctor information
- 🔴 **Delete** — Remove a doctor from the system

#### Doctor Directory
- 🔍 **Search Doctor** bar for quick filtering by name or specialty
- **JTable** displaying: `ID`, `Name`, `Specialization`, `Phone`, `Email`

---

### 📅 Appointments Page

![Appointments Page](screenshots/appointments.jpeg)

The **Appointments Management** page is the most feature-rich screen, combining appointment creation, status tracking, and management in a unified interface.

#### Booking Form (Comprehensive 6-Field Form)

| Field | Type | Description |
|:------|:----:|:------------|
| **Patient** | JComboBox | Dropdown listing all registered patients (format: `ID - Name`) |
| **Doctor** | JComboBox | Dropdown listing all available doctors (format: `ID - Name`) |
| **Date** | Text input | Appointment date (format: `YYYY-MM-DD`) |
| **Time** | JComboBox | Available time slots dropdown |
| **Notes** | Text input | Optional notes for the appointment |
| **Status** | JComboBox | Scheduled / Completed / Cancelled |

#### Action Buttons
- 🟢 **Book** — Create a new appointment
- 🔵 **Update Status** — Change appointment status without recreating
- 🔴 **Delete** — Cancel and remove the appointment

#### Appointments Table with Color-Coded Status Badges

The status column uses **visually distinct color badges** that make scanning the schedule extremely fast:

| Badge | Status | Meaning |
|:-----:|:-------|:--------|
| 🟢 **Completed** | Green badge | The appointment has been fulfilled |
| 🔵 **Scheduled** | Blue badge | The appointment is upcoming/active |
| 🔴 **Cancelled** | Red badge | The appointment was cancelled |

**Table Columns:** `#`, `Patient Name`, `Doctor ID`, `Date`, `Time`, `Status`, `Notes`

**Key UX Features:**
- Selecting a row populates the form for easy status updates
- The Notes column shows additional context per appointment
- Scrollable table accommodates large numbers of appointments
- All changes sync immediately with the SQL Server backend

---

## 🗄️ Database Design

The system uses **SQL Server Express** with a normalized relational schema. All tables use `IDENTITY` for auto-incrementing primary keys and proper foreign key relationships.

### 📊 Entity Relationship Overview

```
┌──────────────┐      ┌──────────────┐      ┌──────────────┐
│    USERS     │      │   PATIENTS   │      │   DOCTORS    │
├──────────────┤      ├──────────────┤      ├──────────────┤
│ id (PK)      │      │ id (PK)      │      │ id (PK)      │
│ username     │      │ name         │      │ name         │
│ password     │      │ phone        │      │ specialization│
│ role         │      │ age          │      │ phone        │
└──────────────┘      │ gender       │      │ email        │
                      └──────┬───────┘      └──────┬───────┘
                             │                     │
                             │  ┌──────────────┐  │
                             └──┤ APPOINTMENTS ├──┘
                                ├──────────────┤
                                │ id (PK)      │
                                │ patient_id   │ (FK)
                                │ doctor_id    │ (FK)
                                │ date         │
                                │ time         │
                                │ status       │
                                │ notes        │
                                └──────────────┘
```

### 📋 Tables Overview

| Table | Purpose | Key Columns |
|:------|:--------|:------------|
| `users` | Admin authentication credentials | `username`, `password`, `role` |
| `patients` | Patient records and demographics | `name`, `phone`, `age`, `gender` |
| `doctors` | Doctor profiles and specializations | `name`, `specialization`, `phone`, `email` |
| `appointments` | Booking records linking patients to doctors | `patient_id`, `doctor_id`, `date`, `time`, `status` |

### 🔧 SQL Server Specific Syntax

The project uses native SQL Server T-SQL syntax (migrated from MySQL):

| Feature | SQL Server Syntax |
|:--------|:------------------|
| Auto-increment | `IDENTITY(1,1)` |
| Current timestamp | `GETDATE()` |
| Current date | `CAST(GETDATE() AS DATE)` |
| Large text fields | `VARCHAR(MAX)` |
| Batch separators | `GO` statements |

---

## 🚀 Getting Started

Follow these steps to set up and run the project on your local Windows machine.

### 📋 Prerequisites

Before you begin, ensure you have the following installed:

- ☕ **Java JDK 11** or higher — [Download](https://www.oracle.com/java/technologies/downloads/)
- 📦 **Apache Maven 3.6+** — Usually bundled with IntelliJ IDEA
- 🗄️ **SQL Server Express** (with `SQLEXPRESS` named instance) — [Download](https://www.microsoft.com/en-us/sql-server/sql-server-downloads)
- 🛠️ **SQL Server Management Studio (SSMS)** — [Download](https://learn.microsoft.com/en-us/sql/ssms/download-sql-server-management-studio-ssms)
- 💻 **IntelliJ IDEA** (Community or Ultimate) — [Download](https://www.jetbrains.com/idea/download/)
- 🌐 **Git** — [Download](https://git-scm.com/downloads)

### 🔧 Step-by-Step Installation

#### 1️⃣ Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/HealthHubClinic.git
cd HealthHubClinic
```

#### 2️⃣ Open the Project in IntelliJ IDEA

- Launch IntelliJ IDEA
- Select **File → Open** and navigate to the cloned folder
- Wait for Maven to automatically download all dependencies (this may take a few minutes on first launch)

#### 3️⃣ Set Up the SQL Server Database

**3.1 — Open SQL Server Management Studio (SSMS):**
- Connect to your local instance using: `localhost\SQLEXPRESS`
- Use **Windows Authentication**

**3.2 — Run the Database Setup Script:**
- Open the SQL setup script from the project
- Execute the script in SSMS (press **F5** or click **Execute**)
- This will create the `HealthHubDB` database with all required tables and seed data

#### 4️⃣ Configure SQL Server for JDBC Connection

**4.1 — Enable TCP/IP Protocol:**
- Open **SQL Server Configuration Manager**
- Navigate to **SQL Server Network Configuration → Protocols for SQLEXPRESS**
- Right-click **TCP/IP** → **Enable**
- Restart the SQL Server (SQLEXPRESS) service

**4.2 — Start SQL Server Browser Service:**
- Open **Services** (Win + R, type `services.msc`)
- Find **SQL Server Browser**
- Set **Startup Type** to **Automatic**
- Click **Start** if not already running

#### 5️⃣ Verify the JDBC Connection String

Open `src/main/java/healthhub/utils/DBConnection.java` and confirm the connection string matches your setup:

```java
private static final String URL =
    "jdbc:sqlserver://localhost\\SQLEXPRESS;"
    + "databaseName=HealthHubDB;"
    + "integratedSecurity=true;"
    + "encrypt=false;"
    + "trustServerCertificate=true;";
```

#### 6️⃣ Build and Run

**Option A — From IntelliJ:**
- Open `Main.java`
- Click the green **Run** button (▶️)

**Option B — From Terminal:**
```bash
mvn clean install
mvn exec:java
```

#### 7️⃣ Log In

When the application launches:
- Click **Get Started** on the welcome screen
- Enter the default credentials:
  - **Username:** `admin`
  - **Password:** `admin123`
- Click **Login** to access the dashboard

🎉 **You're all set! Welcome to HealthHub Clinic.**

---

## 📂 Project Structure

```
HealthHubClinic/
│
├── 📁 src/
│   └── 📁 main/
│       ├── 📁 java/
│       │   └── 📁 healthhub/
│       │       │
│       │       ├── 📄 Main.java                    # Application entry point
│       │       │
│       │       ├── 📁 views/                       # UI Layer
│       │       │   ├── 📄 WelcomeFrame.java        # Splash welcome screen
│       │       │   ├── 📄 LoginFrame.java          # Authentication screen
│       │       │   ├── 📄 DashboardFrame.java      # Main dashboard with charts
│       │       │   ├── 📄 PatientsPanel.java       # Patient CRUD
│       │       │   ├── 📄 DoctorsPanel.java        # Doctor CRUD
│       │       │   └── 📄 AppointmentsPanel.java   # Appointment management
│       │       │
│       │       ├── 📁 models/                      # Data Models (POJOs)
│       │       │   ├── 📄 User.java
│       │       │   ├── 📄 Patient.java
│       │       │   ├── 📄 Doctor.java
│       │       │   └── 📄 Appointment.java
│       │       │
│       │       ├── 📁 dao/                         # Data Access Layer
│       │       │   ├── 📄 UserDAO.java             # User database operations
│       │       │   ├── 📄 PatientDAO.java          # Patient CRUD queries
│       │       │   ├── 📄 DoctorDAO.java           # Doctor CRUD queries
│       │       │   └── 📄 AppointmentDAO.java      # Appointment queries
│       │       │
│       │       ├── 📁 services/                    # Business Logic Layer
│       │       │   └── 📄 AuthService.java         # Login validation
│       │       │
│       │       └── 📁 utils/                       # Utilities
│       │           ├── 📄 DBConnection.java        # JDBC connection manager
│       │           ├── 📄 ColorPalette.java        # Centralized colors
│       │           └── 📄 UIHelper.java            # UI helper methods
│       │
│       └── 📁 resources/                           # Application resources
│
├── 📁 screenshots/                                 # README screenshots
│   ├── 🖼️ welcome.jpeg
│   ├── 🖼️ login.jpeg
│   ├── 🖼️ dashboard.jpeg
│   ├── 🖼️ patients.jpeg
│   ├── 🖼️ doctors.jpeg
│   └── 🖼️ appointments.jpeg
│
├── 📁 target/                                      # Maven build output
├── 📄 .gitignore                                   # Git ignore rules
├── 📄 pom.xml                                      # Maven dependencies
└── 📄 README.md                                    # You are here!
```

---

## 🎯 Project Scope

This system is intentionally scoped to align with **second-year academic expectations** while still demonstrating professional software development practices.

<div align="center">

| ✅ **Included Features** | ❌ **Excluded Features** |
|:------------------------|:-------------------------|
| Welcome Splash Screen | Billing & Receipts |
| Admin Authentication | Multi-role Permissions |
| Interactive Dashboard | Email/SMS Notifications |
| Bar Chart Analytics | Payment Gateway Integration |
| Patient CRUD Operations | Insurance Management |
| Doctor CRUD Operations | Prescription System |
| Appointment Management | Complex Reporting |
| Color-Coded Status Badges | Patient Medical History |
| Real-Time Search | Multi-Language Support |
| SQL Server Integration | Cloud Deployment |

</div>

> 💡 **Design Philosophy:** The project deliberately avoids over-engineering to maintain focus on core functionality and demonstrate mastery of the curriculum's required topics: OOP, Java Swing, JDBC, and SQL.

---

## 🔧 Configuration Details

### 📦 Maven Dependencies (`pom.xml`)

The project relies on three primary dependencies:

| Dependency | Purpose |
|:-----------|:--------|
| **mssql-jdbc** (12.4.2.jre11) | JDBC driver for SQL Server connectivity |
| **flatlaf** (latest) | Modern UI theming for Java Swing |
| **flatlaf-extras** (optional) | Additional FlatLaf themes and icons |

### 🎨 Color Palette (`ColorPalette.java`)

The application uses a consistent, centralized color system:

| Color | Hex Code | Usage |
|:-----:|:---------|:------|
| 🔵 Primary Blue | `#11529A` | Headers, sidebar, primary buttons |
| 🟢 Success Green | `#38A169` | Add buttons, success states |
| 🔴 Danger Red | `#C53030` | Delete buttons, error states |
| 🔵 Info Blue | `#2C5282` | Update buttons, info states |
| ⚪ Background | `#F4F6F8` | Main content background |
| ⬜ Card White | `#FFFFFF` | Card and panel backgrounds |

### 💾 Database Connection Details

- **Server:** `localhost\SQLEXPRESS` (named instance)
- **Authentication:** Windows Integrated Authentication
- **Database Name:** `HealthHubDB`
- **Port:** Dynamic (managed by SQL Server Browser)
- **Encryption:** Disabled for local development

---

## 🐛 Troubleshooting

### ❗ Common Issues & Solutions

<details>
<summary><strong>🔴 Cannot connect to SQL Server</strong></summary>

**Symptoms:** `Login failed` or `Connection refused` errors

**Solutions:**
1. Verify SQL Server (SQLEXPRESS) service is running in **Services**
2. Enable **TCP/IP** protocol in SQL Server Configuration Manager
3. Start the **SQL Server Browser** service (set to Automatic)
4. Restart the SQL Server service after making changes
5. Verify the connection string in `DBConnection.java` matches your instance name

</details>

<details>
<summary><strong>🔴 "Driver not found" error</strong></summary>

**Symptoms:** `ClassNotFoundException: com.microsoft.sqlserver.jdbc.SQLServerDriver`

**Solutions:**
1. Open `pom.xml` and verify the `mssql-jdbc` dependency is present
2. Right-click the project → **Maven → Reload Project**
3. Run `mvn clean install` from the terminal

</details>

<details>
<summary><strong>🔴 Login screen rejects valid credentials</strong></summary>

**Symptoms:** Default `admin`/`admin123` doesn't work

**Solutions:**
1. Verify the `users` table was populated by the setup script
2. Open SSMS and run: `SELECT * FROM users;`
3. If empty, re-run the setup script seed section
4. Check for case sensitivity in the username

</details>

<details>
<summary><strong>🔴 Dashboard chart is empty</strong></summary>

**Symptoms:** Bar chart shows no data

**Solutions:**
1. Ensure you have at least one appointment in the database
2. Verify doctors have specializations assigned
3. Check the JOIN query in `AppointmentDAO`

</details>

<details>
<summary><strong>🔴 UI looks outdated / no FlatLaf theme</strong></summary>

**Symptoms:** Default Swing look instead of modern flat design

**Solutions:**
1. Verify FlatLaf is set in `Main.java` before any UI is created:
   ```java
   FlatLightLaf.setup();
   ```
2. Check that the `flatlaf` dependency is in `pom.xml`
3. Reload Maven project

</details>

---

## 👥 Team

This project was a collaborative effort by a team of **5 dedicated Computer Science students** at EPNU.

### 💻 Development Team

<div align="center">

| 👤 Name | 🎯 Contribution |
|:--------|:----------------|
| **Hanin Tarek** | Full-Stack Development |
| **Lujain Ahmed** | Full-Stack Development |
| **Kareman Shawky** | Full-Stack Development |
| **Ahmed Wael** | Full-Stack Development |
| **Ahmed Abo Shady** | Full-Stack Development |

</div>

### 🎨 Project Branding & Design

- **Hala Elhadidy**
- **Sara Abo Hashish**

### 🎓 Program Director

**Eng. Osama Elbeksawy** — *Computer Science Program Director, EPNU*

### 🏫 Institution

<div align="center">

**Egyptian-Polish Nile University (EPNU)**
*Faculty of Computer Science — Year 2*

</div>

---

## 🎓 Acknowledgments

We extend our sincere gratitude to:

- 🏛️ **EPNU Faculty** — For invaluable guidance, mentorship, and support throughout the project development cycle
- 👨‍🏫 **Eng. Osama Elbeksawy** — For program direction and academic supervision
- 🎨 **[FlatLaf](https://www.formdev.com/flatlaf/)** — For providing the beautiful modern UI theme that elevated the user experience
- 🗄️ **Microsoft** — For SQL Server Express and the robust JDBC driver
- ☕ **Oracle** — For Java and the comprehensive Swing framework
- 🌐 **Stack Overflow Community** — For countless answered questions during development
- 📚 **Open Source Contributors** — Whose libraries and documentation made this project possible

---

## 📝 License

This project is developed for **educational purposes** as part of the second-year Computer Science curriculum at the **Egyptian-Polish Nile University (EPNU)**.

All rights reserved by the HealthHub development team. The code may be used as a reference for learning purposes with proper attribution.

---

<div align="center">

### 🌟 Show Your Support

If you found this project helpful or inspiring, please consider giving it a ⭐ on GitHub!

---

**Made with ❤️ and ☕ by the HealthHub Team @ EPNU**

*Building the future of healthcare management, one line of code at a time.*

</div>
