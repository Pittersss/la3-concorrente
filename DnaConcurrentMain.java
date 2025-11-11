import java.io.File;
import java.util.concurrent.Semaphore;

public class DnaConcurrentMain {
    
    static Semaphore mutex = new Semaphore(1);
    static long total = 0;

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 2) {
            System.err.println("Uso: java DnaSerialMain DIRETORIO_ARQUIVOS PADRAO");
            System.err.println("Exemplo: java DnaSerialMain dna_inputs CGTAA");
            System.exit(1);
        }

        String dirName = args[0];
        String pattern = args[1];

        File dir = new File(dirName);
        if (!dir.isDirectory()) {
            System.err.println("Caminho não é um diretório: " + dirName);
            System.exit(2);
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.err.println("Nenhum arquivo .txt encontrado em: " + dirName);
            System.exit(3);
        }

        Thread[] threads = new Thread[files.length];
	    for (int i = 0; i < files.length; i++) {
            threads[i] = new Thread(new CountFile(mutex, files[i], pattern));
            threads[i].start();
        }

        for (int j = 0; j < threads.length; j++)
        {
            threads[j].join();
        }

	    System.out.println("Sequência " + pattern + " foi encontrada " + total + " vezes.");
    } 
}


