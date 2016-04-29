package com.renovavision.tcpsocketchat.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

// utils class for getting image
public class IPUtils {

    public static String getIpAddresses() {

        StringBuilder ipBuilder = new StringBuilder();

        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ipBuilder.append("Ip address: ").
                                append(inetAddress.getHostAddress()).append("\n");
                    }

                }

            }

        } catch (SocketException e) {
            ipBuilder.append("Something wrong! ");
        }

        return ipBuilder.toString();
    }
}
