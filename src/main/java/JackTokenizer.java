
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JackTokenizer {

    private BufferedReader bufferedReader;
    private StringBuilder strippedList = new StringBuilder();
    private static HashSet<String> symbols = new HashSet<String>();
    private static HashSet<String> keywords = new HashSet<>();
    private ArrayList<String> arrayTokens;
    private String currentToken;
    private int index = -1;



    public JackTokenizer(String inputFile) throws IOException {

        FileInputStream fileInputStream = new FileInputStream(inputFile);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        bufferedReader = new BufferedReader(inputStreamReader);

        while(bufferedReader.ready()) {
            char c = (char)bufferedReader.read();
            strippedList.append(c);
        }

        String text = strippedList.toString();
        String outputText = removeComments(text);
        System.out.println(outputText); //without comments
        arrayTokens = tokenizer(outputText);
        arrayTokens = removeEmptyStrings(arrayTokens);
    }

    private void initializeSymbols() {
        symbols.add("{");
        symbols.add("}");
        symbols.add("(");
        symbols.add(")");
        symbols.add("[");
        symbols.add("]");
        symbols.add(".");
        symbols.add(",");
        symbols.add(";");
        symbols.add("+");
        symbols.add("-");
        symbols.add("*");
        symbols.add("/");
        symbols.add("&");
        symbols.add("|");
        symbols.add("<");
        symbols.add(">");
        symbols.add("=");
        symbols.add("~");

        keywords.add("class");
        keywords.add("method");
        keywords.add("function");
        keywords.add("constructor");
        keywords.add("field");
        keywords.add("static");
        keywords.add("var");
        keywords.add("int");
        keywords.add("char");
        keywords.add("boolean");
        keywords.add("void");
        keywords.add("true");
        keywords.add("false");
        keywords.add("null");
        keywords.add("this");
        keywords.add("let");
        keywords.add("do");
        keywords.add("if");
        keywords.add("else");
        keywords.add("while");
        keywords.add("return");

    }

    private ArrayList removeEmptyStrings(ArrayList<String> tokens) {
        ArrayList<String> result = new ArrayList<>();
        for (String t : tokens) {
            if (t != null && !t.trim().isEmpty()) {
                result.add(t.trim());
            }
        }

        return result;
    }

    private ArrayList tokenizer(String inputString) {

        StringBuilder token = new StringBuilder();
        ArrayList<String> tokenizedList = new ArrayList();
        boolean tokenStarted = false;
        boolean tokenEnded = false;
        boolean stringStarted = false;
        boolean endString = false;
        char currentChar;
        String previousToken = "";
        initializeSymbols();

        for (int i = 0; i < inputString.length() -1; i++) {
            currentChar = inputString.charAt(i);
            char nextChar = inputString.charAt(i+1);
            String nextToken = Character.toString(nextChar);
            String currentToken = Character.toString(currentChar);

            if (stringStarted == true) {
                if (nextToken.equals("\"")) {
                    token.append(currentToken);
                    currentChar = inputString.charAt(i +2);
                    currentToken = Character.toString(currentChar);
                    stringStarted = false;
                    tokenizedList.add(token.toString());
                    token.setLength(0);
                    i = i+2;
                } else {
                    token.append(currentToken);
                    if (currentToken.equals("\"")) {
                        token.setLength(0);
                    }
                }
            }

            if (currentToken.equals("\"")) {
                stringStarted = true;
                if (token.length() != 0) {
                    tokenizedList.add(token.toString());
                    token.setLength(0);
                }

            }


            if (currentToken.equals(" ") || currentToken.equals("\n")) {
                if (stringStarted == true){

                } else {
                    if (token.length() != 0) {
                        tokenizedList.add(token.toString());
                        token.setLength(0);
                    }
                }

            }
            if (symbols.contains(currentToken) && stringStarted == false) {
                    if (token.length() != 0) {
                        tokenizedList.add(token.toString());
                    }
                    tokenizedList.add(currentToken);
                    token.setLength(0);
            }

            else if (!symbols.contains(currentToken) && !currentToken.equals(" ") && !currentToken.equals("\n") && stringStarted == false) {
                if (currentToken.equals("\"")) {

                } else {
                    token.append(currentChar);
                }
            }
        }
        return tokenizedList;
    }

    private String removeComments(String file) {

        StringBuilder noComments = new StringBuilder();
        char previousChar = ' ';
        boolean lineCommentStarted = false;
        boolean multiLineCommentStarted = false;

        for (int i = 0; i <file.length(); i++) {
            char c = file.charAt(i);
            if (previousChar == '/' && c == '/') {
                int lastChar = noComments.length() - 1;
                noComments.deleteCharAt(lastChar);
                lineCommentStarted = true;
                previousChar = c;
            }
            if (previousChar == '/' && c == '*') {
                int lastChar = noComments.length() -1;
                noComments.deleteCharAt(lastChar);
                multiLineCommentStarted = true;
                previousChar = c;
            }
            if (c == '\n') {
                lineCommentStarted = false;
                previousChar = c;
            }
            if (previousChar == '*' && c == '/') {
                multiLineCommentStarted = false;
                previousChar = c;
            }
            else if (multiLineCommentStarted == false && lineCommentStarted == false) {
                previousChar = c;
                noComments.append(previousChar);
            }
            previousChar = c;

        }

        return noComments.toString();

    }

    private boolean matchIntConstant(String input){
        Pattern intConstant = Pattern.compile("\\d+");
        Matcher m = intConstant.matcher(input);
        boolean b = m.matches();
        return b;
    }

    private boolean matchStrConstant(String input) {
        Pattern strConstant = Pattern.compile("[^\"\n]+");
        Matcher m = strConstant.matcher(input);
        boolean b = m.matches();
        return b;
    }

    private boolean matchIdentifier(String input) {
        Pattern identifier = Pattern.compile("[a-zA-Z_][a-zA-Z_0-9]*");
        Matcher m = identifier.matcher(input);
        boolean b = m.matches();
        return b;
    }

    public boolean hasMoreTokens() {
        return index < arrayTokens.size() -1;
    }

    public void advance() {
        index++;
        currentToken = arrayTokens.get(index);
    }


    public TokenType tokenType() {
        if (keywords.contains(currentToken)) {
            return TokenType.KEYWORD;
        }
        if (symbols.contains(currentToken)) {
            return TokenType.SYMBOL;
        }
        if (matchIntConstant(currentToken)) {
            return TokenType.INT_CONST;
        }
        if (matchIdentifier(currentToken)) {
            return TokenType.IDENTIFIER;
        }
        if (matchStrConstant(currentToken)) {
            return TokenType.STRING_CONST;
        }
        return TokenType.None;
    }


    public Keyword keyword() {
        if (tokenType().equals(TokenType.KEYWORD) && keywords.contains(currentToken)) {
            if (currentToken.equals("class")) {
                return Keyword.CLASS;
            }
            if (currentToken.equals("method")) {
                return Keyword.METHOD;
            }
            if (currentToken.equals("function")) {
                return Keyword.FUNCTION;
            }
            if (currentToken.equals("constructor")) {
                return Keyword.CONSTRUCTOR;
            }
            if (currentToken.equals("int")) {
                return Keyword.INT;
            }
            if (currentToken.equals("boolean")) {
                return Keyword.BOOLEAN;
            }
            if (currentToken.equals("char")) {
                return Keyword.CHAR;
            }
            if (currentToken.equals("void")) {
                return Keyword.VOID;
            }
            if (currentToken.equals("var")) {
                return Keyword.VAR;
            }
            if (currentToken.equals("static")) {
                return Keyword.STATIC;
            }
            if (currentToken.equals("field")) {
                return Keyword.FIELD;
            }
            if (currentToken.equals("let")) {
                return Keyword.LET;
            }
            if (currentToken.equals("do")) {
                return Keyword.DO;
            }
            if (currentToken.equals("if")) {
                return Keyword.IF;
            }
            if (currentToken.equals("else")) {
                return Keyword.ELSE;
            }
            if (currentToken.equals("while")) {
                return Keyword.WHILE;
            }
            if (currentToken.equals("return")) {
                return Keyword.RETURN;
            }
            if (currentToken.equals("true")) {
                return Keyword.TRUE;
            }
            if (currentToken.equals("false")) {
                return Keyword.FALSE;
            }
            if (currentToken.equals("null")) {
                return Keyword.NULL;
            }
            if (currentToken.equals("this")) {
                return Keyword.THIS;
            }
        }
        return Keyword.None;
    }

    public char symbol() {
        if (tokenType().equals(TokenType.SYMBOL) && symbols.contains(currentToken)) {
            return currentToken.charAt(0);
        }
        return ' ';
    }

    public String identifier() {
        if (tokenType().equals(TokenType.IDENTIFIER)) {
            return currentToken;
        }
        return null;
    }

    public int intVal() {
        if (tokenType().equals(TokenType.INT_CONST)) {
            return Integer.valueOf(currentToken);
        }
        return -1;
    }

    public String stringVal() {
        if (tokenType().equals(TokenType.STRING_CONST)) {
            return currentToken;
        }
        return null;
    }

    public String getTokenStringOriginalInput() {
        return currentToken;
    }

}
