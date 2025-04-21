# Color Blindness Detection

A modern Android application built with Jetpack Compose that helps users detect and understand their type of color blindness.

## Features

- **Ishihara Test Implementation**: 10 digital Ishihara plates that test for various types of color blindness
- **User-Friendly Interface**: Clean, intuitive UI built with Material Design 3 and Jetpack Compose
- **Color Blindness Analysis**: Detects potential color vision deficiencies:
  - Normal Vision
  - Protanopia (red-green color blindness with deficiency in red perception)
  - Deuteranopia (red-green color blindness with deficiency in green perception)
  - Tritanopia (blue-yellow color blindness)
- **Color Blindness Simulation**: Visualize how images might appear to people with different types of color blindness
- **Detailed Results**: Comprehensive breakdown of test results with explanations
- **Result Sharing**: Share test results with others

## Architecture

The app is built using the MVVM (Model-View-ViewModel) architectural pattern with the following components:

- **Model**: Data classes representing test questions and results
- **View**: Jetpack Compose UI components
- **ViewModel**: Manages UI-related data and handles user interactions

The app uses Jetpack Navigation to handle screen transitions between:
1. Introduction screen
2. Test question screens
3. Results screen

## Technical Details

- **Kotlin**: 100% Kotlin codebase
- **Jetpack Compose**: Modern declarative UI toolkit
- **Material 3**: Latest Material Design components
- **ColorMatrix Filters**: Used to simulate different types of color blindness
- **Light/Dark Theme Support**: Adaptive themes for user comfort

## Image Assets

For the Ishihara test to work properly, you need to make sure the test plate images are in the correct location:

### Expected Image Files
The app expects to find 10 PNG files with Ishihara plates in one of these locations:
- `/app/src/main/assets/` (directly in assets folder)
- `/app/src/main/assets/images/` (in images subfolder)
- `/app/src/assests/images/` (in the custom assests folder)

### Required Image Files
The following image files are needed:
1. `Screenshot 2025-04-21 210205.png` (Number 12)
2. `Screenshot 2025-04-21 210222.png` (Number 8)
3. `Screenshot 2025-04-21 210248.png` (Number 29)
4. `Screenshot 2025-04-21 210301.png` (Number 5)
5. `Screenshot 2025-04-21 210321.png` (Number 3)
6. `Screenshot 2025-04-21 210341.png` (Number 15)
7. `Screenshot 2025-04-21 210355.png` (Number 74)
8. `Screenshot 2025-04-21 210417.png` (Number 6)
9. `Screenshot 2025-04-21 210429.png` (Number 45)
10. `Screenshot 2025-04-21 210443.png` (Number 16)

### Setup Help
To ensure all images are correctly copied to the assets folder, you can:
1. Run the included `copy_assets.bat` script (on Windows)
2. Manually copy all images to `/app/src/main/assets/images/`
3. Build the project again after copying the images

## Getting Started

To run this project:

1. Clone the repository
2. Open in Android Studio
3. Make sure Ishihara plate images are in the correct location (see Image Assets section)
4. Run on an emulator or physical device (minimum SDK 24 - Android 7.0)

## Disclaimers

This app provides an indication only and is not a medical diagnosis. If you are concerned about your color vision, please consult an eye care professional.

## License

This project is open-sourced under the MIT license.
