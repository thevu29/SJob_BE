# SJob_BE 🚀

This is the backend for the SJob job board platform, built on a Spring Boot microservices architecture. The system allows employers to post job listings and enables candidates to search and apply for jobs.

Frontend: https://github.com/thevu29/SJob_FE

---

## 📑 Table of Contents
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [System Architecture](#system-architecture)
- [Setup and Installation](#setup-and-installation)
- [API Documentation](#api-documentation)

---

## ✨ Key Features

* **User Management**: Registration, login, and role-based access control for employers and candidates.
* **Job Management**: Employers can create, read, update, and delete (CRUD) job postings.
* **Search & Apply**: Candidates can search for jobs using various criteria and submit their applications.
* **Smart Job Recommendations**: Utilizes the **Gemini API** to analyze user profiles and suggest the most suitable job opportunities.
* **Asynchronous Communication**: Employs **Kafka** for handling asynchronous tasks like notifications, emails, etc.
* **High-Performance Caching**: Uses **Redis** to cache frequently accessed job listings, reducing database load and improving response times.

## 🚀 Tech Stack

* **Language**: Java
* **Framework**: Spring Boot
* **Architecture**: Microservices
* **Service Discovery**: Netflix Eureka
* **API Gateway**: Spring Cloud Gateway
* **Authentication & Authorization**: Keycloak
* **Database**: PostgreSQL, MongoDB
* **Message Broker**: Apache Kafka
* **Caching**: Redis
* **AI**: Gemini API

## 🏛️ System Architecture

The system is designed with a microservices architecture, comprising the following core components:

* **Eureka Server**: Acts as a service registry, enabling microservices to discover and communicate with each other.
* **API Gateway**: Serves as the single entry point for all client requests, managing routing, security, and other cross-cutting concerns.
* **Keycloak**: Provides a centralized authentication and authorization solution for the entire system.
* **Business Microservices**:
    * **User Service**: Manages core user data, profiles, and password changes.
    * **Job Service**: Handles all logic related to creating and managing job postings.
    * **Application Service**: Processes job applications submitted by candidates.
    * **Notification Service**: Creates and manages user notifications and preferences.
    * **Auth Service**: Handles authentication and authorization logic, likely integrating with Keycloak.
    * **Job Seeker Service**: Manages profiles and functionalities specific to job seekers (candidates).
    * **Recruiter Service**: Manages recruiter profiles, company information, and invitations.
    * **Report Service**: Generates and manages business reports.
    * **Mail Service**: Responsible for formatting and sending emails.
    * **S3 Service**: Manages file uploads and storage on Amazon S3 (e.g., resumes, company logos).
* **Kafka**: Functions as an intermediary for asynchronous communication between services.
* **Redis**: Stores a cache of data to accelerate query performance.

## 🛠️ Setup and Installation

### Prerequisites

* JDK 17 or later
* Maven
* Docker and Docker Compose

### Installation Guide

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/thevu29/SJob_BE.git](https://github.com/thevu29/SJob_BE.git)
    cd SJob_BE
    ```

2.  **Configure your environment:**
    * Update the configuration details for each service in their respective `application.properties` or `application.yml` files (e.g., database credentials, Keycloak server info, Kafka/Redis connection strings, Gemini API key).

3.  **Run the services:**
    * **Using Docker Compose (Recommended):**
        ```bash
        docker-compose up -d
        ```
    * **Running manually:**
        1.  Start the Eureka Server.
        2.  Start Keycloak, Kafka, and Redis instances.
        3.  Start the API Gateway.
        4.  Start the other microservices.

4.  **Verify the setup:**
    * Access the Eureka Dashboard at `http://localhost:8761`.
    * Test the API Gateway endpoints, which should be available at `http://localhost:8080`.

## 📄 API Documentation

You can find detailed API documentation via Swagger UI, which is integrated into the services.

* **API Gateway Swagger UI:** `http://localhost:8080/swagger-ui.html`
