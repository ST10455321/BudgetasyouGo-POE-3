# BudgetasyouGo-POE-3
BudgetasYouGO is an Android app built in Kotlin that helps users track their income and expenses in real time. Features include Firebase authentication, transaction logging, budget goals, and visual spending reports.
BudgetasYouGO

A real-time personal finance tracker for Android — budget as you go, not after the fact.

---

Description

BudgetasYouGO is an Android application built in Kotlin that helps users take control of their personal finances on the go. Users can securely log in, record their income and expenses instantly, set monthly budget goals per category, and view visual reports of their spending habits. All data is stored in the cloud using Firebase, meaning nothing is ever lost between sessions. The app was designed to be fast, simple, and easy to use — so that budgeting becomes a habit, not a chore.

---

Features

Secure login and registration using Firebase Authentication
Log expenses and income instantly with category, amount, and description
Set monthly budget goals per spending category
Visual spending reports and charts using MPAndroidChart
Full transaction history with edit and delete support
Real-time cloud sync with Firebase Firestore
Dark mode support following system theme
Clean and simple Material Design 3 interface

---

Screenshots

> _Screenshots will be added here_

| Dashboard | Add Transaction | Visual chart |
|:---------:|<img width="165" height="356" alt="Screenshot 2026-06-15 152534" src="https://github.com/user-attachments/assets/75fa7f9f-979b-4321-b2c8-26595a90665b" />|:-------:|
| <img width="157" height="359" alt="Screenshot 2026-06-15 152508" src="https://github.com/user-attachments/assets/52ada84b-8a1e-498c-af43-f8d2422b4719" />
 |
 | <img width="164" height="360" alt="Screenshot 2026-06-15 152602" src="https://github.com/user-attachments/assets/22e01511-c80c-40c9-bd20-9e941261a33b" />
 |

---

Video Presentation

Video link will be added here

---

How to Run the App

1. Clone the repository
   ```bash
   git clone https://github.com/YOUR_USERNAME/BudgetasYouGO.git
   ```

2. Open in Android Studio
   - Open Android Studio → Open the cloned folder → Wait for Gradle to sync

3. Add Firebase
   - Go to [Firebase Console](https://console.firebase.google.com)
   - Create a project → Add an Android app → Download `google-services.json`
   - Place `google-services.json` inside the `/app` folder

4. Run the app
   - Connect a phone or start an emulator
   - Click the green Run button in Android Studio

---

Tech Stack

| Tool | Purpose |
|------|---------|
| Kotlin | Main programming language |
| Android Studio | Development environment |
| Firebase Authentication | User login and registration |
| Firebase Firestore | Cloud database for storing transactions |
| MPAndroidChart | Spending charts and graphs |
| Material Design 3 | UI components and theming |
| GitHub Actions | Automated build and lint checks |

---

GitHub Actions

A GitHub Actions workflow runs automatically every time code is pushed to the repository. It performs the following steps:

1. Checks out the latest code
2. Sets up a Java 17 environment
3. Runs **Android Lint** to catch code quality issues
4. Builds a **debug APK** to confirm the app compiles successfully
5. Uploads the APK as a downloadable build artifact

This ensures that any broken code is caught automatically before it reaches the main branch.

```yaml
name: Android CI

on:
  push:
    branches: [ "main", "dev" ]
  pull_request:
    branches: [ "main" ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle
      - run: chmod +x gradlew
      - run: ./gradlew lint
      - run: ./gradlew assembleDebug
      - uses: actions/upload-artifact@v4
        with:
          name: debug-apk
          path: app/build/outputs/apk/debug/app-debug.apk
```

---

References

- [Android Developer Documentation](https://developer.android.com/docs)
- [Firebase for Android](https://firebase.google.com/docs/android/setup)
- [Material Design 3](https://m3.material.io/)
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Kotlin Language Guide](https://kotlinlang.org/docs/home.html)

---

