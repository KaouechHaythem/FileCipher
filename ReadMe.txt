The main purpose of this project is to encrypt a file on demand using java and springboot, send it to minio , 
control the main CRUD functions via an angular website
only the destined user is allowed to decrypt the file

the project contains mainly 2 prespectives
 1 admin prespective :
    add and delete users 
    send files to users 
 2 user prespective : 
    see all the owned files 
    upload file 
    delete file
    download file
the encryption for now is performed in the backend using aes algorithm 
the secret key is stored in the applicatiob.properties folder but in production it must be stored in a vault 
the files are not encrypted in the front and https protocol can provide the needed security in the front
but a project that encrypts a file safely in the front is the next step


technologies used :
spring boot
angular
postgresql
jpa hibernate
postman
pgadmin

How to start the app: 
1 install all the project dependencies(springboot/maven/java/npm/angular/sql)
2 be sure to open line command in the same folder as minio.exe and execute the order line written in minio.txt
3 run the springboot backend
4 run ng serve command in the front 
