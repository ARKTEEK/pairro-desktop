# Pairro for Desktop

Pairro for Desktop is a Java-based desktop application designed to work with [Pairro for Android](https://github.com/arkteek/pairro-android). It allows users to remotely control their desktop environment's mouse, keyboard, clipboard, and audio directly from an Android device with minimal latency.

## Features
- **Mouse Control**: Real-time cursor movement, left/right clicks, and scrolling.
- **Keyboard Input**: Send keystrokes and text directly to the host machine.
- **Clipboard Synchronization**: Share and sync clipboard text between mobile and desktop devices.
- **Volume Management**: Adjust host device system volume remotely.

## Prerequisites
- **Java Runtime Environment (JRE)**: Java 17 or higher recommended.
- **Pairro for Android**: The mobile application required to control this host. Download the latest APK release from [HERE](https://github.com/arkteek/pairro-android).


## Getting Started

### Installation
1. Download the latest release from the [Releases](https://github.com/arkteek/pairro-desktop/releases) section.
2. Ensure Java is installed and added to your system PATH:
   ```bash
   java -version

### Pairing & Usage
1. Launch Pairro on your Desktop device.
2. Launch Pairro on your Android device.
3. Turn on Discoverability on both devices.
4. Select Desktop device you want to pair with on your Android device.
5. Verify codes match on Desktop and Android devices and accept Android device's pairing request.
6. After that you will be able to connect to paired Desktop device and control desired peritherals using your Android device.
