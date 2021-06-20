package InformationObjects;

import java.io.File;
import java.io.Serializable;

public class FileInformation implements Serializable, Comparable<FileInformation> {
    private String serverIpAddress;
    private File fileName;
    private String checksum;
    private int serverPort;

    public FileInformation(String serverIpAddress, File fileName, String checksum, int serverPort) {
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

    public File getFile() {
        return fileName;
    }

    public void setFile(File fileName) {
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
                + ", fileName=" + fileName.getName() + ", checksum=" + checksum 
                + ", serverPort=" + serverPort + '}';
    }

    @Override
    public int compareTo(FileInformation o) {
        int result;
        result = serverIpAddress.compareTo(o.serverIpAddress);
        if (result != 0)
            return result;
        
        result = fileName.compareTo(o.fileName);
        if (result != 0)
            return result;
        
        result = checksum.compareTo(o.checksum);
        if (result != 0)
            return result;
        
        result = serverPort - o.serverPort;
        
        return result;
    }
    
    
}
