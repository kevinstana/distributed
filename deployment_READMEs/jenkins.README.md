# Configuring the jenkins machine
The jenkins machine will run ansible playbooks. The `ansible_hosts` must be the same as the hosts in the `.ssh/config` file of the `jenkins user`.  
To gain access as the jenkins user:  
```bash
sudo su jenkins
cd
```
With `cd` we go to the home directory of the jenkins user.  
Now the `.ssh/config` file can be modified accordingly.
# Creating jenkins jobs
## Ansible job (required for the pipelines to work)
This job will be used by other jobs. It clones a repo containing ansible playbooks.  

1. Create a freestyle job.
2. Select `Git` from Source Code Management.  
3. Enter the URL of the repository:
```bash
https://github.com/kevinstana/devops-ansible.git
``` 

In the branch field, enter `/main` and press `Save`. That's it.  

## Pipeline for deployment with Ansible
Create a pipeline job.  
Then:  
```bash
https://github.com/kevinstana/distributed.git
```

![testtt](https://github.com/kevinstana/distributed/assets/122367928/db92bbe5-4936-4fc5-b971-b13b938047e4)  

![testttt](https://github.com/kevinstana/distributed/assets/122367928/9827f78b-1aaf-4525-94e3-2fa772bc16f4)     

## Pipeline for deployment with Ansible - Docker
Create a pipeline job. Then follow the steps from the ansible pipeline but write `docker.Jenkinsfile` instead of `Jenkinsfile` in the Script Path.  

## Pipeline for Kubernetes deployment
Create a pipeline job. Then follow the steps from the ansible pipeline but write `k8s.Jenkinsfile` instead of `Jenkinsfile` in the Script Path.
