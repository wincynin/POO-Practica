package es.upm.etsisi.poo.domain.ticket;

// Represents ticket print types with their associated flags as defined by E3.
public enum TicketPrintType {
    COMPANY('c'),
    SERVICE('s'),
    STANDARD('p');

    private final char flag;

    TicketPrintType(char flag) {
        this.flag = flag;
    }

    public char getFlag() {
        return flag;
    }

    public static TicketPrintType fromFlag(char flag) {
        for (TicketPrintType type : TicketPrintType.values()) {
            if (type.getFlag() == flag) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ticket print type flag: " + flag);
    }
}
