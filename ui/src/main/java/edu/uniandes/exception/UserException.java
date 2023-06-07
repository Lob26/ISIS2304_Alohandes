package edu.uniandes.exception;

public class UserException extends RuntimeException {
    private enum Status {
        CANCEL("Usuario ha cancelado la operacion"),
        INVALID("Algo invalido");
        private final String msg;
        Status(String s) {this.msg = s;}
    }
    public UserException(String status) {super(Status.valueOf(status).msg);}
}
