# Learning Guide for Newsy

This guide will help you understand the key technologies and concepts used in the Newsy project.

## 🎯 What You'll Learn

This project is a **news aggregator** application that demonstrates full-stack development using modern technologies. You'll learn how to build a complete web application from backend to frontend, work with external APIs, schedule tasks, and deploy to the cloud.

## 📚 Core Technologies

### Backend Technologies

#### 1. **Java 21**
- Modern programming language for building robust applications
- **Learn**: [Java Tutorial](https://docs.oracle.com/javase/tutorial/)
- **Used for**: All backend logic and business rules

#### 2. **Spring Boot**
- Framework that simplifies Java application development
- **Learn**: [Spring Boot Guides](https://spring.io/guides)
- **Used for**: REST API, dependency injection, scheduling

#### 3. **MongoDB**
- NoSQL database for storing news articles
- **Learn**: [MongoDB University](https://learn.mongodb.com/)
- **Used for**: Persistent data storage with flexible schema

#### 4. **Maven**
- Build automation and dependency management tool
- **Learn**: [Maven Getting Started](https://maven.apache.org/guides/getting-started/)
- **Used for**: Managing Java dependencies and building the project

### Frontend Technologies

#### 1. **Angular**
- Framework for building single-page applications
- **Learn**: [Angular Tutorial](https://angular.io/tutorial)
- **Used for**: User interface and client-side logic

#### 2. **TypeScript**
- Typed superset of JavaScript
- **Learn**: [TypeScript Handbook](https://www.typescriptlang.org/docs/)
- **Used for**: Type-safe frontend code

#### 3. **RxJS**
- Library for reactive programming with observables
- **Learn**: [RxJS Guide](https://rxjs.dev/guide/overview)
- **Used for**: Handling async operations and API calls

## 🔑 Key Concepts to Understand

### 1. **REST API**
Learn how to design and consume RESTful web services.
- Endpoints: `/api/news`, `/api/news/{category}`
- HTTP methods: GET, POST
- **Learn**: [REST API Tutorial](https://restfulapi.net/)

### 2. **Scheduled Tasks**
Understand how to run code automatically at specific times.
- Daily news fetch at 8:00 AM UTC
- **Learn**: Spring `@Scheduled` annotation

### 3. **CORS (Cross-Origin Resource Sharing)**
Learn how frontend and backend communicate across different domains.
- Frontend on Vercel, Backend on Render
- **Learn**: [CORS Explained](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)

### 4. **Environment Variables**
Keep sensitive data secure and configurations flexible.
- MongoDB URI, API keys
- **Learn**: Never hardcode secrets!

### 5. **Responsive Design**
Make web applications work on all screen sizes.
- 4-column grid layout
- **Learn**: CSS Grid, Flexbox

## 🛠️ Development Skills

### Version Control
- **Git**: Track changes and collaborate
- **Learn**: [Git Handbook](https://guides.github.com/introduction/git-handbook/)

### API Integration
- **NewsData.io**: Fetch real-time news data
- **Learn**: How to read API documentation and make HTTP requests

### Cloud Deployment
- **Render**: Backend hosting
- **Vercel**: Frontend hosting
- **MongoDB Atlas**: Cloud database
- **Learn**: DevOps basics and cloud platforms

## 📖 Learning Path

### Beginners
1. Start with Java basics
2. Learn about REST APIs
3. Understand databases (SQL vs NoSQL)
4. Learn HTML, CSS, JavaScript basics
5. Explore the code: Start with `NewsController.java` and `app.component.ts`

### Intermediate
1. Dive into Spring Boot framework
2. Learn Angular fundamentals
3. Study the project structure
4. Understand dependency injection
5. Practice with the API endpoints

### Advanced
1. Implement new features (e.g., search, bookmarks)
2. Add authentication and user accounts
3. Optimize performance and caching
4. Set up CI/CD pipelines
5. Add comprehensive testing

## 🚀 Hands-On Practice

### Easy Tasks
- [ ] Change the color scheme
- [ ] Add a new news category
- [ ] Modify the scheduled time
- [ ] Add more articles per category

### Medium Tasks
- [ ] Add a search feature
- [ ] Implement pagination
- [ ] Add article bookmarking
- [ ] Create a filter by date

### Advanced Tasks
- [ ] Add user authentication
- [ ] Implement real-time updates with WebSockets
- [ ] Add article recommendations
- [ ] Create a mobile app version

## 📝 Best Practices You'll Learn

1. **Separation of Concerns**: Backend vs Frontend
2. **Clean Code**: Readable and maintainable code
3. **Error Handling**: Graceful failure management
4. **Security**: Environment variables, no hardcoded secrets
5. **Documentation**: Clear README and code comments

## 🎓 Additional Resources

- **Spring Boot**: [Spring.io](https://spring.io)
- **Angular**: [Angular.io](https://angular.io)
- **MongoDB**: [MongoDB Docs](https://docs.mongodb.com)
- **Full-Stack Development**: [Full Stack Open](https://fullstackopen.com)

## 💡 Tips for Learning

1. **Start Small**: Run the application locally first
2. **Read the Code**: Understanding existing code is crucial
3. **Make Changes**: Modify small things to see what happens
4. **Debug**: Use logs and browser dev tools
5. **Ask Questions**: Don't hesitate to search or ask for help
6. **Build Something**: The best way to learn is by doing!

---

**Happy Learning! 🎉**
