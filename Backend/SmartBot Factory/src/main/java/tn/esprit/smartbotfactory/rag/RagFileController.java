package tn.esprit.smartbotfactory.rag;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/rag")
public class RagFileController {

    private final FileIngestService fileIngest;

    public RagFileController(FileIngestService fileIngest) {
        this.fileIngest = fileIngest;
    }

    @PostMapping(
            value = "/{botId}/ingest-file",
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Map<String, Object> ingestFile(
            @PathVariable long botId,
            @RequestParam("file") MultipartFile file) {
        return fileIngest.ingest(botId, file);
    }

}