import java.io.*;
import java.net.*;
import java.util.*;


public class Server {

    private static List<Account> accounts = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {

        ServerSocket server;
        int port = Integer.parseInt(args[0]);


        try {
            // server is listening on port 5858(?)
            server = new ServerSocket(port);

            // running infinite loop accepting client request
            while (true) {
                // socket object to receive incoming client requests
                Socket client = server.accept();
                System.out.println("New client connected " + client.getInetAddress().getHostAddress());

                // create a new thread object of Runnable
                ClientHandler clientSock = new ClientHandler(client);

                // This thread will handle the client separately
                new Thread(clientSock).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //----------------------------------ClientHandler----------------------------------//
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        // Constructor
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }


        // the thread .......
        public void run() {

            try {

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                String str = in.readLine();
                System.out.println(str);
                String[] split = str.split(" ");

                switch (Integer.parseInt(split[2])) {

                    //CREATE ACCOUNTS---------------------------------------------------------------
                    case 1:
                        System.out.println("--Case 1--");

                        //Spelling check
                        boolean allLetters = split[3].chars().allMatch(Character::isLetter);
                        if (!allLetters) {
                            System.out.println("Invalid Username");
                            out.println("Invalid Username");
                            break;
                        }

                        //Username Exists? check
                        if (findUsername(split[3]) != -1) {
                            System.out.println("Sorry, the user already exists");
                            out.println("Sorry, the user already exists");
                            break;
                        }


                        Random random = new Random();
                        int auth = random.nextInt(9999);


                        //Token exist? check
                        while (findAuthToken(auth + "") != -1) {
                            auth = random.nextInt(9999);
                        }

                        //Creating account
                        accounts.add(new Account(split[3], auth));
                        System.out.println(accounts.getLast().username);
                        out.println(auth);

                        break;


                    //SHOW ACCOUNTS---------------------------------------------------------------
                    case 2:
                        System.out.println("--Case 2--");

                        for (int i = 0; i < accounts.size(); i++) {
                            if (accounts.get(i).authToken != Integer.parseInt(split[3])) {
                                System.out.println(i + 1 + ". " + accounts.get(i).username);
                                out.println(i + 1 + ". " + accounts.get(i).username);

                            }
                        }

                        Thread.currentThread().interrupt();
                        break;


                    //SEND MESSAGE---------------------------------------------------------------
                    case 3:
                        System.out.println("--Case 3--");

                        int senderIndex = findAuthToken(split[3]);
                        int receiverIndex = findUsername(split[4]);

                        //Username-Token exists check
                        if (senderIndex == -1 || receiverIndex == -1) {
                            break;
                        }

                        Message message = new Message(false, split[3], split[4], split[5]);

                        accounts.get(receiverIndex).addMessageBox(message);


                        break;


                    //SHOW INBOX---------------------------------------------------------------
                    case 4:
                        System.out.println("--Case 4--");

                        if (findAuthToken(split[3]) == -1) {
                            System.out.println("wrong token");
                        }

                        break;
                    case 5:
                        System.out.println("--Case 5--");
                        break;
                    case 6:
                        System.out.println("--Case 6--");
                        break;
                }


            } catch (IOException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            // command "finally" is omitted
        }
    }

    //----------------------------------Message----------------------------------//
    private static class Message {

        private boolean isRead;
        private String sender;
        private String receiver;
        private String body;

        //Constructor
        public Message(boolean isRead, String sender, String receiver, String body) {
            this.isRead = isRead;
            this.sender = sender;
            this.receiver = receiver;
            this.body = body;
        }

        public boolean isRead() {
            return isRead;
        }

        public void setRead(boolean read) {
            isRead = read;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getReceiver() {
            return receiver;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }


    //----------------------------------Account----------------------------------//
    private static class Account {

        private String username;
        private int authToken;
        private List<Message> messageBox = new ArrayList<Message>();

        //Constructor
        public Account(String username, int authToken) {
            this.username = username;
            this.authToken = authToken;
        }


        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getAuthToken() {
            return authToken;
        }

        public void setAuthToken(int authToken) {
            this.authToken = authToken;
        }

        public List<Message> getMessageBox() {
            return messageBox;
        }

        public void addMessageBox(Message message) {
            messageBox.add(message);
        }


    }

    public static int findUsername(String username) {
        int index = -1;

        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).username.equals(username)) {
                index = i;
            }
        }

        return index;
    }

    public static int findAuthToken(String authToken) {
        int index = -1;

        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).authToken == Integer.parseInt(authToken)) {
                index = i;
            }
        }

        return index;
    }
}