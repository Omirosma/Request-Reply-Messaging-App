import java.io.*;
import java.net.Socket;

public class Client {


    public static void main(String[] args) {

        try {
            if (args.length == 0) {
                System.out.println("Usage: java Client <ip> <port number> <op_id> <username>");
                System.exit(1);
            }

            String ipAddress = args[0];
            int portNumber = Integer.parseInt(args[1]);

            //Start Client Socket
            Socket socket = new Socket(ipAddress,portNumber);
            System.out.println("Client started");

            BufferedReader received = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter sent = new PrintWriter(socket.getOutputStream(),true);
            String str="";

            //Convert args to line
            for (int i = 0; i < args.length; i++) {
                str = str + args[i] + " ";
            }

            //Send to Server
            sent.println(str);
            System.out.println("Arguments sent to server: "+str);

            while(true) {
                System.out.println(received.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
