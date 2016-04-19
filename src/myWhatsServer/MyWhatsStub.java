package myWhatsServer;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MyWhatsStub {

    /**
     * trata dos argumentos introduzidos pelo utilizador para enviar para o servidor
     *
     * @throws IOException
     */

    // TODO mau formato a ser impresso no ficheiro ( check SKEL )
    public static void handle(List<String> lista, ObjectInputStream in, ObjectOutputStream out) throws IOException {

        String msg;

        System.out.println("dentro do handle");
        if (!(lista.size() == 0)) {
            switch (lista.get(0)) {
                case "-r":
                    // all
                    if (lista.size() == 1)
                        msg = "-r";
                        // contact
                    else if (lista.size() == 2)
                        msg = lista.get(0) + ":" + lista.get(1);
                        // contact file
                    else
                        msg = lista.get(0) + ":" + lista.get(1) + ":" + lista.get(2);
                    out.writeObject(msg);
                    break;
                case "-f":
                    Path path = Paths.get(lista.get(2));
                    byte[] data = Files.readAllBytes(path);

                    msg = lista.get(0) + ":" + lista.get(1) + ":" + lista.get(2);

                    out.writeObject(msg);
                    out.writeObject(data);
                    break;
                case "-m":
                case "-a":
                case "-d":
                    msg = lista.get(0) + ":" + lista.get(1) + ":" + lista.get(2);
                    System.out.println(msg);
                    out.writeObject(msg);

                    // resposta do server

                    // in.readObject(cenas);

                    // check -r or -a or -d
                    // print (-r: List<String>.split("\")

                    break;
                default:
                    throw new IOException("opcao errada");
            }
        } else {
            out.writeObject("Nothing");
        }

    }
}
