import java.io.*;
import java.util.HashSet;

public class CompilationEngine {

    private String currentToken;
    JackTokenizer jackTokenizer;
    BufferedWriter outputXML;
    private HashSet<String> type = new HashSet<>();
    private  HashSet<String> op = new HashSet<>();
    private HashSet<String> statements = new HashSet<>();
    SymbolTable classLevelSymbolTable = new SymbolTable();
    SymbolTable subroutineLevelSymbolTable = new SymbolTable();
    VMWriter vmWriter;

    String subroutineName;
    String subroutineType;
    String className;

    public CompilationEngine(String inputFile, String outputFile) throws Exception {
        jackTokenizer = new JackTokenizer(inputFile);
        outputXML = new BufferedWriter(new FileWriter(outputFile, false));


        vmWriter = new VMWriter(outputFile + ".vm");

        if (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
            compileClass();
        }


    }

    private String setKeyword(Keyword keyword) {
        return keyword.name().toString().toLowerCase();
    }

    private void initializeStatements() {
        statements.add("let");
        statements.add("do");
        statements.add("return");
        statements.add("if");
        statements.add("while");
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
        className = jackTokenizer.identifier();
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
        vmWriter.close();

    }

    private void compileClassVarDec() throws Exception {
        String name;
        String varType;
        Kind varKind;
        initializeType();
        outputXML.write("<classVarDec>");
        outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        if (setKeyword(jackTokenizer.keyword()).equals("field")) {
            varKind = Kind.FIELD;
        }else {
            varKind = Kind.STATIC;
        }
        jackTokenizer.advance();
        if (type.contains(jackTokenizer.getTokenStringOriginalInput())) {
            varType = setKeyword(jackTokenizer.keyword());
            outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        } else {
            varType = jackTokenizer.identifier();
            outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
        }
        jackTokenizer.advance();
        outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
        name = jackTokenizer.identifier();
        jackTokenizer.advance();

        classLevelSymbolTable.define(name, varType, varKind);

        while (jackTokenizer.getTokenStringOriginalInput().equals(",")) {
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat(",");
            outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
            classLevelSymbolTable.define(jackTokenizer.identifier(), varType, varKind);
            jackTokenizer.advance();

        }
        System.out.println(classLevelSymbolTable.toString());
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat(";");
        outputXML.write("</classVarDec>");


    }

    private void compileSubroutine() throws Exception {
        subroutineLevelSymbolTable = new SymbolTable();
        subroutineLevelSymbolTable.startSubroutine();
        initializeType();
        outputXML.write("<subroutineDec>");
        if (jackTokenizer.getTokenStringOriginalInput().equals("constructor") || jackTokenizer.getTokenStringOriginalInput().equals("function") ||
                jackTokenizer.getTokenStringOriginalInput().equals("method")) {
//            outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
            subroutineType = jackTokenizer.getTokenStringOriginalInput();
        } else {
            subroutineType = null;
        }
        jackTokenizer.advance();
        if (type.contains(jackTokenizer.getTokenStringOriginalInput()) || jackTokenizer.getTokenStringOriginalInput().equals("void")) {
            outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        } else {
            outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
        }
        jackTokenizer.advance();
        outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
        subroutineName = jackTokenizer.identifier();

        if (subroutineType.equals("constructor")) {
            vmWriter.writeFunction(className + "." + subroutineName, classLevelSymbolTable.fieldIndex);
        }

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
        String parameterType;
        String name;
        initializeType();
        outputXML.write("<parameterList>");
        if (type.contains(jackTokenizer.getTokenStringOriginalInput())) {
            outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
            parameterType = setKeyword(jackTokenizer.keyword());
            jackTokenizer.advance();
            outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
            name = jackTokenizer.identifier();
            jackTokenizer.advance();
            subroutineLevelSymbolTable.define(name, parameterType, Kind.ARG);
            while (jackTokenizer.getTokenStringOriginalInput().equals(",")) {
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat(",");
                outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
                parameterType = setKeyword(jackTokenizer.keyword());
                jackTokenizer.advance();
                outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
                name = jackTokenizer.identifier();
                jackTokenizer.advance();
                subroutineLevelSymbolTable.define(name, parameterType, Kind.ARG);

            }
        }
        System.out.println(subroutineLevelSymbolTable);
        outputXML.write("</parameterList>");
    }

    private void compileSubroutineBody() throws Exception {
        outputXML.write("<subroutineBody>");

        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("{");
        if (setKeyword(jackTokenizer.keyword()).equals("var")) {
            while (setKeyword(jackTokenizer.keyword()).equals("var")) {
                compileVarDec();
            }

            if (subroutineType.equals("function")) {
                vmWriter.writeFunction(className + "." + subroutineName, subroutineLevelSymbolTable.varIndex);
            }
            if (subroutineType.equals("method")) {
                vmWriter.writeFunction(className + "." + subroutineName, subroutineLevelSymbolTable.varIndex);
                vmWriter.writePop(Segment.ARG, 0);
                vmWriter.writePop(Segment.POINTER, 0);
            }

        } else {
            if (subroutineType.equals("method")) {
                vmWriter.writeFunction(className + "." + subroutineName, 0);
                vmWriter.writePush(Segment.ARG, 0);
                vmWriter.writePop(Segment.POINTER, 0);
            }
            if (subroutineType.equals("function")) {
                vmWriter.writeFunction(className + "." +subroutineName, 0);
            }
        }


        compileStatements();
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("}");
        outputXML.write("</subroutineBody>");
    }

    private void compileVarDec() throws Exception {
        String name;
        String varDecType;

        initializeType();
        outputXML.write("<varDec>");

        outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        eat("var");
        if(type.contains(jackTokenizer.getTokenStringOriginalInput())) {
            outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
            varDecType = setKeyword(jackTokenizer.keyword());
        } else {
            outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
            varDecType = jackTokenizer.identifier();
        }
        jackTokenizer.advance();
        outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
        name = jackTokenizer.identifier();
        jackTokenizer.advance();

        subroutineLevelSymbolTable.define(name, varDecType, Kind.VAR);

        while (jackTokenizer.getTokenStringOriginalInput().equals(",")) {
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat(",");
            outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
            name = jackTokenizer.identifier();
            jackTokenizer.advance();

            subroutineLevelSymbolTable.define(name, varDecType, Kind.VAR);
        }
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat(";");

        System.out.println(subroutineLevelSymbolTable.toString());

        outputXML.write("</varDec>");
    }

    private void compileStatements() throws Exception {
        initializeStatements();
        outputXML.write("<statements>");
        while (statements.contains(setKeyword(jackTokenizer.keyword()))) {
            if (setKeyword(jackTokenizer.keyword()).equals("let")) {
                compileLet();
            }

            if (setKeyword(jackTokenizer.keyword()).equals("if")) {
                compileIf();
            }

            if (setKeyword(jackTokenizer.keyword()).equals("while")) {
                compileWhile();
            }

            if (setKeyword(jackTokenizer.keyword()).equals("do")) {
                compileDo();
            }

            if (setKeyword(jackTokenizer.keyword()).equals("return")) {
                compileReturn();
            }
        }
        outputXML.write("</statements>");


    }

    private void compileLet() throws Exception {
        String name;
        outputXML.write("<letStatement>");

        outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        eat("let");
        outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
        name = jackTokenizer.identifier();
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
        compileSubroutineCall();
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat(";");

        outputXML.write("</doStatement>");
    }

    private void compileSubroutineCall() throws Exception {
        outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
        jackTokenizer.advance();
        if (jackTokenizer.getTokenStringOriginalInput().equals("(")) {
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat("(");
            compileExpressionList();
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat(")");
        }
        if (jackTokenizer.getTokenStringOriginalInput().equals(".")) {
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
            if (jackTokenizer.getTokenStringOriginalInput().equals("<")) {
                outputXML.write("<symbol>" + "&lt;" + "</symbol>");
                jackTokenizer.advance();
                compileTerm();

            } else if (jackTokenizer.getTokenStringOriginalInput().equals(">")) {
                outputXML.write("<symbol>" + "&gt;" + "</symbol>");
                jackTokenizer.advance();
                compileTerm();
            } else if (jackTokenizer.getTokenStringOriginalInput().equals("&")) {
                outputXML.write("<symbol>" + "&amp;" + "</symbol>");
                jackTokenizer.advance();
                compileTerm();
            } else {
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                jackTokenizer.advance();
                compileTerm();
            }

        }
        outputXML.write("</expression>");
    }

    private void compileTerm() throws Exception {
        outputXML.write("<term>");
        if (jackTokenizer.tokenType().equals(TokenType.IDENTIFIER)) {
            outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
            jackTokenizer.advance();
            String nextToken = jackTokenizer.getTokenStringOriginalInput();

            if (nextToken.equals("[")) {
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat("[");
                compileExpression();
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat("]");
            }

            if (nextToken.equals("(")) {
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat("(");
                compileExpressionList();
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat(")");
            }
            if (nextToken.equals(".")) {
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

        }


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
        }  else if (jackTokenizer.getTokenStringOriginalInput().equals("-") || jackTokenizer.getTokenStringOriginalInput().equals("~")) {
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            jackTokenizer.advance();
            compileTerm();
        }


        outputXML.write("</term>");



    }

    private void compileExpressionList() throws Exception {
        outputXML.write("<expressionList>");

        if (!jackTokenizer.tokenType().equals(TokenType.SYMBOL)) {
            compileExpression();
            while (jackTokenizer.getTokenStringOriginalInput().equals(",")) {
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat(",");
                compileExpression();
            }

        }else if (jackTokenizer.getTokenStringOriginalInput().equals("(")) {
            compileExpression();
            while (jackTokenizer.getTokenStringOriginalInput().equals(",")) {
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat(",");
                compileExpression();
            }
        }

        outputXML.write("</expressionList>");
    }


}
