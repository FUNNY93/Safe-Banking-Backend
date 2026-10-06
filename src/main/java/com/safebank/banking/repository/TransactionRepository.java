
package com.safebank.banking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.safebank.banking.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{

   
    

    List<Transaction> findBySenderAccountOrReceiverAccount(
            String senderAccount,
            String receiverAccount
    );
}
