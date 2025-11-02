# Newsy - Full Stack News Aggregator

A modern news aggregator with Spring Boot backend and Angular frontend that fetches and displays news from NewsData.io API.

## 🚀 Features

- 5 news categories with 4 articles each
- Daily scheduled news fetching at 8:00 AM UTC
- Dark/light theme with responsive design
- REST API endpoints for news retrieval

## 🛠️ Tech Stack

**Backend:** Java 21, Spring Boot 3.5.7, MongoDB Atlas, Maven  
**Frontend:** Angular 17, TypeScript, RxJS

## 📋 Quick Start

**Prerequisites:** Java 21, Maven 3.8+, Node.js 18+, MongoDB Atlas, NewsData.io API Key

```bash
# Clone & configure
git clone <your-repo-url>
cd newsy
cp .env.example .env  # Add your MongoDB URI and NewsData.io API key

# Run backend (http://localhost:8080)
mvnw clean install
mvnw spring-boot:run

# Run frontend (http://localhost:4200)
cd newsy-ui
npm install && npm start
```

## 🌐 API Endpoints

- `GET /api/news` - Get all news grouped by category
- `GET /api/news/{category}` - Get news by specific category
- `POST /api/news/fetch` - Manually trigger news fetch

## 🚀 Deployment

**Free deployment:** Render (backend) + Vercel (frontend) + MongoDB Atlas + NewsData.io = $0/month

## 📚 Learn More

For detailed explanations, tutorials, and learning resources, see [LEARN.md](LEARN.md)

## 📄 License

Open source for educational use.

---

**Built with ❤️ using Spring Boot & Angular**
