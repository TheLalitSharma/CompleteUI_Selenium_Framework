pipeline {
    agent any

    tools {
        maven 'mvn 3.6.3'
        jdk 'JDK'
    }

    stages {
        stage('⚙️ Setup Infrastructure') {
            steps {
                echo 'Starting Selenium Grid...'
                sh 'docker compose up -d'
            }
        }

        stage('📢 Run Automation') {
            steps {
                echo 'Executing Selenium Tests...'
                sh 'mvn clean test'
            }
        }
    }

    post {
        always {
            echo 'Stopping and cleaning up Grid...'
            sh 'docker compose down'

            echo 'Archiving Test Reports...'
            junit '**/target/surefire-reports/*.xml'
        }
    }
}