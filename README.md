📝 Voices of Java Dev - Backend

This is the **backend API** for the _Voices of Java Dev_ blog website, built using **Spring Boot**.  
It powers the blog platform, providing RESTful APIs for posts, categories, comments, and user management.

🌐 **Live Website:** [voices-of-java-dev.onrender.com](https://voices-of-java-dev.onrender.com/)  

(Hey everyone, due to a minor issue, the posts may take 1-2 minutes to load. Kindly wait for a short while. Thank you for your patience!)

---

🚀 Tech Stack

- **Language:** Java
- **Framework:** Spring Boot
- **Database:** Mongo DB Atlas (configure as needed)
- **API:** RESTful APIs
- **ORM:** Hibernate / JPA
- **Build Tool:** Maven
- **Deployment:** Render
- **Other:** Spring Security (for user authentication), Docker (optional)

---

src/main/java/com/voicesofjavadev

├── controller      → REST Controllers (handles API requests and responses)

├── model           → Entity classes (defines database models)

├── repository      → JPA Repositories (data access layer)

├── service         → Business logic (application services)

├── dto             → Data Transfer Objects (for API request/response payloads)

└── config          → Security & Application Configurations




⚙️ Setup Instructions


1️⃣ Clone the Repository


git clone https://github.com/Chaiudbbhd/voices-of-java-dev-backend.git
cd voices-of-java-dev-backend

2️⃣ Configure Database

Update your database credentials in src/main/resources/application.properties:

# Application Name
spring.application.name=user-service

# MongoDB Configuration
spring.data.mongodb.uri=${MONGO_URI}
spring.data.mongodb.database=22kd1a0548(name it with your db name)

# JWT Authentication Settings
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# Cloudinary Configuration for Image Upload
cloudinary.cloud_name=${CLOUDINARY_CLOUD_NAME}
cloudinary.api_key=${CLOUDINARY_API_KEY}
cloudinary.api_secret=${CLOUDINARY_API_SECRET}

# File Upload Limits
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging Levels
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.web=DEBUG

3️⃣ Build and Run

./mvnw clean install
./mvnw spring-boot:run
Server will start at: http://localhost:8080/

🔐 API Authentication (Optional)
JWT-based authentication

Role-based access control (Admin, User)

📑 API Endpoints (Examples)
Method	Endpoint	Description
GET	/api/posts	Get all blog posts
POST	/api/posts	Create a new post
GET	/api/categories	List all categories
POST	/api/auth/register	Register new user
POST	/api/auth/login	Login & get JWT token

🐳 Docker (Optional)
Build Docker image:


docker build -t voicesofjavadev-backend .
Run the container:

docker run -p 8080:8080 voicesofjavadev-backend

📦 Deployment

This backend is deployed on Render:
➡️ https://voices-of-java-dev.onrender.com/

🙌 Contributions
Pull requests are welcome! Feel free to submit issues for feature requests or bug fixes.

📄 License
MIT License

