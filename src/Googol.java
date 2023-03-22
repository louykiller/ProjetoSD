// Main class que vai iniciar todas as outras classes incluindo os indexStorageBarrels, RMISearchModule e os Downloaders

public class Googol {
    public static void main(String[] args) {
       new Downloader("https://quantum-computing.ibm.com");
    }
}