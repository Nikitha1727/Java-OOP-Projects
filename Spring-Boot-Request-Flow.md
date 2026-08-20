# Spring Boot — What Happens When an API Request Comes In 🚀

I recently started learning Spring Boot, and one thing I wanted to understand properly was:

> **What actually happens when I hit an API like `http://localhost:8080/students`?**

At first, I only knew that `@GetMapping` somehow connects the URL to the method.

But there is a whole flow happening behind it.

---

## 1. The basic flow

A simplified Spring Boot request flow looks like this:

```text
Client / Browser / Postman
          │
          │ HTTP Request
          ↓
   Embedded Tomcat Server
          │
          ↓
   DispatcherServlet
          │
          ↓
      Controller
          │
          ↓
       Service
          │
          ↓
      Repository
          │
          ↓
       Database
          │
          ↑
       Response
          │
          ↑
       Controller
          │
          ↓
     JSON Response
          │
          ↓
Client / Browser / Postman
```

The exact flow can contain more components, but this is the mental model I'm using while learning.

---

# 2. Creating a simple API

Let's say I have:

```java
@RestController
public class StudentController {

    @GetMapping("/students")
    public String getStudents() {
        return "Students";
    }
}
```

When I open:

```text
http://localhost:8080/students
```

the browser sends an HTTP GET request.

Spring Boot then has to figure out:

> "Which Java method should handle `/students`?"

That's where the request handling mechanism comes in.

---

# 3. Spring Boot starts an embedded server

One reason Spring Boot feels much easier than older Java web applications is that we normally don't have to separately install and configure a web server just to run the application.

When the application starts, Spring Boot can start an **embedded server**, commonly Tomcat for traditional Spring MVC applications.

So when I see:

```text
Tomcat started on port 8080
```

it basically means my application is listening for HTTP requests on that port.

```text
localhost
   │
   └── :8080
          │
          └── Spring Boot application
```

---

# 4. What is DispatcherServlet?

This was one of the first Spring concepts that made the request flow make more sense to me.

`DispatcherServlet` acts as a **front controller** for Spring MVC.

Instead of every controller directly handling network-level requests, the request comes through the DispatcherServlet.

Conceptually:

```text
HTTP Request
      ↓
DispatcherServlet
      ↓
Which controller should handle this?
      ↓
Controller method
```

So if I request:

```text
GET /students
```

the DispatcherServlet helps route that request to the appropriate handler.

---

# 5. What does `@RestController` mean?

When I write:

```java
@RestController
public class StudentController {
}
```

I'm basically telling Spring:

> "This class contains methods that can handle web requests, and their return values should generally be written directly into the HTTP response."

`@RestController` is effectively associated with:

```java
@Controller
@ResponseBody
```

So:

```java
@RestController
```

is commonly used when building REST APIs.

---

# 6. What does `@GetMapping` do?

Consider:

```java
@GetMapping("/students")
public String getStudents() {
    return "Students";
}
```

There are two important pieces:

```text
@GetMapping
      ↓
HTTP GET request

"/students"
      ↓
URL path
```

So Spring can map:

```text
GET /students
```

to:

```java
getStudents()
```

That's why I don't need to manually write code like:

```java
if (request == "/students") {
    ...
}
```

Spring handles the mapping for me.

---

# 7. Controller Layer

The controller is generally responsible for handling the HTTP side of the application.

Example:

```java
@RestController
@RequestMapping("/students")
public class StudentController {

    @GetMapping
    public String getStudents() {
        return "All students";
    }
}
```

The controller shouldn't ideally contain all the business logic.

Instead, it can pass the work to a service.

---

# 8. Service Layer

Example:

```java
@Service
public class StudentService {

    public String getStudents() {
        return "All students";
    }
}
```

Then the controller can use the service:

```java
@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public String getStudents() {
        return studentService.getStudents();
    }
}
```

Now the responsibilities are separated.

```text
Controller
   ↓
Handles HTTP request

Service
   ↓
Handles application/business logic
```

---

# 9. Why not put everything inside the Controller?

We could technically write:

```java
@GetMapping("/students")
public String getStudents() {

    // business logic
    // database code
    // validation
    // calculations
    // etc.

    return "...";
}
```

But as the application grows, the controller can become huge.

Instead:

```text
Controller
    ↓
Service
    ↓
Repository
```

keeps different responsibilities separated.

This makes the application easier to maintain and test.

---

# 10. Repository Layer

When the application needs to communicate with a database, we generally introduce a repository layer.

For example:

```java
@Repository
public class StudentRepository {
    
}
```

With Spring Data JPA, it can be even simpler:

```java
public interface StudentRepository
        extends JpaRepository<Student, Long> {
}
```

The general flow becomes:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

---

# 11. Dependency Injection

This is another important Spring concept.

Suppose the controller needs `StudentService`.

Instead of creating it manually:

```java
StudentService service = new StudentService();
```

Spring can provide the dependency.

Example:

```java
private final StudentService studentService;

public StudentController(StudentService studentService) {
    this.studentService = studentService;
}
```

Spring creates and manages the required object and passes it into the constructor.

This is **Dependency Injection**.

---

# 12. Why is this useful?

Without dependency injection:

```text
Controller
    │
    └── creates Service itself
          │
          └── creates Repository itself
```

With Spring:

```text
             Spring Container
             /       |       \
            ↓        ↓        ↓
      Controller  Service  Repository
```

Spring manages these objects and their dependencies.

This is one of the reasons Spring is called a framework based heavily around **IoC (Inversion of Control)**.

---

# 13. What is a Spring Bean?

A Spring-managed object is commonly called a **Bean**.

For example:

```java
@Service
public class StudentService {
}
```

Spring can detect this class and create/manage an instance of it as part of the application context.

Similarly:

```java
@RestController
public class StudentController {
}
```

is also managed by Spring.

So I can think:

```text
Spring Container
      │
      ├── StudentController Bean
      ├── StudentService Bean
      └── StudentRepository Bean
```

---

# 14. What happens when the API returns an object?

Suppose:

```java
@GetMapping("/student")
public Student getStudent() {
    return new Student(1, "Nikitha");
}
```

The Java method returns:

```text
Student object
```

But an HTTP client normally receives data such as JSON.

It can become:

```json
{
  "id": 1,
  "name": "Nikitha"
}
```

Spring Boot commonly uses **Jackson** for converting Java objects to JSON and JSON back into Java objects.

This process is called:

```text
Serialization
```

and the reverse process is:

```text
Deserialization
```

---

# 15. Serialization vs Deserialization

### Java → JSON

```text
Java Object
     ↓
Serialization
     ↓
JSON
```

### JSON → Java Object

```text
JSON
 ↓
Deserialization
 ↓
Java Object
```

For example, a POST request might send:

```json
{
  "name": "Nikitha",
  "age": 18
}
```

Spring can convert this JSON into a Java object.

---

# 16. `@RequestBody`

For example:

```java
@PostMapping("/students")
public String addStudent(@RequestBody Student student) {

    return student.getName();
}
```

Here:

```java
@RequestBody
```

tells Spring to take the request body and convert it into the `Student` object.

Conceptually:

```text
JSON Request
     ↓
@RequestBody
     ↓
Student object
```

---

# 17. `@PathVariable`

Suppose the URL is:

```text
/students/101
```

We can capture `101` using:

```java
@GetMapping("/students/{id}")
public String getStudent(@PathVariable Long id) {
    return "Student ID: " + id;
}
```

Here:

```text
/students/{id}
          ↑
       variable
```

If we request:

```text
/students/101
```

then:

```text
id = 101
```

---

# 18. `@RequestParam`

Another way of sending data is through query parameters.

Example:

```text
/students?name=Nikitha
```

We can use:

```java
@GetMapping("/students")
public String search(@RequestParam String name) {
    return name;
}
```

So:

```text
/students?name=Nikitha
```

gives:

```text
name = Nikitha
```

---

# 19. The three can look confusing

### `@PathVariable`

```text
/students/101
```

```java
@PathVariable Long id
```

---

### `@RequestParam`

```text
/students?id=101
```

```java
@RequestParam Long id
```

---

### `@RequestBody`

Data comes inside the request body:

```json
{
  "id": 101,
  "name": "Nikitha"
}
```

```java
@RequestBody Student student
```

---

# 20. A small complete example

### Student.java

```java
public class Student {

    private Long id;
    private String name;

    public Student(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
```

### StudentService.java

```java
@Service
public class StudentService {

    public Student getStudent() {
        return new Student(1L, "Nikitha");
    }
}
```

### StudentController.java

```java
@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public Student getStudent() {
        return studentService.getStudent();
    }
}
```

Now:

```text
GET /students
```

roughly goes through:

```text
Browser / Postman
       ↓
Tomcat
       ↓
DispatcherServlet
       ↓
StudentController
       ↓
StudentService
       ↓
Student object
       ↓
Jackson
       ↓
JSON response
```

Response:

```json
{
  "id": 1,
  "name": "Nikitha"
}
```

---

# 21. The whole thing in one picture

```text
                         CLIENT
                           │
                           │
                     HTTP Request
                           │
                           ↓
                ┌───────────────────┐
                │ Embedded Tomcat   │
                └─────────┬─────────┘
                          │
                          ↓
                ┌───────────────────┐
                │ DispatcherServlet │
                └─────────┬─────────┘
                          │
                          ↓
                ┌───────────────────┐
                │    Controller     │
                │  @RestController  │
                └─────────┬─────────┘
                          │
                          ↓
                ┌───────────────────┐
                │      Service      │
                │     @Service      │
                └─────────┬─────────┘
                          │
                          ↓
                ┌───────────────────┐
                │    Repository     │
                │    @Repository    │
                └─────────┬─────────┘
                          │
                          ↓
                     DATABASE
                          │
                          ↑
                     Data/result
                          │
                          ↑
                Repository → Service
                          │
                          ↑
                     Controller
                          │
                          ↓
                    JSON Response
                          │
                          ↓
                        CLIENT
```

---

# 22. What I learned from this

Before learning this flow, I mostly saw Spring Boot annotations as separate things:

```text
@RestController
@GetMapping
@Service
@Repository
@Autowired
@RequestBody
```

Now I'm trying to connect them into one system.

The important mental model is:

```text
HTTP Request
     ↓
DispatcherServlet
     ↓
Controller
     ↓
Service
     ↓
Repository
     ↓
Database
     ↓
Repository
     ↓
Service
     ↓
Controller
     ↓
JSON Response
```

The annotations aren't the whole concept.

They are basically telling Spring **how different parts of the application should behave and connect**.

---

## Quick Revision

```text
@RestController
→ REST API controller

@GetMapping
→ Handles HTTP GET request

@PostMapping
→ Handles HTTP POST request

@RequestBody
→ Converts request body into an object

@PathVariable
→ Gets value from URL path

@RequestParam
→ Gets value from query parameter

@Service
→ Service/business logic layer

@Repository
→ Database/persistence layer

Dependency Injection
→ Spring provides required dependencies

Bean
→ Object managed by Spring

DispatcherServlet
→ Front controller for Spring MVC requests

Jackson
→ Java object ↔ JSON conversion
```

## My main takeaway

> **Spring Boot isn't just about writing annotations. The important part is understanding the flow behind those annotations.**

Once the request flow makes sense, concepts like REST APIs, JPA, validation, exception handling, and database connectivity become much easier to understand.
