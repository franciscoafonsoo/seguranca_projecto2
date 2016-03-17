package myWhatsServer;


import com.sun.xml.internal.bind.v2.TODO;

import java.io.*;
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
        else {
            // NOT HANDLING BAD WRINTING
            msg = args[0] + args[1] + args[2];
            out.writeObject(msg);
        }
    }

    private void sendFile (ObjectOutputStream out, File f, byte[] barray) throws IOException {

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));

        // re check this line
        bis.read(barray, 0, barray.length);

        out.write(barray, 0, barray.length);
        out.flush();
    }
}
