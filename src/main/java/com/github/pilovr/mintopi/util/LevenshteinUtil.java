package com.github.pilovr.mintopi.util;

import java.util.HashSet;
import java.util.Set;

public class LevenshteinUtil {

    public static int calculateDistance(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                                    + (x.charAt(i - 1) == y.charAt(j - 1) ? 0 : 1),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[x.length()][y.length()];
    }

    private static int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    public static Set<String> getClosestStrings(Set<String> strings, String target, int threshold){
        Set<String> closests = new HashSet<>();
        int currentDistance = threshold;
        for(String s : strings){
            int dist = calculateDistance(s, target);
            if(dist < currentDistance) {
                currentDistance = dist;
                closests.clear();
                closests.add(s);
            } else if(dist == currentDistance){
                closests.add(s);
            }
        }
        return closests;
    }

}
