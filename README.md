# Chat History RAG API

## Chat History Service
A Spring Boot-based microservice designed to manage and retrieve chat history logs. This service provides a secure API for storing conversation data and integrates with PostgreSQL for persistent storage.

## 🚀 Getting Started
### Prerequisites
**Java 17** or higher<br/>
**Maven 3.8+** <br/>
**PostgreSQL** (running locally) <br/>
**Git** <br/>
## Setup & Installation
### 1. Clone the repository:
```bash
git clone https://github.com/Mystieee/CG-RAG-Chat-Storage.git
```

### 2. Environment Configuration:
Create a ```.env``` file in the root directory (same level as pom.xml) to manage your secrets. File is not committed to version control.
```bash
env
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=your_password_here

# Security
CHAT_API_KEY=your-secure-api-key-here
```

### 3. Build the application:
```bash
mvn clean install
```

### 4. Run the application:
```bash
mvn spring-boot:run
```

The application will start on http://localhost:8080 by default.

## 🛠 API Documentation
### Authentication
All API requests (except Swagger paths) require an ```X-API-KEY``` header for authentication.<br/>
**Header Name**: ```X-API-KEY``` <br/>
**Value**: (Defined in your ```.env``` file)
### Endpoints
| Method | 	Endpoint                                             | 	Description                                                           | 
|:-------|:------------------------------------------------------|:-----------------------------------------------------------------------|
| ```POST```   | ```/api/v1/sessions```                                | Start a new chat session.                                              |
| ```POST ```  | ```/api/v1/sessions/{{sessionId}}/messages ```        | Add a new message to a sessio for a specific user (User or Assistant). |
| ```GET``` | ```/api/v1/sessions/{{sessionId}}/history?page=0&size=10``` | Retrieve paginated message history for a session|
| ```PATCH``` | ```api/v1/sessions/{{sessionId}}```                         | Update session metadata (Rename or Toggle Favorite)|
| ```DELETE``` | ```/api/v1/sessions/{{sessionId}}   ```                     | Delete a session and all its messages |
| ```POST``` | ```/api/v1/sessions ```                                     | Handle Error scenario, if userId is not present|

### API Documentation (Swagger/OpenAPI)
If the application is running, you can access the interactive Swagger UI to test the endpoints: <br/>
**Swagger UI**: http://localhost:8080/swagger-ui.html <br/>
**API Docs**: http://localhost:8080/v3/api-docs <br/>
**Note**: To test via Swagger, click the "**Authorize**" button 
and enter the ```CHAT_API_KEY``` defined in your ```.env``` file.


### 🏗 Project Structure
```src/main/java```: Contains the Spring Boot application logic (Controllers, Services, Entities).
<br/>
```src/main/resources```: Contains application.properties and static resources.<br/>
```.env```: (Local only) Environment variables for sensitive data.
### ⚙️ Configuration Details
The application uses the following key properties (located in ```application.properties```):
<br/>
```spring.jpa.hibernate.ddl-auto=update```: Automatically manages database schema updates.
```management.health.redis.enabled=false```: Redis health checks are disabled as this service focuses on PostgreSQL.

### 🧪 Testing & Code Coverage

1. **Run the Test Suite**: <br/>
   Execute the following command to run all JUnit 5 tests and generate the coverage report:
   ```bash
   mvn clean test jacoco:report
   ```
   Navigate to ```target/site/jacoco/index.html``` to view the report.

### 🏥 Health Checks & Monitoring
The application uses **Spring Boot Actuator** to provide real-time health monitoring. These endpoints are whitelisted and do not require an API Key.

- **Status Check**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
   - **Response**: `{"status": "UP", "components": {"db": {"status": "UP", ...}}}`
   - *Confirms the application and PostgreSQL connection are both healthy.*
