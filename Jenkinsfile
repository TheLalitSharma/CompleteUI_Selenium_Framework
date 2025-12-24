pipeline {
    agent any

    tools {
        maven 'mvn 3.6.3'
        jdk 'JDK'
    }

    parameters {
            choice(name: 'EXECUTION_ENV', choices: ['live', 'staging', 'test'], description: 'Select environment')
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
                withMaven(maven: 'mvn 3.6.3', jdk: 'JDK'){
                    sh "mvn clean test -Denv=${params.EXECUTION_ENV} -Dmaven.test.failure.ignore=true"
                }
            }
        }
    }

    post {
        always {
            script {

                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'reports',
                    reportFiles: 'ExtentReport.html',
                    reportName: 'HTML Report'
                ])

                echo 'Stopping and cleaning up Grid...'
                sh 'docker compose down'

                echo 'Archiving Test Reports...'
                junit '**/target/surefire-reports/*.xml'
            }

        }
    }
}