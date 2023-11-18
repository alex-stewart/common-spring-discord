package fun.pancakes.commonspringdiscord.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringSimilarityUtil {

    private static final LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

    public static double similarity(String s1, String s2) {
        String longer = s1;
        String shorter = s2;
        if (s1.length() < s2.length()) {
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0;
        }
        return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength;
    }
}
