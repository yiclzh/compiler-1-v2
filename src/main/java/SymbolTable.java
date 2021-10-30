import java.io.IOException;
import java.util.Hashtable;

public class SymbolTable {

    Hashtable<String, VariableDef> symbolTable;
    int fieldIndex = 0;
    int staticIndex = 0;
    int argIndex = 0;
    int varIndex = 0;

    public SymbolTable() {
        symbolTable = new Hashtable<>();
    }


    public void startSubroutine() {
        symbolTable.clear();
    }

    public boolean contains(String key) {
        if (symbolTable.containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }


    public void define(String name, String type, Kind kind) {
        //if kind = static || field, insert class scope (class-level symbol table)
        //if kind = arg || var, insert subroutine scope (subroutine-level symbol table)

        VariableDef variableDef = new VariableDef();
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
            return varIndex;
        }
        if (kind.equals(Kind.FIELD)) {
            return fieldIndex;
        }
        if (kind.equals(Kind.STATIC)) {
            return staticIndex;
        }
        if (kind.equals(Kind.ARG)) {
            return argIndex;
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

    @Override
    public String toString() {
        return symbolTable.toString();
    }


}
