import java.io.*;
import java.net.*;
import java.util.*;


public class Server{

    private static List<Account> accounts = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {

        ServerSocket server = null;
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
            public ClientHandler(Socket socket)
            {
                this.clientSocket = socket;
            }


            // the thread .......
            public void run()
            {

                try {

                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
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
                            }

                            //Exists? check
                            for (int i = 0; i < accounts.size(); i++) {
                                if (accounts.get(i).username.equals(split[3])) {
                                    System.out.println("Sorry, the user already exists");
                                    out.println("Sorry, the user already exists");
                                    throw new RuntimeException();
                                }
                            }


                            Random random = new Random();
                            int auth=random.nextInt(9999);


                            //Token exist? check
                            for (int i = 0; i < accounts.size(); i++) {
                                if (auth == accounts.get(i).authToken) {
                                    auth = random.nextInt(9999);
                                    i=0;
                                }
                            }

                            //Creating account
                            accounts.add(new Account(split[3],auth));
                            System.out.println(accounts.getLast().username);
                            out.println(auth);
                            break;




                        //SHOW ACCOUNTS---------------------------------------------------------------
                        case 2:
                            System.out.println("--Case 2--");

                            String userNameList="";

                            for (int i = 0; i < accounts.size(); i++) {
                                if (accounts.get(i).authToken != Integer.parseInt(split[3])) {
                                    System.out.println(i+1+". "+accounts.get(i).username);
//                                    i++;
//                                    userNameList +=  i + ". " + accounts.get(i).username+" ";
//                                    i--;
                                    out.println(i+1+". "+accounts.get(i).username);

                                }
                            }
//                            System.out.println(userNameList);

                            Thread.currentThread().interrupt();
                            break;



                        //SEND MESSAGE---------------------------------------------------------------
                        case 3:
                            System.out.println("--Case 3--");
                            break;
                        case 4:
                            System.out.println("--Case 4--");
                            break;
                        case 5:
                            System.out.println("--Case 5--");
                            break;
                        case 6:
                            System.out.println("--Case 6--");
                            break;
                    }




                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                // command "finally" is omitted
            }
        }
/*
        private static class Package {
            private boolean operationId;
            private String username;
            private String authToken;
            private String recipient;
            private String message_body;
            private String message_id;


            public Package(boolean operationId) {
                this.operationId = operationId;
            }

            public Package(boolean operationId, String username, String authToken, String recipient, String message_body, String message_id) {
                this.operationId = operationId;
                this.username = username;
                this.authToken = authToken;
                this.recipient = recipient;
                this.message_body = message_body;
                this.message_id = message_id;
            }


        }*/

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
                this.messageBox = messageBox;
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

        public void setMessageBox(List<Message> messageBox) {
            this.messageBox = messageBox;
        }
    }

}
