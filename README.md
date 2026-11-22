# RentIQ-Java
Java-based Vehicle Rental Management System

Welcome to the Java backend for **RentIQ**! This repository houses the core Java backend services, domain models, and business logic for the vehicle rental management platform.

---

## Prerequisites

Before building or running the project, ensure you have the following installed on your machine:

1. **Java Development Kit (JDK):**
   - **Version:** Java 26 (JDK 26.0.1+)
   - Verify by running:
     ```bash
     java -version
     javac -version
     ```
   - Make sure your `JAVA_HOME` environment variable points to your JDK 26 installation directory (e.g., `C:\Program Files\Java\jdk-26.0.1`).

2. **Apache Maven:**
   - **Version:** Apache Maven 3.9.0 or higher
   - Verify by running:
     ```bash
     mvn -version
     ```
   - *Note:* A Maven Wrapper (`mvnw` / `mvnw.cmd`) is also provided in `backend-java/`, allowing you to build and run the project even if Maven is not yet in your system PATH.

---

## Windows Maven Setup Guide (If `mvn` is not in PATH)

If running `mvn -version` displays:
```
'mvn' is not recognized as an internal or external command
```
Follow these steps to configure Maven on Windows:

1. **Locate your Maven directory:**
   - If you downloaded Apache Maven, note the extracted folder path (e.g., `C:\apache-maven-3.9.16` or `C:\Program Files\Maven`).
   - If using the pre-cached Maven on this system, it is located at:
     ```
     C:\Users\<YourUser>\.m2\wrapper\dists\apache-maven-3.9.16\0daed3be3ebd1c706f0e69e8b07c6b73f5cc4ea3dfce72a8d0ec2e849ca2ddb0
     ```

2. **Add Maven to your User PATH:**
   - Press `Win + R`, type `sysdm.cpl`, and press **Enter**.
   - Navigate to the **Advanced** tab and click **Environment Variables**.
   - Under **User variables**, select `Path` and click **Edit**.
   - Click **New** and add the path to the `bin` folder:
     ```
     <Maven_Directory>\bin
     ```
   - Click **OK** on all dialogs.
   - Restart your terminal/command prompt.

3. **Verify:**
   ```cmd
   mvn -version
   ```

---

## Project Structure

```
RentIQ-Java/
├── backend-java/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/rentiq/
│   │   │   │       └── Main.java       # Application entry point
│   │   │   └── resources/
│   │   └── test/
│   │       ├── java/
│   │       └── resources/
│   ├── .mvn/wrapper/                   # Maven wrapper configuration
│   ├── mvnw                            # Linux / macOS wrapper script
│   ├── mvnw.cmd                        # Windows wrapper script
│   └── pom.xml                         # Maven build configuration
├── database/
│   ├── schema.sql
│   └── sample-data.sql
└── README.md
```

---

## How to Compile & Run

Navigate to the `backend-java/` directory:

```bash
cd backend-java
```

### 1. Compile the Project

Using standard Maven:
```bash
mvn compile
```

Or using the Maven Wrapper:
- **Windows:**
  ```cmd
  mvnw.cmd compile
  ```
- **macOS / Linux:**
  ```bash
  ./mvnw compile
  ```

### 2. Run the Application

The project is configured with the `exec-maven-plugin` to launch `com.rentiq.Main`.

Using standard Maven:
```bash
mvn exec:java
```

Or using the Maven Wrapper:
- **Windows:**
  ```cmd
  mvnw.cmd exec:java
  ```
- **macOS / Linux:**
  ```bash
  ./mvnw exec:java
  ```

### Expected Output

```
Welcome to RentIQ - Vehicle Rental Management System
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### 3. Clean Build Artifacts

To delete compiled classes and the `target/` folder:
```bash
mvn clean
```
*(or `mvnw.cmd clean`)*
