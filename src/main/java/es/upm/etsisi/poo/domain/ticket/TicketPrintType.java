package es.upm.etsisi.poo.domain.ticket;

public enum TicketPrintType {
    SERVICE('s'),
    COMPANY('c'),
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
