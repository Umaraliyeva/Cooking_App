package org.example.cooking_app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cooking_app.entity.Attachment;
import org.example.cooking_app.entity.AttachmentContent;
import org.example.cooking_app.entity.Ingredient;
import org.example.cooking_app.repo.AttachmentContentRepository;
import org.example.cooking_app.repo.AttachmentRepository;
import org.example.cooking_app.repo.IngredientRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/ingredient")
public class IngredientController {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository attachmentContentRepository;
    private final IngredientRepository ingredientRepository;

    public IngredientController(AttachmentRepository attachmentRepository, AttachmentContentRepository attachmentContentRepository, IngredientRepository ingredientRepository) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentContentRepository = attachmentContentRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @Tag(name = "Ingredient add qilinadi")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpEntity<?> addIngredient(@RequestParam String name, @RequestParam MultipartFile file) throws IOException {
        Optional<Ingredient> optionalIngredient = ingredientRepository.findByName(name);
        if (optionalIngredient.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Bu nomdagi ingredient allaqachon mavjud!");
        }else{
            Attachment attachment = Attachment.builder().fileName(file.getOriginalFilename()).build();
            attachmentRepository.save(attachment);
            AttachmentContent attachmentContent = AttachmentContent.builder().attachment(attachment).content(file.getBytes()).build();
            attachmentContentRepository.save(attachmentContent);
            Ingredient ingredient = Ingredient.builder()
                    .name(name)
                    .photo(attachment)
                    .build();
           return ResponseEntity.status(201).body(ingredientRepository.save(ingredient));
        }

    }
}
