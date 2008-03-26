package org.python.antlr;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Encoding {

    public static void main(String[] args) {
        String inputStr = "# Hello, my encoding= foo-bar.baz OK?";
        String encoding = readEncodingFromFile("foo.txt");
        System.err.println("Yay! " + encoding);
    }

    private static String readEncodingFromFile(String filename) {
        String encoding = "ascii";
        FileInputStream fstream;
        DataInputStream in;
        BufferedReader br;
        try {
            fstream = new FileInputStream(filename);
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            for (int i = 0; i < 2; i++) {
                String strLine = br.readLine();
                System.err.println("line:" + strLine);
                if (strLine == null) {
                    break;
                }
                String result = matchEncoding(strLine);
                if (result != null) {
                    encoding = result;
                    break;
                }
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return encoding;
    }

    private static String matchEncoding(String inputStr) {
        String patternStr = "coding[:=]\\s*([-\\w.]+)";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(inputStr);
        boolean matchFound = matcher.find();

        if (matchFound && matcher.groupCount() == 1) {
            String groupStr = matcher.group(1);
            return groupStr;
        } else {
            System.err.println("Yuck");
        }
        return null;
    }
}
