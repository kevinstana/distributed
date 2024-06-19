# Configuring the jenkins machine
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
3. Enter the URL of the repository:
```bash
https://github.com/kevinstana/devops-ansible.git
``` 

In the branch field, enter `/main` and press `Save`. That's it.  

## Pipeline for deployment with Ansible
First create a pipeline job. Then do the following:  

![test](https://github.com/kevinstana/distributed/assets/122367928/115dd3a7-046d-41d9-a0b3-7d4e206d17ee)  
  
Create another two string parameters  

![testt](https://github.com/kevinstana/distributed/assets/122367928/8f3c6ae1-b23e-405f-8644-5de3a17c8b14)  

Then:  
```bash
https://github.com/kevinstana/distributed.git
```

![testtt](https://github.com/kevinstana/distributed/assets/122367928/db92bbe5-4936-4fc5-b971-b13b938047e4)  

![testttt](https://github.com/kevinstana/distributed/assets/122367928/9827f78b-1aaf-4525-94e3-2fa772bc16f4)  


To run the job click on `Build with parameters`.    

## Pipeline for deployment with Ansible - Docker
First create a pipeline job.  

Then follow the steps from the previous images BUT only create one string parameter, the MY_EMAIL, and make sure to write `docker.Jenkinsfile` instead of `Jenkinsfile` in the Script Path.  

Don't forget the token to push docker images (docker-push-secret, as secret text).  

To run the job click on `Build with parameters`.   

## Pipeline for Kubernetes deployment
First create a pipeline job.  

Then follow the steps from the previous images BUT only create one string parameter, the MY_EMAIL, and make sure to write `k8s.Jenkinsfile` instead of `Jenkinsfile` in the Script Path.  

Don't forget the token to push docker images (docker-push-secret, as secret text).  

To run the job click on `Build with parameters`. 
