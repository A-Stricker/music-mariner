package com.amandastricker.musicmariner.utilities;

public class StringUtils {
    /**
     * Calculates the Levenshtein distance between two character sequences.
     * @param lhs the left-hand character sequence
     * @param rhs the right-hand character sequence
     * @return the Levenshtein distance
     */
    public static int levenshteinDistance(CharSequence lhs, CharSequence rhs) {
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;

        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        for (int i = 0; i < len0; i++) cost[i] = i;

        for (int j = 1; j < len1; j++) {
            newcost[0] = j;

            for (int i = 1; i < len0; i++) {
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

                int costReplace = cost[i - 1] + match;
                int costInsert = cost[i] + 1;
                int costDelete = newcost[i - 1] + 1;

                newcost[i] = Math.min(Math.min(costInsert, costDelete), costReplace);
            }

            int[] swap = cost;
            cost = newcost;
            newcost = swap;
        }

        return cost[len0 - 1];
    }
    public static String normalizeString(String input) {
        return input
                .replaceAll("\\(.*?\\)|\\[.*?\\]|\\{.*?\\}|\\<.*?>", "") // Removes text in brackets of any kind
                .replaceAll("-.*", "") // Removes text after a hyphen
                .trim()
                .toLowerCase();
    }
}
