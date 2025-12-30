## CompleteUI Selenium Framework

**CompleteUI Selenium Framework** is a clean, production‑ready UI automation framework built from the ground up with **Java**, **Selenium WebDriver**, **TestNG**, and **Maven**.  
It is designed as a starter kit for modern UI test automation: opinionated where it matters, but easy to extend for your own project.

---

### 🚀 Key Features

- **Multi‑browser execution**: Chrome, Firefox, Safari (local & remote)
- **Multiple environments**: Local, Test, Staging, Live
- **Local & Selenium Grid modes**: Switch via simple config (`runMode=local|remote`)
- **Page Object Model (POM)**: Clean separation of test logic and UI interactions
- **Rich reporting**: ExtentReports HTML report with screenshots
- **Parallel execution**: TestNG suite with parallel methods
- **Resilient tests**: Explicit waits, retry mechanism, screenshot on failure
- **Centralized configuration**: Properties + YAML for env and credentials
- **Logging**: Log4j2 + SLF4J for detailed debugging
- **Docker & CI ready**: Docker Compose for Grid, Jenkins pipeline provided

---

### 📋 Prerequisites

- Java **11** or higher  
- Maven **3.6+**  
- Docker & Docker Compose (for Selenium Grid)  
- Any Java IDE (IntelliJ IDEA, Eclipse, VS Code)

---

### 🛠 Tech Stack

- **Language**: Java 11  
- **Build**: Maven  
- **Test Framework**: TestNG  
- **WebDriver**: Selenium 4  
- **Drivers**: WebDriverManager  
- **Reporting**: ExtentReports  
- **Logging**: Log4j2 + SLF4J  
- **Containers**: Docker + Docker Compose  

---

### 📁 Project Layout

```text
src/
  main/java/com/medsky/automation/
    config/      # Config reader and environment helpers
    core/        # DriverFactory, DriverManager
    enums/       # Enums (e.g. user types)
    listeners/   # TestNG + Extent listeners
    managers/    # Credential and other managers
    models/      # Data models
    pages/       # Page Objects (BasePage, LoginPage, HomePage, ...)
    reporters/   # ExtentReports setup
    utils/       # Wait, screenshot, file, encryption, YAML, etc.

  test/java/com/medsky/automation/tests/
    BaseTest.java
    login/       # Login test classes
    dashboard/   # Dashboard/product tests

  test/resources/
    config/                  # *.properties per environment
    regression-suite.xml     # TestNG suite

docker-compose.yml           # Selenium Grid (hub + nodes)
Jenkinsfile                  # Jenkins pipeline
pom.xml                      # Maven configuration
```

---

### ⚙️ Configuration

Environment configs live under `src/test/resources/config/`:

- `config.properties` – default/base config  
- `config_local.properties` – local execution  
- `config_test.properties` – test env  
- `config_staging.properties` – staging env  
- `config_live.properties` – production/live  

Basic example:

```properties
browser=chrome
runMode=local       # local | remote
headless=false
implicitWait=15
explicitWait=25
baseUrl=https://your-app-url.com
```

For Selenium Grid:

```properties
runMode=remote
remoteURL=http://localhost:4444/wd/hub
```

**Credentials** are stored in `src/main/resources/credentials.yaml` and handled through a credentials manager with optional encryption.

---

### 🚀 Getting Started

```bash
git clone <repository-url>
cd CompleteUI_Selenium_Framework

# build & run all tests
mvn clean test
```

Run a specific test class:

```bash
mvn test -Dtest=LoginTests
```

Run against a specific environment:

```bash
mvn test -Denv=staging
```

Use the TestNG suite:

```bash
mvn test -Dsuite.file=src/test/resources/regression-suite.xml
```

---

### 🌐 Remote Execution with Docker (Selenium Grid)

Start Grid:

```bash
docker-compose up -d
```

Run tests with `runMode=remote`:

```bash
mvn clean test
```

Stop Grid:

```bash
docker-compose down
```

---

### 📊 Reports & Logs

- **ExtentReports**: `target/reports/ExtentReport.html`  
- **TestNG reports**: `target/surefire-reports/`  
- **Logs**: `target/logs/automation.log`  

---

### 🧩 Maven Profiles & Execution Modes

This project uses **Maven profiles** to drive how and where tests run by wiring system properties into TestNG/Selenium:

- **`local-grid`** (default)  
  - `gridURL=http://localhost:4444`  
  - `runMode=remote`, `headless=true`  
  - Ideal for running against a local Selenium Grid (e.g. via `docker-compose`).  

- **`docker-grid`**  
  - `gridURL=http://host.docker.internal:4444`  
  - `runMode=remote`, `headless=true`  
  - Useful when the tests themselves are running in a Docker container and need to talk to a Grid on the host machine.  

- **`aws-grid`**  
  - `gridURL` points to a remote AWS Selenium Grid (EC2)  
  - `runMode=remote`, `headless=true`  
  - Demonstrates how to execute the same suite against cloud infrastructure.  

Example commands:

```bash
# default profile (local-grid)
mvn clean test

# run against Docker-based Grid
mvn clean test -P docker-grid

# run against AWS Grid
mvn clean test -P aws-grid
```

Under the hood, these profiles populate `gridURL` and `runMode` as system properties in the Surefire plugin, which are then consumed by `ConfigReader` and `DriverFactory` to decide **where** and **how** WebDriver instances are created.

---

### 🔧 Core Framework Pieces

- **BaseTest** (`com.medsky.automation.tests.BaseTest`)  
  - Sets up and tears down WebDriver  
  - Logs environment, base URL, and browser  

- **DriverFactory / DriverManager** (`com.medsky.automation.core`)  
  - Handles local vs remote driver creation  
  - Central place to change browser options/capabilities  

- **Pages** (`com.medsky.automation.pages`)  
  - `BasePage` with common helpers  
  - `LoginPage`, `HomePage`, etc. implement flows and actions  

- **Utils** (`com.medsky.automation.utils`)  
  - `WaitUtils`, `ScreenshotUtils`, `FileHelper`, `YamlHelper`, `EncryptionHelper`, `RetryProvider`, `TestUtils`  

---

### 🔄 CI/CD

A sample **Jenkins pipeline** (`Jenkinsfile`) is included:

- Starts Selenium Grid using Docker Compose  
- Runs `mvn clean test`  
- Tears down the Grid  
- Publishes JUnit/TestNG XML reports  

You can plug this repository directly into Jenkins and point it at the `Jenkinsfile`.

The project structure and Maven commands are also **GitHub Actions–friendly**: a typical workflow would:

- Check out the code  
- Set up Java and Maven cache  
- (Optionally) start Selenium Grid using Docker  
- Run the same Maven commands as above (`mvn clean test -P <profile>`)  
- Upload `target/surefire-reports` and `target/reports/ExtentReport.html` as build artifacts  

This makes it easy to showcase **local, Docker, and cloud (AWS) execution** off the same codebase in any CI system.

---

### 🐛 Troubleshooting

- **Drivers not found**  
  - Run `mvn dependency:tree` and ensure WebDriverManager is correctly resolved.  

- **Cannot connect to Grid**  
  - Check `docker ps` and verify hub + nodes are running.  

- **No reports generated**  
  - Make sure tests actually executed and verify ExtentReports/TestNG output paths.  

- **Timeouts / flaky tests**  
  - Tune `implicitWait` / `explicitWait` and consider adjusting waits in `WaitUtils`.  

---

### 📌 Notes

- This repository is intended as a **template** for modern UI automation projects.  
- Fork it, rename packages, and adapt pages/tests to your application under test.  

**Happy testing!**


