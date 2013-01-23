package uk.ac.lifesci.dundee.tools.converter;

public class Utils<T> {

    protected T nvl(T value, T replacement) {
        return (value == null) ? replacement : value;
    }

}
