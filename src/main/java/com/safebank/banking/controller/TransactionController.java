package com.safebank.banking.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.safebank.banking.entity.Transaction;
import com.safebank.banking.entity.User;
import com.safebank.banking.service.StatementService;
import com.safebank.banking.service.TransactionService;
import com.safebank.banking.service.UserService;

@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private StatementService statementService;

    @Autowired
    private UserService userService;


    // =========================
    // CREATE TRANSACTION
    // =========================

    @PostMapping
    public Transaction saveTransaction(
            @RequestBody Transaction transaction) {

        return transactionService.saveTransaction(
                transaction
        );
    }


    // =========================
    // GET ALL TRANSACTIONS
    // =========================

    @GetMapping
    public List<Transaction> getAllTransactions() {

        return transactionService.getAllTransactions();
    }


    // =========================
    // GET TRANSACTION BY ID
    // =========================

    @GetMapping("/{id}")
    public Transaction getTransactionById(
            @PathVariable Long id) {

        return transactionService.getTransactionById(id);
    }


    // =========================
    // UPDATE TRANSACTION
    // =========================

    @PutMapping("/{id}")
    public Transaction updateTransaction(
            @PathVariable Long id,
            @RequestBody Transaction transaction) {

        return transactionService.updateTransaction(
                id,
                transaction
        );
    }


    // =========================
    // DELETE TRANSACTION
    // =========================

    @DeleteMapping("/{id}")
    public String deleteTransaction(
            @PathVariable Long id) {

        transactionService.deleteTransaction(id);

        return "Transaction deleted successfully";
    }


    // =========================
    // SEND MONEY
    // =========================

    @PostMapping("/send")
    public Transaction sendMoney(
            @RequestBody Map<String, Object> data) {

        String senderAccount =
                (String) data.get("senderAccount");

        String receiverAccount =
                (String) data.get("receiverAccount");

        Double amount =
                Double.valueOf(
                        data.get("amount").toString()
                );

        String remarks =
                (String) data.get("remarks");


        return transactionService.sendMoney(
                senderAccount,
                receiverAccount,
                amount,
                remarks
        );
    }


    // =========================
    // DEPOSIT
    // =========================

    @PostMapping("/deposit")
    public Transaction deposit(
            @RequestBody Map<String, Object> data) {

        String accountNumber =
                (String) data.get("accountNumber");

        Double amount =
                Double.valueOf(
                        data.get("amount").toString()
                );

        String remarks =
                (String) data.get("remarks");


        return transactionService.deposit(
                accountNumber,
                amount,
                remarks
        );
    }


    // =========================
    // DOWNLOAD + EMAIL STATEMENT
    // =========================

    @GetMapping("/statement/{accountNumber}")
    public ResponseEntity<byte[]> downloadStatement(
            @PathVariable String accountNumber) {

        try {

            // =========================
            // FIND USER
            // =========================

            User user =
                    userService.getUserByAccountNumber(
                            accountNumber
                    );


            if (user == null) {

                return ResponseEntity
                        .notFound()
                        .build();
            }


            // =========================
            // GENERATE PDF
            // =========================

            byte[] pdf =
                    statementService.generateStatement(
                            accountNumber,
                            user.getFullName()
                    );


            // =========================
            // GET REGISTERED EMAIL
            // =========================

            String email =
                    user.getEmail();


            // =========================
            // GET MOBILE NUMBER
            // =========================

            String mobile =
                    user.getMobile();

if (mobile == null || mobile.length() < 4) {
    return ResponseEntity.badRequest().build();
}

String password =
        mobile.substring(mobile.length() - 4);


        
            // SEND EMAIL
            // =========================
			
			byte[] encryptedPdf =
			statementService.encryptPdf(
			        pdf,
			        password
			);

            statementService.sendStatementEmail(
                    email,
                    encryptedPdf,
                    password
            );


            // =========================
            // ALSO DOWNLOAD PDF
            // =========================

            return ResponseEntity
                    .ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=SAFE-Bank-Statement.pdf"
                    )
                    .contentType(
                            MediaType.APPLICATION_PDF
                    )
                    .body(encryptedPdf);


        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }
}