import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Bulk Certificate Generator
 *
 * Usage:
 *   java -cp out Main [participants.csv] [outputDir] ["Organization Name"]
 *
 * Defaults:
 *   data/participants.csv   output   "HCL Hackathon"
 */
public class Main {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");

        Path csv       = Paths.get(args.length > 0 ? args[0] : "data/participants.csv");
        Path outDir    = Paths.get(args.length > 1 ? args[1] : "output");
        String orgName = args.length > 2 ? args[2] : "HCL Hackathon";

        System.out.println("=== Bulk Certificate Generator ===");
        System.out.println("Input  : " + csv);
        System.out.println("Output : " + outDir);
        System.out.println();

        List<Participant> participants;
        try {
            participants = CsvReader.read(csv);
            Files.createDirectories(outDir);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
            return;
        }

        CertificateGenerator generator = new CertificateGenerator(orgName);
        int ok = 0;
        int failed = 0;

        for (int i = 0; i < participants.size(); i++) {
            Participant p = participants.get(i);
            String fileName = String.format("%03d_%s.png", i + 1, safeName(p.getName()));
            File out = outDir.resolve(fileName).toFile();
            try {
                generator.generate(p, out);
                System.out.println("  [ok]   " + fileName);
                ok++;
            } catch (IOException e) {
                System.out.println("  [fail] " + p.getName() + " -> " + e.getMessage());
                failed++;
            }
        }

        System.out.println();
        System.out.println("Done. Generated: " + ok + "  |  Failed: " + failed);
        if (failed > 0) System.exit(2);
    }

    /** Turns "Priya R." into "Priya_R." so it is safe to use as a file name. */
    private static String safeName(String name) {
        return name.replaceAll("[^A-Za-z0-9._-]+", "_");
    }
}
