pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '5'))
        ansiColor('xterm')
        disableConcurrentBuilds()
    }

    environment {
        APP_NAME = 'retech'
        SPRING_PROFILES_ACTIVE = 'test'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code from Git repository...'
                checkout scm
            }
        }

        stage('Environment Verification') {
            steps {
                script {
                    echo '=== System Environment Information ==='
                    if (isUnix()) {
                        sh 'java -version'
                        sh 'mvn -version'
                    } else {
                        bat 'java -version'
                        bat 'mvn -version'
                    }
                }
            }
        }

        stage('Compile') {
            steps {
                script {
                    echo 'Compiling Java 21 Spring Boot source code...'
                    if (isUnix()) {
                        sh 'mvn clean compile -DskipTests=true'
                    } else {
                        bat 'mvn clean compile -DskipTests=true'
                    }
                }
            }
        }

        stage('Test & Quality Gate') {
            steps {
                script {
                    echo 'Running JUnit 5 Unit and Integration Tests in isolated in-memory DB...'
                    if (isUnix()) {
                        sh 'mvn test'
                    } else {
                        bat 'mvn test'
                    }
                }
            }
            post {
                always {
                    echo 'Publishing JUnit test results...'
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                script {
                    echo 'Packaging Spring Boot executable JAR with embedded static frontend...'
                    if (isUnix()) {
                        sh 'mvn package -DskipTests=true'
                    } else {
                        bat 'mvn package -DskipTests=true'
                    }
                }
            }
        }

        stage('Archive Artifacts') {
            steps {
                echo 'Archiving built application JAR...'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, allowEmptyArchive: false
            }
        }
    }

    post {
        success {
            echo "SUCCESS: ReTech CI/CD Pipeline completed successfully for build #${env.BUILD_NUMBER}."
        }
        failure {
            echo "FAILURE: ReTech CI/CD Pipeline failed on build #${env.BUILD_NUMBER}. Please check test reports and logs."
        }
        cleanup {
            echo 'Cleaning workspace temporary artifacts...'
        }
    }
}
