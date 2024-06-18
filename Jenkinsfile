pipeline {
    agent any

    environment {
        EMAIL_TO = "${MY_EMAIL}"
    }

    stages {
        stage('Checkout') {
            steps {
                // git branch: 'main', url: 'git@github.com:kevinstana/distributed.git'
                git branch: 'main', url: 'https://github.com/kevinstana/distributed.git'
            }
        }
        stage('Test') {
            steps {
                sh './mvnw test'
            }
        }
        stage('run ansible pipeline') {
            steps {
                build job: 'ansible'
            }
        }
        stage('Install postgres') {
            steps {
                sh '''
                    export ANSIBLE_CONFIG=~/workspace/ansible/ansible.cfg
                    ansible-playbook -i ~/workspace/ansible/hosts.yaml -l db ~/workspace/ansible/playbooks/postgres.yaml
                '''
            }
        }

        stage('Deploy spring boot app') {
            steps {
                sh '''
                    # edit host var for appserver

                    export ANSIBLE_CONFIG=~/workspace/ansible/ansible.cfg
                    # ansible-playbook -i ~/workspace/ansible/hosts.yaml -l backend -e db_url=13.94.118.26 ~/workspace/ansible/playbooks/spring.yaml
                    ansible-playbook -i ~/workspace/ansible/hosts.yaml -l backend -e db_url=${DB_URL} ~/workspace/ansible/playbooks/spring.yaml
                '''
            }
        }
       stage('Deploy frontend') {
            steps {
                sh '''
                    export ANSIBLE_CONFIG=~/workspace/ansible/ansible.cfg
                    # ansible-playbook -i ~/workspace/ansible/hosts.yaml -l frontend -e backend_server_url=http://13.94.118.26:9090 ~/workspace/ansible/playbooks/angular.yaml
                    ansible-playbook -i ~/workspace/ansible/hosts.yaml -l frontend -e backend_server_url=${BACKEND_URL} ~/workspace/ansible/playbooks/angular.yaml
                '''
            }
       }
    }


    post {
        always {
            mail  to: "${EMAIL_TO}", body: "Project ${env.JOB_NAME} <br>, Build status ${currentBuild.currentResult} <br> Build Number: ${env.BUILD_NUMBER} <br> Build URL: ${env.BUILD_URL}", subject: "JENKINS: Project name -> ${env.JOB_NAME}, Build -> ${currentBuild.currentResult}"
        }
    }
}
