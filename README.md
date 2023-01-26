Project for Distributed Systems course
=======
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
  
Import the folder to your IDE of choice  
  
### *Option 3* (IntelliJ IDEA)  
1. Open IntelliJ IDEA  
2. From the top left select **File>New>Project from Version Control**  
3. Paste the repository link in the "URL" field: **https://github.com/kevinstana/distributed**  
4. Select directory to download the repository  
5. Click "Clone" at the bottom right  
## Run a postgres database using docker
Note: Use the IntelliJ terminal after importing the project  
### *Create database (with initial data)*

#### Linux
```bash
docker run --name distributed_container --rm -e POSTGRES_PASSWORD=password -e POSTGRES_DB=it21774_distributed -h localhost -p 5432:5432 -v "$(pwd)"/assets/db:/docker-entrypoint-initdb.d -v pgdata14:/var/lib/postgresql/data -d postgres:14
```
  
#### Windows
```bash
docker run --name distributed_container --rm -e POSTGRES_PASSWORD=password -e POSTGRES_DB=it21774_distributed -h localhost -p 5432:5432 -v ${pwd}\assets\db:/docker-entrypoint-initdb.d -v pgdata14:/var/lib/postgresql/data -d postgres:14
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
3. From the window that appears, select "Files"  
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
7. Enter "it21774_distributed" as the database  
8. Click on "Test Connection"  
9. If the connection is successful, click "OK"  
  
![Screenshot (741)](https://user-images.githubusercontent.com/122367928/212890352-207c9e5f-ee1a-4885-b51f-631c45455d1e.png)
  

  
## Using the application
Since I haven't created a complete frontend at the moment, you will have to use Postman to make requests to the REST API  
That's why the Postman helper files are required  
  
Once the application is running, open Postman and go to the collection you imported, "Distributed Ergasia"  
  
First, click on on the "ADMIN" folder and run the "Login" request. Then run the "Create Notary" request to create a new notary account  
Run the folder "Create Group 1" to create the 2 lawyers and the 2 clients   
   
I did't intialize this data in the database creation because it's satisfying to see the application work lol  
  
![Screenshot (742)](https://user-images.githubusercontent.com/122367928/212917396-02ab7c16-b241-4a30-b24a-58f702e2a1f4.png)
  
![Screenshot (763)](https://user-images.githubusercontent.com/122367928/214138075-82b579a0-2365-432f-a00a-33aae6c4c78b.png)

![Screenshot (744)](https://user-images.githubusercontent.com/122367928/212917883-b23d676a-5ee3-47cc-bf9b-00ae678497b0.png)
  
If it hopefully works you will get this result  
  
![Screenshot (745)](https://user-images.githubusercontent.com/122367928/212918636-569b4a50-a549-4e18-8e34-c39306ac9ae7.png)
  
You can check the database to verify the data was added (if asked for password, enter "password")  
```bash
docker exec -it distributed_container psql -U postgres -W
```
```bash
\c it21774_distributed
```
```bash
select * from app_user;
```
  
You should get the following result  
  
 id |    afm    |    amka     | answer |        email         | first_name | last_name |                           password                           |  username  | contract_id 
----|-----------|-------------|--------|----------------------|------------|-----------|--------------------------------------------------------------|------------|-------------       
  1 | 111111111 | 11111111111 |        | admin@gmail.com      | Kevin      | Stana     | $2a$10$VwKas4ss8uuLL.YpbycfXeT52yMjOXMce3OJe9wotGL4MT/Juo7tS | admin      |
  2 | 222222222 | 22222222222 |        | notary@gmail.com     | Notary     | Only One  | $2a$10$CO/q1dqq1r/pEumILzvIWu9Wjggcdx71PENhQ07U7KsO8xu7Jn.u6 | notary     |
  3 | 333333333 | 33333333333 |        | lawyer_one@gmail.com | Lawyer     | One       | $2a$10$UqM2w57kQ3bS5j6lOxGstuG8m/duLVHQGCBZySENYdSoE3FD2pLM6 | lawyer_one |
  4 | 444444444 | 44444444444 |        | lawyer_two@gmail.com | Lawyer     | Two       | $2a$10$p2oUdxRL37qW90H19cTeLOGQOzqEStainL5Bfw4TbItK1e/vrkcTe | lawyer_two |
  5 | 555555555 | 55555555555 |        | client_one@gmail.com | Client     | One       | $2a$10$sgJXKB4iLZhZm8pPD.pDGeILT/91n.e.YU4uzrly7ghnkOd9Obl5m | client_one |
  6 | 666666666 | 66666666666 |        | client_two@gmail.com | Client     | Two       | $2a$10$GZ34zUWKfspkxt218jvHT.bB4leHBc4DfEvc.rrmPbFNDZXsIIQie | client_two |
  
Now you can go to the "Group 1 Actions" folder, open the "Lawyer 1" or "Lawyer 2" folder, run the login request first and then create a contract.  
In the path for the "Create Contract" request, the number after users/ is for the lawyer id. By default it is the id of lawyer 1 (id=3) or the  id of lawyer 2 (is=4), depending on who you choose.  
The contract has the afms (the afm must be a 9 digit integer) of the 2 lawyers and the 2 clients. It also has a text (for example that the clients want a divorce)  
  
![Screenshot (747)](https://user-images.githubusercontent.com/122367928/212924052-04784bfc-d27c-4bfa-85cf-37d1881d556f.png)
![Screenshot (750)](https://user-images.githubusercontent.com/122367928/212925315-726a51fa-9136-4d98-8a79-043d765cd133.png)
  
Now you can run the "Answer Contract" and the "Get Contract" requests  
  
![Screenshot (748)](https://user-images.githubusercontent.com/122367928/212924762-a281135c-e8a7-4134-8340-e5655b258d6f.png)
![Screenshot (749)](https://user-images.githubusercontent.com/122367928/212924812-45273a14-3359-4a5d-8a2b-b5b4d85a2779.png)
  
You can check the database for the contract  
```bash
select * from contract;
```
  
You should get the following result  
 id | date_approved |    date_created     |   status    |                text                
----|---------------|---------------------|-------------|------------------------------------
  1 |               | 17/01/2023 16:22:58 | In Progress | This is a new contract for Group 1
  
The contract has also been associated with the users. Run  
```bash
select * from app_user;
```
  
You should get the following result  
 id |    afm    |    amka     | answer |        email         | first_name | last_name |                           password                           |  username  | contract_id 
----|-----------|-------------|--------|----------------------|------------|-----------|--------------------------------------------------------------|------------|-------------       
  1 | 111111111 | 11111111111 |        | admin@gmail.com      | Kevin      | Stana     | $2a$10$VwKas4ss8uuLL.YpbycfXeT52yMjOXMce3OJe9wotGL4MT/Juo7tS | admin      |
  2 | 222222222 | 22222222222 |        | notary@gmail.com     | Notary     | Only One  | $2a$10$CO/q1dqq1r/pEumILzvIWu9Wjggcdx71PENhQ07U7KsO8xu7Jn.u6 | notary     |
  4 | 444444444 | 44444444444 | No     | lawyer_two@gmail.com | Lawyer     | Two       | $2a$10$p2oUdxRL37qW90H19cTeLOGQOzqEStainL5Bfw4TbItK1e/vrkcTe | lawyer_two |           1        
  6 | 666666666 | 66666666666 | No     | client_two@gmail.com | Client     | Two       | $2a$10$GZ34zUWKfspkxt218jvHT.bB4leHBc4DfEvc.rrmPbFNDZXsIIQie | client_two |           1        
  5 | 555555555 | 55555555555 | No     | client_one@gmail.com | Client     | One       | $2a$10$sgJXKB4iLZhZm8pPD.pDGeILT/91n.e.YU4uzrly7ghnkOd9Obl5m | client_one |           1        
  3 | 333333333 | 33333333333 | Yes    | lawyer_one@gmail.com | Lawyer     | One       | $2a$10$UqM2w57kQ3bS5j6lOxGstuG8m/duLVHQGCBZySENYdSoE3FD2pLM6 | lawyer_one |           1        
  
Now you can run the login requests for the other lawyer and the clients (Client 1, Client 2), view the contract and answer it  
  
Once the contract is answered by all members, go to the "Notary" folder and login first. Run the "All Contracts" and the "Confirm Contract" request. The "Confirm Contract" takes the id of the contract as a path variable, in this case contract_id=1.  
  
![Screenshot (752)](https://user-images.githubusercontent.com/122367928/212928829-c5a86e5d-81df-4ef7-9ac0-896d408373c1.png)
![Screenshot (767)](https://user-images.githubusercontent.com/122367928/214140745-11e09108-e068-4f30-9f69-75e98cfa76cd.png)
  
You can check the database to verify the changes to the contract  
```bash
select * from contract;
```
  
You should get the following result  
  
 id |    date_approved    |    date_created     |  status  |                text                
----|---------------------|---------------------|----------|------------------------------------
  1 | 17/01/2023 16:40:33 | 17/01/2023 16:22:58 | Approved | This is a new contract for Group 1
  
That's it pretty much for the notary, lawyers and clients. Now as the ADMIN you can delete contracts from the "Delete Contract" request providing the contract id .The contract must be approved by the notary to be deleted. If you want to delete a contract regardless of approval, use the "Force Delete Contract" request, providing the contract id to the path.  
  
![Screenshot (755)](https://user-images.githubusercontent.com/122367928/212932667-e3da6723-eb6a-44d9-9cf5-dfd7075fb914.png)
  
You can also update a user's details by running the "PUT Update User" request. You can run the "GET One User" request first to view the current details. In both cases you have to add the id of the user you want to update to the path  
  
![Screenshot (764)](https://user-images.githubusercontent.com/122367928/214139021-d307995b-2a11-491a-beea-78ea6f8bed73.png)
  
In the "PUT Update User Request", you can also change the password of the user. If you just give "" the password will stay the same  
  
To delete a user, run the "Detele User" request. You have to provide the user's id as a path variable. If the user is an ADMIN or has a contract "In Progress", the user will not be deleted  
  
![Screenshot (758)](https://user-images.githubusercontent.com/122367928/212935036-74fda6d6-4c2b-4f67-964e-dd5c4a7bb9df.png)
  
The "GET One User" request can be run by all users, just change the Bearer token to the token of the user that makes the request (make sure the user is logged in first for the token to be valid). The ADMIN can view the details of every user, whereas individual users can only view their own details  
  
The "Create User Generic" request allows you to create a new user, just make sure the roles are either "admin", "lawyer", "notary" or "client". The afm must be a 9 digit integer and the amka must be an 11 digit integer. If you don't provide any roles, the user is given the role of a client by default  
  
Now you can try to give wrong inputs for the user and contract ids in the paths of the request or the input fields (for example assign the role "afsdgasg" to a user) or you can create the users of the "Create Group 2" folder and run the requests in the "Group 2 Actions" folder  
  
<p align="center">
  <img src="https://user-images.githubusercontent.com/122367928/214137078-c47ec9b9-458f-4fda-855e-4848cb06e32e.gif"/>
</p>


