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
1. Open Postman. From the left sidebar, select "Environments"  
2. Select "Import"  
3. From the window that appears, select "File"  
4. Click on "Choose files"  
5. Navigate to the directory you downloaded the repository and select "assets/postman/SpringBootEnv.postman_environment.json"  
6. Click on "Import"
7. Hover over the new environment and click on the checkmark to activate it  
  
![Screenshot (736)](https://user-images.githubusercontent.com/122367928/212832156-dfc1d7c9-d5b6-4209-b27f-a8884b5c23f2.png)
![Screenshot (737)](https://user-images.githubusercontent.com/122367928/212832178-dcaaf5ad-feaf-419e-a902-d025e76a6e1f.png)
  
Now for the 2nd file
  
1. From the left sidebar, choose "Collections"  
2. Select "Import"  
3. From the windows that appears, select "Files"  
4. Click on "Choose files"  
5. Navigate to the directory you downloaded the repository and select "assets/postman/Distributed Ergasia.postman_collection.json"  
6. Click on "Import" and that's it  
  
![Screenshot (738)](https://user-images.githubusercontent.com/122367928/212834120-10bebd3d-dac6-4d69-b249-60f0b8548646.png)
  
## Run the application from your IDE
Right click on the "it21774Application" class and run it  
  
![Screenshot (739)](https://user-images.githubusercontent.com/122367928/212890784-12b25866-d221-417c-bc3e-a18776330e74.png)
  
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
  
You can also use IntelliJ to view the database  
  
1. On the far right, select "Database"  
2. Click on the "+" icon  
3. Select "Data Source"  
4. Select "PostgreSQL"  
  
![Screenshot (740)](https://user-images.githubusercontent.com/122367928/212888400-6ce22fe8-9a34-414e-9bb7-b72034706d9d.png)  
  
5. In the window that appears, enter "postgres" as the username  
6. Enter "password" as the password  
7. Enter "it21774_distribued" as the database  
8. Click on "Test Connection"  
9. If the connection is successful, click "OK"  
  
![Screenshot (741)](https://user-images.githubusercontent.com/122367928/212890352-207c9e5f-ee1a-4885-b51f-631c45455d1e.png)
  

  
## Using the application
Since I haven't created a complete frontend at the moment, you will have to use Postman to make request to the REST API  
That's why the Postman helper files are required  
