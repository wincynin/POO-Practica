<div align="center">
	<h1> OOP-ASSIGNMENT: <br> 🧾 Ticket Management System 🧾</h1>
</div>


## 🏬 UPM STORE (English)

This project is the first delivery (E1) for the Object-Oriented Programming (POO) course assignment. It is a command-line interface (CLI) application developed in Java to manage products and sales tickets for the UPM university store.

---

### 🚀 How to Run the Application

This project is built using Apache Maven.

#### 📋 Prerequisites

- Java 21 or higher
- Apache Maven

#### ⚙️ Compilation and Packaging

From the project’s root directory, run:

```bash
mvn clean package
```

This will generate a runnable .jar file:

```
target/POO-practica-1-1.0-SNAPSHOT.jar
```

#### ▶️ Execution

Run the application with:

```bash
java -jar target/POO-practica-1-1.0-SNAPSHOT.jar
```

---

### 🏗️ Justification of Class Design

The application follows Object-Oriented Principles such as Encapsulation, Separation of Concerns, and Data Integrity. Responsibilities are divided across specialized classes:

- **App.java — The Controller**
	- Entry point and main controller of the application, handling user interaction.
	- Deals only with console I/O and command parsing, delegating business logic to other classes.
	- Uses Regex to robustly parse user commands (supports quoted arguments with spaces).
	- Manages the lifecycle of Catalog and Ticket objects.

- **Catalog.java — Product Management**
	- Manages the collection of products available in the store.
	- Uses HashMap<Integer, Product> for efficient lookups, updates, and deletions.
	- The products map is private and can only be accessed via public methods (addProduct, removeProduct, etc.).
	- Enforces business rules, such as a maximum of 200 products and the uniqueness of product IDs.

- **Ticket.java — Sales and Discount Logic**
	- Represents a customer’s shopping cart and handles all price and discount calculations.
	- All discount logic is contained within the getTicketDetails() method.
	- Uses HashMap<Product, Integer> to store products and their quantities.
	- Enforces a maximum of 100 items per ticket, throwing an exception if the limit is exceeded.

- **Product.java — The Data Model**
	- A Plain Old Java Object (POJO) that represents a product.
	- The constructor and setters validate all inputs (e.g., prices cannot be negative).
	- The id field is final and immutable. The class is also final to prevent unsafe inheritance.
	- The overridden toString() method ensures the output consistently matches the required format.

- **ProductCategory.java — Type-Safe Categories**
	- An Enum representing the fixed set of product categories.
	- Prevents errors and typos that could occur with string-based categories (e.g., "ELECTRONICA" vs "ELECCTRONICA").
	- Each enum constant directly holds its associated discount percentage.

---

### 📚 Libraries Used

- **Java Standard Library** → Used for all core application logic. No external dependencies are required for the main application.
- **JUnit 5** → Included via Maven for unit testing.

<br>

---
<div align="center">
	<h3>📦 Entrega E1 📦</h3>
</div>

---
<br>

## Descripción

El cliente solicita un módulo de tickets que permita crear y gestionar productos, aplicar descuentos por categoría y emitir factura de un ticket ordenada alfabéticamente por nombre de producto.

---

## 🛒 Productos

- Cada producto tiene:
	- **ID**: Identificador único y positivo (no pueden existir dos productos con el mismo ID).
	- **Nombre**: No vacío y de menos de 100 caracteres.
	- **Categoría**: Una de las siguientes: `MERCH`, `PAPELERIA`, `ROPA`, `LIBRO`, `ELECTRONICA`.
	- **Precio**: Número mayor a 0, sin límite superior.
- Máximo **200 productos** diferentes en el sistema.

---

## 🎟️ Tickets y Descuentos

- Al generar un ticket, se aplican descuentos automáticos si hay más de un producto de la misma categoría:
	- `MERCH`: **0%**
	- `PAPELERIA`: **5%**
	- `ROPA`: **7%**
	- `LIBRO`: **10%**
	- `ELECTRONICA`: **3%**
- El ticket se imprime ordenado alfabéticamente por nombre de producto.
- Máximo **100 productos** por ticket.
- Se puede reiniciar el ticket en cualquier momento.
- Al modificar el ticket, se imprime el importe provisional con descuentos aplicados.
- Eliminar un producto borra todas sus apariciones en el ticket.

---

## 🖥️ Comandos Disponibles

```bash
prod add <id> "<nombre>" <categoria> <precio>   # Agrega un producto con nuevo id
prod list                                        # Lista productos actuales
prod update <id> campo valor                     # Actualiza campo (nombre|categoria|precio)
prod remove <id>                                 # Elimina producto por id
ticket new                                       # Resetea ticket en curso
ticket add <prodId> <cantidad>                   # Agrega cantidad de producto al ticket
ticket remove <prodId>                           # Elimina todas las apariciones del producto
ticket print                                     # Imprime factura
help                                             # Lista los comandos
echo "<texto>"                                   # Imprime el texto
exit                                             # Cierra la aplicación
```

## 📦 Entregables

- Código fuente y empaquetado (`jar`) comprimidos en un **zip** subido a Moodle.
- Diagrama **UML** del modelo propuesto en formato legible (PNG, JPG, SVG, ...).
	- Justificación del diseño de clases y uso de librerías (si las hubiera).

---

## ⚠️ Consideraciones

- El código entregado **no debe tener errores de compilación**.
- Se debe respetar el formato de entrada y salida de comandos especificado.
- **No** se pueden crear más comandos de los pedidos en el enunciado.
- El código debe ejecutar sin errores todos los comandos del ejemplo propuesto (verificado en la defensa).

---