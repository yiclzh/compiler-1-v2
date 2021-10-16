import java.io.*;
import java.util.HashSet;

public class CompilationEngine {

    private String currentToken;
    JackTokenizer jackTokenizer;
    BufferedWriter outputXML;
    private HashSet<String> type = new HashSet<>();
    private  HashSet<String> op = new HashSet<>();

    public CompilationEngine(String inputFile, String outputFile) throws Exception {
        jackTokenizer = new JackTokenizer(inputFile);
        outputXML = new BufferedWriter(new FileWriter(outputFile, false));

        if (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
            compileClass();
        }


    }

    private String setKeyword(Keyword keyword) {
        return keyword.name().toString().toLowerCase();
    }

    private void initializeType() {
        type.add("int");
        type.add("char");
        type.add("boolean");
    }

    private void initializeOp() {
        op.add("+");
        op.add("-");
        op.add("*");
        op.add("/");
        op.add("&");
        op.add("|");
        op.add("<");
        op.add(">");
        op.add("=");
    }

    private void eat(String targetString) throws Exception {
        if (jackTokenizer.getTokenStringOriginalInput().equalsIgnoreCase(targetString)) {
            if (jackTokenizer.hasMoreTokens()) {
                jackTokenizer.advance();
            }

        } else {
            throw new IllegalStateException("DOES NOT MATCH LANGUAGE GRAMMAR");
        }
    }

    private void compileClass() throws Exception {
        outputXML.write("<class>");
        outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        eat("class");
        outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
        jackTokenizer.advance();
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("{");
        while (jackTokenizer.getTokenStringOriginalInput().equals("static") || jackTokenizer.getTokenStringOriginalInput().equals("field")) {
            compileClassVarDec();
        }
        while (jackTokenizer.getTokenStringOriginalInput().equals("constructor") || jackTokenizer.getTokenStringOriginalInput().equals("function") ||
                jackTokenizer.getTokenStringOriginalInput().equals("method") ) {
            compileSubroutine();
        }
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("}");
        outputXML.write("</class>");
        outputXML.close();

    }

    private void compileClassVarDec() throws Exception {
        initializeType();
        outputXML.write("<classVarDec>");
        outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        jackTokenizer.advance();
        if (type.contains(jackTokenizer.getTokenStringOriginalInput())) {
            outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        } else {
            outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
        }
        jackTokenizer.advance();
        outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
        jackTokenizer.advance();
        while (jackTokenizer.getTokenStringOriginalInput().equals(",")) {
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat(",");
            outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
            jackTokenizer.advance();

        }
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat(";");
        outputXML.write("</classVarDec>");

    }

    private void compileSubroutine() throws Exception {
        initializeType();
        outputXML.write("<subroutineDec>");
        if (jackTokenizer.getTokenStringOriginalInput().equals("constructor") || jackTokenizer.getTokenStringOriginalInput().equals("function") ||
                jackTokenizer.getTokenStringOriginalInput().equals("method")) {
            outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        } else {
            outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
        }
        jackTokenizer.advance();
        if (type.contains(jackTokenizer.getTokenStringOriginalInput()) || jackTokenizer.getTokenStringOriginalInput().equals("void")) {
            outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        } else {
            outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
        }
        jackTokenizer.advance();
        outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
        jackTokenizer.advance();
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("(");
        compileParameterList();
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat(")");
        compileSubroutineBody();
        outputXML.write("</subroutineDec>" + "\n");
    }

    private void compileParameterList() throws Exception {
        initializeType();
        outputXML.write("<parameterList>");
        if (type.contains(jackTokenizer.getTokenStringOriginalInput())) {
            outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
            jackTokenizer.advance();
            outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
            jackTokenizer.advance();
            while (jackTokenizer.getTokenStringOriginalInput().equals(",")) {
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat(",");
                outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
                jackTokenizer.advance();
                outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
                jackTokenizer.advance();

            }
        }
        outputXML.write("</parameterList>");
    }

    private void compileSubroutineBody() throws Exception {
        outputXML.write("<subroutineBody>");

        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("{");
        while (setKeyword(jackTokenizer.keyword()).equals("var")) {
            compileVarDec();
        }
        compileStatements();
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("}");
        outputXML.write("</subroutineBody>");
    }

    private void compileVarDec() throws Exception {
        initializeType();
        outputXML.write("<varDec>");

        outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        eat("var");
        if(type.contains(jackTokenizer.getTokenStringOriginalInput())) {
            outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        } else {
            outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
        }
        jackTokenizer.advance();
        outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
        jackTokenizer.advance();
        while (jackTokenizer.getTokenStringOriginalInput().equals(",")) {
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat(",");
            outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
            jackTokenizer.advance();
        }
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat(";");


        outputXML.write("</varDec>");
    }

    private void compileStatements() throws Exception {
        outputXML.write("<statements>");
        if (setKeyword(jackTokenizer.keyword()).equals("let")) {
            compileLet();
            compileStatements();
        }

        if (setKeyword(jackTokenizer.keyword()).equals("if")) {
            compileIf();
            compileStatements();
        }

        if (setKeyword(jackTokenizer.keyword()).equals("while")) {
            compileWhile();
            compileStatements();
        }

        if (setKeyword(jackTokenizer.keyword()).equals("do")) {
            compileDo();
            compileStatements();
        }

        if (setKeyword(jackTokenizer.keyword()).equals("return")) {
            compileReturn();
            compileStatements();
        }
        outputXML.write("</statements>");
    }

    private void compileLet() throws Exception {
        outputXML.write("<letStatement>");

        outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        eat("let");
        outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
        jackTokenizer.advance();
        if (jackTokenizer.getTokenStringOriginalInput().equals("[")) {
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat("[");
            compileExpression();
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat("]");
        }
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("=");
        compileExpression();
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat(";");

        outputXML.write("</letStatement>");
    }

    private void compileIf() throws Exception {
        outputXML.write("<ifStatement>");

        outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        eat("if");
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("(");
        compileExpression();
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat(")");
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("{");
        compileStatements();
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("}");

        if (jackTokenizer.getTokenStringOriginalInput().equals("else")) {
            outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
            eat("else");
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat("{");
            compileStatements();
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat("}");
        }

        outputXML.write("</ifStatement>");
    }

    private void compileWhile() throws Exception {
        outputXML.write("<whileStatement>");

        outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        eat("while");
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("(");
        compileExpression();
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat(")");
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("{");
        compileStatements();
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("}");

        outputXML.write("</whileStatement>");
    }

    private void compileDo() throws Exception {
        outputXML.write("<doStatement>");

        outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        eat("do");
        compileTerm();
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat(";");

        outputXML.write("</doStatement>");
    }

    private void compileReturn() throws Exception {
        outputXML.write("<returnStatement>");

        outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        eat("return");
        if (!jackTokenizer.tokenType().equals(TokenType.SYMBOL)) {
            compileExpression();
        }
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat(";");

        outputXML.write("</returnStatement>");
    }

    private void compileExpression() throws Exception {
        initializeOp();
        outputXML.write("<expression>");
        compileTerm();
        while (op.contains(jackTokenizer.getTokenStringOriginalInput())) {
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            jackTokenizer.advance();
            compileTerm();
        }
        outputXML.write("</expression>");
    }

    private void compileTerm() throws Exception {
        outputXML.write("<term>");

        if (jackTokenizer.getTokenStringOriginalInput().equals("(")) {
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat("(");
            compileExpression();
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat(")");
        } else if (jackTokenizer.tokenType().equals(TokenType.INT_CONST)) {
            outputXML.write("<integerConstant>" + jackTokenizer.intVal() + "</integerConstant>");
            jackTokenizer.advance();
        } else if (jackTokenizer.tokenType().equals(TokenType.STRING_CONST)) {
            outputXML.write("<stringConstant>" + jackTokenizer.stringVal() + "</stringConstant>");
            jackTokenizer.advance();
        } else if (jackTokenizer.tokenType().equals(TokenType.KEYWORD)) {
            outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
            jackTokenizer.advance();
        } else if (jackTokenizer.tokenType().equals(TokenType.IDENTIFIER)) {
            outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
            jackTokenizer.advance();
            String nextToken = jackTokenizer.getTokenStringOriginalInput();

            if (nextToken.equals("[")) {
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat("[");
                compileExpression();
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat("]");
            } else if (nextToken.equals("(")) {
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat("(");
                compileExpressionList();
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat(")");
            } else if (nextToken.equals(".")) {
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat(".");
                outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
                jackTokenizer.advance();
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat("(");
                compileExpressionList();
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat(")");
            }
        } else if (jackTokenizer.getTokenStringOriginalInput().equals("-") || jackTokenizer.getTokenStringOriginalInput().equals("~")) {
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            jackTokenizer.advance();
            compileTerm();
        }

        outputXML.write("</term>");

    }

    private void compileExpressionList() throws Exception {
        outputXML.write("<expressionList>");

        compileExpression();
        while (jackTokenizer.getTokenStringOriginalInput().equals(",")) {
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat(",");
            compileExpression();
        }

        outputXML.write("</expressionList>");
    }


}
