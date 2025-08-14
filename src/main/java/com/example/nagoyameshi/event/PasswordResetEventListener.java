package com.example.nagoyameshi.event;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.service.VerificationTokenService;

@Component
public class PasswordResetEventListener {
	private final VerificationTokenService verificationTokenService;
	private final JavaMailSender javaMailSender;
	
	public PasswordResetEventListener(VerificationTokenService verificationTokenService, JavaMailSender mailSender) {
		this.verificationTokenService = verificationTokenService;
		this.javaMailSender = mailSender;
	}
		
	@EventListener
	private void onPasswordResetEvent(PasswordResetEvent passwordResetEvent) {
		// ⚠️try-catch(デバック確認用)
		try {
			User user = passwordResetEvent.getUser();
			String token = UUID.randomUUID().toString();

			// トークンが無ければ作成
			verificationTokenService.update(user, token);

			String recipientAddress = user.getEmail();
			String subject = "パスワードリセット";
			String confirmationUrl = passwordResetEvent.getRequestUrl() + "/verify?token=" + token;
			String message = "新しいパスワードを入力してください。";

			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setTo(recipientAddress);
			mailMessage.setSubject(subject);
			mailMessage.setText(message + "\n" + confirmationUrl);

			javaMailSender.send(mailMessage);

			// ⚠️デバック用（確認後に削除）
			System.out.println("✅ パスワードリセットメール送信成功: " + recipientAddress);
		} catch (Exception e) {
			System.err.println("❌ パスワードリセットメール送信失敗: " + e.getMessage());
			e.printStackTrace();
		}
	}
}