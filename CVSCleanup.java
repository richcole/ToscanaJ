import java.io.*;

public class CVSCleanup {
    public static void main(String[] args) {
        try {
            File firstFile = new File(args[0]);
            processFile(firstFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processFile(File file) throws IOException {
        if(file.isDirectory()) {
            File[] children = file.listFiles();
            for (int i = 0; i < children.length; i++) {
                File child = children[i];
                processFile(child);
            }
            return;
        }
        BufferedReader in;
        in = new BufferedReader(new FileReader(file));
        String curLine = in.readLine();
        if(! curLine.startsWith("/*")) {
            return;
        }
        curLine = in.readLine();
        if(! curLine.equals("")) {
            return;
        }
        BufferedWriter out = new BufferedWriter(new FileWriter(file.getPath() + ".clean"));
        out.write("/*");
        out.newLine();
        while(curLine != null) {
            curLine = in.readLine();
            if(curLine!=null) {
                out.write(curLine);
                out.newLine();
                curLine = in.readLine();
            }
        }
        in.close();
        out.close();
    }
}
