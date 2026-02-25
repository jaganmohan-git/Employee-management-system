# How to Run Employee Management System

## Prerequisites
1.  **MySQL Database**:
    - Ensure MySQL is running on `localhost:3306`.
    - Create a database named `employeemanagement`.
    - Credentials configured in `application.properties`:
        - Username: `root`
        - Password: `Jagan@3027` (Update this in `src/main/resources/application.properties` if your local password is different).

2.  **Java**:
    - Ensure Java 21 is installed (referenced in `pom.xml`).

## Running via Spring Tool Suite (STS) / Eclipse
1.  Right-click on the project `EmployeeManagementSystem`.
2.  Select **Run As** > **Spring Boot App**.
3.  The application will start on port **2027**.

## Running via Command Line
> [!NOTE]
> Since the Maven Wrapper is currently incomplete on your system, you must have Maven (`mvn`) installed globally.

1.  Open a terminal in the project root.
2.  Run:
    ```powershell
    mvn spring-boot:run
    ```

## Accessing the Application
Once the application is started, access it in your browser:

- **Login Page**: [http://localhost:2027/login.html](http://localhost:2027/login.html)
- **Manager Dashboard**: [http://localhost:2027/manager-dashboard.html](http://localhost:2027/manager-dashboard.html)
