package com.vittig.spring_digital_wallet.service.impl;

import com.vittig.spring_digital_wallet.data.entity.Transfer;
import com.vittig.spring_digital_wallet.data.entity.User;
import com.vittig.spring_digital_wallet.data.entity.Wallet;
import com.vittig.spring_digital_wallet.data.repository.AuthRepository;
import com.vittig.spring_digital_wallet.data.repository.TransferRepository;
import com.vittig.spring_digital_wallet.data.repository.WalletRepository;
import com.vittig.spring_digital_wallet.data.util.TransferStatus;
import com.vittig.spring_digital_wallet.dto.transfer.TransferDto;
import com.vittig.spring_digital_wallet.exception.InvalidInputException;
import com.vittig.spring_digital_wallet.service.contract.TransferService;
import com.vittig.spring_digital_wallet.service.contract.WalletService;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class TransferServiceImplIntTest {

    private User sender;
    private User receiver;

    private Wallet senderWallet;
    private Wallet receiverWallet;

    @Autowired
    private TransferService transferService;

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private AuthRepository authRepository;

    @BeforeEach
    void setUp(){
        sender = new User();
        senderWallet = new Wallet();
        sender.setWallet(senderWallet);
        sender.setEmail("martin123@gmail.com");
        senderWallet.setOwner(sender);

        receiver = new User();
        receiverWallet = new Wallet();
        receiver.setWallet(receiverWallet);
        receiver.setEmail("gogo123@gmail.com");
        receiverWallet.setOwner(receiver);

        senderWallet.setBalance(new BigDecimal("150.00"));
        receiverWallet.setBalance(new BigDecimal("200.00"));

        authRepository.save(sender);
        authRepository.save(receiver);

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        Transfer transfer1 = new Transfer();
        transfer1.setFromWallet(senderWallet);
        transfer1.setToWallet(receiverWallet);
        transfer1.setAmount(new BigDecimal("35.00"));
        transfer1.setCreatedAt(LocalDateTime.now().minusMonths(1));
        transfer1.setStatus(TransferStatus.SUCCESSFUL);

        Transfer transfer2 = new Transfer();
        transfer2.setFromWallet(senderWallet);
        transfer2.setToWallet(receiverWallet);
        transfer2.setAmount(new BigDecimal("55.00"));
        transfer2.setCreatedAt(LocalDateTime.now().minusDays(5));
        transfer2.setStatus(TransferStatus.SUCCESSFUL);

        transferRepository.save(transfer1);
        transferRepository.save(transfer2);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                sender, null, sender.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void createTransfer_shouldUpdateSenderAndReceiverBalances() {
        BigDecimal amount = new BigDecimal("90.00");
        transferService.createTransfer(receiverWallet.getId(), amount);

        Wallet senderW = walletService.getEntityById(senderWallet.getId());
        Wallet receiverW = walletService.getEntityById(receiverWallet.getId());

        BigDecimal expectedSenderBalance = new BigDecimal("60.00");
        BigDecimal expectedReceiverBalance = new BigDecimal("290.00");

        assertEquals(expectedSenderBalance, senderW.getBalance());
        assertEquals(expectedReceiverBalance, receiverW.getBalance());
    }

    @Test
    void createTransfer_shouldThrowErrorWhenAmountLessThanZero(){
        BigDecimal amount = new BigDecimal("-5.00");

        assertThrows(InvalidInputException.class,
                () -> transferService.createTransfer(receiverWallet.getId(), amount)
        );

        Wallet senderW = walletService.getEntityById(senderWallet.getId());
        Wallet receiverW = walletService.getEntityById(receiverWallet.getId());

        assertEquals(new BigDecimal("150.00"), senderW.getBalance());
        assertEquals(new BigDecimal("200.00"), receiverW.getBalance());
    }

    @Test
    void createTransfer_shouldThrowErrorWhenSenderHasNotEnoughMoney(){
        BigDecimal amount = new BigDecimal("350.00");

        assertThrows(InvalidInputException.class,
                () -> transferService.createTransfer(receiverWallet.getId(), amount)
        );

        Wallet senderW = walletService.getEntityById(senderWallet.getId());
        Wallet receiverW = walletService.getEntityById(receiverWallet.getId());

        assertEquals(new BigDecimal("150.00"), senderW.getBalance());
        assertEquals(new BigDecimal("200.00"), receiverW.getBalance());
    }

    @Test
    void filterTransfers_ShouldReturn(){
        BigDecimal minAmount = new BigDecimal("5.00");
        BigDecimal maxAmount = new BigDecimal("100.00");

        LocalDateTime startDate = LocalDateTime.now().minusMonths(2);
        LocalDateTime endDate = LocalDateTime.now();

        TransferStatus status = TransferStatus.SUCCESSFUL;

        List<TransferDto> result = transferService.filterTransfers(minAmount, maxAmount, startDate, endDate, status);

        assertEquals(2, result.size());
    }
}