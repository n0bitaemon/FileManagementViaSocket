# Requirements
1. MySQL is installed

# Configs
1. Changing your MySQL credentials in `.env` file (located at the root folder of FileManagementServer)
2. Config a database, you can use the following queries:
```
CREATE DATABASE account;
USE account;
CREATE TABLE Account(
  username VARCHAR(100) PRIMARY KEY,
  password VARCHAR(100) NOT NULL
);
```
If you want to change database name, you must also change the database name in `.env` file

# Usage
1. The folder `FileManagementServer` contains all code for the server. To start the server, you need to run the main class in `filemanager.com.server.Server`. The default port is `3000`, please make sure no application is using that port. The server log file is located in `logs/app.log`.

2. The folder `FileManagementClient` contains all code for the client. To start a client, you need to run the main class in `filemanager.com.client.Client`. The client log file is located in `logs/app.log`. The server can serve multiple clients at the same time, and no conflict is 

3. Our file server operates in a simple way: The client will wait for you to type in a command, the server will receive and handle it. The following commands are avaiable:

        +) reg <username> <password> - Register a new account
         Ex: reg example example
        
        +) login <username> <password>
        Ex: login example example
        
        +) logout

        +) ls [<path>] - List all files and directories in <path>. If no path is specified, the default path is the root directory of current user will be used
        Ex: ls here/is/an/example/
        
        +) upload <local path> <server path> - Upload a file from local to server, the maximum size allowed is 10MB
        Ex: upload C:\Users\example\hello.pdf test.pdf
        
        +) download <server path> <local path> - Download a file from server to your local machine
        Ex: download test.pdf C:\Users\example\new_file.pdf
        
        +) rm <file/folder path> - Delete a file or folder on server
        Ex: rm test/hello.txt
        
        +) mv <source> <destination> - Move a file/folder from source to destination
        Ex: mv source/hello.txt my/new/location/test.txt
        
        +) cp <source> <destination> - Copy a file/folder from source to destination
        Ex: cp source/hello.txt my/new/location/tada.txt
        
        +) mkdir <path> - Create a new directory
        Ex: mkdir just/a/test

# How the server works
After registration, a folder will be created in `storage` folder on the server filesystem (the name of folder is the registered username). For example, with the command `reg examiner defaultpass`, a folder with path `storage/examiner` will be created and it will be the root folder for the examiner user. It means, if you type `/test/abc` as a path, it will correspond to `storage/examiner/test/abc` in the server filesystem.
