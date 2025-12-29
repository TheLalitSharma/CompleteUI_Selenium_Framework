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
                    description: 'Target App Environment'
            )
            choice(
                    name: 'EXECUTION_PROFILE',
                    choices: ['docker-grid', 'local', 'aws-grid'],
                    description: 'Where to run?'
            )
            choice(
                    name: 'BROWSER',
                    choices: ['chrome', 'firefox'],
                    description: 'Target Browser'
                    )
            choice(
                    name: 'NODE_COUNT',
                    choices: ['1', '2', '3', '4', '5'],
                    description: 'Docker Grid only'
            )
    }

    environment {
            COMPOSE_FILE = 'docker-compose.yml'
            AWS_IP = "13.50.249.217"
            AWS_USER = "ec2-user"
            SSH_CRED_ID = "aws-grid-key"
            DOCKER_GRID_URL = 'http://host.docker.internal:4444'
    }

    stages {
        stage('🧹 Clean Workspace & Environment') {
                steps {
                    script {
                        echo "Cleaning up previous docker containers..."
                        if (params.EXECUTION_PROFILE == 'docker-grid') {
                            sh "docker compose -f ${COMPOSE_FILE} down --remove-orphans"
                        } else if (params.EXECUTION_PROFILE == 'aws-grid') {
                            sshagent([env.SSH_CRED_ID]) {
                                sh "ssh -o StrictHostKeyChecking=no ${env.AWS_USER}@${env.AWS_IP} 'docker compose -f ${COMPOSE_FILE} down --remove-orphans'"
                            }
                        }
                    }
                }
            }
        stage('⚙️ Setup Infrastructure') {
            steps {
                script {
                    if (params.EXECUTION_PROFILE == 'docker-grid') {
                                            echo "Provisioning Docker Grid Infrastructure..."
                                            sh "docker compose -f ${COMPOSE_FILE} --profile ${params.BROWSER} up -d --scale ${params.BROWSER}=${params.NODE_COUNT}"
                    } else if (params.EXECUTION_PROFILE == 'aws-grid') {
                                              echo "Provisioning REMOTE AWS Infrastructure..."
                                              sshagent([env.SSH_CRED_ID]) {
                                                  sh "ssh ${env.AWS_USER}@${env.AWS_IP} 'docker compose -f ${COMPOSE_FILE} --profile ${params.BROWSER} up -d --scale ${params.BROWSER}=${params.NODE_COUNT}'"
                                              }
                                          }

                    echo "Waiting for Grid Readiness..."
                                def gridUrl = (params.EXECUTION_PROFILE == 'aws-grid') ? "http://${env.AWS_IP}:4444" : "${env.DOCKER_GRID_URL}"

                                sh """
                                    timeout 60s bash -c 'until curl -s ${gridUrl}/status | grep -q "\\"ready\\": true"; do
                                        echo "Grid not ready yet... retrying in 2s"
                                        sleep 2
                                    done'
                                """
                                echo "Grid is UP and Nodes are registered!"
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
                if (params.EXECUTION_PROFILE == 'docker-grid') {
                                    sh "docker compose -f ${COMPOSE_FILE} down"
                                } else if (params.EXECUTION_PROFILE == 'aws-grid') {
                                    sshagent([env.SSH_CRED_ID]) {
                                        sh "ssh ${env.AWS_USER}@${env.AWS_IP} 'docker compose -f ${COMPOSE_FILE} down'"
                                    }
                                }
            }
        }
    }
}