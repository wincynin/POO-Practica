# AI AGENT CONTEXT & PROJECT RULES (UPM POO)

## 1. Project Overview
* **Subject:** Object Oriented Programming (UPM - ETSISI).
* **Current Phase:** **Entrega 3 (Final)**.
* **Goal:** 70% of final grade. Requires strict adherence to OOP theory, **Design Patterns**, and oral defense.
* **Language:** Java 21+ (Maven Project).
* **Persistence:** Local persistence (files) required.

## 2. STRICT ACADEMIC CONSTRAINTS (DO NOT VIOLATE)
* **Forbidden Libraries:** No Spring, Lombok, Hibernate, or database drivers. Use standard Java `java.io` / `java.nio` for persistence.
* **Syllabus Alignment:**
    * **Generics:** MANDATORY. [cite_start]You must use generic classes for Ticket management[cite: 10998].
    * **Design Patterns:** MANDATORY. [cite_start]You must implement at least one pattern (e.g., Strategy for printing, Factory for products, Singleton for Store)[cite: 10999].
    * **Exceptions:** Do NOT print stack traces to the console. [cite_start]Handle errors gracefully with custom exceptions[cite: 11003].

## 3. Architecture & Domain
* **Pattern:** Hexagonal / Clean Architecture (Separation of concerns).
* **Packages:**
    * `es.upm.etsisi.poo.domain`: Pure business logic (Entities, Interfaces).
    * `es.upm.etsisi.poo.application`: Use cases / Services.
    * `es.upm.etsisi.poo.ui`: Command Line Interface / Console interaction.
    * `es.upm.etsisi.poo.infrastructure`: Persistence implementations (File I/O).

## 4. Grading Criteria (The "Nagging" Points)
* [cite_start]**Polymorphism:** Use it for the different Ticket types (Company vs. Common) and Product types[cite: 10992].
* [cite_start]**Injection:** Inject the "Print Behavior" into the Ticket class (Strategy Pattern) to handle the different visualizations[cite: 11008].
* **Static Abuse:** Do not use `static` variables to share state. Pass the `Store` or `Catalog` instance dependencies via constructors.
* **Magic Numbers:** Use constants (final static) for fixed values (e.g., the 15% discount, tax rates).

## 5. Defense Preparation
* If suggesting code, explain *which* Design Pattern it uses and *why*.
* Prepare me to answer: "Why did you use Generics here?" and "How is the dependency injection working?"