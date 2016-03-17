package myWhatsServer;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MyWhatsStub {

    /**
     * trata dos argumentos introduzidos pelo utilizador para enviar para o servidor
     * @throws IOException
     *
     */

    public static void handle(List<String> lista, ObjectOutputStream out) throws IOException {
        String[] args = (String[]) lista.toArray();

        String msg;

        if (args[0].equals("-r")){
            if (args.length==1)
                msg="-r";
                // contact
            else if (args.length==2)
                msg = args[0] + ":" + args[1];
                // contact file
            else
                msg = args[0] + ":" + args[1] + ":" + args[2];
            out.writeObject(msg);
        }

        // opcao -m -f -a -d
        // TODO: fazer passar o ficheiro na opcao -f em vez do nome
        // TODO: feito aqui, falta no server
        else if (args[0].equals("-f")){
            Path path = Paths.get("IIO-Exame_2014_01_20.pdf");
            byte[] data = Files.readAllBytes(path);

            msg = args[0] + ":" + args[1] + ":" + args[2];

            out.writeObject(msg);
            out.writeObject(data);
        }
        else {
            // NOT HANDLING BAD WRINTING
            msg = args[0] + args[1] + args[2];
            out.writeObject(msg);
        }
    }
}
