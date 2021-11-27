import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Enumeration;
import java.util.List;

public class IPaddress {
    static String localAddress() {
        String localIpAddr = null;
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            netInterFace : while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] hardwareAddress = networkInterface.getHardwareAddress();
                if ((hardwareAddress == null) || (hardwareAddress.length == 0)) {
                    continue;
                }

                List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
                for (InterfaceAddress interfaceAddress : interfaceAddresses) {
                    InetAddress address = interfaceAddress.getAddress();
                    if (address.isLoopbackAddress()) {
                        continue;
                    }
                    if (address instanceof Inet4Address) {
                        localIpAddr = address.getHostAddress();
                        break netInterFace;
                    }
                }
            }
        } catch (Exception e) {
            localIpAddr = "127.0.0.1";
        }

        if (localIpAddr == null) {
            try {
                localIpAddr = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                localIpAddr = "127.0.0.1";
            }
        }
        return localIpAddr;
    }
}
