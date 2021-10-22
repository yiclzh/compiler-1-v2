import java.io.IOException;
import java.util.Hashtable;

public class SymbolTable {

    Hashtable<String, VariableDef> symbolTable;
    int fieldIndex;
    int staticIndex;
    int argIndex;
    int varIndex;

    public SymbolTable() {
        symbolTable = new Hashtable<>();
    }


    public void startSubroutine() {
        symbolTable.clear();
    }

    public void define(String name, String type, Kind kind) {
        //if kind = static || field, insert class scope (class-level symbol table)
        //if kind = arg || var, insert subroutine scope (subroutine-level symbol table)

        VariableDef variableDef = new VariableDef();

        variableDef.setType(type);
        variableDef.setKind(kind);

        if (variableDef.getKind().equals(Kind.VAR)){
            variableDef.setIndex(varIndex);
        }
        if (variableDef.getKind().equals(Kind.FIELD)){
            variableDef.setIndex(fieldIndex);
        }
        if (variableDef.getKind().equals(Kind.STATIC)){
            variableDef.setIndex(staticIndex);
        }
        if (variableDef.getKind().equals(Kind.ARG)){
            variableDef.setIndex(argIndex);
        }

        symbolTable.put(name, variableDef);
    }
}
