package com.bank.bankapi.repository;

import com.bank.bankapi.entity.Account;
import com.bank.bankapi.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromAccountOrToAccount(Account fromAccount, Account toAccount);

}
