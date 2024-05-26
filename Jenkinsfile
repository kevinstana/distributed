pipeline {
    agent any

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
        // stage('install ansible prerequisites') {
        //     steps {
        //         sh '''
        //             ansible-galaxy install geerlingguy.postgresql
        //         '''
        //     }
        // }
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
                   # replace db in host_vars
                    # sed -i 's/dbserver/4.211.249.239/g' ~/workspace/ansible/host_vars/appserver-vm.yaml
                   # replace workingdir in host_vars
                    # sed -i 's/vagrant/azureuser/g' ~/workspace/ansible/host_vars/appserver-vm.yaml
                '''
                sh '''
                    # edit host var for appserver

                    export ANSIBLE_CONFIG=~/workspace/ansible/ansible.cfg
                    ansible-playbook -i ~/workspace/ansible/hosts.yaml -l backend ~/workspace/ansible/playbooks/spring.yaml
                '''
            }
        }
       stage('Deploy frontend') {
            steps {
                sh '''
                    export ANSIBLE_CONFIG=~/workspace/ansible/ansible.cfg
                    ansible-playbook -i ~/workspace/ansible/hosts.yaml -l frontend ~/workspace/ansible/playbooks/angular.yaml
                '''
            }
       }
    }
}
