package org.example.cooking_app.controller;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.cooking_app.entity.Attachment;
import org.example.cooking_app.entity.AttachmentContent;
import org.example.cooking_app.repo.AttachmentContentRepository;
import org.example.cooking_app.repo.AttachmentRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
@MultipartConfig
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository attachmentContentRepository;




    @GetMapping("/{attachmentId}")
    public void file(@PathVariable Integer attachmentId, HttpServletResponse response) throws IOException {
        AttachmentContent attachmentContent = attachmentContentRepository.findById(attachmentId).orElseThrow();
        response.getOutputStream().write(attachmentContent.getContent());

    }


    @PostMapping
    public Integer upload(@RequestParam("file") MultipartFile file) throws IOException {
        Attachment attachment=Attachment.builder()
                        .fileName(file.getOriginalFilename())
                .build();
        attachmentRepository.save(attachment);
        AttachmentContent attachmentContent=AttachmentContent.builder()
                .content(file.getBytes())
                .attachment(attachment)
                .build();
        attachmentContentRepository.save(attachmentContent);
        return attachment.getId();
    }
}
