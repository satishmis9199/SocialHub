package com.social.SocialHub.restControlller;

import com.social.SocialHub.dto.EmailRequestDTO;
import com.social.SocialHub.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/send-email")
public class EmailController {

    private static final Logger log =
            LoggerFactory.getLogger(EmailController.class);

    @Autowired
    private MailService mailService;

    @PostMapping
    public Map<String, Object> sendMail(@ModelAttribute EmailRequestDTO emailRequestDTO) {

        Map<String, Object> response = new HashMap<>();

        try {

            log.info("=== API HIT ===");
            log.info("Emails: {}", emailRequestDTO.getEmails());
            log.info("Subject: {}", emailRequestDTO.getSubject());

            int count = mailService.sendMail(emailRequestDTO);

            log.info("MAIL SENT SUCCESS");

            response.put("count", count);

        } catch (Exception e) {

            log.error("ERROR WHILE SENDING MAIL", e);

            response.put("count", -1);
            response.put("error", e.getMessage());
        }

        return response;
    }




    @PostMapping("/otp")
    public Map<String, Object> sendOtpMail(
            @RequestParam String email,
            @RequestParam String otp
    ) {

        Map<String, Object> response = new HashMap<>();

        try {

            log.info("=== OTP MAIL API HIT ===");
            log.info("Receiver Email: {}", email);
            log.info("OTP: {}", otp);

            // Hardcoded Subject
            String subject = "SocialHub Password Reset OTP";

            // Hardcoded HTML Message
            String message = """
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
</head>

<body style="
    margin:0;
    padding:0;
    background:#0b1020;
    font-family:Arial,sans-serif;
">

<div style="
    width:100%;
    padding:40px 0;
    background:
    radial-gradient(circle at top left,#7c3aed 0%,transparent 35%),
    radial-gradient(circle at bottom right,#ec4899 0%,transparent 35%),
    #0b1020;
">

    <table align="center"
           width="600"
           cellpadding="0"
           cellspacing="0"
           style="
               background:rgba(255,255,255,0.05);
               border:1px solid rgba(255,255,255,0.08);
               border-radius:24px;
               overflow:hidden;
               backdrop-filter:blur(10px);
               box-shadow:0 20px 60px rgba(0,0,0,0.45);
           ">

        <!-- TOP BANNER -->
        <tr>
            <td style="
                padding:50px 40px;
                text-align:center;
                background:linear-gradient(135deg,#7c3aed,#ec4899);
            ">

                <div style="
                    font-size:42px;
                    margin-bottom:12px;
                ">
                    🔐
                </div>

                <div style="
                    font-size:34px;
                    font-weight:900;
                    color:white;
                    letter-spacing:1px;
                ">
                    SnapSphere
                </div>

                <div style="
                    margin-top:10px;
                    color:rgba(255,255,255,0.85);
                    font-size:15px;
                    line-height:1.6;
                ">
                    Secure Account Verification System
                </div>

            </td>
        </tr>

        <!-- BODY -->
        <tr>
            <td style="
                padding:45px 40px;
                color:#e5e7eb;
            ">

                <div style="
                    font-size:26px;
                    font-weight:700;
                    margin-bottom:18px;
                    color:white;
                ">
                    Password Reset OTP
                </div>

                <p style="
                    font-size:15px;
                    line-height:1.8;
                    color:#cbd5e1;
                    margin:0 0 18px;
                ">
                    Hello User,
                </p>

                <p style="
                    font-size:15px;
                    line-height:1.8;
                    color:#cbd5e1;
                    margin:0 0 30px;
                ">
                    We received a request to reset your SnapSphere account password.
                    Use the secure OTP below to continue:
                </p>

                <!-- OTP BOX -->
                <div style="text-align:center; margin:35px 0;">

                    <div style="
                        display:inline-block;
                        padding:22px 40px;
                        background:linear-gradient(135deg,#111827,#1f2937);
                        border:1px solid rgba(255,255,255,0.08);
                        border-radius:18px;
                        box-shadow:0 10px 30px rgba(124,58,237,0.35);
                    ">

                        <div style="
                            font-size:42px;
                            font-weight:900;
                            letter-spacing:10px;
                            color:#a855f7;
                            font-family:monospace;
                        ">
                            """ + otp + """
                        </div>

                    </div>

                </div>

                <!-- INFO BOX -->
                <div style="
                    background:rgba(124,58,237,0.08);
                    border:1px solid rgba(168,85,247,0.18);
                    border-radius:14px;
                    padding:18px;
                    margin-top:25px;
                ">

                    <div style="
                        font-size:14px;
                        color:#d8b4fe;
                        line-height:1.8;
                    ">
                        ⏳ This OTP is valid for only <b>5 minutes</b>.<br>
                        🔒 Never share this OTP with anyone.<br>
                        🚨 If you didn't request this reset, simply ignore this email.
                    </div>

                </div>

                <!-- BUTTON -->
                <div style="
                    text-align:center;
                    margin-top:40px;
                ">

                    <a href="#"
                       style="
                           display:inline-block;
                           padding:14px 28px;
                           background:linear-gradient(135deg,#7c3aed,#ec4899);
                           color:white;
                           text-decoration:none;
                           border-radius:12px;
                           font-weight:700;
                           font-size:15px;
                           box-shadow:0 10px 25px rgba(124,58,237,0.35);
                       ">
                        Verify Account
                    </a>

                </div>

            </td>
        </tr>

        <!-- FOOTER -->
        <tr>
            <td style="
                padding:28px 40px;
                background:#0f172a;
                border-top:1px solid rgba(255,255,255,0.06);
                text-align:center;
            ">

                <div style="
                    font-size:20px;
                    font-weight:800;
                    color:white;
                    margin-bottom:8px;
                ">
                    SnapSphere
                </div>

                <div style="
                    font-size:13px;
                    color:#94a3b8;
                    line-height:1.7;
                ">
                    Secure • Fast • Modern Social Platform
                </div>

                <div style="
                    margin-top:18px;
                    font-size:12px;
                    color:#64748b;
                    line-height:1.8;
                ">
                    © 2026 SnapSphere. All rights reserved.<br>
                    This is an automated security email.
                </div>

            </td>
        </tr>

    </table>

</div>

</body>
</html>
""";

            // Mail send
            mailService.sendSimpleMail(email, subject, message);

            log.info("OTP MAIL SENT SUCCESSFULLY");

            response.put("success", true);
            response.put("message", "OTP sent successfully");

        } catch (Exception e) {

            log.error("ERROR WHILE SENDING OTP MAIL", e);

            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }


}