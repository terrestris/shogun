package de.terrestris.shogun.importer.validator;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @author Andre Henn
 */
public class TableNameValidator {

    /**
     * assumption only lower case letters
     */
    protected static final String[] SEARCH_ARRAY = {"ä", "ü", "ö", "ß", "õ", " ", ".-", "-", ",", "+", "´", "(", ")", ":"};
    /**
     * Constant <code>REPLACEMENT_ARRAY</code>
     */
    protected static final String[] REPLACEMENT_ARRAY = {"ae", "ue", "oe", "sz", "oe", "_", "", "_", "_", "u", "", "_", "_", "_"};

    /**
     * constant value of maximum table name length in db
     */
    private static int MAX_TABLE_NAME_WIDTH = 30;

    /**
     * Apply naming convention to the input string
     * using {@link org.apache.commons.lang3.StringUtils}
     *
     * @param oldName input string
     * @return converted string according to naming convention
     */
    public static String createValidTableName(String oldName) {

        assert (SEARCH_ARRAY.length == REPLACEMENT_ARRAY.length) : "Search array and replacement array in class NamingConvention have different sizes: " + SEARCH_ARRAY.length + " vs. " + REPLACEMENT_ARRAY.length;

        String lowerCaseString = StringUtils.lowerCase(oldName);
        String val = StringUtils.replaceEachRepeatedly(lowerCaseString, SEARCH_ARRAY, REPLACEMENT_ARRAY);
        if (val.length() > MAX_TABLE_NAME_WIDTH) {
            val = StringUtils.left(val, MAX_TABLE_NAME_WIDTH);
        }
        return val;
    }

    /**
     * check, if filename / tablename / attributename is valid
     *
     * @param s filename / tablename / attributename
     * @return <code>true</code> valid; <code>false</code> not valid
     */
    public static boolean isValidName(String s) {
        if (Pattern.matches("[a-z0-9\\.\\_\\/]+", s)) {
            return true;
        }
        return false;
    }
}

