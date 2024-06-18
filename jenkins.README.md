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
2. In the `Definition` field under `Pipeline`, select `Pipeline script from SCM`.  
3. In the `SCM` field select `Git`.  
4. Enter the URL of the repository: `https://github.com/kevinstana/distributed.git`. The repo is public so no credentials are required.  
5. In the branch field, enter `/main`.  
6. In the `Script Path` field, type `Jenkinsfile` and hit `Save`.  

Now go to `/workspace/<JOB_NAME>/distributed`. Open the Jenkinsfile with an editor.  
In the `Deploy spring boot app` stage, change the value of `db_url` to the URL of the machine that the postgres database runs on.  
In the `Deploy frontend` stage, change the value of `backend_server_url` to the URL of the machine that the spring boot app runs on. Make sure to write the port as well.  

You can also change the `EMAIL_TO` variable to your email address to receive notifications about the job.  

## Pipeline for deployment with Ansible - Docker
1. Create a pipeline job.  
2. In the `Definition` field under `Pipeline`, select `Pipeline script from SCM`.  
3. In the `SCM` field select `Git`.  
4. Enter the URL of the repository: `https://github.com/kevinstana/distributed.git`. The repo is public so no credentials are required.
5. In the branch field, enter `/main`.  
6. In the `Script Path` field, type `docker.Jenkinsfile` and hit `Save`.

If you want to change the `EMAIL_TO` variable, go to `workspace/<JOB_NAME>/distributed` and open `docker.Jenkinsfile` with an editor.  

In case you don't have a github token to push docker images, you can comment the `Docker build and push` stage.  

## Pipeline for Kubernetes deployment
1. Create a pipeline job.  
2. In the `Definition` field under `Pipeline`, select `Pipeline script from SCM`.  
3. In the `SCM` field select `Git`.  
4. Enter the URL of the repository: `https://github.com/kevinstana/distributed.git`. The repo is public so no credentials are required.
5. In the branch field, enter `/main`.  
6. In the `Script Path` field, type `k8s.Jenkinsfile` and hit `Save`.  

If you want to change the `EMAIL_TO` variable, go to `workspace/<JOB_NAME>/distributed` and open `docker.Jenkinsfile` with an editor.  

In case you don't have a github token to push docker images, you can comment the `Docker build and push` stage.  
