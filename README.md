# Object-Oriented Programming (OOP) in Java

## Introduction

Object-Oriented Programming (OOP) is a programming approach where programs are designed using **objects and classes**. It helps in organizing code, reusing existing code, improving readability, and making applications easier to maintain.

In Java, OOP is mainly based on **four pillars**:

* Encapsulation
* Inheritance
* Polymorphism
* Abstraction

---

# Class and Object

## Class

A class is a blueprint or template used to create objects.

Example:

```java
class Student {
    String name;
    int age;
}
```

## Object

An object is an instance of a class.

```java
Student s1 = new Student();
s1.name = "Nikitha";
s1.age = 19;
```

---

# The Four Pillars of OOP

## 1. Encapsulation

Encapsulation means **binding data and methods together in a single unit (class)** and restricting direct access to data.

It is achieved using **private variables** and **getter/setter methods**.

```java
class Student {
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
```

### Advantages

* Data security
* Controlled access
* Better code maintenance

---

## 2. Inheritance

Inheritance allows one class to acquire the properties and methods of another class.

```java
class Animal {
    void sound() {
        System.out.println("Animal makes a sound");
    }
}

class Dog extends Animal {
    void bark() {
        System.out.println("Dog barks");
    }
}
```

### Types of inheritance in Java

* Single
* Multilevel
* Hierarchical

Java does **not support multiple inheritance through classes**, but it can be achieved using interfaces.

---

## 3. Polymorphism

Polymorphism means **one action can be performed in multiple ways**.

### Method Overloading (Compile-time Polymorphism)

Methods with the same name but different parameters.

```java
class Calculator {
    int add(int a, int b) {
        return a + b;
    }

    int add(int a, int b, int c) {
        return a + b + c;
    }
}
```

### Method Overriding (Run-time Polymorphism)

A child class provides its own implementation of a parent class method.

```java
class Animal {
    void sound() {
        System.out.println("Animal sound");
    }
}

class Dog extends Animal {
    @Override
    void sound() {
        System.out.println("Dog barks");
    }
}
```

---

## 4. Abstraction

Abstraction means **hiding implementation details and showing only essential features**.

It can be achieved using:

* Abstract classes
* Interfaces

### Abstract Class

```java
abstract class Vehicle {
    abstract void start();

    void stop() {
        System.out.println("Vehicle stopped");
    }
}

class Car extends Vehicle {
    void start() {
        System.out.println("Car started");
    }
}
```

An abstract class can have both **abstract and concrete methods**.

---

# Interface

An interface contains abstract methods and is used to achieve abstraction and multiple inheritance.

```java
interface Payment {
    void pay();
}

class UPI implements Payment {
    public void pay() {
        System.out.println("Payment through UPI");
    }
}
```

### Difference between Abstract Class and Interface

| Abstract Class                  | Interface                       |
| ------------------------------- | ------------------------------- |
| Can have constructors           | Cannot have constructors        |
| Can contain implemented methods | Methods are abstract by default |
| Supports partial abstraction    | Supports full abstraction       |

---

# Constructor

A constructor is a special method used to initialize objects.

## Default Constructor

```java
class Student {
    Student() {
        System.out.println("Constructor called");
    }
}
```

## Parameterized Constructor

```java
class Student {
    String name;

    Student(String name) {
        this.name = name;
    }
}
```

Constructors have the same name as the class and do not have a return type.

---

# this Keyword

`this` refers to the current object.

```java
class Student {
    String name;

    Student(String name) {
        this.name = name;
    }
}
```

Uses:

* Resolve variable conflicts
* Call another constructor
* Refer to the current object

---

# super Keyword

`super` refers to the parent class object.

```java
class Animal {
    Animal() {
        System.out.println("Animal constructor");
    }
}

class Dog extends Animal {
    Dog() {
        super();
        System.out.println("Dog constructor");
    }
}
```

Uses:

* Call parent constructor
* Access parent methods
* Access parent variables

---

# Method Overloading vs Method Overriding

| Overloading             | Overriding               |
| ----------------------- | ------------------------ |
| Same class              | Parent and child classes |
| Different parameters    | Same parameters          |
| Compile-time            | Run-time                 |
| No inheritance required | Inheritance required     |

---

# Collections (ArrayList)

Collections are used to store multiple objects dynamically.

```java
import java.util.ArrayList;

ArrayList<String> names = new ArrayList<>();
names.add("Nikitha");
names.add("Rahul");

System.out.println(names);
```

Common methods:

* add()
* remove()
* get()
* set()
* size()

ArrayList automatically increases its size when needed.

---

# Exception Handling

Exceptions are runtime errors that interrupt normal program execution.

## try-catch

```java
try {
    int result = 10 / 0;
} catch (ArithmeticException e) {
    System.out.println("Cannot divide by zero");
}
```

## finally

```java
finally {
    System.out.println("This block always executes");
}
```

---

# Custom Exception

```java
class InvalidAgeException extends Exception {
    InvalidAgeException(String message) {
        super(message);
    }
}
```

Custom exceptions help in validating application-specific conditions.

---

# Advantages of OOP

* Code reusability
* Better organization
* Easier debugging
* Improved scalability
* Data security through encapsulation
* Flexible design through abstraction and polymorphism

---

# Summary

OOP in Java is built around **classes and objects**. The four pillars—**Encapsulation, Inheritance, Polymorphism, and Abstraction**—form the foundation of object-oriented programming. Additional concepts such as **constructors, interfaces, method overloading and overriding, `this`, `super`, collections, and exception handling** are essential for writing structured and reusable Java programs.

These concepts are widely used in Java applications and projects, including systems like **student management, food ordering, banking, inventory management, and other real-world software applications**.
