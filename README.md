# Intergenerational Family Support System (IGFSS)

A TCP client-server Java application developed using JavaFX and MySQL for managing older couples, young families, and community events.

---

# Features

- Register Older Couples
- Register Young Families
- Secure Login System
- RSA Public/Private Key Password Encryption
- View Registered Members
- Search Member Details using FIDN
- Create Community Events
- View Created Events
- Multi-client TCP Server
- MySQL Database Storage

---

# Technologies Used

- Java
- JavaFX
- TCP Socket Programming
- MySQL
- JDBC
- RSA Encryption
- Object Serialization
- Multithreading

---

# Project Structure

```text
client/
server/
database.sql
run_server.sh
run_client.sh
.env
```

---

# Configuration

Create `.env` file in project root:

```env
DB_USER=root
DB_PASSWORD=
DB_NAME=igfss
DB_HOST=localhost
DB_PORT=3306
```

---

# Requirements

- JDK 17
- JavaFX SDK
- Maven 4
- MySQL Server
- MySQL Connector/J

---

# Running the Application

## Start Server

```bash
./run_server.sh
```

## Start Client

Open another terminal:

```bash
./run_client.sh
```

If you have the maven build tool, then

follow these steps:
```sh
cd client
```
    
To compile and run the client:
``` 
mvn clean javafx:run
```
`
---

# Notes

- Database and tables are automatically created.
- RSA key files are automatically generated on first server run.
- Login requires email and password.
- Password must contain letters and numbers.
