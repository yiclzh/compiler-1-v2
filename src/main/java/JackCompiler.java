import java.io.File;
import java.io.IOException;

public class JackCompiler {
    public static void main(String[] args) throws Exception {
        File path = new File("//home/clarez/Downloads/nand2tetris/nand2tetris/projects/11/ComplexArrays");
        File [] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith(".jack") && files[i].isFile()) {
                CompilationEngine compilationEngine = new CompilationEngine(files[i].toString(), files[i].getName() + ".xml");
            }
        }

    }
}
