import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CountFile implements Runnable {

    File file;
    String pattern;

    public CountFile(File file, String pattern)
    {
        this.file = file;
        this.pattern = pattern;
    }
    
    @Override
    public void run() {
        long count;
        try {
            count = countInFile(file, pattern);
            DnaConcurrentMain.mutex.acquire();
            DnaConcurrentMain.total += count;
            DnaConcurrentMain.mutex.release();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static long countInFile(File file, String pattern) throws IOException {
        long total = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    total += countInSequence(line, pattern);
                }
            }
        }
        return total;    
    }

    public static long countInSequence(String sequence, String pattern) {
        if (sequence == null || pattern == null) {
            return 0;
        }
        int n = sequence.length();
        int m = pattern.length();
        if (m == 0 || n < m) {
            return 0;
        }
        long count = 0;
        for (int i = 0; i <= n - m; i++) {
            if (sequence.regionMatches(false, i, pattern, 0, m)) {
                count++;
            }
        }
        return count;
    }
    
}
