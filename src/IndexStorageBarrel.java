// Index Storage Barrel – É o servidor central (replicado) que armazena todos os
//dados da aplicação, recebendo os elementos do índice (palavras e URLs) através de Multicast,
// enviados pelos Downloaders. Para tal, deverão aplicar um protocolo
// de multicast fiável, uma vez que todos os storage barrels devem ter informação
// idêntica ainda que possam existir avarias de omissão.
import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;


public class IndexStorageBarrel {
    // Main class
    /**
     * @param nome_barrel nome do txt onde se vai estrair informação
     * @return retorna o barrel com a informação do txt
     * @throws IOException
     */
    public static HashMap<String,String> GetInfoBarrel(String nome_barrel) throws IOException{
        HashMap<String,String> urls = new HashMap<String,String>();

        File barrel_1 = new File(nome_barrel);
        BufferedReader br1 = new BufferedReader(new FileReader(barrel_1));
        String st = "s";
        String [] st_aux;

        while ((st = br1.readLine()) != null){
			st_aux = st.split(",");
            urls.put(st_aux[0],st_aux[1]);
        }
        br1.close();
        return urls;
    }

    /**
     * 
     * @param barrel barrel atual
     * @return barrel com o novo par palavra:url
     * @throws IOException
     */
    public static HashMap<String,String> AddToHash(HashMap<String,String> barrel) throws IOException{
        HashMap<String,String> urls = new HashMap<String,String>();
        urls = barrel;

        Scanner myObj = new Scanner(System.in);
        System.out.println("Word searched: ");
        String searchedWord = myObj.nextLine();
        
        System.out.println("Url searched: ");
        String searchedUrl = myObj.nextLine();
        
        myObj.close();

        barrel.put(searchedWord, searchedUrl);
        return urls;
    }

    // Para separar barrel no futuro ignorar por enquanto

    
    /*
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
        */

	public static void main(String[] args) throws Exception
	{
        HashMap<String,String> barrel1 = new HashMap<String,String>();
        HashMap<String,String> barrel2 = new HashMap<String,String>();
        
        HashMap<String,String> barrel_merged = new HashMap<String,String>();

        barrel1 = GetInfoBarrel("a-m_barrel.txt");
        barrel2 = GetInfoBarrel("n-z_barrel.txt");


        for (Map.Entry<String, String> set : barrel1.entrySet()) {
            barrel_merged.put(set.getKey(),set.getValue());
        }

        for (Map.Entry<String, String> set : barrel2.entrySet()) {
            barrel_merged.put(set.getKey(),set.getValue());
        }

        System.out.println(barrel_merged);

        barrel_merged = AddToHash(barrel_merged);
        
        System.out.println(barrel_merged);;
    }

}
