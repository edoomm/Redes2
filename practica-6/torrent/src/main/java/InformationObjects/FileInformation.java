package InformationObjects;

import java.io.Serializable;

public class FileInformation implements Serializable {
    private String serverIpAddress;
    private String fileName;
    private String checksum;
    private int serverPort;

    public FileInformation(String serverIpAddress, String fileName, String checksum, int serverPort) {
        this.serverIpAddress = serverIpAddress;
        this.fileName = fileName;
        this.checksum = checksum;
        this.serverPort = serverPort;
    }

    public String getServerIpAddress() {
        return serverIpAddress;
    }

    public void setServerIpAddress(String serverIpAddress) {
        this.serverIpAddress = serverIpAddress;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public String toString() {
        return "FileInformation{" + "serverIpAddress=" + serverIpAddress 
                + ", fileName=" + fileName + ", checksum=" + checksum 
                + ", serverPort=" + serverPort + '}';
    }
    
    
}
