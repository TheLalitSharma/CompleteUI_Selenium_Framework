pipeline {
    agent any

    tools {
        maven 'mvn 3.6.3'
        jdk 'JDK'
    }

    parameters {
            choice(
                    name: 'EXECUTION_ENV',
                    choices: ['live', 'staging', 'test'],
                    description: 'Select environment'
            )
            choice(
                    name: 'EXECUTION_PROFILE',
                    choices: ['docker-grid', 'local-grid'],
                    description: 'Select the Maven Profile for configuration (URL, Timeouts, etc.)'
            )
            choice(
                        name: 'BROWSER',
                        choices: ['chrome', 'firefox'],
                        description: 'Target Browser'
                    )
            choice(
                    name: 'NODE_COUNT',
                    choices: ['2', '3', '4', '5'],
                    description: 'Scale: Number of browser containers'
            )
    }

    environment {
            COMPOSE_FILE = 'docker-compose.yml'
    }

    stages {
        stage('🧹 Clean Workspace & Environment') {
                steps {
                    script {
                        echo "Cleaning up previous docker containers..."
                        sh "docker compose -f ${COMPOSE_FILE} down"
                    }
                }
            }
        stage('⚙️ Setup Infrastructure') {
            steps {
                script {
                    echo "Scaling Grid to ${params.NODE_COUNT} ${params.BROWSER} nodes..."
                    sh "docker compose -f ${COMPOSE_FILE} up -d --scale ${params.BROWSER}=${params.NODE_COUNT}"
                    echo "Waiting for Grid to register nodes..."
                    sh "sleep 15"
                }
            }
        }

        stage('📢 Run Automation') {
            steps {
                script {
                    echo "Running tests using Profile: ${params.EXECUTION_PROFILE}"
                    withMaven(maven: 'mvn 3.6.3', jdk: 'JDK'){
                        sh "mvn clean test -P${params.EXECUTION_PROFILE} -Denv=${params.EXECUTION_ENV} -Dbrowser=${params.BROWSER} -Dmaven.test.failure.ignore=true"
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                echo 'Generating Reports...'

                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'reports',
                    reportFiles: 'ExtentReport.html',
                    reportName: 'HTML Report'
                ])


                echo 'Archiving Test Reports...'
                junit '**/target/surefire-reports/*.xml'

            }

        }
        cleanup {
            script {
                echo 'Tearing down Infrastructure...'
                sh "docker compose -f ${COMPOSE_FILE} down"
            }
        }
    }
}