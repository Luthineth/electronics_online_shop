package com.store.Online.Store.service;

import com.store.Online.Store.exception.EmailSendingException;
import com.store.Online.Store.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    void sendEmail_Successful() {
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Body";
        emailService.sendEmail(to, subject, body);
        verify(javaMailSender,times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_Failure() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";
        doThrow(new RuntimeException("Simulated exception")).when(javaMailSender).send(any(SimpleMailMessage.class));
        assertThrows(EmailSendingException.class, () -> emailService.sendEmail(to, subject, body));
    }
}
