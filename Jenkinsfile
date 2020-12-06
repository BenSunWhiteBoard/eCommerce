pipeline {
    agent any
//     agent {
//         docker {
//             image 'jenkinsci/blueocean'
//             options '--rm -u root -d --name jenkins -p 8080:8080
//                     -v jenkins-data:/var/jenkins_home
//                     -v /var/run/docker.sock:/var/run/docker.sock'
//         }
//     }
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
                echo 'running applications..'
                sh 'java -jar target/auth-course-0.0.1-SNAPSHOT.jar'
                echo 'stop running'
            }
        }
    }
}