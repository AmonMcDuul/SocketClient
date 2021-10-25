package com.socket_client;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Console {

    public void Menu() {
        Console console = new Console();
        ClientSocket clientSocket = new ClientSocket();
        Scanner scanner = new Scanner(System.in);
        System.out.print(" Press 1 for the clientFiles\r\n " +
                "Press 2 for listFiles\r\n " +
                "Press 3 for getFile\r\n " +
                "Press 4 for upload\r\n " +
                "Press 5 for deleteFile\r\n " +
                "Press 6 for deleteClientFile\r\n " +
                "Press 7 for getAllFiles\r\n " +
                "Press 8 for compareFiles\r\n " +
                "Press 9 to update clientFileList and serverFileList\r\n " +
                "Press L to lock document\r\n " +
                "Press U to unlock document\r\n " +
                "Press q to exit..");

        String keus = scanner.next();
        switch (keus) {
            case "1":
                // code block
                try {
                    clientSocket.clientFiles();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case "2":
                try {
                    clientSocket.listFiles();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "3":
                System.out.print("What file?\r\n");
                String fileChoice = scanner.next();
                try {
                    System.out.print("Your file is: " + fileChoice + "\r\n");
                    clientSocket.fileCompare(fileChoice);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.print("Continue?\r\nPress y to continue,. Press any key to cancel\r\n");
                String getKeus = scanner.next();
                switch(getKeus)    {
                    case "y":
                        try {
                            clientSocket.getFile(fileChoice);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        System.out.println("Back to start menu\r\n");
                }

                break;
            case "4":
                System.out.print("What file?\r\n");
                String fileName = scanner.next();
                try {
                    File file = new File("./client_files/"+fileName);
                    FileUpload files = new FileUpload(file);
                    clientSocket.upload(files);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "5":
                System.out.print("What file?\r\n");
                String fileToDelete = scanner.next();
                try {
                    clientSocket.deleteFile(fileToDelete);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "6":
                System.out.print("Press capital A to delete all files. Press 1 to delete one specific file");
                String allOrOne = scanner.next();
                switch(allOrOne) {
                    case "A":
                        System.out.print("Are you sure to delete all client files?\r\nThere is no turning back!\r\n Press y to delete. Press any other key to cancel\r\n");
                        String delete = scanner.next();
                        switch (delete) {
                            case "y":
                                try {
                                    clientSocket.deleteAllClientFiles();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                        }
                    default:
                        System.out.println("Back to start menu\r\n");
                        console.Menu();
                    case "1":
                        System.out.print("What file?\r\n");
                        String clientFileToDelete = scanner.next();
                        System.out.print("Are you sure to delete " + clientFileToDelete + "\r\n Press y to delete. Press any key to cancel\r\n");
                        String deleteKeus = scanner.next();
                        switch (deleteKeus) {
                            case "y":
                                // code block
                                try {
                                    clientSocket.deleteClientFile(clientFileToDelete);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                System.out.println("Back to start menu\r\n");
                        }
                }
                break;
            case "7":
                System.out.println("Get all files\r\n");
                try{
                    clientSocket.getAllFiles();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "8":
                System.out.println("Trying to compare files\r\n");
                System.out.print("What file?\r\n");
                String fileCompareName = scanner.next();
                try{
                    clientSocket.fileCompare(fileCompareName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "9":
                try {
                    clientSocket.updateClientList();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "L":
                System.out.print("What file do you want to lock?\r\n");
                String fileToLock = scanner.next();
                try {
                    clientSocket.lockFile(fileToLock);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "U":
                System.out.print("What file do you want to unlock?\r\n");
                String fileToUnlock = scanner.next();
                try {
                    clientSocket.unlockFile(fileToUnlock);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "q":
                System.out.print("Exit...\r\n");
                System.exit(0);
                break;
            default:
                System.out.println("You have to choose!\r\n");
                System.out.println("Your choice was " + keus + "\r\n");
        }console.Menu();
    }
}

