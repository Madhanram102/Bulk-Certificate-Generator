import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Draws one certificate (A4 landscape, 150 DPI) and saves it as a PNG.
 */
public class CertificateGenerator {

    private static final int W = 1754;
    private static final int H = 1240;

    private static final Color PAPER = new Color(253, 251, 244);
    private static final Color NAVY  = new Color(24, 38, 74);
    private static final Color GOLD  = new Color(184, 148, 62);
    private static final Color INK   = new Color(70, 70, 70);

    private final String organization;

    public CertificateGenerator(String organization) {
        this.organization = organization;
    }

    public void generate(Participant p, File output) throws IOException {
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            drawBackground(g);
            drawText(g, p);
            drawSeal(g, W / 2, 1000, 72);
            drawSignatures(g, p);
        } finally {
            g.dispose();
        }
        ImageIO.write(img, "png", output);
    }

    // ---------------------------------------------------------------- layout

    private void drawBackground(Graphics2D g) {
        g.setColor(PAPER);
        g.fillRect(0, 0, W, H);

        g.setColor(NAVY);
        g.setStroke(new BasicStroke(14f));
        g.drawRect(40, 40, W - 80, H - 80);

        g.setColor(GOLD);
        g.setStroke(new BasicStroke(4f));
        g.drawRect(70, 70, W - 140, H - 140);

        // small gold squares in the four corners
        int s = 26;
        int[][] corners = {{58, 58}, {W - 58 - s, 58}, {58, H - 58 - s}, {W - 58 - s, H - 58 - s}};
        g.setColor(GOLD);
        for (int[] c : corners) {
            g.fillRect(c[0], c[1], s, s);
        }
    }

    private void drawText(Graphics2D g, Participant p) {
        drawCentered(g, organization.toUpperCase(), spaced(Font.SERIF, Font.BOLD, 30, 0.25f), 200, NAVY);

        drawCentered(g, "CERTIFICATE", spaced(Font.SERIF, Font.BOLD, 110, 0.12f), 335, NAVY);
        drawCentered(g, "OF ACHIEVEMENT", spaced(Font.SERIF, Font.PLAIN, 40, 0.3f), 405, GOLD);

        drawDivider(g, 455);

        drawCentered(g, "This certificate is proudly presented to",
                new Font(Font.SERIF, Font.ITALIC, 34), 540, INK);

        // participant name: shrink the font until it fits inside the border
        Font nameFont = fitFont(g, p.getName(), Font.BOLD | Font.ITALIC, 118, W - 360);
        drawCentered(g, p.getName(), nameFont, 680, NAVY);

        g.setColor(GOLD);
        g.setStroke(new BasicStroke(3f));
        int half = Math.max(450, g.getFontMetrics(nameFont).stringWidth(p.getName()) / 2 + 30);
        g.drawLine(W / 2 - half, 712, W / 2 + half, 712);

        drawCentered(g, "for successfully completing",
                new Font(Font.SERIF, Font.ITALIC, 32), 785, INK);

        Font courseFont = fitFont(g, p.getCourse(), Font.BOLD, 50, W - 360);
        drawCentered(g, p.getCourse(), courseFont, 855, NAVY);
    }

    private void drawSignatures(Graphics2D g, Participant p) {
        int lineY = 1090;
        int leftCx = 430;
        int rightCx = W - 430;
        int half = 190;

        g.setColor(INK);
        g.setStroke(new BasicStroke(2f));
        g.drawLine(leftCx - half, lineY, leftCx + half, lineY);
        g.drawLine(rightCx - half, lineY, rightCx + half, lineY);

        Font value = new Font(Font.SERIF, Font.PLAIN, 32);
        Font label = new Font(Font.SERIF, Font.ITALIC, 26);

        drawCenteredAt(g, p.getDate(), value, leftCx, lineY - 14, NAVY);
        drawCenteredAt(g, "Date", label, leftCx, lineY + 38, INK);
        drawCenteredAt(g, "Authorized Signature", label, rightCx, lineY + 38, INK);
    }

    private void drawDivider(Graphics2D g, int y) {
        g.setColor(GOLD);
        g.setStroke(new BasicStroke(3f));
        g.drawLine(W / 2 - 330, y, W / 2 - 24, y);
        g.drawLine(W / 2 + 24, y, W / 2 + 330, y);

        Polygon diamond = new Polygon(
                new int[]{W / 2, W / 2 + 14, W / 2, W / 2 - 14},
                new int[]{y - 14, y, y + 14, y},
                4);
        g.fillPolygon(diamond);
    }

    private void drawSeal(Graphics2D g, int cx, int cy, int r) {
        g.setColor(GOLD);
        g.fillOval(cx - r, cy - r, r * 2, r * 2);

        g.setColor(PAPER);
        g.setStroke(new BasicStroke(3f));
        g.drawOval(cx - r + 9, cy - r + 9, (r - 9) * 2, (r - 9) * 2);

        // five-pointed star
        int points = 10;
        int[] xs = new int[points];
        int[] ys = new int[points];
        for (int i = 0; i < points; i++) {
            double radius = (i % 2 == 0) ? r * 0.55 : r * 0.24;
            double angle = -Math.PI / 2 + i * Math.PI / 5;
            xs[i] = cx + (int) Math.round(radius * Math.cos(angle));
            ys[i] = cy + (int) Math.round(radius * Math.sin(angle));
        }
        g.setColor(PAPER);
        g.fillPolygon(xs, ys, points);
    }

    // --------------------------------------------------------------- helpers

    private void drawCentered(Graphics2D g, String text, Font font, int baselineY, Color color) {
        drawCenteredAt(g, text, font, W / 2, baselineY, color);
    }

    private void drawCenteredAt(Graphics2D g, String text, Font font, int cx, int baselineY, Color color) {
        g.setFont(font);
        g.setColor(color);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, cx - fm.stringWidth(text) / 2, baselineY);
    }

    private Font fitFont(Graphics2D g, String text, int style, int startSize, int maxWidth) {
        int size = startSize;
        Font font = new Font(Font.SERIF, style, size);
        while (size > 20 && g.getFontMetrics(font).stringWidth(text) > maxWidth) {
            size -= 2;
            font = new Font(Font.SERIF, style, size);
        }
        return font;
    }

    private Font spaced(String family, int style, int size, float tracking) {
        Map<TextAttribute, Object> attrs = new HashMap<>();
        attrs.put(TextAttribute.FAMILY, family);
        attrs.put(TextAttribute.WEIGHT, (style & Font.BOLD) != 0 ? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR);
        attrs.put(TextAttribute.SIZE, (float) size);
        attrs.put(TextAttribute.TRACKING, tracking);
        return new Font(attrs);
    }
}
