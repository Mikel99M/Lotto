**Lotto Application - Backend**

A full-stack lottery system based on a micro-services architecture. The application allows users to register, play the lottery, and check results in real-time.

Lotto Game deployed on AWS EC2:
http://ec2-63-179-100-176.eu-central-1.compute.amazonaws.com

Backend endpoints presented on Swagger:
http://ec2-63-179-100-176.eu-central-1.compute.amazonaws.com:8080/swagger-ui/index.html#/



**Technical Stack**

Backend: Java 17, Spring Boot 2.7.x / 3.x

Security: Spring Security + JWT (JSON Web Token)

Database: MongoDB (Data persistence)

Caching: Redis (Result processing & performance)

Testing: JUnit 5, MockMvc, Testcontainers (Integration Tests)

DevOps: Docker, Docker Compose, AWS (EC2, ECR)



**Features**

User Management: Secure registration and login using JWT.

Number Input: Users can submit their 6 numbers for the upcoming draw.

Automated Draws: Scheduled winning numbers generation (Cron based).

Result Checker: Automatic validation of user tickets against winning numbers.

REST API: Clean and documented endpoints for frontend integration.



**Architecture & Deployment**

The project is containerized using Docker and deployed on AWS EC2. Images are managed via Amazon ECR.

Infrastructure Layout:
Backend: Java Spring Boot application.

Frontend: React-based UI (optimized with AI suggestions).

Storage: MongoDB for user data and ticket history.

Cache: Redis for efficient ticket processing.

Management: Mongo-Express and Redis-Commander for easy database inspection (Development mode).
