package com.bank.bankapi.service;

import com.bank.bankapi.dto.TransactionalResponse;
import com.bank.bankapi.dto.TransferRequest;
import com.bank.bankapi.entity.Account;
import com.bank.bankapi.entity.Transaction;
import com.bank.bankapi.entity.User;
import com.bank.bankapi.repository.AccountRepository;
import com.bank.bankapi.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class TransactionServiceTest {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        transactionRepository = mock(TransactionRepository.class);

        transactionService = new TransactionService();
        transactionService.accountRepository = accountRepository;
        transactionService.transactionRepository = transactionRepository;
    }

    @Test
    void transferMovesMoneyAndCreatesTransaction() {
        User owner = user(10L);
        Account fromAccount = account(2L, owner, "USD", "100.00");
        Account toAccount = account(5L, user(20L), "USD", "25.00");
        TransferRequest request = transferRequest(2L, 5L, "40.00");

        when(accountRepository.findAllByIdForUpdate(List.of(2L, 5L)))
                .thenReturn(List.of(fromAccount, toAccount));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransactionalResponse response = transactionService.transfer(owner, request);

        assertBigDecimalEquals("60.00", fromAccount.getBalance());
        assertBigDecimalEquals("65.00", toAccount.getBalance());
        assertEquals("USD", response.getCurrency());
        assertEquals(2L, response.getFromAccountId());
        assertEquals(5L, response.getToAccountId());
        assertBigDecimalEquals("40.00", response.getAmount());
        assertNotNull(response.getCreatedAt());

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());

        Transaction savedTransaction = transactionCaptor.getValue();
        assertSame(fromAccount, savedTransaction.getFromAccount());
        assertSame(toAccount, savedTransaction.getToAccount());
        assertBigDecimalEquals("40.00", savedTransaction.getAmount());
        assertEquals("USD", savedTransaction.getCurrency());
        assertNotNull(savedTransaction.getCreatedAt());

        verify(accountRepository).findAllByIdForUpdate(List.of(2L, 5L));
        verify(accountRepository).save(fromAccount);
        verify(accountRepository).save(toAccount);
    }

    @Test
    void transferLocksAccountsInIdOrderWhenSenderIdIsGreaterThanReceiverId() {
        User owner = user(10L);
        Account fromAccount = account(9L, owner, "USD", "100.00");
        Account toAccount = account(3L, user(20L), "USD", "20.00");
        TransferRequest request = transferRequest(9L, 3L, "15.00");

        when(accountRepository.findAllByIdForUpdate(List.of(3L, 9L)))
                .thenReturn(List.of(toAccount, fromAccount));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransactionalResponse response = transactionService.transfer(owner, request);

        assertEquals(9L, response.getFromAccountId());
        assertEquals(3L, response.getToAccountId());
        assertBigDecimalEquals("85.00", fromAccount.getBalance());
        assertBigDecimalEquals("35.00", toAccount.getBalance());

        verify(accountRepository).findAllByIdForUpdate(List.of(3L, 9L));
    }

    @Test
    void transferRejectsSameAccountBeforeLocking() {
        User owner = user(10L);
        TransferRequest request = transferRequest(2L, 2L, "10.00");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> transactionService.transfer(owner, request)
        );

        assertEquals("Cannot transfer money to the same account", exception.getMessage());
        verifyNoInteractions(accountRepository, transactionRepository);
    }

    @Test
    void transferRejectsMissingAccount() {
        User owner = user(10L);
        Account fromAccount = account(2L, owner, "USD", "100.00");
        TransferRequest request = transferRequest(2L, 5L, "10.00");

        when(accountRepository.findAllByIdForUpdate(List.of(2L, 5L)))
                .thenReturn(List.of(fromAccount));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> transactionService.transfer(owner, request)
        );

        assertEquals("One of the accounts was not found", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void transferRejectsForeignAccount() {
        User currentUser = user(10L);
        Account foreignAccount = account(2L, user(99L), "USD", "100.00");
        Account toAccount = account(5L, user(20L), "USD", "25.00");
        TransferRequest request = transferRequest(2L, 5L, "10.00");

        when(accountRepository.findAllByIdForUpdate(List.of(2L, 5L)))
                .thenReturn(List.of(foreignAccount, toAccount));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> transactionService.transfer(currentUser, request)
        );

        assertEquals("No access to this account", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void transferRejectsDifferentCurrencies() {
        User owner = user(10L);
        Account fromAccount = account(2L, owner, "USD", "100.00");
        Account toAccount = account(5L, user(20L), "EUR", "25.00");
        TransferRequest request = transferRequest(2L, 5L, "10.00");

        when(accountRepository.findAllByIdForUpdate(List.of(2L, 5L)))
                .thenReturn(List.of(fromAccount, toAccount));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> transactionService.transfer(owner, request)
        );

        assertEquals("Transfers between different currencies are forbidden", exception.getMessage());
        assertBigDecimalEquals("100.00", fromAccount.getBalance());
        assertBigDecimalEquals("25.00", toAccount.getBalance());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void transferRejectsInsufficientFunds() {
        User owner = user(10L);
        Account fromAccount = account(2L, owner, "USD", "20.00");
        Account toAccount = account(5L, user(20L), "USD", "25.00");
        TransferRequest request = transferRequest(2L, 5L, "30.00");

        when(accountRepository.findAllByIdForUpdate(List.of(2L, 5L)))
                .thenReturn(List.of(fromAccount, toAccount));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> transactionService.transfer(owner, request)
        );

        assertEquals("Insufficient funds", exception.getMessage());
        assertBigDecimalEquals("20.00", fromAccount.getBalance());
        assertBigDecimalEquals("25.00", toAccount.getBalance());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountRepository, never()).save(any(Account.class));
    }

    private static User user(long id) {
        User user = new User();
        user.setId(id);
        user.setPhoneNumber("+79990000000");
        user.setPassword("Password1!");
        user.setFullName("Test User");
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    private static Account account(long id, User user, String currency, String balance) {
        Account account = new Account();
        account.setId(id);
        account.setUser(user);
        account.setCurrency(currency);
        account.setBalance(new BigDecimal(balance));
        account.setCreatedAt(LocalDateTime.now());
        return account;
    }

    private static TransferRequest transferRequest(Long fromAccountId, Long toAccountId, String amount) {
        TransferRequest request = new TransferRequest();
        request.setFromAccountId(fromAccountId);
        request.setToAccountId(toAccountId);
        request.setAmount(new BigDecimal(amount));
        return request;
    }

    private static void assertBigDecimalEquals(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}
