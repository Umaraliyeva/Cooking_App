package org.example.cooking_app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.cooking_app.entity.Attachment;
import org.example.cooking_app.entity.AttachmentContent;
import org.example.cooking_app.repo.AttachmentContentRepository;
import org.example.cooking_app.repo.AttachmentRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
//@MultipartConfig
@RequiredArgsConstructor
@Tag(name = "File API", description = "Fayllarni yuklash va olish uchun API")
public class AttachmentController {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository attachmentContentRepository;




//    @GetMapping("/{attachmentId}")
//    public void file(@PathVariable Integer attachmentId, HttpServletResponse response) throws IOException {
//        AttachmentContent attachmentContent = attachmentContentRepository.findById(attachmentId).orElseThrow();
//        response.getOutputStream().write(attachmentContent.getContent());
//
//    }
//
//
//    @PostMapping
//    public Integer upload(@RequestParam("file") MultipartFile file) throws IOException {
//        Attachment attachment=Attachment.builder()
//                        .fileName(file.getOriginalFilename())
//                .build();
//        attachmentRepository.save(attachment);
//        AttachmentContent attachmentContent=AttachmentContent.builder()
//                .content(file.getBytes())
//                .attachment(attachment)
//                .build();
//        attachmentContentRepository.save(attachmentContent);
//        return attachment.getId();
//    }


    @GetMapping("/{attachmentId}")
    @Operation(summary = "Faylni olish", description = "Berilgan `attachmentId` bo‘yicha faylni qaytaradi")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fayl topildi"),
            @ApiResponse(responseCode = "404", description = "Fayl topilmadi")
    })
    public void file(@PathVariable Integer attachmentId, HttpServletResponse response) throws IOException {
        AttachmentContent attachmentContent = attachmentContentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Fayl topilmadi"));
        response.getOutputStream().write(attachmentContent.getContent());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Integer> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Attachment attachment = Attachment.builder()
                .fileName(file.getOriginalFilename())
                .build();
        attachmentRepository.save(attachment);

        AttachmentContent attachmentContent = AttachmentContent.builder()
                .content(file.getBytes())
                .attachment(attachment)
                .build();
        attachmentContentRepository.save(attachmentContent);

        return ResponseEntity.ok(attachment.getId());
    }
}
