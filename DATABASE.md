•  Container name: mysql-container
•  Root password: rootpassword
•  Database: omniride (created automatically)
•  Port mapping: 3306:3306 (MySQL default port)
•  MySQL version: Latest

The container is now running in detached mode. You can connect to it using:

Connection details:
•  Host: localhost (or 127.0.0.1)
•  Port: 3306
•  Username: root
•  Password: rootpassword
•  Database: omniride

Useful commands:
•  Check container status: docker ps
•  Stop the container: docker stop mysql-container
•  Start the container: docker start mysql-container
•  Connect to MySQL: docker exec -it mysql-container mysql -u root -p