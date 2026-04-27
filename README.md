# Smart Campus — Student Portal
**Your Complete Academic Companion**

Smart Campus is a modern, intuitive mobile application designed to streamline the college experience for students and administrators. Built with Material Design 3 principles and Jetpack Compose, this app serves as a centralized hub for all academic needs — featuring a full **role-based access system** with separate student and admin portals.

---

## 🎯 Key Features

### 🔐 Secure Authentication
- Clean, user-friendly login interface with credential validation
- Role-based routing — students go to the Dashboard, admins go to the Admin Panel
- Secure password visibility toggle for enhanced privacy
- Forgot password flow with email verification and 2-step reset
- Smooth onboarding with elegant animations

### 🏠 Interactive Dashboard
- Personalized welcome screen with your name displayed
- Quick access cards for all essential functions:
  - Class Schedule viewing
  - To-Do List / Task management
  - Grade monitoring
  - Campus announcements (with live unread badge)
- Recent activity feed tracking the last 5 features visited
- Real-time notification bell with badge indicator
- Navigation drawer with Profile, Settings, Campus Info, and Logout

### 📋 To-Do List & Task Management
- Add, edit, and delete personal tasks with full CRUD support
- Due date and time picker with a custom styled date/time dialog
- Smart status indicators — Overdue (red), Due Soon (amber), Normal (green)
- Tasks sorted automatically: incomplete first by due date, then completed
- Pending and Completed sections with count badges
- Strikethrough and animated checkbox on completion

### 📅 Class Schedule
- Full weekly grid view (Monday–Sunday, 6AM–10PM)
- Color-coded subject cards with instructor, type, and room details
- Dual-scroll (horizontal + vertical) for full week navigation
- 30-minute time slot resolution with header rows top and bottom

### 📊 Grade Viewer
- Animated GWA ring with progress indicator on load
- Two tabs: Overview (grade distribution bars) and All Grades (subject cards)
- Performance Highlights — best and worst subjects
- Latin Honors tracker (Summa, Magna, Cum Laude, or Keep Pushing)
- Color-coded grades from Excellent (dark green) to Failed (gray)

### 📢 Campus Announcements
- Live announcement feed ordered by date (newest first)
- Unread dot indicator and animated background color per card
- Mark-as-read on tap
- Unread count shown in TopAppBar subtitle

### 👤 Comprehensive Profile Management
- View and edit display name via dialog
- Profile photo upload using Android photo picker with persistent URI permissions
- Camera badge overlay for quick photo change
- Displays Student ID, program, year level, and verified campus email
- Per-user data stored in SharedPreferences (keyed by userId)

### 🏫 Campus Information Hub
Explore all 6 colleges with contact details and imagery:
- College of Health and Allied Sciences (CHAS)
- College of Business, Accountancy and Administration (CBAA)
- College of Computing Studies (CCS)
- College of Engineering (COE)
- College of Education (COED)
- College of Arts and Sciences (CAS)

### ⚙️ Customizable Settings
- **Dark Mode** toggle with full app recreation for consistent theming
- Font size selector (Small / Medium / Large preview)
- Change password with strength indicator (Too Short / Weak / Good / Strong)
- Push notifications and email announcement toggles
- Help Center and About App dialog (version, platform, SDK info)

---

## 🛡️ Admin Panel

Administrators log in through the same login screen and are automatically routed to a **dedicated Admin Dashboard** — a completely separate dark-themed interface.

### Admin Capabilities
- **Post** new announcements with title and content — instantly visible to all students
- **Edit** any existing announcement
- **Delete** any announcement with confirmation dialog
- **View all** announcements with read/unread status badges
- Live stats overview (Total, Unread, Read counts)

### Admin Access
| Field    | Value       |
|----------|-------------|
| Username | `admin`     |
| Password | `admin123`  |

> The admin account is seeded automatically on first install or DB migration. Admin accounts cannot be created through the Sign Up screen.

---

## 🎨 Design Highlights
- **Modern Green Color Palette** — Professional forest green representing academic growth
- **Dark Admin Theme** — Deep navy with cyan accents for the admin panel
- **Smooth Animations** — Fade-in, slide, color transitions throughout
- **Material Design 3** — Latest standards for optimal user experience
- **Edge-to-Edge Display** — Immersive full-screen experience
- **Gradient Accents** — Elegant visual depth in headers and cards
- **Responsive Cards** — Elevation and color shifts based on content state

---

## 🔒 Security & Privacy
- Role-based authentication (Student vs Admin)
- Credential validation with duplicate checks on registration (username, email, student ID)
- Password change requires current password verification
- Persistent URI permissions for profile photos
- Per-user profile data isolation in SharedPreferences
- Session cleared on logout

---

## 🗄️ Architecture & Data

| Layer        | Technology                          |
|-------------|--------------------------------------|
| UI          | Jetpack Compose + Material Design 3  |
| ViewModel   | AndroidViewModel + StateFlow         |
| Database    | Room (SQLite) — 2 databases          |
| Images      | Coil (AsyncImage)                    |
| Theme       | Dynamic light/dark via MaterialTheme |

**Room Databases:**
- `smart_campus_database` — Users + Tasks (version 3)
- `announcement_database` — Announcements (version 1, shared between student and admin)

---

## 📱 User Experience

Smart Campus prioritizes simplicity and efficiency. With an intuitive navigation drawer, clean card-based layouts, and thoughtful micro-interactions, students can quickly access information without unnecessary complexity. The app maintains a consistent visual language while providing contextual feedback through toasts, animated cards, and confirmation dialogs.

The admin panel follows the same philosophy — minimal clicks to post, edit, or remove announcements, with immediate reflection on the student side since both share the same Room database.

---

## 🚀 Version Information

| Field        | Value                     |
|-------------|---------------------------|
| Version     | 1.0.5                     |
| Platform    | Android 8.0+              |
| Language    | Kotlin                    |
| UI Toolkit  | Jetpack Compose           |
| Min SDK     | Android 8.0 (API 26)      |
| Target      | College Students & Admins |
