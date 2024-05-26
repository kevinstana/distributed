pipeline {
    agent any

    // options {
    //     buildDiscarder(logRotator(numToKeepStr: '30', artifactNumToKeepStr: '30'))
    // }

    environment {
        DOCKER_TOKEN = credentials('docker-push-secret')
        DOCKER_USER = 'kevinstana'
        DOCKER_SERVER = 'ghcr.io'
        DOCKER_PREFIX = 'ghcr.io/kevinstana/devops-backend'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'git@github.com:kevinstana/distributed.git'
            }
        }
        // stage('Test') {
        //     steps {
        //         sh './mvnw test'
        //     }
        // }
        stage('run ansible pipeline') {
            steps {
                build job: 'ansible'
            }
        }

        stage('Install project with docker compose') {
                    steps {
                        sh '''
                            export ANSIBLE_CONFIG=~/workspace/ansible/ansible.cfg
                            ansible-playbook -i ~/workspace/ansible/hosts.yaml -l docker ~/workspace/ansible/playbooks/spring-angular-docker.yaml
                        '''
                    }
         }
         
        stage('Docker build and push') {
            steps {
                sh '''
                    HEAD_COMMIT=$(git rev-parse --short HEAD)
                    TAG=$HEAD_COMMIT-$BUILD_ID
                    docker build --rm -t $DOCKER_PREFIX:$TAG -t $DOCKER_PREFIX:latest -f Dockerfile .
                    echo $DOCKER_TOKEN | docker login $DOCKER_SERVER -u $DOCKER_USER --password-stdin
                    docker push $DOCKER_PREFIX --all-tags
                '''
            }
        }
    }
}