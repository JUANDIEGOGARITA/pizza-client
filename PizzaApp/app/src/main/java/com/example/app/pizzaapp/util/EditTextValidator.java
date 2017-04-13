package com.example.app.pizzaapp.util;

import android.support.design.widget.TextInputLayout;
import android.widget.EditText;

import java.util.List;

/**
 * Created by juandiegoGL on 4/11/17.
 */

public class EditTextValidator {
    private static String BLANK_ERROR = "Name cannot be Blank";
    private static String EMPTY_ERROR = "";
    private static String FORMAT_ERROR = "Name format is not valid";
    private static String ALREADY_EXIST_ERROR = "Name already exist";

    public static boolean validateEditText(List<String> nameList, TextInputLayout textInputLayout, EditText editText) {
        String name = editText.getText().toString();
        if (name.isEmpty() || name.equals(EMPTY_ERROR)) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(BLANK_ERROR);
            return false;
        }

        if (!name.matches("[a-zA-Z 0-9]+")) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(FORMAT_ERROR);
            return false;
        }

        if (!nameList.isEmpty()) {
            for (int i = 0; i < nameList.size(); i++) {
                if (name.equalsIgnoreCase(nameList.get(i))) {
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError(ALREADY_EXIST_ERROR);
                    return false;
                }
            }
        }
        textInputLayout.setErrorEnabled(false);
        textInputLayout.setError("");
        return true;
    }
}