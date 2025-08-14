package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.entity.VerificationToken;
import com.example.nagoyameshi.repository.VerificationTokenRepository;

@Service
public class VerificationTokenService {
	private final VerificationTokenRepository verificationTokenRepository;

	public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
		this.verificationTokenRepository = verificationTokenRepository;
	}

	@Transactional
	public void createVerificationToken(User user, String token) {
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setUser(user);
		verificationToken.setToken(token);

		verificationTokenRepository.save(verificationToken);
	}
	
	@Transactional
	public void update(User user, String token) {
		VerificationToken verificationToken = verificationTokenRepository.findByUser(user);
		
		if (verificationToken == null) {
	        // 既存トークンが無ければ新規作成
	        createVerificationToken(user, token);
	        return;
	    }
        
		// 既存トークンがある場合、トークンをリセット
        verificationToken.setToken(token);
		verificationTokenRepository.save(verificationToken);
	}

	// トークンの文字列で検索した結果を返す
	public VerificationToken findVerificationTokenByToken(String token) {
		
		return verificationTokenRepository.findByToken(token);
	}
}
