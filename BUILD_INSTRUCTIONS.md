# Build Instructions

## Prerequisites

- Java 21 or higher
- Internet connection (for downloading dependencies)

## Building the Plugin

The project uses Gradle as its build system. You don't need to install Gradle separately - the Gradle Wrapper is included.

### On Linux/macOS:

```bash
./gradlew clean build
```

### On Windows:

```cmd
gradlew.bat clean build
```

## Output

After a successful build, you'll find the plugin JAR file at:

```
build/libs/EventCoreLib-1.0.0.jar
```

## Installing

1. Copy the JAR file from `build/libs/` to your server's `plugins/` directory
2. Start or restart your server
3. Configure the plugin in `plugins/EventCoreLib/config.yml`

## Troubleshooting

### "JAVA_HOME is not set"

Make sure Java 21 is installed and the JAVA_HOME environment variable is set:

```bash
# Linux/macOS
export JAVA_HOME=/path/to/java21

# Windows
set JAVA_HOME=C:\path\to\java21
```

### Build fails with "Unsupported class file major version"

You need Java 21 or higher to build this project. Check your Java version:

```bash
java -version
```

### Gradle wrapper fails to download

If you're behind a proxy or firewall, you may need to configure Gradle to use your proxy settings. Create or edit `gradle.properties`:

```properties
systemProp.http.proxyHost=your.proxy.host
systemProp.http.proxyPort=8080
systemProp.https.proxyHost=your.proxy.host
systemProp.https.proxyPort=8080
```

## Development

### IDE Setup

#### IntelliJ IDEA
1. Open the project folder in IntelliJ IDEA
2. The IDE will automatically detect it as a Gradle project
3. Wait for dependencies to download
4. You're ready to code!

#### Eclipse
1. Import as "Existing Gradle Project"
2. Select the project root directory
3. Wait for dependencies to download

#### VS Code
1. Install the "Extension Pack for Java"
2. Open the project folder
3. VS Code will detect it as a Gradle project

## Clean Build

To perform a clean build (removes all previous build artifacts):

```bash
./gradlew clean build
```

## Running Tests

Currently, this project doesn't include unit tests, but you can add them in `src/test/java/`.

To run tests:

```bash
./gradlew test
```
