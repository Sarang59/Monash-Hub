package com.example.loginpage.ui.screens
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.Message.RecipientType

class GMailSender(private val email: String, private val password: String) {

    /**
     * A function which is used to send an email to the user using SMTP and java libraries
     */
    fun sendMail(subject: String, body: String, recipient: String) {
        // Apply various properties
        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
        }

        // Perform authentication of the password
        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(email, password)
            }
        })

        // Try sending an email
        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(email))
                setRecipients(RecipientType.TO, InternetAddress.parse(recipient))
                setSubject(subject)
                setText(body)
            }
            Thread {
                Transport.send(message)
            }.start()
        }
        // Catch exception if it occurs
        catch (e: MessagingException) {
            e.printStackTrace()
        }
    }
}

class MailSender {
}