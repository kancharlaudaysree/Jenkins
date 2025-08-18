pipeline {
  agent any

  options {
    timestamps()
  }

  tools {
    jdk   'JDK_17'    // must match Global Tool Config
    maven 'Maven_3.8.6'    // must match Global Tool Config
  }

  stages {
    stage('Checkout') {
      steps {
        git branch: 'main', url: 'https://github.com/kancharlaudaysree/Jenkins.git'
      }
    }

    stage('Build') {
      steps {
        bat 'mvn clean package -DskipTests'
      }
    }

    stage('Test') {
      steps {
        bat 'mvn test'
      }
      post {
        always {
          junit '**/target/surefire-reports/*.xml'
        }
      }
    }

    stage('Archive') {
      steps {
        archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
      }
    }
  }
   post {
        success {
            mail to: 'kancharlaudaysree@gmail.com',
                 subject: "SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                 body: "The build succeeded!"
        }
        failure {
            mail to: 'kancharlaudaysree@gmail.com',
                 subject: "FAILURE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                 body: "The build failed!"
        }
    }
}
