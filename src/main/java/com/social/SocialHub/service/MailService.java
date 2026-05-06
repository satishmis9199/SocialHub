package com.social.SocialHub.service;

import com.social.SocialHub.dto.EmailRequestDTO;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public int sendMail(EmailRequestDTO dto) {

        int count = 0;
        String[] mails = dto.getEmails().split(",");

        // 🔥 Strong Subject
        String subject = "Application for Java Backend Developer | 2+ Years Experience | Spring Boot | Microservices | Kafka | Immediate Joiner";

        // 🔥 FINAL IMPRESSIVE MAIL BODY
        String body = """
                <html>
                <body>

                <p>Dear Hiring Team,</p>

                <p>I hope you are doing well.</p>

                <p>
                I am writing to express my interest in the <b>Java Backend Developer</b> role. 
                I bring <b>2+ years of experience</b> in backend development, specializing in 
                <b>Spring Boot, Microservices architecture, and cloud-based systems</b>.
                </p>

                <p>
                Currently, I am working as a <b>Java Backend Developer at TCS</b>, where I design and develop 
                scalable microservices and high-performance APIs handling concurrent users and large-scale data.
                </p>

                <p><b>Key Contributions at TCS:</b></p>
                <ul>
                    <li>Designed and developed scalable microservices using Java, Spring Boot, and REST APIs</li>
                    <li>Built high-performance APIs for large-scale concurrent systems</li>
                    <li>Implemented Kafka-based event-driven architecture for asynchronous workflows</li>
                    <li>Integrated Redis caching reducing API latency by <b>40%</b></li>
                    <li>Optimized SQL queries, joins, and indexing for better performance</li>
                    <li>Handled production issues (L1/L2), RCA, log analysis, and incident resolution</li>
                    <li>Deployed and monitored applications on AWS ensuring high availability</li>
                </ul>

                <p>
                Prior to this, I worked as a <b>Backend Developer Intern (Freelance - Turing Platform)</b>, 
                where I built a complete <b>Order Management System</b> using Spring Boot microservices.
                </p>

                <p><b>Key Contributions (Freelance):</b></p>
                <ul>
                    <li>Developed REST APIs for order processing, inventory, and user management</li>
                    <li>Implemented JWT-based authentication using Spring Security</li>
                    <li>Integrated MySQL with JPA/Hibernate and optimized queries</li>
                    <li>Used Apache Kafka for inter-service communication</li>
                    <li>Improved API performance by <b>30%</b></li>
                </ul>

                <p><b>Projects:</b></p>
                <ul>
                    <li><b>E-Commerce Backend System:</b> Built product catalog, cart, and order services with secure authentication and transactional consistency</li>
                    <li><b>Real-Time Notification System:</b> Developed Kafka-based system for scalable and fault-tolerant notifications</li>
                </ul>

                <p><b>Technical Skills:</b></p>
                <ul>
                    <li>Java, Spring Boot, Microservices, REST APIs</li>
                    <li>Apache Kafka, Event-Driven Architecture</li>
                    <li>MySQL, SQL Server, JPA/Hibernate</li>
                    <li>Redis, Caching, Query Optimization</li>
                    <li>AWS (S3, EC2), Docker, CI/CD</li>
                </ul>

                <p>
                I am highly motivated to contribute to a forward-thinking organization by building 
                scalable, high-performance systems and continuously enhancing my technical expertise.
                </p>

                <p>Please find my resume attached for your review.</p>

                <p>
                I would welcome the opportunity to discuss how my experience aligns with your requirements.
                </p>

                <p>Thank you for your time and consideration.</p>

                <p>
                <b>Best regards,</b><br>
                Satish Kumar Mishra<br>
                Email: satishmis9199@gmail.com<br>
                Phone: +91-9229299776
                </p>

                </body>
                </html>
                """;

        try {

            for (String email : mails) {

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setFrom("satishmis9199@gmail.com");
                helper.setTo(email.trim());

                helper.setSubject(subject);
                helper.setText(body, true);

                // Resume attachment
                if (dto.getFile() != null && !dto.getFile().isEmpty()) {
                    helper.addAttachment(
                            dto.getFile().getOriginalFilename(),
                            dto.getFile()
                    );
                }

                mailSender.send(message);

                logger.info("Mail sent to: {}", email);
                count++;
            }

            return count;

        } catch (Exception e) {
            logger.error("Error while sending mail", e);
            return -1;
        }
    }
}