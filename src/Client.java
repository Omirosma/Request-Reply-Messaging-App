import java.io.*;
import java.net.Socket;

public class Client {


    public static void main(String[] args) {

        try {
            if (args.length == 0) {
                System.out.println("Usage: java Client <ip> <port number> <op_id>");
                throw new IllegalArgumentException("Not enough arguments provided");
            }

            String ipAddress = args[0];
            int portNumber = Integer.parseInt(args[1]);

            //Start Client Socket
            Socket socket = new Socket(ipAddress,portNumber);
//            System.out.println("Client started");

            BufferedReader received = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter sent = new PrintWriter(socket.getOutputStream(),true);
            StringBuilder str= new StringBuilder();

            //Convert args to line
            for (String arg : args) {
                str.append(arg).append(" ");
            }

            //Send to Server
            sent.println(str);
//            System.out.println("Arguments sent to server: "+str);

            String serverResponse;
            while(!(serverResponse = received.readLine()).equals("#terminate")) {
//                System.out.println("Server returned: ");
                System.out.println(serverResponse);
            }

            socket.close();
        } catch (IOException | IllegalArgumentException e) {
            System.out.println(e.getMessage()+"\n");
        }
    }
}
