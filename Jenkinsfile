pipeline {
    agent {
        docker {
            image 'maven:3-alpine'
            options '-p 8082:8082'
            args '-v /root/.m2:/root/.m2'
        }
    }
    options {
        skipStagesAfterUnstable()
    }
    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                sh 'mvn -B -DskipTests clean package'
                echo 'Building finish'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
                sh 'mvn test'
                echo 'Testing finish'
            }
        }
        stage('Deploy') {
            steps {
                withEnv(['JENKINS_NODE_COOKIE=dontkill']) {
                    echo 'running applications..'
                    sh 'java -jar target/auth-course-0.0.1-SNAPSHOT.jar'
                }
            }
        }
    }
}