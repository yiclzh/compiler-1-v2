public class VariableDef {

    private String type;
    private Kind kind;
    private int index;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }


    @Override
    public String toString() {
        return "VariableDef{" +
                "type='" + type + '\'' +
                ", kind=" + kind +
                ", index=" + index +
                '}';
    }
}
