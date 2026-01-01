# <p align="center">OOP-ASSIGNMENT: <br> 🧾 Ticket Management System 🧾</p>
## <p align="center">📦 E3 Delivery (Final) 📦</p>

## Description

Final project delivery. The system evolves to manage **Company Clients** and **Services** (Transportation, Insurance, Events). Advanced billing logic (cross-discounts) and **Local Persistence** are implemented to save the state between executions, and the architecture is restructured using **Generics** and **Design Patterns** (Strategy) for ticket visualization.

-----

## 👥 Users (Clients and Cashiers)

  - **Cashier**: Identified by `UW` + 7 digits. Manages ticket creation.
  - **Client**:
      - **Individual**: Identified by `DNI`. Can only have Common Tickets.
      - **Company (New E3)**: Identified by `NIF`. Can hire Services and generate Company Tickets with special billing.

-----

## 🛒 Products and Services

The catalog is expanded with intangible items (Services):

  - **Products (E1/E2)**:
      - Standard, Meals (3-day expiration), Meetings (12h), and Customizable.
      - They maintain their pricing and expiration logic.
  - **Services (New E3)**:
      - **ID**: Sequential, ending in 'S' (e.g., `1S`, `2S`).
      - **No Initial Price**: The price is calculated later on the invoice. They are defined only by **Maximum Use Date**.
      - **Types**: Transportation, Events, Insurance.

-----

## 🎟️ Tickets and Logic (E3)

The system uses **Generics** to handle the different ticket types in a unified way.

  - **Common Ticket**:
      - For Individual Clients. Only contains Products.
  - **Company Ticket**:
      - **Services Only**: Does not print prices or discounts.
      - **Combined (Products + Services)**:
          - **Rule**: Must have at least **1 Product and 1 Service** to be closed.
          - **Discount**: A **15% discount** is applied to *products* for each *service* hired.
  - **Visualization (Strategy Pattern)**:
      - Printing is dynamically injected (**Strategy**) depending on the client type and content (Common vs. Company vs. Services).
  - **Persistence**:
      - All tickets and their states are saved locally and recovered upon restart.

-----

## 🖥️ Available Commands (E3)

```bash
# --- Clients/Cashiers ---
# Automatically detects if it is an Individual (DNI) or Company (NIF)
client add "<name>" <DNI|NIF> <email> <cashId>
client remove <id>
client list

cash add [id] "<name>" <email>
cash remove <id>
cash list
cash tickets <id>

# --- Products and Services ---
prod add [id] "<name>" <category> <price> [<maxPers>]
prod addFood [id] "<name>" <price> <expiration> <max_people>
prod addMeeting [id] "<name>" <price> <expiration> <max_people>

# New command for Services (E3)
# Types: Transport, Event, Insurance (defined by category)
prod add <expiration: yyyy-MM-dd> <category>

prod list
prod update <id> NAME|CATEGORY|PRICE <value>
prod remove <id>

# --- Tickets (E3 Logic) ---
# Flags: -c (combined), -p (product/default), -s (service)
# Must inject the correct display strategy.
ticket new [id] <cashId> <userId> -[c/p/s]

ticket add <ticketId> <cashId> <prodId> <amount> [--p <text>]
ticket remove <ticketId> <cashId> <prodId>

# Closes the ticket and applies formatting according to the Client (Company/Individual)
ticket print <ticketId> <cashId>
ticket list

# --- General ---
help
echo "<text>"
exit
```
## 📦 Deliverables

  - **Source code and packaging** (`jar`) compressed in a **zip** uploaded to Moodle.
      - **Executable**: `java -jar POO-practica-1-1.0-SNAPSHOT.jar "Ficheros de Prueba/Fichero Input.txt"`
  - **Persistence**: The data files must be generated within the project and attached.
  - **PDF Documentation**:
      - Explanation of the implemented **Persistence**.
      - Final **UML** Diagram.
      - Justification of the chosen **Design Pattern**.
      - Exception Management.

-----
## ⚠️ Considerations

  - **No Console Errors**: The user should not see Java exceptions (stack traces).
  - **Generics**: Mandatory use in the Ticket structure.
  - **Defense**: The use of the Design Pattern and the architecture must be justified in the oral defense.