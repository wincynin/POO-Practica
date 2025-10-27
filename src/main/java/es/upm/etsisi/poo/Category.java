/*ENUM DE CATEGORÍAS
Category (enum)
  Valores: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS
  Metodo auxiliar:
    double getDiscount() → devuelve el porcentaje correspondiente:
      MERCH → 0%
      STATIONERY → 5%
      CLOTHES → 7%
      BOOK → 10%
      ELECTRONICS → 3%
*/
package es.upm.etsisi.poo;

public enum Category {
    MERCH(0.0),
    STATIONERY(0.05),
    CLOTHES(0.07),
    BOOK(0.10),
    ELECTRONICS(0.03);

    private final double discount;

    Category(double discount) {
        this.discount = discount;
    }

    /**
     * Devuelve el descuento asociado a la categoría.
     * @return descuento en formato decimal (ejemplo: 0.10 = 10%)
     */
    public double getDiscount() {
        return discount;
    }

}