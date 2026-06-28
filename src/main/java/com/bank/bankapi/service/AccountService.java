package com.bank.bankapi.service;

import com.bank.bankapi.dto.AccountResponse;
import com.bank.bankapi.entity.Account;
import com.bank.bankapi.entity.User;
import com.bank.bankapi.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;

    public void createAccount(User user, String currency){

        if (accountRepository.existsByUserAndCurrency(user, currency)){
            throw new RuntimeException("Account in " + currency + " is already exist");
        }

        Account account = new Account();
        account.setUser(user);
        account.setCurrency(currency);
        account.setCreatedAt(LocalDateTime.now());
        account.setBalance(BigDecimal.ZERO);


        accountRepository.save(account);
    }

    public List<AccountResponse> getAccounts(User user){
        List<AccountResponse> allUserAccounts = accountRepository.findByUser(user)
                .stream().map(account -> new AccountResponse(
                        account.getId(),
                        account.getCurrency(),
                        account.getBalance(),
                        account.getCreatedAt()
                )).toList();
        return allUserAccounts;
    }
}
