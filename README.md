# GGConnect

GGConnect is an Android social application tailored specifically for gamers, designed to facilitate communication, community building, and interaction among users who share common gaming interests.

## Features

- **Chat System:** Supports private messaging between users and public game-specific channels.
- **Realtime Messaging:** Powered by Firebase Realtime Database, ensuring instantaneous communication.
- **User Feed:** Displays recent gaming activities such as when friends like a game, creating an interactive and engaging community.
- **Favorite Games Integration:** Selecting favorite games automatically adds users to corresponding game-specific channels, enabling users to easily connect with other fans and participate in relevant discussions.
- **Dynamic Profile Images:** Automatically fetches user profile images from Firestore, while game channel images are managed statically.
- **Chat Previews:** Chat list displays the last messages with timestamps, sorted to show recent conversations first.
- **Custom Chat UI:** Visually distinct chat bubbles, with unique colors for sent and received messages, providing clear and enjoyable chat experiences.
- **Channel Management:** Efficiently manages user memberships by automatically updating channel subscriptions based on favorite game selections.
- **ViewModel and LiveData Integration:** Utilizes ViewModel and LiveData components to ensure efficient data handling, seamless UI updates, and a reactive user experience.

## Technology Stack

- **Kotlin:** Modern and efficient Android app development.
- **Firebase Firestore:** Persistent storage for user-related data including profile information and game preferences.
- **Firebase Realtime Database:** Handles all chat functionalities for real-time interactions.
- **Jetpack Components:** Employs ViewModel, LiveData, and Navigation components to support a robust and maintainable app architecture.

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/afiksoco/GGConnect.git
cd GGConnect
```

### Build and Run

Use the provided Gradle wrapper:

- **Unix/macOS:**

```bash
./gradlew build
./gradlew installDebug
```

- **Windows:**

```bash
gradlew.bat build
gradlew.bat installDebug
```

Open and run the project using Android Studio or your preferred IDE.

## License

GGConnect is licensed under the MIT License. See the LICENSE file for details.

