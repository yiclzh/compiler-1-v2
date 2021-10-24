import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class VMWriter {


    BufferedWriter vmOutput;

    public VMWriter(String outputFile) throws IOException {
        vmOutput = new BufferedWriter(new FileWriter(outputFile, false));
    }

    private String setSegment(Segment segment) {
        if (segment.equals(Segment.CONST)) {
            return "constant";
        }
        if (segment.equals(Segment.ARG)) {
            return "argument";
        } else {
             return segment.name().toString().toLowerCase();
        }
    }

    private String setCommand(Command command) {
        return command.name().toString().toLowerCase();
    }

    public void writePush(Segment segment, int index) throws IOException {
        vmOutput.write("push " + setSegment(segment) + " " + index);
    }

    public void writePop(Segment segment, int index) throws IOException {
        vmOutput.write("pop " + setSegment(segment) + " " + index);
    }

    public void writeArithmetic(Command command) throws IOException {
        vmOutput.write(setCommand(command));
    }

    public void writeLabel(String label) throws IOException {
        vmOutput.write("Label " + label);
    }

    public void writeGoto(String label) throws IOException {
        vmOutput.write("goto " + label);
    }

    public void writeIf(String label) throws IOException {
        vmOutput.write("if-goto " + label);
    }

    public void writeCall(String name, int nArgs) throws IOException {
        vmOutput.write("call " + name + " " + nArgs);
    }

    public void writeFunction(String name, int nLocals) throws IOException {
        vmOutput.write("function " + name + " " + nLocals);
    }

}
