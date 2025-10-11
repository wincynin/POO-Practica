<div align="center">
	<h1> OOP-ASSIGNMENT: <br> 🧾 Ticket Management System 🧾</h1>
</div>
<div align="center">
	<h3>📦 Entrega E1 📦</h3>
</div>

---

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