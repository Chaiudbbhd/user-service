📝 Voices of Java Dev - Backend

This is the **backend API** for the _Voices of Java Dev_ blog website, built using **Spring Boot**.  
It powers the blog platform, providing RESTful APIs for posts, categories, comments, and user management.

🌐 **Live Website:** [voices-of-java-dev.onrender.com](https://voices-of-java-dev.onrender.com/)

---

🚀 Tech Stack

- **Language:** Java
- **Framework:** Spring Boot
- **Database:** MySQL / PostgreSQL (configure as needed)
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

spring.datasource.url=jdbc:mysql://localhost:3306/blog_db
spring.datasource.username=root
spring.datasource.password=your_password

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

