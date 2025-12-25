# Building EventCoreLib

## Prerequisites

- Java Development Kit (JDK) 21 or higher
- Git

## Build Instructions

### Linux/macOS

```bash
git clone https://github.com/Smughito/EventCoreLib.git
cd EventCoreLib
chmod +x gradlew
./gradlew shadowJar
```

### Windows

```cmd
git clone https://github.com/Smughito/EventCoreLib.git
cd EventCoreLib
gradlew.bat shadowJar
```

## Output

The compiled plugin JAR file will be located at:
```
build/libs/EventCoreLib-1.0.0.jar
```

## Installing JDK 21

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install openjdk-21-jdk
```

### macOS (using Homebrew)
```bash
brew install openjdk@21
```

### Windows
Download and install from: https://adoptium.net/

## GitHub Actions

This project includes a GitHub Actions workflow that automatically builds the plugin when code is pushed to the repository. The built JAR file will be available as an artifact in the Actions tab.

## Troubleshooting

### "JAVA_HOME is not set" error
Set the JAVA_HOME environment variable:

**Linux/macOS:**
```bash
export JAVA_HOME=/path/to/jdk-21
export PATH=$JAVA_HOME/bin:$PATH
```

**Windows:**
```cmd
set JAVA_HOME=C:\path\to\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%
```

### Gradle build fails
Try cleaning the build:
```bash
./gradlew clean
./gradlew shadowJar
```
