package ch.hatbe.minecraft.tcpserver;

import java.util.Base64;

public class PackageHelper {
    public static String encodeNetworkPackage(String[] segments) {
        StringBuilder res = new StringBuilder();

        for (int i = 0; i < segments.length; i++) {
            res.append(Base64.getEncoder().encodeToString(segments[i].getBytes()));
            if(i != segments.length - 1) {
                res.append(",");
            }
        }

        return res.toString();
    }
}
