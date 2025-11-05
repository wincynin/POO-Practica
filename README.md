# <p align="center">OOP-ASSIGNMENT: <br> 🧾 Ticket Management System 🧾</p>
## <p align="center">📦 Entrega E2 📦</p>

## Descripción

El cliente amplía el alcance del módulo de tickets. El sistema ahora incorpora **Usuarios** (que pueden ser **Clientes** o **Cajeros**) y gestiona múltiples tickets. Se añaden nuevos tipos de productos: **Comidas** y **Reuniones** (con fechas de caducidad y precios por persona) y **Productos Personalizables** (con un recargo del 10% por cada personalización).

-----

## 👥 Usuarios (Clientes y Cajeros)

  - **Cliente**: Identificado por `DNI`. Al darse de alta, se asocia con el `cashId` del cajero que lo registró.
  - **Cajero (Cashier)**: Identificado por un código "UW" + 7 dígitos aleatorios (si no se proporciona uno). Los cajeros guardan una lista de los tickets que han creado.

-----

## 🛒 Productos

Además de los productos básicos de E1, se añaden:

  - **Productos Base (E1)**:
      - ID, Nombre (\<100 caracteres), Categoría (Enum), Precio (\>0).
      - Máximo **200 productos** en el catálogo.
  - **Comidas y Reuniones (E2)**:
      - No tienen categoría.
      - Tienen fecha de caducidad y un **máximo de 100 participantes**.
      - El precio se calcula por persona.
      - Restricción de tiempo: Las comidas requieren 3 días de antelación, y las reuniones 12 horas.
  - **Productos Personalizables (E2)**:
      - Tienen un número máximo de textos personalizables.
      - El precio tiene un **recargo del 10%** sobre el precio base *por cada texto* agregado.
      - Se pueden añadir al ticket sin personalizar.

-----

## 🎟️ Tickets y Lógica

  - El sistema ahora gestiona **múltiples tickets**.
  - **ID del Ticket**: Se genera automáticamente (`YY-MM-dd-HH:mm-` + 5 dígitos aleatorios) si no se provee. Al cerrarse, se añade la fecha de cierre al ID.
  - **Asociación**: Cada ticket está vinculado a un `cashId` y un `userId`.
  - **Estado**: Los tickets tienen un estado `VACIO`, `ACTIVO` o `CERRADO`.
  - **Permisos**: Solo el cajero que creó un ticket puede añadir, eliminar o imprimir (cerrar) dicho ticket.
  - **Borrado**: Si se borra un Cajero, todos los tickets creados por él también se borran.
  - **Límites**: Un ticket no puede tener más de **100 líneas** de producto (de E1).
  - **Impresión**: La impresión del ticket lo cierra (`CERRADO`) y debe seguir ordenado alfabéticamente por nombre de producto (de E1).

-----

## 🖥️ Comandos Disponibles (E2)

```bash
# --- Clientes/Cajeros ---
client add "<nombre>" <DNI> <email> <cashId>
client remove <DNI>
client list

cash add [id] "<nombre>" <email>
cash remove <id>
cash list
cash tickets <id>

# --- Productos (Actualizados) ---
prod add [id] "<name>" <category> <price> [<maxPers>]
prod addFood [id] "<name>" <price> <expiration: yyyy-MM-dd> <max_people>
prod addMeeting [id] "<name>" <price> <expiration: yyyy-MM-dd> <max_people>
prod list
prod update <id> NAME|CATEGORY|PRICE <value>
prod remove <id>

# --- Tickets (Actualizados) ---
ticket new [id] <cashId> <userId>
ticket add <ticketId> <cashId> <prodId> <amount> [--p <text>]
ticket remove <ticketId> <cashId> <prodId>
ticket print <ticketId> <cashId>
ticket list

# --- Generales ---
help
echo "<text>"
exit
```

## 📦 Entregables

  - Código fuente y empaquetado (`jar`) comprimidos en un **zip** subido a Moodle.
      - El código debe poder ejecutarse pasando un archivo `.txt` como argumento.
      - Ejemplo: `java -jar POO-practica-1-1.0-SNAPSHOT.jar "Ficheros de Prueba/Fichero Input D.txt"`
  - Diagrama **UML** del modelo propuesto en formato legible (PNG, JPG, SVG, ...).
      - Justificación del diseño de clases y uso de librerías (si las hubiera).

-----

## ⚠️ Consideraciones

  - El código entregado **no debe tener errores de compilación**.
  - Se debe respetar el formato de entrada y salida de comandos especificado.
  - **No** se pueden crear más comandos de los pedidos en el enunciado.
  - El código debe ejecutar sin errores todos los comandos del ejemplo propuesto (verificado en la defensa).