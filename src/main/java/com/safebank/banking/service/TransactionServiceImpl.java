package com.safebank.banking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safebank.banking.entity.Transaction;
import com.safebank.banking.entity.User;
import com.safebank.banking.repository.TransactionRepository;
import com.safebank.banking.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;


    // ==============================
    // SAVE TRANSACTION
    // ==============================

    @Override
    public Transaction saveTransaction(Transaction transaction) {

        if (transaction.getTransactionId() == null ||
            transaction.getTransactionId().isEmpty()) {

            transaction.setTransactionId(
                    "TXN-" + UUID.randomUUID()
            );
        }

        if (transaction.getDate() == null ||
            transaction.getDate().isEmpty()) {

            transaction.setDate(
                    LocalDateTime.now().toString()
            );
        }

        if (transaction.getStatus() == null ||
            transaction.getStatus().isEmpty()) {

            transaction.setStatus("Success");
        }

        return transactionRepository.save(transaction);
    }


    // ==============================
    // GET ALL TRANSACTIONS
    // ==============================

    @Override
    public List<Transaction> getAllTransactions() {

        return transactionRepository.findAll();
    }


    // ==============================
    // GET TRANSACTION BY ID
    // ==============================

    @Override
    public Transaction getTransactionById(Long id) {

        return transactionRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Transaction not found"
                        )
                );
    }


    // ==============================
    // UPDATE TRANSACTION
    // ==============================

    @Override
    public Transaction updateTransaction(
            Long id,
            Transaction updatedTransaction) {

        Transaction transaction =
                transactionRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Transaction not found"
                        )
                );

        transaction.setSenderAccount(
                updatedTransaction.getSenderAccount()
        );

        transaction.setReceiverAccount(
                updatedTransaction.getReceiverAccount()
        );

        transaction.setAmount(
                updatedTransaction.getAmount()
        );

        transaction.setType(
                updatedTransaction.getType()
        );

        transaction.setRemarks(
                updatedTransaction.getRemarks()
        );

        transaction.setDate(
                updatedTransaction.getDate()
        );

        transaction.setStatus(
                updatedTransaction.getStatus()
        );

        return transactionRepository.save(transaction);
    }


    // ==============================
    // DELETE TRANSACTION
    // ==============================

    @Override
    public void deleteTransaction(Long id) {

        if (!transactionRepository.existsById(id)) {

            throw new RuntimeException(
                    "Transaction not found"
            );
        }

        transactionRepository.deleteById(id);
    }


    // ==============================
    // SEND MONEY
    // ==============================

    @Override
    @Transactional
    public Transaction sendMoney(
            String senderAccount,
            String receiverAccount,
            Double amount,
            String remarks) {

        if (amount == null || amount <= 0) {

            throw new RuntimeException(
                    "Amount must be greater than zero"
            );
        }

        if (senderAccount.equals(receiverAccount)) {

            throw new RuntimeException(
                    "Sender and receiver cannot be same"
            );
        }


        // Find sender

        User sender =
                userRepository.findByAccountNumber(senderAccount);

        if (sender == null) {
        	 throw new ResponseStatusException(
        	            HttpStatus.NOT_FOUND,
        	            "Sender account not found"

           
            );
        }


        // Find receiver

        User receiver =
                userRepository.findByAccountNumber(receiverAccount);

        if (receiver == null) {
        	 throw new ResponseStatusException(
        	            HttpStatus.NOT_FOUND,
        	            "Receiver account not found"

          
            );
        }


        // Check sender balance

        Double senderBalance =
                sender.getDeposit() == null
                        ? 0.0
                        : sender.getDeposit();

        if (senderBalance < amount) {
        	 throw new ResponseStatusException(
        	            HttpStatus.BAD_REQUEST,
        	            "Insufficient balance"

        
            );
        }


        // Deduct money from sender

        sender.setDeposit(
                senderBalance - amount
        );


        // Add money to receiver

        Double receiverBalance =
                receiver.getDeposit() == null
                        ? 0.0
                        : receiver.getDeposit();

        receiver.setDeposit(
                receiverBalance + amount
        );


        // Save both users

        userRepository.save(sender);
        userRepository.save(receiver);


        // Create transaction

        Transaction transaction =
                new Transaction();

        transaction.setTransactionId(
                "TXN-" + UUID.randomUUID()
        );

        transaction.setSenderAccount(
                senderAccount
        );

        transaction.setReceiverAccount(
                receiverAccount
        );

        transaction.setAmount(amount);

        transaction.setType("Debit");

        transaction.setRemarks(
                remarks == null
                        ? "Money Transfer"
                        : remarks
        );

        transaction.setDate(
                LocalDateTime.now().toString()
        );

        transaction.setStatus("Success");


        return transactionRepository.save(
                transaction
        );
    }


    // ==============================
    // DEPOSIT
    // ==============================

    @Override
    @Transactional
    public Transaction deposit(
            String accountNumber,
            Double amount,
            String remarks) {

        if (amount == null || amount <= 0) {

            throw new RuntimeException(
                    "Amount must be greater than zero"
            );
        }


        // Find user

        User user =
                userRepository.findByAccountNumber(
                        accountNumber
                );

        if (user == null) {

            throw new RuntimeException(
                    "Account not found"
            );
        }


        // Current balance

        Double currentBalance =
                user.getDeposit() == null
                        ? 0.0
                        : user.getDeposit();


        // Add deposit

        user.setDeposit(
                currentBalance + amount
        );


        // Save user

        userRepository.save(user);


        // Create transaction

        Transaction transaction =
                new Transaction();

        transaction.setTransactionId(
                "TXN-" + UUID.randomUUID()
        );

        transaction.setSenderAccount(
                "BANK"
        );

        transaction.setReceiverAccount(
                accountNumber
        );

        transaction.setAmount(amount);

        transaction.setType("Credit");

        transaction.setRemarks(
                remarks == null
                        ? "Cash Deposit"
                        : remarks
        );

        transaction.setDate(
                LocalDateTime.now().toString()
        );

        transaction.setStatus("Success");


        return transactionRepository.save(
                transaction
        );
    }
}