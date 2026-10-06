
package com.safebank.banking.service;

import java.util.List;

import com.safebank.banking.entity.Transaction;

public interface TransactionService {
	

    Transaction saveTransaction(Transaction transaction);

    List<Transaction> getAllTransactions();

    Transaction getTransactionById(Long id);

    Transaction updateTransaction(Long id, Transaction transaction);

    void deleteTransaction(Long id);
    

    Transaction sendMoney(String senderAccount,
                          String receiverAccount,
                          Double amount,
                          String remarks);

    Transaction deposit(String accountNumber,
                        Double amount,
                        String remarks);
    
}