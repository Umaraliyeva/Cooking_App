package org.example.cooking_app.service;


import org.example.cooking_app.entity.BlockedUsers;
import org.example.cooking_app.repo.BlockedUsersRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailVerificationService {
    private final Map<String, String> verificationCodes = new HashMap<>();
    private final Map<String, Integer> attempts = new HashMap<>();
    private final BlockedUsersRepository blockedUsersRepository;

    public EmailVerificationService(BlockedUsersRepository blockedUsersRepository) {
        this.blockedUsersRepository = blockedUsersRepository;
    }


    public void saveCode(String email, String code) {
        if (isBlocked(email)) {
            System.out.println("User is blocked and cannot receive a new code.");
            return; // Bloklangan foydalanuvchiga kod yuborilmaydi
        }
        verificationCodes.put(email, code);
        attempts.put(email, 0);
    }

    public boolean verifyCode(String email, String code) {
        checkAndUnlockUser(email); // Bloklangan userni tekshiramiz va kerak bo‘lsa ochamiz
        if (isBlocked(email)) return false; // Agar user hali ham bloklangan bo‘lsa, verification rad etiladi

        if (!verificationCodes.containsKey(email)) return false; // Email kodlar orasida yo‘q bo‘lsa, xato

        String correctCode = verificationCodes.get(email);
        if (!correctCode.equals(code)) { // Agar noto‘g‘ri kod kiritilgan bo‘lsa
            int attemptCount = attempts.getOrDefault(email, 0) + 1;
            attempts.put(email, attemptCount);

            if (attemptCount >= 3) { // 3 ta noto‘g‘ri urinishdan keyin bloklash
                blockUser(email);
                System.out.println("User is now blocked due to multiple failed attempts: " + email);
            }
            return false;
        }

        // Agar kod to‘g‘ri bo‘lsa
        verificationCodes.remove(email); // Kodni o‘chirib tashlaymiz (bir martalik)
        attempts.remove(email); // Urinishlar sonini ham tozalaymiz
        return true;
    }

    public boolean isBlocked(String email) {
        checkAndUnlockUser(email);
        return blockedUsersRepository.findById(email).isPresent();
    }

    public void blockUser(String email) {
        blockedUsersRepository.save(new BlockedUsers(email, LocalDateTime.now()));
    }


    public void checkAndUnlockUser(String email) {
        blockedUsersRepository.findById(email).ifPresent(blockedUser -> {
            LocalDateTime unlockTime = blockedUser.getBlockedAt().plusMinutes(2);
            if (LocalDateTime.now().isAfter(unlockTime)) {
                blockedUsersRepository.delete(blockedUser);
                System.out.println("User unblocked: " + email);
            }
        });
    }


}
