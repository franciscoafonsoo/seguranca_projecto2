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

    // TODO mau formato a ser impresso no ficheiro ( check SKEL )

    public static void handle(List<String> lista, ObjectInputStream in, ObjectOutputStream out) throws IOException {
        String[] args = (String[]) lista.toArray();
        String msg;

        // sou bue crianca. ass tiago. 
        // A MINHA CONTRIBUICAO TA FEITA
        switch (args[0]) {
            case "-r":
                    // all
                if (args.length==1)
                    msg="-r";
                    // contact
                else if (args.length==2)
                    msg = args[0] + ":" + args[1];
                    // contact file
                else
                    msg = args[0] + ":" + args[1] + ":" + args[2];
                out.writeObject(msg);
                break;
            case "-f":
                Path path = Paths.get(args[2]);
                byte[] data = Files.readAllBytes(path);

                msg = args[0] + ":" + args[1] + ":" + args[2];

                out.writeObject(msg);
                out.writeObject(data);
                break;
            case "-m":
            case "-a":
            case "-d":
                msg = args[0] + args[1] + args[2];
                out.writeObject(msg);

                // resposta do server

                // in.readObject(cenas);

                // check -r or -a or -d
                // print (-r: List<String>.split("\")

                break;
            default:
                throw new IOException("opcao errada");
        }
    }
}
