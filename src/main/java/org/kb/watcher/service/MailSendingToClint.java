package org.kb.watcher.service;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class MailSendingToClint {

	@Autowired
	JavaMailSender mailSender;

	@Autowired
	TemplateEngine engine;

	public void send(String uEmail, int otp, String name) throws UnsupportedEncodingException, MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

		helper.setFrom("shivayogik48@gmail.com", "Verify Otp");
		helper.addTo(uEmail);
		helper.setSubject("Verify the otp");

		Context context = new Context();
		context.setVariable("name", name);
		context.setVariable("otp", otp);

		String body = engine.process("otp-template.html", context);
		helper.setText(body, true);

		mailSender.send(mimeMessage);

	}

}
