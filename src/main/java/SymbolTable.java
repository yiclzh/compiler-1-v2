import java.util.Hashtable;

public class SymbolTable {

    Hashtable<String, VariableDef> symbolTable;

    public SymbolTable() {
        symbolTable = new Hashtable<>();
    }

    public void startSubroutine() {
        symbolTable.clear();
    }

    public void define(String name, String type, Kind kind) {
        //if kind = static || field, insert class scope (class-level symbol table)
        //if kind = arg || var, insert subroutine scope (subroutine-level symbol table)
    }
}
