import java.io.IOException;
import java.util.Hashtable;

public class SymbolTable {

    Hashtable<String, VariableDef> symbolTable;
    int fieldIndex;
    int staticIndex;
    int argIndex;
    int varIndex;
    VariableDef variableDef = new VariableDef();

    public SymbolTable() {
        symbolTable = new Hashtable<>();
    }


    public void startSubroutine() {
        symbolTable.clear();
    }

    public void define(String name, String type, Kind kind) {
        //if kind = static || field, insert class scope (class-level symbol table)
        //if kind = arg || var, insert subroutine scope (subroutine-level symbol table)


        variableDef.setType(type);
        variableDef.setKind(kind);

        if (variableDef.getKind().equals(Kind.VAR)){
            variableDef.setIndex(varIndex);
            varIndex++;
        }
        if (variableDef.getKind().equals(Kind.FIELD)){
            variableDef.setIndex(fieldIndex);
            fieldIndex++;
        }
        if (variableDef.getKind().equals(Kind.STATIC)){
            variableDef.setIndex(staticIndex);
            staticIndex++;
        }
        if (variableDef.getKind().equals(Kind.ARG)){
            variableDef.setIndex(argIndex);
            argIndex++;
        }

        symbolTable.put(name, variableDef);
    }

    public int varCount(Kind kind) {
        if (kind.equals(Kind.VAR)) {
            return varIndex + 1;
        }
        if (kind.equals(Kind.FIELD)) {
            return fieldIndex + 1;
        }
        if (kind.equals(Kind.STATIC)) {
            return staticIndex + 1;
        }
        if (kind.equals(Kind.ARG)) {
            return argIndex + 1;
        }
        return -1;
    }

    public Kind kindOf(String name) {
        return symbolTable.get(name).getKind();
    }

    public String typeOf(String name) {
        return symbolTable.get(name).getType();
    }

    public int indexOf(String name) {
        return symbolTable.get(name).getIndex();
    }


}
