import java.io.File;
import java.io.IOException;

public class JackAnalyzer {
    public static void main(String[] args) throws Exception {
        File path = new File("/home/clarez/Downloads/nand2tetris/projects/10/Square");
        File [] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith(".jack") && files[i].isFile()) {
                CompilationEngine compilationEngine = new CompilationEngine(files[i].toString(), files[i].toString().replace(".jack", ".xml"));
            }
        }

    }
}
