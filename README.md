# CS 4530 Drawing App

A feature-rich Android drawing application built for **CS 4530: Mobile App Programming** (Fall 2025) at the University of Utah, taught by **Nabil Makarem**.

**Contributors:** Collin Giles, Eric Nguyen, Jacob Nguyen

---

## Overview

This app lets users create digital drawings with a customizable canvas, save and manage a personal gallery, analyze artwork with AI, and share creations with a community of other users.

**Core Features:**
- Touch-based drawing canvas with customizable brush settings (color, size, shape)
- Local image gallery for saving and loading artwork
- Firebase authentication and cloud storage for user accounts
- Community screen to browse and download drawings shared by other users
- Google Cloud Vision AI integration for automatic image analysis (label detection and object localization with bounding boxes)
- Animated splash screen with portrait and landscape variants

---

## Technologies

| Category | Technology |
|----------|-----------|
| Language | Kotlin |
| UI Framework | Jetpack Compose (Material3) |
| Architecture | MVVM + Repository Pattern |
| Dependency Injection | Hilt (Dagger) |
| Local Database | Room (SQLite ORM) |
| Networking | Ktor Client |
| Serialization | Kotlinx Serialization |
| Image Loading | Coil |
| Authentication | Firebase Authentication |
| Cloud Database | Firebase Firestore |
| Cloud Storage | Firebase Storage |
| AI / Vision | Google Cloud Vision API |
| Navigation | Jetpack Navigation Compose |
| Testing | JUnit 4, Mockito, Espresso |
| Build System | Gradle (Kotlin DSL) |

**Minimum SDK:** Android API 24 (Android 7.0)
**Target SDK:** API 36

---

## Architecture

The project follows **MVVM (Model-View-ViewModel)** with a clean **Repository Pattern** to separate data concerns from UI logic.

```
UI Layer (Composables)
    ↕
ViewModel Layer (state management, business logic)
    ↕
Repository Layer (abstracts data sources)
    ↕
Data Sources: Room DB  |  File System  |  Firebase  |  Vision API
```

### Key Architectural Patterns

**MVVM** — ViewModels (`DrawingViewModel`, `HomeViewModel`, `LoginViewModel`) own and expose all UI state. Composables observe state reactively and never access data sources directly.

**Repository Pattern** — `ImageRepository` provides a unified interface over local Room storage and the file system. `VisionRepository` wraps the entire Cloud Vision API client. Firebase operations live in `HomeViewModel` and `LoginViewModel` via Firebase SDK calls.

**Dependency Injection (Hilt)** — `AppModule` provides application-scoped singletons (Room database, DAO, repositories). ViewModels receive dependencies via constructor injection using `@HiltViewModel`.

**Immutable Data Models** — Drawing strokes and image records are Kotlin `data class` instances. State updates use `.copy()` rather than mutation, making state changes predictable and traceable.

**Coroutines** — All async operations (file I/O, database queries, network calls, Firebase) use Kotlin coroutines with `viewModelScope.launch` and `.await()` for suspension, preventing memory leaks and UI blocking.

---

## Screens

| Screen | Description |
|--------|-------------|
| **SplashScreen** | Animated intro with spring/ease animations; adapts for portrait and landscape orientations |
| **HomeScreen** | Gallery of saved drawings with options to open, delete, import, or share |
| **LoginScreen** | Firebase email/password authentication with account creation |
| **Drawing Canvas** | Primary drawing interface with touch input, pen settings panel, and save dialog |
| **AnalysisScreen** | Displays Vision API results — labels and detected objects with bounding box overlays |
| **CommunityScreen** | Browse and download drawings shared by all users via Firestore and Firebase Storage |

---

## Concepts & Skills Demonstrated

### Mobile UI Development
- Declarative UI with **Jetpack Compose** and **Material3** design components
- Custom `Canvas` composable handling multi-point touch gesture detection and stroke rendering
- Three brush shapes (circle, square, triangle) rendered in real time
- Color picker and size slider integrated into a brush settings dialog
- Orientation-aware layouts — canvas coordinates are mathematically transformed on rotation (90° rotation handling)

### State Management
- ViewModel-scoped state survives configuration changes (screen rotation)
- Reactive data flow using Compose `State`, `produceState`, and `collectAsStateWithLifecycle`
- UI state sealed classes (`VisionUIState`) model loading, success, and error conditions

### Data Persistence
- **Room** database stores image metadata (name, file path, date) with DAO abstraction
- Bitmap images saved to the file system via a custom `ImageHandler` utility
- `FileProvider` used for secure file URI sharing between app components

### Cloud & Backend Integration
- **Firebase Authentication** for user account creation and login
- **Firestore** for storing image metadata documents in the cloud
- **Firebase Storage** for uploading and retrieving binary image files
- Community feed populated by querying Firestore for all shared drawings

### AI / Machine Learning Integration
- **Google Cloud Vision API** called via Ktor HTTP client with JSON serialization
- Label detection surfaces semantic descriptions of drawing content
- Object localization returns bounding polygon coordinates, drawn as overlays on the image
- API key injected at build time via `BuildConfig` (gradle.properties)

### Software Engineering Practices
- **Dependency Injection** reduces coupling and makes components independently testable
- **Unit tests** with Mockito verify ViewModel and Repository logic in isolation
- **Instrumented tests** (Espresso, Room in-memory DB) validate Android-specific behavior
- **Navigation graph** centralizes all route definitions with URL-encoded parameters for file paths
- Async work scoped to ViewModels to prevent coroutine leaks on screen exit

---

## Project Structure

```
app/src/main/java/com/example/phase1/
├── data/
│   ├── file/           ImageHandler (bitmap I/O)
│   ├── local/          Room database, DAO, ImageRecord entity
│   └── repository/     ImageRepository, VisionRepository, Vision API models
├── model/              Stroke, BrushShape
├── ui/
│   ├── Navigation/     NavGraph (Compose routing)
│   ├── SplashScreen/
│   ├── MainScreen/     DrawingCanvas, MainScreen container
│   ├── drawscreen/     ColorPicker, PenSettings dialog, SaveDialogue
│   ├── homescreen/     HomeScreen, ImportScreen, ShareScreen
│   ├── login/          Firebase auth UI
│   ├── analysis/       Vision API results display
│   ├── communityscreen/
│   └── theme/          Material3 colors and typography
├── vm/                 DrawingViewModel, HomeViewModel, LoginViewModel, VisionUIState
├── di/                 AppModule (Hilt bindings)
├── MainActivity.kt
└── MainApplication.kt
```

---

## Course Information

**Course:** CS 4530 — Mobile App Programming, Fall 2025
**Instructor:** Nabil Makarem
**University:** University of Utah
**Team:** Collin Giles, Eric Nguyen, Jacob Nguyen
