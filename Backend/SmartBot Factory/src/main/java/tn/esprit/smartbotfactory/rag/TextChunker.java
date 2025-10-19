package tn.esprit.smartbotfactory.rag;

import java.util.ArrayList;
import java.util.List;

public final class TextChunker {

    private TextChunker() {}

    /** Nettoyage simple (espaces, sauts de ligne). */
    public static String normalize(String s) {
        if (s == null) return "";
        return s.replaceAll("\\r", "\n")
                .replaceAll("[ \\t\\x0B\\f]+", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    /**
     * Découpage robuste par nombre de caractères, avec overlap.
     * @param text       contenu normalisé
     * @param chunkSize  ~1000-1500 recommandé
     * @param overlap    ~150-200 recommandé
     */
    public static List<String> split(String text, int chunkSize, int overlap) {
        List<String> out = new ArrayList<>();
        text = normalize(text);
        if (text.isEmpty()) return out;

        int start = 0;
        while (start < text.length()) {
            int end = Math.min(text.length(), start + chunkSize);

            // tenter de couper “propre” (fin de phrase / saut de ligne)
            int soft = Math.max(text.lastIndexOf(". ", end - 1), text.lastIndexOf("\n", end - 1));
            if (soft > start + chunkSize / 2) {
                end = soft + 1;
            }

            String chunk = text.substring(start, end).trim();
            if (!chunk.isEmpty()) out.add(chunk);

            if (end >= text.length()) break;
            start = Math.max(0, end - overlap);
        }
        return out;
    }
}
