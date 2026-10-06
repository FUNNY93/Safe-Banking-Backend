package com.safebank.banking.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safebank.banking.entity.Transaction;
import com.safebank.banking.repository.TransactionRepository;


import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;

@Service
public class StatementService {
	
	@Autowired
	private JavaMailSender mailSender;
	public byte[] encryptPdf(
	        byte[] pdf,
	        String password) throws IOException {

	    try (PDDocument document =
	                 Loader.loadPDF(pdf)) {

	        AccessPermission accessPermission =
	                new AccessPermission();

	        StandardProtectionPolicy protectionPolicy =
	                new StandardProtectionPolicy(
	                        password,
	                        password,
	                        accessPermission
	                );

	        protectionPolicy.setEncryptionKeyLength(256);

	        protectionPolicy.setPermissions(
	                accessPermission
	        );

	        document.protect(
	                protectionPolicy
	        );

	        ByteArrayOutputStream output =
	                new ByteArrayOutputStream();

	        document.save(output);

	        return output.toByteArray();
	    }
	}

	public void sendStatementEmail(
	        String email,
	        byte[] pdf,
	        String password) throws MessagingException {
		

	    System.out.println("=================================");
	    System.out.println("STATEMENT EMAIL START");
	    System.out.println("To Email: " + email);
	    System.out.println("PDF Size: " + pdf.length);
	    System.out.println("Password: " + password);
	    System.out.println("=================================");


	    MimeMessage message = mailSender.createMimeMessage();

	    MimeMessageHelper helper =
	            new MimeMessageHelper(message, true);

	    helper.setTo(email);

	    helper.setSubject(
	            "SAFE BANK - Transaction Statement"
	    );

	    helper.setText(
	            "Dear SAFE BANK Customer,\n\n"
	            + "Your transaction statement is attached to this email.\n\n"
	            + "PDF Password: Use the last 4 digits of your registered mobile number.\n\n"
	            + "Example: If your mobile number is ******1234, your password is 0000.\n\n"
	            + "Please do not share your statement or password with anyone.\n\n"
	            + "Please do not share your statement or password with anyone.\n\n"
	            + "Regards,\n"
	            + "SAFE BANK"
	    );

	    helper.addAttachment(
	            "SAFE-Bank-Statement.pdf",
	            new ByteArrayResource(pdf)
	    );
	    System.out.println("Sending email now...");
	    mailSender.send(message);
	    System.out.println("EMAIL SENT SUCCESSFULLY");
	}

    @Autowired
    private TransactionRepository transactionRepository;

    public byte[] generateStatement(
            String accountNumber,
            String accountHolder) throws IOException {

        List<Transaction> transactions =
                transactionRepository
                        .findBySenderAccountOrReceiverAccount(
                                accountNumber,
                                accountNumber
                        );

        try (PDDocument document = new PDDocument()) {

            PDPage page =
                    new PDPage(PDRectangle.A4);

            document.addPage(page);

            PDPageContentStream content =
                    new PDPageContentStream(
                            document,
                            page
                    );

            float y = 780;

            // =========================
            // TITLE
            // =========================

            content.beginText();

            content.setFont(
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA_BOLD
                    ),
                    20
            );

            content.newLineAtOffset(
                    220,
                    y
            );

            content.showText("SAFE BANK");

            content.endText();

            y -= 35;


            // =========================
            // SUBTITLE
            // =========================

            content.beginText();

            content.setFont(
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA_BOLD ),14 );

            content.newLineAtOffset(190, y   );

            content.showText("Transaction Statement"     );

            content.endText();

            y -= 50;


            // =========================
            // USER DETAILS
            // =========================

            content.beginText();

            content.setFont(
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA ), 11);
            content.newLineAtOffset( 50,   y      );

            content.showText(
                    "Account Holder : "
                            + safeText(accountHolder)    );

            content.endText();

            y -= 20;


            content.beginText();

            content.setFont(
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA   ),  11 );

            content.newLineAtOffset( 50, y  );

            content.showText(
                    "Account Number : "
                            + maskAccount(accountNumber)
            );

            content.endText();

            y -= 40;


            // =========================
            // TABLE HEADER
            // =========================

            content.beginText();

            content.setFont(
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA_BOLD ),9 );

            content.newLineAtOffset( 40, y );

            content.showText("Date");

            content.newLineAtOffset(75,0);

            content.showText("Transaction ID");

            content.newLineAtOffset(110,0);

            content.showText("Type");

            content.newLineAtOffset(  65, 0 );

            content.showText("Amount");

            content.newLineAtOffset( 70, 0  );

            content.showText("Status");

            content.endText();

            y -= 20;


            // =========================
            // TRANSACTIONS
            // =========================

            for (Transaction transaction :
                    transactions) {

                if (y < 60) {

                    content.close();

                    page =
                            new PDPage(
                                    PDRectangle.A4
                            );

                    document.addPage(page);

                    content =
                            new PDPageContentStream(
                                    document,
                                    page
                            );

                    y = 780;
                }


                boolean credit =
                        accountNumber.equals(
                                transaction
                                        .getReceiverAccount()
                        );

                String type =
                        credit
                                ? "Credit"
                                : "Debit";

                String amount =
                        (credit ? "+" : "-")
                                + " Rs."
                                + String.format(
                                    "%.2f",
                                    transaction
                                        .getAmount()
                                );


                content.beginText();

                content.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA
                        ),
                        8
                );

                content.newLineAtOffset(
                        40,
                        y
                );

                content.showText(
                        safeText(
                                transaction.getDate()
                        )
                );

                content.newLineAtOffset(
                        75,
                        0
                );

                content.showText(
                        safeText(
                            transaction
                                .getTransactionId()
                        )
                );

                content.newLineAtOffset(
                        110,
                        0
                );

                content.showText(type);

                content.newLineAtOffset(
                        65,
                        0
                );

                content.showText(amount);

                content.newLineAtOffset(
                        70,
                        0
                );

                content.showText(
                        safeText(
                            transaction.getStatus()
                        )
                );

                content.endText();

                y -= 18;
            }


            // =========================
            // FOOTER
            // =========================

            content.beginText();

            content.setFont(
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA
                    ),
                    9
            );

            content.newLineAtOffset(
                    50,
                    30
            );

            content.showText(
                    "SAFE BANK - Smart • Safe • Secure Banking"
            );

            content.endText();


            content.close();


            // =========================
            // PDF TO BYTE ARRAY
            // =========================

            ByteArrayOutputStream output =
                    new ByteArrayOutputStream();

            document.save(output);

            return output.toByteArray();
        }
    }


    // =========================
    // MASK ACCOUNT
    // =========================

    private String maskAccount(
            String accountNumber) {

        if (accountNumber == null ||
                accountNumber.length() < 4) {

            return "XXXX";
        }

        return "XXXX XXXX "
                + accountNumber.substring(
                        accountNumber.length() - 4
                );
    }


    // =========================
    // SAFE TEXT
    // =========================

    private String safeText(
            String text) {

        if (text == null ||
                text.isBlank()) {

            return "-";
        }

        return text
                .replace("\n", " ")
                .replace("\r", " ");
    }
}