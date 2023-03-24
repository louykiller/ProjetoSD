// Index Storage Barrel – É o servidor central (replicado) que armazena todos os
//dados da aplicação, recebendo os elementos do índice (palavras e URLs) através de Multicast,
// enviados pelos Downloaders. Para tal, deverão aplicar um protocolo
// de multicast fiável, uma vez que todos os storage barrels devem ter informação
// idêntica ainda que possam existir avarias de omissão.
import java.io.*;
import java.util.Scanner;

public class IndexStorageBarrel {
    // Main class
	public static void main(String[] args) throws Exception
	{
        File barrel_1 = new File("a-m_barrel.txt");
        File barrel_2 = new File("n-z_barrel.txt");
		
		BufferedReader br1 = new BufferedReader(new FileReader(barrel_1));
        BufferedReader br2 = new BufferedReader(new FileReader(barrel_2));
        
        String st;

        System.out.println("Urls in barrel 1!");
		while ((st = br1.readLine()) != null)
			System.out.println(st);

        System.out.println("Urls in barrel 2!");
        while ((st = br2.readLine()) != null)
            System.out.println(st);
    
        br1.close();
        br2.close();

        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Add file to barrel");
    
        String fileName = myObj.nextLine();  // Read user input
        System.out.println("File to be added: " + fileName);  // Output user input
        
        myObj.close();

        char firstChar = fileName.charAt(0);
        char upperFirst = Character.toUpperCase(firstChar);

        String fullUrl = "https://"+fileName;
        
        try{
            if(Character.compare(upperFirst, 'M') < 0){
                System.out.println("Insert in first barrel!");
                BufferedWriter bw1 = new BufferedWriter(new FileWriter("a-m_barrel.txt",true));
                bw1.write("\n"+fullUrl);
                bw1.close();
            }
            else{
                System.out.println("Insert in the second barrel!");
                BufferedWriter bw2 = new BufferedWriter(new FileWriter("n-z_barrel.txt",true));
                bw2.write("\n"+fullUrl);
                bw2.close();
            }
        }
        catch(IOException exception){
            exception.printStackTrace();
        }

    }

}
