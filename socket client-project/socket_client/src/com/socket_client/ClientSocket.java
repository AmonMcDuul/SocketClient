package com.socket_client;

import com.google.gson.*;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ClientSocket {

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedReader reader;
    private String filePath = "./client_files/";
    private byte[] outputBody;
    private Header outputHeader;
    private final File fileFolder = new File(filePath);

    void openSocket() throws IOException {
        // Initialize socket and stream
        socket = new Socket("127.0.0.1", 9123);
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
        System.out.println("Connectie gemaakt");
    }

    void closeSocket() throws IOException {
        // Close writer, stream and socket
        inputStream.close();
        outputStream.close();
        socket.close();
        System.out.println("Connectie gesloten");
    }

    // Get client files in a list
    public void clientFiles() throws IOException {
        List<FileUpload> fileList = new ArrayList<>();
        String jsonFileList;
        System.out.println("Get client file list\n\n");
        // Read files in file directory
        for (final File file : Objects.requireNonNull(fileFolder.listFiles())) {
            if (!file.getName().startsWith(".")) {
                fileList.add(new FileUpload(file));
                System.out.println(file.getName());
            }
        }
        System.out.println("\n");
    }

    //Get server files in a list
    public void listFiles() throws IOException {
        System.out.println("---------------------");
        System.out.println("List server files");
        System.out.println("---------------------");

        // Open socket connection
        openSocket();

        // Define output header and sent request
        outputHeader = new Header();
//        outputHeader.protocolName = "AFTP";
//       outputHeader.protocolVersion = "1.0";
        outputHeader.filePath = "../files/";
        outputHeader.method = "LIST";

        outputStream.write(outputHeader.toByteArray());

        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line = reader.readLine();
        String str = "";
        while (line != null && !line.isEmpty()) {
            str = str + " " + line;
            line = reader.readLine();
        }
        String parentStringValue = str.substring(str.indexOf("A"));

        List<String> serverFileList = new ArrayList<>(Arrays.asList(parentStringValue.split(" ")));

        System.out.println("Get server file list\n");
        int size = serverFileList.size();
        for (int i = 5; i < size; i= i+3) {
            System.out.println(serverFileList.get(i));
        }
        System.out.println("\n");

        try{
            Writer writer = new FileWriter(filePath+ "listOfServerFiles.json");
            Gson gson = new GsonBuilder().create();
            gson.toJson(serverFileList, writer);
            writer.flush(); //flush data to file   <---
            writer.close(); //close write          <---
        }catch(Exception e){
            e.printStackTrace();
        }

        // Close socket connection
        closeSocket();
    }


    // GET server file
    public void getFile(String fileName) throws IOException {
        System.out.println("---------------------");
        System.out.println("Get server file");
        System.out.println("---------------------");

        // Open socket connection
        openSocket();

        // Define output header and sent request
        outputHeader = new Header();
//        outputHeader.protocolName = "AFTP";
//        outputHeader.protocolVersion = "1.0";
        outputHeader.filePath = "";
        outputHeader.fileName = fileName;
        outputHeader.method = "GET";
        outputStream.write(outputHeader.toByteArray());
        //outputStream.flush();

        File file = new File(filePath + fileName);
        byte[] mybytearray = new byte[2048];
        InputStream is = socket.getInputStream();
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

            int count;
            while ((count = is.read(mybytearray)) > 0) {
                bos.write(mybytearray, 0, count);
                System.out.println(count);
                System.out.println("Downloading..");
            }

        //Close BufferedOutputStream
        bos.close();
        // Close socket connection
        closeSocket();
    }

    //Get all server files
    public void getAllFiles() throws IOException {

        System.out.println("---------------------");
        System.out.println("Get all server files");
        System.out.println("---------------------");

        StringBuilder listOfFiles = new StringBuilder();
        FileInputStream fileInputStream = new FileInputStream(filePath + "listOfServerFiles.json");
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;

        // Append each line to the output
        while ((line = bufferedReader.readLine()) != null) {
        //    System.out.println(line);
            listOfFiles.append(line);
        }
      //  System.out.println(listOfFiles);
        String list = listOfFiles.toString().replace("\"","").replace("[","");

        List<String> serverFileList = new ArrayList<>(Arrays.asList(list.split(",")));

        //see results:
        StringBuilder results = new StringBuilder();
        int countResults = 0;
        int size = serverFileList.size();
        for (int i = 5; i < size; i = i+3) {
            System.out.println("Downloading file: "+serverFileList.get(i));
            getFile(serverFileList.get(i));
            results.append(serverFileList.get(i)).append("\n");
            countResults++;
        }
        System.out.println("\n");
        System.out.println("Downloaded "+ countResults + " files");
        System.out.println(results);
        System.out.println("\n");
        // Close socket connection
        closeSocket();
    }

    //PUT file on server
    public void upload(FileUpload fileUpload) throws IOException {
        System.out.println("---------------------");
        System.out.println("Upload file: " + fileUpload.fileName);
        System.out.println("---------------------");

        // Open socket connection
        openSocket();
        // Define output header and sent request
        outputHeader = new Header();
        outputHeader.method = "PUT";
        outputHeader.contentLength = fileUpload.contentLength;
        outputHeader.contentCheckSum = fileUpload.checksum;
        outputHeader.contentName = fileUpload.fileName;
        outputHeader.filePath = "/";

        // Read file in file directory
        File file = new File(filePath + "/" + fileUpload.fileName);
        // Assign bytes to output body
        long size = fileUpload.contentLength;
        //byte[] outputBody = new byte[(int) size];
        outputBody = new byte[(int) size];
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        bufferedInputStream.read(outputBody, 0, outputBody.length);
        bufferedInputStream.close();

        // Write header and body
        outputStream.write(outputHeader.toByteArray());
        outputStream.write(outputBody);
        outputStream.flush();

//        outputHeader = new Header();
//        outputHeader.responseCode = "200";
//        outputHeader.contentLength = fileUpload.contentLength;
//        outputHeader.contentName = fileUpload.fileName;
//        outputStream.write(outputHeader.toByteArray());

        closeSocket();
    }

    //DELETE server file
    public void deleteFile(String fileName) throws IOException {
        System.out.println("---------------------");
        System.out.println("Delete file: " + fileName);
        System.out.println("---------------------");

        //Make connection
        openSocket();

        //Header
            outputHeader = new Header();
            outputHeader.method = "DELETE";
            outputHeader.filePath = "";
            outputHeader.fileName = fileName;

            outputStream.write(outputHeader.toByteArray());

        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // Close socket connection
        closeSocket();
    }

    //Delete client file
    public void deleteClientFile(String fileName) throws IOException {
        System.out.println("---------------------");
        System.out.println("Delete client file: " + fileName);
        System.out.println("---------------------");

        // Read file in file directory
        File file = new File(filePath + "/" + fileName);
        if (!file.exists()) {
            System.out.println("File not found");
        } else {
            // File found
            Files.delete(file.toPath());
        }
    }

    //Delete all client files
    public void deleteAllClientFiles() throws IOException {
        System.out.println("---------------------");
        System.out.println("Delete all client files: ");
        System.out.println("---------------------");

        File file = new File(filePath);
        String[] myFiles;
        if (file.isDirectory()) {
            myFiles = file.list();
            for (int i = 0; i < myFiles.length; i++) {
                File myFile = new File(file, myFiles[i]);
                myFile.delete();
                System.out.println("File Deleted: "+ myFile);
            }
         System.out.println("All client files deleted.");
        }
    }

    //VOOR SYNCHRONISATIE E.D.
    public void updateClientList() throws IOException {
        System.out.println("---------------------");
        System.out.println("Update Client list");
        System.out.println("---------------------");

        List<FileUpload> fileList = new ArrayList<>();
        String jsonFileList;

        // Read files in file directory
        for (final File file : Objects.requireNonNull(fileFolder.listFiles())) {
            if (!file.getName().startsWith(".")) {
                fileList.add(new FileUpload(file));
                System.out.println(file.getName());
            }
        }

        // Convert file list to JSON object
        jsonFileList = (new Gson()).toJson(fileList);

        // Write into file
        File file = new File(filePath + "/" + "listOfClientFiles.json");
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
        bufferedOutputStream.write(jsonFileList.getBytes(), 0, jsonFileList.length());

        // Close buffer stream
        bufferedOutputStream.close();
        listFiles();
    }

    //file compare
    public void fileCompare(String fileName) throws IOException {

        //get server files
        listFiles();
        StringBuilder listOfServerFiles = new StringBuilder();
        FileInputStream fileInputStream = new FileInputStream(filePath+ "listOfServerFiles.json");
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;

        // Append each line to the json output
        while ((line = bufferedReader.readLine()) != null) {
            listOfServerFiles.append(line);
        }

        String list = listOfServerFiles.toString().replace("\"","").replace(("]"),"").replace("[","");

        List<String> serverFileList = new ArrayList<>(Arrays.asList(list.split(",")));

        String actualServerFile = "";
        int size = serverFileList.size();
        for (int i = 5; i < size; i++) {
            if(serverFileList.get(i).equals(fileName)){
                actualServerFile = serverFileList.get(i) + " " + serverFileList.get(i+2);
            }
        }
        inputStreamReader.close();
        bufferedReader.close();

        //get client files
        StringBuilder listOfClientFiles = new StringBuilder();
        FileInputStream fileInputStreamClient = new FileInputStream(filePath + "listOfClientFiles.json");
        InputStreamReader inputStreamReaderClient = new InputStreamReader(fileInputStreamClient);
        BufferedReader bufferedReaderClient = new BufferedReader(inputStreamReaderClient);
        String lineClient;

        // Append each line to the output
        while ((lineClient = bufferedReaderClient.readLine()) != null) {
            listOfClientFiles.append(lineClient);
        }

        String listClient = listOfClientFiles.toString().replace("\"","").replace("{fileName:","").replace("contentLength:","").replace("checksum:","").replace("}","").replace("[","").replace("]","");

        List<String> clientFileList = new ArrayList<>(Arrays.asList(listClient.split(",")));
        String actualClientFile = "";
        int sizeClient = clientFileList.size();
        for (int i = 0; i < sizeClient; i++) {
            if(clientFileList.get(i).equals(fileName)){
                actualClientFile = clientFileList.get(i) + " " + clientFileList.get(i+2);
            }
        }

        inputStreamReaderClient.close();
        bufferedReaderClient.close();
        System.out.println("\r\n");
        System.out.println("Client File: " + actualClientFile);
        System.out.println("Server File: " + actualServerFile);

        if (actualServerFile.equals(actualClientFile)){
            System.out.println("Files are the same");
            System.out.println("\r\n");
        } else{
            System.out.println("Files are different");
            System.out.println("\r\n");
        }
    }


    //Lock document
    public void lockFile(String fileName) throws IOException {
        System.out.println("---------------------");
        System.out.println("Lock file: " + fileName);
        System.out.println("---------------------");

        //Make connection
        openSocket();

        //Header
        outputHeader = new Header();
        outputHeader.method = "LOCK";
        outputHeader.filePath = "";
        outputHeader.fileName = fileName;

        outputStream.write(outputHeader.toByteArray());

        // Close socket connection
        closeSocket();
    }
    //Lock document
    public void unlockFile(String fileName) throws IOException {
        System.out.println("---------------------");
        System.out.println("Unlock file: " + fileName);
        System.out.println("---------------------");

        //Make connection
        openSocket();

        //Header
        outputHeader = new Header();
        outputHeader.method = "UNLOCK";
        outputHeader.filePath = "";
        outputHeader.fileName = fileName;

        outputStream.write(outputHeader.toByteArray());

        // Close socket connection
        closeSocket();
    }

}
