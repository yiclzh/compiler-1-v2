import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

public class CompilationEngine {

    private String currentToken;
    JackTokenizer jackTokenizer;
    BufferedWriter outputXML;
    private HashSet<String> type = new HashSet<>();
    private  HashSet<String> op = new HashSet<>();
    private HashSet<String> statements = new HashSet<>();
    private HashSet<String> keywordConstant = new HashSet<>();
    SymbolTable classLevelSymbolTable = new SymbolTable();
    SymbolTable subroutineLevelSymbolTable = new SymbolTable();
    VMWriter vmWriter;

    String subroutineName;
    String subroutineType;
    String className;
    int ifConst = 0;
    int whileConst = 0;
    int nSubArgs = -1;
    boolean isVoidSubroutine = false;
    char[] characters;
    HashMap<Character, Integer> hackCharacterSet = new HashMap();

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

    private void initializeKeywordConstant() {
        keywordConstant.add("true");
        keywordConstant.add("false");
        keywordConstant.add("null");
        keywordConstant.add("this");
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

    private void initializeCharacterSet() {
        hackCharacterSet.put(' ', 32);
        hackCharacterSet.put('!', 33);
        hackCharacterSet.put('"', 34);
        hackCharacterSet.put('#', 35);
        hackCharacterSet.put('$', 36);
        hackCharacterSet.put('%', 37);
        hackCharacterSet.put('&', 38);
        hackCharacterSet.put('\'', 39);
        hackCharacterSet.put('(', 40);
        hackCharacterSet.put(')', 41);
        hackCharacterSet.put('*', 42);
        hackCharacterSet.put('+', 43);
        hackCharacterSet.put(',', 44);
        hackCharacterSet.put('-', 45);
        hackCharacterSet.put('.', 46);
        hackCharacterSet.put('/', 47);
        hackCharacterSet.put('0', 48);
        hackCharacterSet.put('1', 49);
        hackCharacterSet.put('2', 50);
        hackCharacterSet.put('3', 51);
        hackCharacterSet.put('4', 52);
        hackCharacterSet.put('5', 53);
        hackCharacterSet.put('6', 54);
        hackCharacterSet.put('7', 55);
        hackCharacterSet.put('8', 56);
        hackCharacterSet.put('9', 57);
        hackCharacterSet.put(':', 58);
        hackCharacterSet.put(';', 59); //;, 59
        hackCharacterSet.put('<', 60); //<, 60
        hackCharacterSet.put('=', 61); //=, 61
        hackCharacterSet.put('>', 62); //>, 62
        hackCharacterSet.put('?', 63); //?, 63
        hackCharacterSet.put('@', 64); //@, 64
        hackCharacterSet.put('A', 65); //A, 65
        hackCharacterSet.put('B', 66); //B, 66
        hackCharacterSet.put('C', 67); //C, 67
        hackCharacterSet.put('D', 68); //D, 68
        hackCharacterSet.put('E', 69); //E, 69
        hackCharacterSet.put('F', 70); //F, 70
        hackCharacterSet.put('G', 71); //G, 71
        hackCharacterSet.put('H', 72); //H, 72
        hackCharacterSet.put('I', 73); //I, 73
        hackCharacterSet.put('J', 74); //J, 74
        hackCharacterSet.put('K', 75); //K, 75
        hackCharacterSet.put('L', 76); //L, 76
        hackCharacterSet.put('M', 77); //M, 77
        hackCharacterSet.put('N', 78); //N, 78
        hackCharacterSet.put('O', 79); //O, 79
        hackCharacterSet.put('P', 80); //P, 80
        hackCharacterSet.put('Q', 81); //Q, 81
        hackCharacterSet.put('R', 82); //R, 82
        hackCharacterSet.put('S', 83); //S, 83
        hackCharacterSet.put('T', 84); //T, 84
        hackCharacterSet.put('U', 85); //U, 85
        hackCharacterSet.put('V', 86); //V, 86
        hackCharacterSet.put('W', 87); //W, 87
        hackCharacterSet.put('X', 88); //X, 88
        hackCharacterSet.put('Y', 89); //Y, 89
        hackCharacterSet.put('Z', 90); //Z, 90
        hackCharacterSet.put('[', 91); //[, 91
        hackCharacterSet.put('/', 92); // /, 92
        hackCharacterSet.put(']', 93); // ], 93
        hackCharacterSet.put('^', 94); // ^, 94
        hackCharacterSet.put('_', 95); // _, 95
        hackCharacterSet.put('`', 96); // `, 96
        hackCharacterSet.put('a', 97); //a, 97
        hackCharacterSet.put('b', 98); //b, 98
        hackCharacterSet.put('c', 99); //c, 99
        hackCharacterSet.put('d', 100); //d, 100
        hackCharacterSet.put('e', 101); //e, 101
        hackCharacterSet.put('f', 102); //f, 102
        hackCharacterSet.put('g', 103); //g, 103
        hackCharacterSet.put('h', 104); //h, 104
        hackCharacterSet.put('i', 105); //i, 105
        hackCharacterSet.put('j', 106); //j, 106
        hackCharacterSet.put('k', 107); //k, 107
        hackCharacterSet.put('l', 108); //l, 108
        hackCharacterSet.put('m', 109); //m, 109
        hackCharacterSet.put('n', 110); //n, 110
        hackCharacterSet.put('o', 111); //o, 111
        hackCharacterSet.put('p', 112); //p, 112
        hackCharacterSet.put('q', 113); //q, 113
        hackCharacterSet.put('r', 114); //r, 114
        hackCharacterSet.put('s', 115); //s, 115
        hackCharacterSet.put('t', 116); //t, 116
        hackCharacterSet.put('u', 117); //u, 117
        hackCharacterSet.put('v', 118); //v, 118
        hackCharacterSet.put('w', 119); //w, 119
        hackCharacterSet.put('x', 120); //x, 120
        hackCharacterSet.put('y', 121);  //y, 121
        hackCharacterSet.put('z', 122); //z, 122
        hackCharacterSet.put('{', 123); //{, 123
        hackCharacterSet.put('|', 124); //|, 124
        hackCharacterSet.put('}', 125); //}, 125
        hackCharacterSet.put('~', 126); //~, 126


    }

    private int getIntValueOfString(char c) {
        initializeCharacterSet();
        int nKey = hackCharacterSet.get(c);
        return nKey;
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
        isVoidSubroutine = false;
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
            if (jackTokenizer.getTokenStringOriginalInput().equals("void")) {
                isVoidSubroutine = true;
            }
        } else {
            outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
        }
        jackTokenizer.advance();
        outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
        subroutineName = jackTokenizer.identifier();

        if (subroutineType.equals("constructor")) {
            vmWriter.writeFunction(className + "." + subroutineName, 0);
            vmWriter.writePush(Segment.CONST, classLevelSymbolTable.fieldIndex);
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop(Segment.POINTER, 0);
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
        if (subroutineType.equals("constructor")) {
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
                classLevelSymbolTable.define(name, parameterType, Kind.ARG);
                while (jackTokenizer.getTokenStringOriginalInput().equals(",")) {
                    outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                    eat(",");
                    outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
                    parameterType = setKeyword(jackTokenizer.keyword());
                    jackTokenizer.advance();
                    outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
                    name = jackTokenizer.identifier();
                    jackTokenizer.advance();
                    classLevelSymbolTable.define(name, parameterType, Kind.ARG);

                }
            }
            System.out.println(classLevelSymbolTable);
            outputXML.write("</parameterList>");
        } else {
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
                if (subroutineType.equals("method")) {
                    subroutineLevelSymbolTable.argIndex = 1;
                }
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
                vmWriter.writePush(Segment.ARG, 0);
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
                ifConst++;
            }

            if (setKeyword(jackTokenizer.keyword()).equals("while")) {
                compileWhile();
                whileConst++;
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



    private static boolean isStringNumber(String n) {
        try {
            int i = Integer.parseInt(n);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private Segment kindOfToSegment(Kind kind) {
        if (kind.equals(Kind.ARG)) {
            return Segment.ARG;
        }
        if (kind.equals(Kind.STATIC)) {
            return Segment.STATIC;
        }
        if (kind.equals(Kind.VAR)) {
            return Segment.LOCAL;
        }
        if (kind.equals(Kind.FIELD)) {
            return Segment.THIS;
        } else {
            return Segment.None;
        }
    }


    private void compileLet() throws Exception {
        boolean isArray = false;
        String name;
        outputXML.write("<letStatement>");

        outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        eat("let");
        outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
        name = jackTokenizer.identifier();
        jackTokenizer.advance();
        if (jackTokenizer.getTokenStringOriginalInput().equals("[")) {
            isArray = true;
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat("[");
            compileExpression();
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat("]");
            if (subroutineLevelSymbolTable.contains(name)) {
                vmWriter.writePush(kindOfToSegment(subroutineLevelSymbolTable.kindOf(name)), subroutineLevelSymbolTable.indexOf(name));
            }
            if (classLevelSymbolTable.contains(name)) {
                vmWriter.writePush(kindOfToSegment(classLevelSymbolTable.kindOf(name)), classLevelSymbolTable.indexOf(name));
            }
            vmWriter.writeArithmetic(Command.ADD);

        }
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("=");
        compileExpression();
        if (isArray == true) {
            //pop temp 0
            //pop pointer 0
            //push temp 0
            //pop that 0
            vmWriter.writePop(Segment.TEMP, 0);
            vmWriter.writePop(Segment.POINTER, 0);
            vmWriter.writePush(Segment.TEMP, 0);
            vmWriter.writePop(Segment.THAT, 0);
        } else {
            if (subroutineLevelSymbolTable.contains(name)) {
                vmWriter.writePop(kindOfToSegment(subroutineLevelSymbolTable.kindOf(name)), subroutineLevelSymbolTable.indexOf(name));
            }
            if (classLevelSymbolTable.contains(name)) {
                vmWriter.writePop(kindOfToSegment(classLevelSymbolTable.kindOf(name)), classLevelSymbolTable.indexOf(name));
            }
        }
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat(";");
        System.out.println(classLevelSymbolTable);
        outputXML.write("</letStatement>");
    }

    private void compileIf() throws Exception {
        outputXML.write("<ifStatement>");

        outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        eat("if");
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("(");
        compileExpression();
        vmWriter.writeArithmetic(Command.NOT);
        vmWriter.writeIf("IF_FALSE" + ifConst);
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat(")");


        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("{");
        compileStatements();
        vmWriter.writeGoto("IF_TRUE" + ifConst);
        vmWriter.writeLabel("IF_FALSE" + ifConst);
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("}");


        if (jackTokenizer.getTokenStringOriginalInput().equals("else")) {
            outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
            eat("else");
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat("{");
            compileStatements();
            vmWriter.writeLabel("IF_TRUE" + ifConst);
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat("}");
        }

        outputXML.write("</ifStatement>");
    }

    private void compileWhile() throws Exception {
        outputXML.write("<whileStatement>");

        vmWriter.writeLabel("WHILE_TRUE" + whileConst);

        outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        eat("while");
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("(");
        compileExpression();
        vmWriter.writeArithmetic(Command.NOT);
        vmWriter.writeIf("WHILE_FALSE" + whileConst);
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat(")");
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("{");
        compileStatements();
        vmWriter.writeGoto("WHILE_TRUE" + whileConst);
        vmWriter.writeLabel("WHILE_FALSE" + whileConst);
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat("}");

        outputXML.write("</whileStatement>");
    }

    private void compileDo() throws Exception {
        outputXML.write("<doStatement>");

        outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
        eat("do");
        compileSubroutineCall();
        vmWriter.writePop(Segment.TEMP, 0);
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat(";");

        outputXML.write("</doStatement>");
    }

    private void compileSubroutineCall() throws Exception {

        boolean isVar = false;
        String commandFunction = null;
        String commandName = null;

        outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
        commandName = jackTokenizer.identifier();
        jackTokenizer.advance();
        if (subroutineLevelSymbolTable.contains(commandName)) {
            isVar = true;
            vmWriter.writePush(kindOfToSegment(subroutineLevelSymbolTable.kindOf(commandName)), subroutineLevelSymbolTable.indexOf(commandName));
        } else {
            if (classLevelSymbolTable.contains(commandName)) {
                isVar = true;
                vmWriter.writePush(kindOfToSegment(classLevelSymbolTable.kindOf(commandName)), classLevelSymbolTable.indexOf(commandName));
            }
        }
        if (jackTokenizer.getTokenStringOriginalInput().equals("(")) {
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat("(");
            vmWriter.writePush(Segment.POINTER, 0);
            compileExpressionList();
            vmWriter.writeCall(className + "." + commandName, nSubArgs+1);
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat(")");
        }
        if (jackTokenizer.getTokenStringOriginalInput().equals(".")) {
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat(".");
            outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
            commandFunction = jackTokenizer.identifier();
            jackTokenizer.advance();
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat("(");
            compileExpressionList();
            if (isVar ==  true) {
                if (classLevelSymbolTable.contains(commandName)) {
                    vmWriter.writeCall(classLevelSymbolTable.typeOf(commandName) + "." + commandFunction, nSubArgs+1);
                } else {
                    vmWriter.writeCall(subroutineLevelSymbolTable.typeOf(commandName) + "." + commandFunction, nSubArgs+1);
                }

            } else {
                vmWriter.writeCall(commandName + "." + commandFunction, nSubArgs);
            }
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
        if (isVoidSubroutine) {
            vmWriter.writePush(Segment.CONST, 0);
        }
        vmWriter.writeReturn();
        outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
        eat(";");
        outputXML.write("</returnStatement>");
    }


    private void compileExpression() throws Exception {
        initializeOp();
        String opString;
        Command command = Command.None;
        outputXML.write("<expression>");
        compileTerm();
        while (op.contains(jackTokenizer.getTokenStringOriginalInput())) {
            opString = jackTokenizer.getTokenStringOriginalInput();
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            jackTokenizer.advance();
            compileTerm();
            if (opString.trim().equals("+")) { command = Command.ADD; }
            if (opString.trim().equals("-")) { command = Command.SUB; }
            if (opString.trim().equals("*")) { command = Command.MULT; }
            if (opString.trim().equals("/")) { command = Command.DIVIDE; }
            if (opString.trim().equals("&")) { command = Command.AND; }
            if (opString.trim().equals("|")) { command = Command.OR; }
            if (opString.trim().equals("<")) { command = Command.LT; }
            if (opString.trim().equals(">")) { command = Command.GT; }
            if (opString.trim().equals("=")) { command = Command.EQ; }
            vmWriter.writeArithmetic(command);
        }
        outputXML.write("</expression>");
    }

    private void compileTerm() throws Exception {
        initializeKeywordConstant();
        outputXML.write("<term>");
        if (jackTokenizer.tokenType().equals(TokenType.IDENTIFIER)) {
            boolean isVar = false;
            String name = null;
            String commandFunction = null;
            outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
            name = jackTokenizer.identifier();
            jackTokenizer.advance();
            String nextToken = jackTokenizer.getTokenStringOriginalInput();

            if (subroutineLevelSymbolTable.contains(name)) {
                isVar = true;
                vmWriter.writePush(kindOfToSegment(subroutineLevelSymbolTable.kindOf(name)), subroutineLevelSymbolTable.indexOf(name));
            } else {
                if (classLevelSymbolTable.contains(name)) {
                    isVar = true;
                    vmWriter.writePush(kindOfToSegment(classLevelSymbolTable.kindOf(name)), classLevelSymbolTable.indexOf(name));
                }
            }

            if (nextToken.equals("[")) {
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat("[");
                compileExpression();
                if (subroutineLevelSymbolTable.contains(name)) {
                    vmWriter.writePush(kindOfToSegment(subroutineLevelSymbolTable.kindOf(name)), subroutineLevelSymbolTable.indexOf(name));
                } else {
                    if (classLevelSymbolTable.contains(name)) {
                        vmWriter.writePush(kindOfToSegment(classLevelSymbolTable.kindOf(name)), classLevelSymbolTable.indexOf(name));
                    }
                }
                vmWriter.writeArithmetic(Command.ADD);

                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat("]");
            }

            if (nextToken.equals("(")) {
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat("(");
                vmWriter.writePush(Segment.POINTER, 0);
                compileExpressionList();
                vmWriter.writeCall(className + "." + name, nSubArgs+1);
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat(")");
            }
            if (nextToken.equals(".")) {
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat(".");
                outputXML.write("<identifier>" + jackTokenizer.identifier() + "</identifier>");
                commandFunction = jackTokenizer.identifier();
                jackTokenizer.advance();
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat("(");
                compileExpressionList();
                if (isVar ==  true) {
                    if (classLevelSymbolTable.contains(name)) {
                        vmWriter.writeCall(classLevelSymbolTable.typeOf(name) + "." + commandFunction, nSubArgs+1);
                    } else {
                        vmWriter.writeCall(subroutineLevelSymbolTable.typeOf(name) + "." + commandFunction, nSubArgs+1);
                    }

                } else {
                    vmWriter.writeCall(name + "." + commandFunction, nSubArgs);
                }
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat(")");
            }


        }else if (jackTokenizer.getTokenStringOriginalInput().equals("(")) {
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat("(");
            compileExpression();
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            eat(")");
        } else if (jackTokenizer.tokenType().equals(TokenType.INT_CONST)) {
            outputXML.write("<integerConstant>" + jackTokenizer.intVal() + "</integerConstant>");
            vmWriter.writePush(Segment.CONST, jackTokenizer.intVal());
            jackTokenizer.advance();
        } else if (jackTokenizer.tokenType().equals(TokenType.STRING_CONST)) {
            outputXML.write("<stringConstant>" + jackTokenizer.stringVal() + "</stringConstant>");
            characters = jackTokenizer.stringVal().toCharArray();
            vmWriter.writePush(Segment.CONST, characters.length);
            vmWriter.writeCall("String.new", 1);
            //adding string,  to the jack keyboard memory map
            for (int i = 0; i < characters.length; i++ ) {
                int p = getIntValueOfString(characters[i]);
                vmWriter.writePush(Segment.CONST, p);
                vmWriter.writeCall("String.appendChar", 2);
            }
            jackTokenizer.advance();
        } else if (keywordConstant.contains(jackTokenizer.getTokenStringOriginalInput())) {
            outputXML.write("<keyword>" + setKeyword(jackTokenizer.keyword()) + "</keyword>");
            if (jackTokenizer.getTokenStringOriginalInput().equals("true")) {
                vmWriter.writePush(Segment.CONST, 0);
                vmWriter.writeArithmetic(Command.NOT);
            }
            if (jackTokenizer.getTokenStringOriginalInput().equals("false")) {vmWriter.writePush(Segment.CONST, 0);}
            if (jackTokenizer.getTokenStringOriginalInput().equals("null")) {vmWriter.writePush(Segment.CONST, 0);}
            if (jackTokenizer.getTokenStringOriginalInput().equals("this")) {vmWriter.writePush(Segment.POINTER, 0);}
            jackTokenizer.advance();
        }
        else if (jackTokenizer.getTokenStringOriginalInput().equals("-") || jackTokenizer.getTokenStringOriginalInput().equals("~")) {
            Command command = Command.None;
            outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
            if (jackTokenizer.getTokenStringOriginalInput().equals("-")) {
                command = Command.NEG;
            }
            if (jackTokenizer.getTokenStringOriginalInput().equals("~")) {
                command = Command.NOT;
            }
            jackTokenizer.advance();
            compileTerm();
            vmWriter.writeArithmetic(command);
        }


        outputXML.write("</term>");



    }

    private void compileExpressionList() throws Exception {
        outputXML.write("<expressionList>");

        if (jackTokenizer.getTokenStringOriginalInput().equals(")")) {
            nSubArgs = 0;
        } else {
            nSubArgs = 1;
        }

        if (!jackTokenizer.tokenType().equals(TokenType.SYMBOL)) {
            compileExpression();
            while (jackTokenizer.getTokenStringOriginalInput().equals(",")) {
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat(",");
                compileExpression();
                nSubArgs++;
            }

        }else if (jackTokenizer.getTokenStringOriginalInput().equals("(")) {
            compileExpression();
            while (jackTokenizer.getTokenStringOriginalInput().equals(",")) {
                outputXML.write("<symbol>" + jackTokenizer.symbol() + "</symbol>");
                eat(",");
                compileExpression();
                nSubArgs++;
            }
        }

        outputXML.write("</expressionList>");
    }


}
