package org.example.auth_service.security.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Profile({"dev", "prod"})
@Component
@RequiredArgsConstructor
public class HtmlEmailService implements EmailService {

  private final JavaMailSender javaMailSender;

  @Override
  public void sendEmail(EmailMessage emailMessage) {

    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    try {
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
      mimeMessageHelper.setTo(emailMessage.getTo());
      mimeMessageHelper.setFrom(emailMessage.getFrom());
      mimeMessageHelper.setSubject(emailMessage.getSubject());
      mimeMessageHelper.setText(emailMessage.getMessage(), true);
      javaMailSender.send(mimeMessage);
      log.info("send email: {}", emailMessage.getMessage());
    } catch (MessagingException e) {
      log.error("failed to send email", e);
      throw new RuntimeException(e);
    }
  }
}
