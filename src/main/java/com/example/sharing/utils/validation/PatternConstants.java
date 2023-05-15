package org.example.sharing.utils.validation;

public class PatternConstants {

    public static final String PASSWORD = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$";

    public static final String MAIL = "^\\w+@[a-zA-Z\\d]+\\.[a-zA-Z\\d]{2,6}$";
}
