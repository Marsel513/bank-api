package com.bank.bankapi.service;

import com.bank.bankapi.dto.TransactionalResponse;
import com.bank.bankapi.dto.TransferRequest;
import com.bank.bankapi.entity.Account;
import com.bank.bankapi.entity.Transaction;
import com.bank.bankapi.entity.User;
import com.bank.bankapi.repository.AccountRepository;
import com.bank.bankapi.repository.TransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ConversionService conversionService;

    @Transactional
    public TransactionalResponse transfer(User user, TransferRequest request) {
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new RuntimeException("Cannot transfer money to the same account");
        }
        List<Long> accountIds;
         if(request.getFromAccountId() < request.getToAccountId()){
             accountIds  =
                     List.of(request.getFromAccountId(), request.getToAccountId());
         }
         else {
             accountIds =
                     List.of(request.getToAccountId(), request.getFromAccountId());
         }


        List<Account> lockedAccounts = accountRepository.findAllByIdForUpdate(accountIds);

        if (lockedAccounts.size() != 2) {
            throw new RuntimeException("One of the accounts was not found");
        }

        Account firstAccount = lockedAccounts.get(0);
        Account secondAccount = lockedAccounts.get(1);

        Account fromAccount;
        if(firstAccount.getId() == request.getFromAccountId()){
                fromAccount = firstAccount;}
        else {
                fromAccount = secondAccount;}

        Account toAccount;
        if(firstAccount.getId() == request.getToAccountId()){
                toAccount = firstAccount;}
        else {
                toAccount = secondAccount;}

        if (user.getId() != fromAccount.getUser().getId()) {
            throw new RuntimeException("No access to this account");
        }

        if (!fromAccount.getCurrency().equals(toAccount.getCurrency())) {
            return transferBetweenCurrencies(fromAccount, toAccount, request.getAmount());
        }

        BigDecimal amount = request.getAmount();

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(amount);
        transaction.setCurrency(fromAccount.getCurrency());
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        return new TransactionalResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getCreatedAt(),
                fromAccount.getId(),
                toAccount.getId()
        );
    }

    public List<TransactionalResponse> getTransactions(User user){
        List<Account> userAccounts = accountRepository.findByUser(user);
        Set<Transaction> userTransactions = new HashSet<>();
        for(Account account : userAccounts){
            userTransactions.addAll(transactionRepository
                    .findByFromAccountOrToAccount(account, account));
        }
        List<TransactionalResponse> userTransactionalsResponses = new ArrayList<>();
        for(Transaction transaction : userTransactions){
            TransactionalResponse response = new TransactionalResponse(
                    transaction.getId(), transaction.getAmount(),
                    transaction.getCurrency(), transaction.getCreatedAt(),
                    transaction.getFromAccount().getId(),
                    transaction.getToAccount().getId()
            );
            userTransactionalsResponses.add(response);
        }
        return userTransactionalsResponses;
    }


    private TransactionalResponse transferBetweenCurrencies(Account from,  Account to, BigDecimal amount){
        if(from.getBalance().compareTo(amount) < 0){
            throw new RuntimeException("Insufficient funds");
        }
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(conversionService.convert(from.getCurrency(), to.getCurrency(), amount)));
        accountRepository.save(from);
        accountRepository.save(to);
        Transaction transaction = new Transaction();
        transaction.setFromAccount(from);
        transaction.setToAccount(to);
        transaction.setAmount(amount);
        transaction.setCurrency(from.getCurrency());
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        return new TransactionalResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getCreatedAt(),
                from.getId(),
                to.getId()
        );

    }

}
