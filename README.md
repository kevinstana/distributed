# distributed
Project for Distributed Systems course

## Download the repository (master branch)
### *Option 1*
1. Download the .zip file  
2. Extract folder  
3. Import folder to your IDE of choice  
### *Option 2*
In terminal, use
```
git clone https://github.com/kevinstana/distributed
```
To view all the branches use  
```
git branch
```
If you are not on the master branch (the * indicates the current branch) use
```
git switch master
```
Import the folder to your IDE of choice  
### *Option 3* (IntelliJ IDEA)  
1. Open IntelliJ IDEA  
2. From the top left select **File>New>Project from Version Control**  
3. Paste the repository link in the "URL" field: **https://github.com/kevinstana/distributed**  
4. Select directory to download the repository  
5. Click "Clone" on the bottom right  
## Run a postgres database using docker
### *Create database (with initial data)*
```bash
docker run --name distributed_container --rm -e POSTGRES_PASSWORD=password -e POSTGRES_DB=it21774_distributed -h localhost -p 5432:5432 -v "$pwd"/assets/db:/docker-entrypoint-initdb.d -v pgdata14:/var/lib/postgresql/data -d postgres:14
```
### *Create database (no initial data)*
```bash
docker run --name distributed_container --rm -e POSTGRES_PASSWORD=password -e POSTGRES_DB=it21774_distributed -h localhost -p 5432:5432 -v pgdata14:/var/lib/postgresql/data -d postgres:14
```
## Add helper files to Postman
1. Open Postman  
2. From the left sidebar, select "Environments"  
3. From "Environments", move the cursor a bit to the right and then up. You should see an option for "Import". Click it  
4. From the window that appears, select "File" from the top left corner  
5. Click on "Choose files" in the middle of the window  
6. Navigate to the directory you downloaded the repository  
7. Select "assets/postman/SpringBootEnv.postman_environment.json"  
8. Click on "Import". Hover over the new environment and click on the checkmark to activate it  
  
Now for the 2nd file
  
1. From the left sidebar, select "Collections"  
2. From "Collections" move the cursor a bit to the right and then up. You should see an option for "Import". Click it  
3. From the window that appears, select "File" from the top left corner  
4. Click on "Choose files" in the middle of the window  
5. Navigate to the directory you downloaded the repository  
6. Select "Distributed Ergasia.postman_collection.json"  
7. Click on "Import". And that's it. No need to activate as the environment  

## Run the application from your IDE
Select the "it21774Application" class and run it  
If you chose to create an empty database, you will have to enter the data for the ADMIN profile. To do so, run the following commands  
```bash
docker exec -it distributed_container psql -U postgres -W
```
The password is "password"  
  
To view existing databases use `\l`.  
  
There should be a database named "it21774_distributed"  
  
Connect to it using `\c it21774_distributed` . You can view the tables by using `\dt` or `\dt+`  
  
To insert the ADMIN info, use  
```bash
INSERT INTO app_user (afm, amka, answer, email, first_name, last_name, password, username) VALUES (111111111, 11111111111, null, 'admin@gmail.com', 'Kevin', 'Stana', '$2a$10$VwKas4ss8uuLL.YpbycfXeT52yMjOXMce3OJe9wotGL4MT/Juo7tS', 'admin');
```
To check if the data was added, use  
```bash
select * from app_user;
```
You should get the following result:  
 id |    afm    |    amka     | answer |      email      | first_name | last_name |                           password                           | username | contract_id 
----|-----------|-------------|--------|-----------------|------------|-----------|--------------------------------------------------------------|----------|-------------
  1 | 111111111 | 11111111111 |        | admin@gmail.com | Kevin      | Stana     | $2a$10$VwKas4ss8uuLL.YpbycfXeT52yMjOXMce3OJe9wotGL4MT/Juo7tS | admin    |
  
  
Now add the roles, using  
```bash
INSERT INTO role (role) VALUES ('ROLE_ADMIN'), ('ROLE_LAWYER'), ('ROLE_NOTARY'), ('ROLE_CLIENT');
```
To check if the data was added, use
```bash
select * from role;
```
You should get the following result  
 id |    role     
----|-------------
  1 | ROLE_ADMIN
  2 | ROLE_LAWYER
  3 | ROLE_NOTARY
  4 | ROLE_CLIENT
  
  
Now add the ADMIN role to the admin user, using
```bash
INSERT INTO app_user_role (app_user_id, role_id) VALUES (1, 1);
```
To check if the data was adde, use
```bash
select * from app_user_role;
```
You should get the following result  
 app_user_id | role_id 
-------------|---------
1|1

phge arga ta upoloipa aurio
  
