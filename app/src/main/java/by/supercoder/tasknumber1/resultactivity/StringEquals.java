package by.supercoder.tasknumber1.resultactivity;

/**
 * Created by user on 28.07.2017.
 */
public class StringEquals {
    // This method delete exception when used method equals and str1 == null
    public static boolean equals(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }
}
