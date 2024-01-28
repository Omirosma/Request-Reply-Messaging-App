import java.io.*;
import java.net.*;
import java.util.*;


public class Server {

    private static final List<Account> accounts = Collections.synchronizedList(new ArrayList<>());


    public static void main(String[] args) {

        ServerSocket server;
        int port = Integer.parseInt(args[0]);


        try {
            // server is listening on port 5858
            server = new ServerSocket(port);

            // running infinite loop accepting client request
                while (true) {
                // socket object to receive incoming client requests
                Socket client = server.accept();
//                System.out.println("New client connected " + client.getInetAddress().getHostAddress());

                // create a new thread object of Runnable
                ClientHandler clientSock = new ClientHandler(client);

                // This thread will handle the client separately
                new Thread(clientSock).start();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //----------------------------------ClientHandler----------------------------------//
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;


        // Constructor
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }


        // The thread
        public void run() {

            try {

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                String str = in.readLine();
                System.out.println(str);
                String[] split = str.split(" ");



                //AUTHENTICATION
                if (Integer.parseInt(split[2]) != 1 && findAuthToken(split[3]) == -1) {
                    out.println("Invalid Auth Token");
                    throw new IllegalArgumentException("Invalid Auth Token");
                }

                switch (Integer.parseInt(split[2])) {

                    //CREATE ACCOUNTS---------------------------------------------------------------
                    case 1:
                        System.out.println("--Case 1--");

                        //Spelling check
                        boolean allLetters = split[3].chars().allMatch(Character::isLetter);
                        if (!allLetters) {
                            out.println("Invalid Username");
                            throw new IllegalArgumentException("Invalid Username");
                        }

                        //Username Exists? check
                        if (findUsername(split[3]) != -1) {
                            out.println("Sorry, the user already exists");
                            throw new IllegalArgumentException("Sorry, the user already exists");
                        }


                        Random random = new Random();
                        int auth = random.nextInt(9999);


                        //Token exist? check
                        while (findAuthToken(auth + "") != -1 || auth<1000) {
                            auth = random.nextInt(9999);
                        }

                        //Creating account
                        accounts.add(new Account(split[3], auth));
                        System.out.println("New account created.\nUsername: "+accounts.getLast().getUsername()+"\nToken: "+auth);
                        out.println(auth);

                        break;


                    //SHOW ACCOUNTS---------------------------------------------------------------
                    case 2:
                        System.out.println("--Case 2--");

                        for (int i = 0; i < accounts.size(); i++) {
                            System.out.println(i + 1 + ". " + accounts.get(i).getUsername());

                            out.println(i + 1 + ". " + accounts.get(i).getUsername());
                        }

                        break;


                    //SEND MESSAGE---------------------------------------------------------------
                    case 3:
                        System.out.println("--Case 3--");

                        //Receiver exists check
                        if (findUsername(split[4]) == -1) {
                            throw new IllegalArgumentException("User does not exist");
                        }

                        StringBuilder body= new StringBuilder();

                        for (int i = 5; i < split.length; i++) {
                            body.append(split[i]).append(" ");
                        }


                        accounts.get(findUsername(split[4])).addMessageBox(new Message(accounts.get(findAuthToken(split[3])).getUsername(), body.toString()));
                        System.out.println("OK");
                        out.println("OK");

                        break;


                    //SHOW INBOX---------------------------------------------------------------
                    case 4:
                        System.out.println("--Case 4--");


                        for (int i = 0; i <accounts.get(findAuthToken(split[3])).getMessageBox().size() ; i++) {

                            System.out.println(i+". from: "+accounts.get(findAuthToken(split[3])).getMessageBox().get(i).getSender()+(accounts.get(findAuthToken(split[3])).getMessageBox().get(i).isRead()?"":"*"));
                            out.println(i+". from: "+accounts.get(findAuthToken(split[3])).getMessageBox().get(i).getSender()+(accounts.get(findAuthToken(split[3])).getMessageBox().get(i).isRead()?"":"*"));
                        }


                        break;

                    //READ MESSAGE---------------------------------------------------------------
                    case 5:
                        System.out.println("--Case 5--");

                        if (accounts.get(findAuthToken(split[3])).getMessageBox().get(Integer.parseInt(split[4])) == null) {
                            throw new IllegalArgumentException("Message ID does not exist");
                        }

                        System.out.printf("("+accounts.get(findAuthToken(split[3])).getMessageBox().get(Integer.parseInt(split[4])).getSender()+") "+accounts.get(findAuthToken(split[3])).getMessageBox().get(Integer.parseInt(split[4])).getBody());
                        out.println("("+accounts.get(findAuthToken(split[3])).getMessageBox().get(Integer.parseInt(split[4])).getSender()+") "+accounts.get(findAuthToken(split[3])).getMessageBox().get(Integer.parseInt(split[4])).getBody());
                        accounts.get(findAuthToken(split[3])).getMessageBox().get(Integer.parseInt(split[4])).setRead(true);

                        break;

                    //DELETE MESSAGE---------------------------------------------------------------
                    case 6:
                        System.out.println("--Case 6--");

                        if (accounts.get(findAuthToken(split[3])).getMessageBox().get(Integer.parseInt(split[4])) == null) {
                            throw new IllegalArgumentException("Message does not exist");
                        }

                        accounts.get(findAuthToken(split[3])).getMessageBox().remove(Integer.parseInt(split[4]));
                        out.println("OK");


                        break;
                }

            } catch (IllegalArgumentException | IOException e) {
                System.out.printf(e.getMessage()+"\n");
            }
        }
    }

    //----------------------------------Message----------------------------------//
    private static class Message {

        private boolean isRead;
        private final String sender;
        private final String body;

        /**
         * Instantiates a new Message.
         *
         * @param sender the sender
         * @param body   the body
         */
//Constructor
        public Message(String sender, String body) {
            this.isRead = false;
            this.sender = sender;
            this.body = body;
        }

        /**
         * Is read boolean.
         *
         * @return the boolean
         */
        public boolean isRead() {
            return isRead;
        }

        /**
         * Sets read.
         *
         * @param read the read
         */
        public void setRead(boolean read) {
            isRead = read;
        }

        /**
         * Gets sender.
         *
         * @return the sender
         */
        public String getSender() {
            return sender;
        }

        /**
         * Gets body.
         *
         * @return the body
         */
        public String getBody() {
            return body;
        }
    }


    //----------------------------------Account----------------------------------//
    private static class Account {

        private final String username;
        private final int authToken;
        private final List<Message> messageBox ;

        /**
         * Instantiates a new Account.
         *
         * @param username  the username
         * @param authToken the auth token
         */
//Constructor
        public Account(String username, int authToken) {
            this.username = username;
            this.authToken = authToken;
            this.messageBox = new ArrayList<>();
        }


        /**
         * Gets username.
         *
         * @return the username
         */
        public String getUsername() {
            return username;
        }

        /**
         * Gets auth token.
         *
         * @return the auth token
         */
        public int getAuthToken() {
            return authToken;
        }

        /**
         * Gets message box.
         *
         * @return the message box
         */
        public List<Message> getMessageBox() {
            return messageBox;
        }

        public void addMessageBox(Message message) {
            messageBox.add(message);
        }
    }

    //Method that returns an index of the place where the username is from
    public static int findUsername(String username) {
        int index = -1;

        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getUsername().equals(username)) {
                index = i;
            }
        }

        return index;
    }


    //Method that returns an index of the place where the authentication token is from
    public static int findAuthToken(String authToken) {
        int index = -1;

        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getAuthToken() == Integer.parseInt(authToken)) {
                index = i;
            }
        }

        return index;
    }
}