# Firebase Setup Steps for HisabKitab App

## 1. Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project" → Name: `HisabKitab`
3. Disable Google Analytics (optional) → Create project.

## 2. Add Android App
1. Click "Add app" → Android icon.
2. Android package name: `com.premium.ledger`
3. App nickname: `HisabKitab` (optional)
4. SHA-1 (optional for now, get via `./gradlew signingReport` later).
5. Register app.

## 3. Download Config
1. Download `google-services.json`
2. Copy to `app/google-services.json` (create if needed).

## 4. Enable Auth & Firestore
**Authentication**:
1. Project Settings → Authentication → Get Started.
2. Enable Email/Password provider.

**Firestore**:
1. Build → Firestore Database → Create database.
2. Start in **test mode** (later secure rules).
3. Rules example:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## 5. Sync & Test
1. Android Studio: Sync Gradle.
2. Build: `./gradlew assembleDebug`
3. Run app → Register → Login → Check Firebase Console (Authentication users, Firestore data).

**Data Structure**: Firestore: `users/{uid}/customers/{id}`, `transactions`, `expenses`.

Done! App ready with auth + data storage.

