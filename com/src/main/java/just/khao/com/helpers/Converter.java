package just.khao.com.helpers;

import org.springframework.stereotype.Service;

@Service
public class Converter {

    public String convertToAlphanumericWithUnderscore(String input) {
        String str = input.replaceAll("[^a-zA-Z0-9]", ".");
        int start = 0;
        int end = str.length() - 1;

        while (start <= end && str.charAt(start) == '.') {
            start++;
        }

        while (end >= start && str.charAt(end) == '.') {
            end--;
        }

        StringBuilder sb = new StringBuilder();
        boolean underscoreSeen = false;

        for (int i = start; i <= end; i++) {
            char c = str.charAt(i);

            if (c == '.') {
                if (!underscoreSeen) {
                    sb.append(c);
                }
                underscoreSeen = true;
            } else {
                sb.append(c);
                underscoreSeen = false;
            }
        }

        return sb.toString();
    }
}
