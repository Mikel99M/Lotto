package com.lotto.domain.resultannouncer;

public enum ResponseMessages {

    NO_TICKET_WITH_THIS_HASH_FOUND("No ticket with this hash found"),
    TICKET_HAS_WON("Ticket has won"),
    TICKET_HAS_LOST("Ticket has lost"),
    IT_IS_BEFORE_DRAW_DATE("It is before draw date");

    final String info;

    ResponseMessages(String info) {
        this.info = info;
    }

}
