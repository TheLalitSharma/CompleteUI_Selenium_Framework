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
                    name: 'CHROME_NODE_COUNT',
                    choices: ['2', '3', '4', '5'],
                    description: 'Number of Chrome containers to spin up'
            )
    }

    stages {
        stage('⚙️ Setup Infrastructure') {
            steps {
                script {
                    echo "Scaling Grid to ${params.CHROME_NODE_COUNT} nodes..."
                    sh "docker compose up -d --scale chromium=${params.CHROME_NODE_COUNT}"
                }
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