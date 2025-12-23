# CompleteUI Selenium Framework

A comprehensive, production-ready Selenium WebDriver automation framework built with Java, TestNG, and Maven. This framework provides a robust foundation for UI automation testing with support for multiple browsers, environments, and execution modes.

## 🚀 Features

- **Multi-Browser Support**: Chrome, Firefox, and Safari
- **Multi-Environment Support**: Local, Test, Staging, and Live environments
- **Execution Modes**: Local and Remote (Selenium Grid) execution
- **Page Object Model (POM)**: Maintainable and scalable test architecture
- **ExtentReports Integration**: Rich HTML test reports with screenshots
- **TestNG Framework**: Advanced test execution with parallel execution support
- **Docker Support**: Selenium Grid setup using Docker Compose
- **CI/CD Ready**: Jenkins pipeline configuration included
- **Credential Management**: Secure credential handling with encryption support
- **Comprehensive Logging**: Log4j2 integration for detailed test execution logs
- **Retry Mechanism**: Automatic test retry on failure
- **Screenshot Capture**: Automatic screenshot capture on test failures
- **WebDriver Manager**: Automatic browser driver management

## 📋 Prerequisites

- **Java JDK 11** or higher
- **Maven 3.6+**
- **Docker** and **Docker Compose** (for remote execution)
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code)

## 🛠️ Technology Stack

- **Language**: Java 11
- **Build Tool**: Maven
- **Test Framework**: TestNG 7.11.0
- **WebDriver**: Selenium 4.35.0
- **Reporting**: ExtentReports 5.1.2
- **Logging**: Log4j2 2.21.1
- **Driver Management**: WebDriverManager 6.1.0
- **Configuration**: Properties files and YAML
- **Containerization**: Docker & Docker Compose

## 📁 Project Structure

```
CompleteUI_Selenium_Framework/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/medsky/automation/
│   │           ├── config/          # Configuration management
│   │           ├── core/            # Driver factory and manager
│   │           ├── enums/           # Enumerations
│   │           ├── listeners/       # TestNG and ExtentReports listeners
│   │           ├── managers/        # Credential managers
│   │           ├── models/          # Data models
│   │           ├── pages/           # Page Object Model classes
│   │           ├── reporters/       # ExtentReports setup
│   │           └── utils/           # Utility classes
│   └── test/
│       ├── java/
│       │   └── com/medsky/automation/tests/
│       │       ├── BaseTest.java    # Base test class
│       │       ├── login/           # Login test classes
│       │       └── dashboard/       # Dashboard test classes
│       └── resources/
│           ├── config/              # Environment configuration files
│           └── regression-suite.xml # TestNG suite configuration
├── docker-compose.yml               # Docker Compose for Selenium Grid
├── Jenkinsfile                      # Jenkins CI/CD pipeline
└── pom.xml                          # Maven dependencies

```

## ⚙️ Configuration

### Environment Configuration

The framework supports multiple environments. Configuration files are located in `src/test/resources/config/`:

- `config.properties` - Default configuration
- `config_local.properties` - Local environment
- `config_test.properties` - Test environment
- `config_staging.properties` - Staging environment
- `config_live.properties` - Live/production environment

### Setting Up Configuration

1. Update the `config.properties` file or create environment-specific files:

```properties
browser=chrome
runMode=local
headless=false
implicitWait=15
explicitWait=25
baseUrl=https://your-application-url.com
```

2. For remote execution, configure the Selenium Grid URL:

```properties
runMode=remote
remoteURL=http://localhost:4444/wd/hub
```

### Credentials Management

Credentials are stored in `src/main/resources/credentials.yaml`. The framework supports encrypted credentials using the `SmartCredentialsManager`.

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd CompleteUI_Selenium_Framework
```

### 2. Install Dependencies

```bash
mvn clean install
```

### 3. Run Tests Locally

**Using Maven:**

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=LoginTests

# Run with specific environment
mvn test -Denv=staging
```

**Using TestNG Suite:**

```bash
mvn test -Dsuite.file=src/test/resources/regression-suite.xml
```

### 4. Run Tests with Docker (Remote Execution)

**Start Selenium Grid:**

```bash
docker-compose up -d
```

**Verify Grid is Running:**

```bash
# Check hub status
open http://localhost:4444

# Check grid console
open http://localhost:4444/ui
```

**Run Tests:**

```bash
# Ensure config.properties has runMode=remote
mvn clean test
```

**Stop Selenium Grid:**

```bash
docker-compose down
```

## 📊 Test Reports

### ExtentReports

After test execution, ExtentReports are generated in:
- `reports/ExtentReport.html`

### TestNG Reports

TestNG reports are available in:
- `target/surefire-reports/`

### Logs

Execution logs are stored in:
- `target/logs/automation.log`

## 🔧 Framework Components

### BaseTest

All test classes extend `BaseTest`, which provides:
- Driver initialization and cleanup
- Environment configuration
- Common test setup and teardown

### Page Object Model

Pages are located in `src/main/java/com/medsky/automation/pages/`:
- `BasePage.java` - Base page with common methods
- `LoginPage.java` - Login page actions
- `HomePage.java` - Home page actions

### Utilities

Utility classes in `src/main/java/com/medsky/automation/utils/`:
- `WaitUtils.java` - Explicit wait utilities
- `ScreenshotUtils.java` - Screenshot capture
- `FileHelper.java` - File operations
- `YamlHelper.java` - YAML file operations
- `EncryptionHelper.java` - Encryption utilities
- `RetryProvider.java` - Test retry mechanism
- `TestUtils.java` - General test utilities

## 🐳 Docker Configuration

The framework includes Docker Compose configuration for Selenium Grid:

- **Selenium Hub**: Port 4444
- **Chrome Node**: Supports Chrome/Chromium
- **Firefox Node**: Supports Firefox

To customize, edit `docker-compose.yml`.

## 🔄 CI/CD Integration

### Jenkins Pipeline

The framework includes a `Jenkinsfile` for Jenkins CI/CD:

```groovy
pipeline {
    agent any
    stages {
        stage('Setup Infrastructure') {
            // Start Selenium Grid
        }
        stage('Run Automation') {
            // Execute tests
        }
    }
    post {
        always {
            // Cleanup and archive reports
        }
    }
}
```

## 📝 Writing Tests

### Example Test Class

```java
package com.medsky.automation.tests.login;

import com.medsky.automation.tests.BaseTest;
import com.medsky.automation.pages.LoginPage;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {
    
    @Test
    public void testSuccessfulLogin() {
        LoginPage loginPage = new LoginPage();
        loginPage.enterUsername("testuser")
                 .enterPassword("password")
                 .clickLogin();
        // Add assertions
    }
}
```

## 🔐 Security

- Credentials are stored in YAML format
- Support for encrypted credentials
- Environment-specific configuration files
- No hardcoded sensitive data

## 📈 Best Practices

1. **Page Object Model**: All page interactions are encapsulated in page classes
2. **Base Test Class**: Common setup/teardown logic in BaseTest
3. **Configuration Management**: Environment-specific configurations
4. **Logging**: Comprehensive logging for debugging
5. **Reporting**: Rich HTML reports with screenshots
6. **Retry Mechanism**: Automatic retry for flaky tests
7. **Parallel Execution**: TestNG parallel execution support

## 🐛 Troubleshooting

### Common Issues

1. **Driver not found**: Ensure WebDriverManager dependencies are resolved
2. **Grid connection failed**: Verify Docker containers are running
3. **Tests timeout**: Check wait times in configuration
4. **Report not generated**: Verify ExtentReports dependencies

### Debug Mode

Enable debug logging by updating `log4j2.xml`:

```xml
<Logger name="com.medsky.automation" level="DEBUG"/>
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👤 Author

**MedSky Automation Team**

## 🙏 Acknowledgments

- Selenium WebDriver community
- TestNG framework
- ExtentReports team
- WebDriverManager contributors

---

**Happy Testing! 🎉**

