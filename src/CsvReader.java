import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Reads participants from a CSV file with the header: name,course,date
 * (the "date" column is optional; today's date is used when it is empty).
 */
public class CsvReader {

    public static List<Participant> read(Path file) throws IOException {
        List<Participant> result = new ArrayList<>();
        String today = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH));

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IOException("CSV file is empty: " + file);
            }
            if (headerLine.startsWith("\uFEFF")) {          // strip BOM added by Excel
                headerLine = headerLine.substring(1);
            }

            List<String> header = splitLine(headerLine);
            int nameIdx   = indexOf(header, "name");
            int courseIdx = indexOf(header, "course");
            int dateIdx   = indexOf(header, "date");
            if (nameIdx < 0 || courseIdx < 0) {
                throw new IOException("CSV header must contain at least: name,course");
            }

            String line;
            int lineNo = 1;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (line.trim().isEmpty()) continue;

                List<String> cols = splitLine(line);
                String name   = get(cols, nameIdx);
                String course = get(cols, courseIdx);
                String date   = dateIdx >= 0 ? get(cols, dateIdx) : "";

                if (name.isEmpty() || course.isEmpty()) {
                    System.out.println("  [skip] line " + lineNo + ": name or course is missing");
                    continue;
                }
                result.add(new Participant(name, course, date.isEmpty() ? today : date));
            }
        }
        return result;
    }

    private static int indexOf(List<String> header, String column) {
        for (int i = 0; i < header.size(); i++) {
            if (header.get(i).trim().equalsIgnoreCase(column)) return i;
        }
        return -1;
    }

    private static String get(List<String> cols, int idx) {
        return (idx >= 0 && idx < cols.size()) ? cols.get(idx).trim() : "";
    }

    /** Splits one CSV line, supporting "quoted, values" and "" escaped quotes. */
    static List<String> splitLine(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    cur.append(c);
                }
            } else if (c == '"') {
                inQuotes = true;
            } else if (c == ',') {
                out.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        out.add(cur.toString());
        return out;
    }
}
