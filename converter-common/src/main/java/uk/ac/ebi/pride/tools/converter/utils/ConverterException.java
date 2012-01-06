package uk.ac.ebi.pride.tools.converter.utils;

public class ConverterException extends RuntimeException {

    public ConverterException() {
    }

    public ConverterException(String message) {
        super(message);
    }

    public ConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConverterException(Throwable cause) {
        super(cause);
    }
}
