package com.socket_client;

public class Header {
    public String protocolName = "AFTP";
    public String protocolVersion = "1.0";
    public String method = null;
    public String filePath = null;
    public String fileName = null;
    public Long contentLength = null;
    public String contentCheckSum = null;
    public String contentName = null;
    public Long contentSize = null;
    public String responseCode = null;
    public String responseName = "OK";

    public byte[] toByteArray() {
        String header = method + " " + filePath + " " + protocolName + "/" + protocolVersion;

        if (fileName != null){
            header = method + " " + filePath + fileName + " " + protocolName + "/" + protocolVersion;
        }

        if (contentSize != null) {
            header += "Content-size: " + contentSize + "\n";
        }
        if (contentLength != null){
            header += "\r\n" + "File-Name: " + contentName + "\r\n" + "Content-Length: " + contentLength;
        }

        if (contentCheckSum != null){
            header += "\r\n" + "ETag: " + contentCheckSum;
        }

        if (responseCode != null){
            header = responseCode + " " + responseName + " " + protocolName + "/" + protocolVersion;
            header += "\r\n" + "File-Name: " + contentName + "\r\n" + "Content-Length: " + contentLength;
        }

        header += "\n";
        System.out.println(header);
        return header.getBytes();
    }
}
