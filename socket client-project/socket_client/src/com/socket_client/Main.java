package com.socket_client;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        ClientSocket clientSocket = new ClientSocket();
        System.out.println("\nInitialising files..\n");
            clientSocket.updateClientList();
        System.out.println("\nFiles are initialised\n\n");

        Console console = new Console();
        console.Menu();
    }
}
