POO-Practica: Tienda UPM - E1
This project is the first delivery (E1) for the Object-Oriented Programming course assignment. It is a command-line interface (CLI) application developed in Java to manage products and sales tickets for the UPM university store.

How to Run the Application
This project is built using Apache Maven.

Prerequisites
Java 21 or higher

Apache Maven

Compilation and Packaging
To compile the source code and package it into a runnable .jar file, navigate to the project's root directory and run the following command:

mvn clean package

This will generate a POO-practica-1-1.0-SNAPSHOT.jar file in the target/ directory.

Execution
To run the application, use the following command from the root directory:

java -jar target/POO-practica-1-1.0-SNAPSHOT.jar

Justification of Class Design
The application is designed following key Object-Oriented principles such as Encapsulation, Separation of Concerns, and Data Integrity. The responsibilities are divided among several specialized classes.

1. App.java - The Controller
Role: This class acts as the main controller and entry point of the application. It is responsible for the user-facing logic.

Design Justification:

Separation of Concerns: App.java handles all user interaction (reading input from the console) and command parsing. It does not contain any business logic related to products or tickets; instead, it delegates these tasks to the Catalog and Ticket classes. This makes the code modular and easier to maintain.

Command Parsing: A Regex Pattern is used to parse user commands. This approach was chosen for its robustness, as it correctly handles arguments that contain spaces when enclosed in double quotes (e.g., product names), a common challenge in CLI applications.

State Management: It manages the lifecycle of the Catalog and Ticket objects, creating new instances as required by the application flow (e.g., creating a new Ticket after printing).

2. Catalog.java - Product Management
Role: This class is responsible for managing the entire collection of products available in the store.

Design Justification:

Data Structure: A HashMap<Integer, Product> is used to store the products. This was a deliberate choice for efficiency. It allows for average-case O(1) time complexity for crucial operations like finding, updating, or removing a product by its unique ID.

Encapsulation: The products map is private and can only be modified through the public methods (addProduct, removeProduct, etc.). This encapsulates the product management logic and protects the data from uncontrolled external access.

Rule Enforcement: This class enforces the business rules specified in the PDF, such as the maximum limit of 200 products and the constraint that all product IDs must be unique.

3. Ticket.java - Sales and Discount Logic
Role: This class models a customer's shopping cart. It manages the items to be purchased and handles all price and discount calculations.

Design Justification:

Encapsulation of Complexity: The most complex logic in the application—the calculation of category-based discounts—is entirely encapsulated within this class. The getTicketDetails() method handles counting categories, applying discounts conditionally, sorting the output, and formatting the final invoice. This prevents the complex logic from leaking into other parts of the application.

Data Structure: A HashMap<Product, Integer> is used to store the items and their respective quantities. This is an efficient way to manage a collection where each unique item can have a variable quantity.

State Integrity: The class enforces the rule that a ticket cannot contain more than 100 items in total, throwing an exception if this limit is exceeded.

4. Product.java - The Data Model
Role: This is a model class (or POJO - Plain Old Java Object) that represents a single product.

Design Justification:

Data Integrity: Validation logic is built directly into the constructor and setters. This guarantees that it is impossible to create a Product object in an invalid state (e.g., with a negative price or an empty name).

Immutability and Safety: The id field is final and can only be set in the constructor. This makes a product's identity immutable, which is crucial for data consistency. The entire class is also declared as final, which prevents inheritance and resolves potential issues with overridable methods being called in the constructor, ensuring robust and predictable behavior.

toString() Method: The toString() method is overridden to produce the exact output format required by the project specifications, promoting code reuse and consistency.

5. ProductCategory.java - Type-Safe Categories
Role: Represents the fixed set of product categories.

Design Justification:

Type Safety: An enum was chosen because there is a predefined, fixed list of categories. This prevents errors that could arise from using simple strings (e.g., typos like "ELECCTRONICA").

Encapsulation: The discount percentage is directly associated with its category within the enum. This is a powerful form of encapsulation, ensuring that the data (the discount rate) is stored right next to the entity it belongs to.

Libraries Used
Java Standard Library: No external libraries were required for the core application logic.

JUnit 5: Used for unit testing, as included by the standard Maven archetype.