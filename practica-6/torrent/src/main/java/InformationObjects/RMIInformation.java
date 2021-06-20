package InformationObjects;

import java.io.Serializable;

public class RMIInformation implements Serializable, Comparable<RMIInformation> {
    private String rmiIpAddress;
    private int rmiPort;
    
    public RMIInformation(String rmiIpAddress, int rmiPort) {
        this.rmiIpAddress = rmiIpAddress;
        this.rmiPort = rmiPort;
    }

    public String getRmiIpAddress() {
        return rmiIpAddress;
    }

    public void setRmiIpAddress(String rmiIpAddress) {
        this.rmiIpAddress = rmiIpAddress;
    }

    public int getRmiPort() {
        return rmiPort;
    }

    public void setRmiPort(int rmiPort) {
        this.rmiPort = rmiPort;
    }
    
    @Override
    public String toString() {
        String informationString = "RMI address: " + rmiIpAddress + " :: Port : " + rmiPort;
        return informationString;
    }

    @Override
    public int compareTo(RMIInformation o) {
        int result = o.rmiIpAddress.compareTo(rmiIpAddress);
        if (result != 0)
            return result;
        result = o.rmiPort - rmiPort;
        return result;
    }
}
