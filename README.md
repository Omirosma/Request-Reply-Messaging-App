# Request Reply Messaging App

This is a simple client-server messaging system implemented in Java. It allows users to create accounts, send messages, and manage their inbox.

## Features

- Create user accounts
- Show list of accounts
- Send messages
- Showing user inboxes
- Reading messages
- Deleting messages

## Prerequisites

- Java 8 or later

## Getting Started

1. **Clone the repository:**
    ```bash
    git clone https://github.com/yourusername/messaging-system.git
    cd messaging-system
    ```

2. **Compile the code:**
    ```bash
    javac Server.java
    javac Client.java
    ```

3. **Run the server:**
    ```bash
    java Server <port number>
    ```

4. **Run the client:**
    ```bash
    java Client <ip> <port number> <FN_ID> [additional_arguments]
    ```

## Class Descriptions

### Server

- The `Server` class is responsible for accepting incoming client connections, handling various operations such as creating accounts, showing accounts, sending messages, etc.

### Client

- The `Client` class connects to the server and sends requests based on the provided operation ID.

### ClientHandler

- The `ClientHandler` class is a helper class within the `Server` that manages individual client connections. It handles threads, authentication, account creation, messaging, and other operations.

### Message

- The `Message` class represents a user message with attributes such as sender, body, and read status.

### Account

- The `Account` class represents a user account with a username, authentication token, and a message box to store received messages.

## Methods

### findUsername & findAuthToken

- Two methods that handle searches based on `username` and `authToken`. Both of them return the index showing the position inside the accounts list.

## Usage

### Server

- The server listens for incoming connections on the specified port.
- It handles various operations such as creating accounts, showing accounts, sending messages, etc.

    ```bash
    java Server <port number>
    ```

### Client

- The client connects to the server and sends requests based on the provided operation ID.

    ```bash
    java Client <ip> <port number> <FN_ID> [additional_arguments]
    ```

- Example:

    ```bash
    java Client localhost 5858 1 Omiros
    ```

  This creates a new account with the username "Omiros."

## Operations

1. **Create Account:**
    ```bash
    java Client <ip> <port number> 1 <username>
    ```

2. **Show Accounts:**
    ```bash
    java Client <ip> <port number> 2
    ```

3. **Send Message:**
    ```bash
    java Client <ip> <port number> 3 <authToken> <recepient> <message_body>
    ```

4. **Show Inbox:**
    ```bash
    java Client <ip> <port number> 4 <authToken>
    ```

5. **Read Message:**
    ```bash
    java Client <ip> <port number> 5 <authToken> <message_id>
    ```

6. **Delete Message:**
    ```bash
    java Client <ip> <port number> 6 <authToken> <message_id>
    ```

## Notes

- Ensure that the server is running before connecting with the client.
- Messages are stored in the user's inbox on the server.
