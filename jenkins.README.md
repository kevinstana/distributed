# Configuring jenkins machine
The Jenkinsfile will run ansible playbooks. The `ansible_hosts` must be the same as the hosts in the `.ssh/config` file of the `jenkins user`.  
To gain access as the jenkins user, run the following commands:  
```bash
sudo su jenkins
cd
```
With `cd` we go to the home directory of the jenkins user.  
Now the `.ssh/config` file can be modified accordingly.
# Creating jenkins jobs
## Ansible job
This job will be used by other jobs. It clones a repo containing ansible playbooks.  

1. Create a freestyle job.
2. Select `Git` from Source Code Management.  
3. Enter the URL of the repository: `https://github.com/kevinstana/devops-ansible.git`. The repo is public so no credentials are required.  
4. In the branch field, enter `/main` and press `Save`. That's it.  

## Pipeline for deployment with Ansible
1. Create a pipeline job.
2. In the `General` section, select `This project is parameterized`.
3. Click on `Add parameter` and select `String parameter`. Name it `DB_URL` with default value the URL of the machine the database runs on.  
4. Add another string parameter. Name it `BACKEND_URL` with default value the URL (include http://) of the machine that spring runs on.
5. Add another string parameter. Name it `MY_EMAIL` with default value the email you want to get notifications about the job.
6. In the `Definition` field under `Pipeline`, select `Pipeline script from SCM`.  
7. In the `SCM` field select `Git`.  
8. Enter the URL of the repository: `https://github.com/kevinstana/distributed.git`. The repo is public so no credentials are required.  
9. In the branch field, enter `/main`.  
10. In the `Script Path` field, type `Jenkinsfile` and hit `Save`.

To run the job click on `Build with parameters`.    

## Pipeline for deployment with Ansible - Docker
1. Create a pipeline job.
2. In the `General` section, select `This project is parameterized`.  
3. Click on `Add parameter` and select `String parameter`. Name it `MY_EMAIL` with default value the he email you want to get notifications about the job.  
4. In the `Definition` field under `Pipeline`, select `Pipeline script from SCM`.  
5. In the `SCM` field select `Git`.  
6. Enter the URL of the repository: `https://github.com/kevinstana/distributed.git`. The repo is public so no credentials are required.
7. In the branch field, enter `/main`.  
8. In the `Script Path` field, type `docker.Jenkinsfile` and hit `Save`.

To run the job click on `Build with parameters`.   

## Pipeline for Kubernetes deployment
1. Create a pipeline job.
2. In the `General` section, select `This project is parameterized`.
3. Click on `Add parameter` and select `String parameter`. Name it `MY_EMAIL` with default value the he email you want to get notifications about the job.  
4. In the `Definition` field under `Pipeline`, select `Pipeline script from SCM`.  
5. In the `SCM` field select `Git`.  
6. Enter the URL of the repository: `https://github.com/kevinstana/distributed.git`. The repo is public so no credentials are required.
7. In the branch field, enter `/main`.  
8. In the `Script Path` field, type `k8s.Jenkinsfile` and hit `Save`.  

To run the job click on `Build with parameters`.   
