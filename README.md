# Rate Limiter

A **distributed rate limiter** demo that evaluates incoming requests based on **user information** and **request URL**, and determines whether the request is **allowed** or **blocked** using a **Token Bucket algorithm**.

This project is designed for **high-performance distributed environments**, leveraging **Redis** for state management and exposing metrics through **Prometheus**, which can be visualized with **Grafana**.

---

## Features

- Distributed rate limiting using **Token Bucket algorithm**
- Supports **user-specific** and **URL-specific** rate limiting
- **Prometheus metrics** exposed for monitoring
- Visual dashboards using **Grafana**
- Built with modern tech stack - **Java 17** & **Spring Boot 3.4.2**
- **Redis** for fast token storage and distributed state management

---

## Tech Stack

| Technology  | Version      |
|-------------|---------------|
| Java        | 17            |
| Spring Boot | 3.4.2         |
| Redis       | Latest        |
| Prometheus  | Latest        |
| Grafana     | Latest        |
