# Image Gallery App

A modern Android application built to demonstrate best practices in app development using Jetpack Compose, Clean Architecture, and MVVM. The app loads images from the device's local storage and displays them in a responsive grid, featuring infinite scrolling with pagination.

[Image Gallery App Screenshot]
<img width="540" height="1170" alt="Screenshot_20260119_110347" src="https://github.com/user-attachments/assets/d410a398-c1b5-42d3-8231-ee25a7bdfbbc" />
<img width="1170" height="540" alt="Screenshot_20260119_110412" src="https://github.com/user-attachments/assets/a7db6e94-9abd-4800-a8a4-b54d13597c81" />


---

## 🚀 Features

*   **Jetpack Compose:** The entire UI is built with Jetpack Compose, Google's modern declarative UI toolkit.
*   **Clean Architecture:** The project follows a clear separation of concerns, divided into Data, Domain, and Presentation layers. This makes the codebase scalable, maintainable, and testable.
*   **MVVM Pattern:** Utilizes the Model-View-ViewModel pattern to separate UI logic from business logic.
*   **Pagination / Infinite Scrolling:** Images are loaded in pages from the device's `MediaStore` to efficiently handle thousands of images without performance degradation.
*   **Coroutines:** Asynchronous operations like data fetching are handled seamlessly using Kotlin Coroutines and `viewModelScope`.
*   **Coil 3:** Asynchronous image loading and caching are managed by [Coil](https://coil-kt.github.io/coil/compose/), the modern image loading library for Kotlin.
*   **Responsive UI:** The gallery uses a `LazyVerticalGrid` that adapts the number of columns based on screen size.

---

## 🏛️ Architectural Overview

This project implements a clean, multi-layered architecture:

### 1. UI Layer (`presentation`)
*   **`ImageGalleryScreen.kt` (Composable):** Responsible for observing state from the `ImageViewModel` and rendering the UI. It is a "dumb" view that only reacts to state changes and forwards user events (like scrolling to the end of the list).
*   **`ImageViewModel.kt`:** The ViewModel holds and manages UI-related state. It interacts with the Domain layer (UseCases) to fetch data and updates the UI state accordingly. It survives configuration changes and has no knowledge of the Android framework beyond the Application context.

### 2. Domain Layer (`domain`)
*   **`Image.kt`:** A simple data model representing an image, used across all layers.

### 3. Data Layer (`data`)
*   **`ImageRepository.kt`:** The repository acts as the single source of truth for the app's data. It abstracts the data source from the rest of the app. In this project, it fetches images from the Android `MediaStore` using `ContentResolver`.

This structure ensures that each component has a single responsibility, making the app robust and easy to debug.

---

## 🛠️ Built With

*   [Kotlin](https://kotlinlang.org/) - First-class and official programming language for Android development.
*   [Jetpack Compose](https://developer.android.com/jetpack/compose) - Android’s modern toolkit for building native UI.
*   [Android Architecture Components](https://developer.android.com/jetpack/arch)
    *   [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores and manages UI-related data in a lifecycle-conscious way.
    *   [Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle) - Manages activity and fragment lifecycles.
*   [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) - For asynchronous programming.
*   [Coil 3](https://coil-kt.github.io/coil/) - An image loading library for Android backed by Kotlin Coroutines.
*   [Material 3](https://m3.material.io/) - Google's latest design system.

---

## 💡 Future Improvements

1.  **Dependency Injection:** Integrate Hilt or Koin to manage dependencies instead of manual instantiation in the ViewModel.
2.  **Unit & UI Tests:** Write unit tests for the ViewModel and UseCase, and instrumentation tests for the Composables.
3.  **Error Handling:** Implement a more robust error-handling mechanism to show user-friendly messages if image loading fails.
4.  **Image Detail Screen:** Add functionality to open an image in a full-screen view when tapped.
5.  **Caching Strategy:** Fine-tune the data layer to cache image lists in memory or a local database to avoid re-fetching on configuration changes.
    
