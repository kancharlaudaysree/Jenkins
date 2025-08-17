pipeline {
  agent any

  options {
    timestamps()
    ansiColor('xterm')
  }

  tools {
    jdk   'JDK_17'
    maven 'Maven_3.8.6'    // Remove if you use Gradle wrapper only
  }

  parameters {
    choice(name: 'ENVIRONMENT', choices: ['dev','qa','prod'], description: 'Deploy target')
    booleanParam(name: 'RUN_TESTS', defaultValue: true, description: 'Run unit tests?')
  }

  environment {
    SONARQUBE = 'MySonarQube' // Manage Jenkins → System → SonarQube servers (name)
    // Example: DEPLOY_HOST, DEPLOY_USER can be defined here or as credentials
  }

  stages {

    stage('Checkout') {
      steps {
        git branch: 'main', url: 'https://github.com/your-org/your-repo.git'
      }
    }

    stage('Build & Test (Parallel)') {
      parallel {
        stage('Build') {
          steps {
            script {
              if (isUnix()) {
                // Maven path (Linux)
                sh 'mvn -B -DskipTests clean package'
                // Gradle alternative (Linux): sh './gradlew clean build -x test'
              } else {
                // Maven path (Windows)
                bat 'mvn -B -DskipTests clean package'
                // Gradle alternative (Windows): bat 'gradlew.bat clean build -x test'
              }
            }
          }
        }

        stage('Test') {
          when { expression { return params.RUN_TESTS } }
          steps {
            script {
              if (isUnix()) {
                sh 'mvn -B test'
                // Gradle: sh './gradlew test'
              } else {
                bat 'mvn -B test'
                // Gradle: bat 'gradlew.bat test'
              }
            }
          }
          post {
            always {
              // Publish JUnit results even if tests fail
              junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml, **/build/test-results/test/*.xml'
            }
          }
        }
      }
    }

    stage('SonarQube') {
      steps {
        withSonarQubeEnv("${env.SONARQUBE}") {
          script {
            if (isUnix()) {
              sh 'mvn -B sonar:sonar'
              // OR: sh "./gradlew sonarqube -Dsonar.login=${SONAR_AUTH_TOKEN}" (if configured)
            } else {
              bat 'mvn -B sonar:sonar'
              // OR: bat "gradlew.bat sonarqube -Dsonar.login=%SONAR_AUTH_TOKEN%"
            }
          }
        }
      }
    }

    // (Optional) Enforce Quality Gate
    stage('Quality Gate') {
      steps {
        timeout(time: 5, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }

    stage('Package Artifact') {
      steps {
        script {
          if (isUnix()) {
            sh 'mvn -B -DskipTests package'
            // Gradle JAR: sh './gradlew jar'  (WAR: './gradlew war')
          } else {
            bat 'mvn -B -DskipTests package'
            // Gradle JAR: bat 'gradlew.bat jar'
          }
        }
      }
    }

    stage('Archive Artifacts') {
      steps {
        archiveArtifacts artifacts: 'target/*.jar, target/*.war, build/libs/*.jar, build/libs/*.war', fingerprint: true
      }
    }

    stage('Deploy') {
      when {
        branch 'main'
      }
      steps {
        echo "Deploying to ${params.ENVIRONMENT}..."
        // Linux example:
        // sh 'scp target/*.jar user@server:/opt/apps/'
        // Windows example:
        // bat 'powershell Copy-Item -Path target\\*.jar -Destination \\\\server\\share\\apps\\ -Force'
      }
    }
  }

  post {
    success { echo '✅ Pipeline successful' }
    failure { echo '❌ Pipeline failed' }
    always  { echo 'Done.' }
  }
}
