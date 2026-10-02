    package com.vittig.spring_digital_wallet.service.impl;

    import com.vittig.spring_digital_wallet.data.entity.User;
    import com.vittig.spring_digital_wallet.data.entity.Wallet;
    import com.vittig.spring_digital_wallet.data.repository.AuthRepository;
    import com.vittig.spring_digital_wallet.data.repository.WalletRepository;
    import com.vittig.spring_digital_wallet.dto.wallet.WalletDto;
    import com.vittig.spring_digital_wallet.exception.InvalidInputException;
    import com.vittig.spring_digital_wallet.exception.ObjectNotFoundException;
    import com.vittig.spring_digital_wallet.service.contract.WalletService;
    import lombok.RequiredArgsConstructor;
    import org.modelmapper.ModelMapper;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.math.BigDecimal;
    import java.util.Random;

    @Service
    @RequiredArgsConstructor
    public class WalletServiceImpl implements WalletService {

        private final WalletRepository walletRepository;
        private final ModelMapper modelMapper;
        private final AuthRepository authRepository;

        @Override
        public WalletDto getCurrentWallet() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                throw new ObjectNotFoundException("Authenticated user not found!");
            }

            User user = (User) authentication.getPrincipal();

            Wallet wallet = user.getWallet();

            return this.modelMapper.map(wallet, WalletDto.class);
        }

        @Override
        @Transactional
        public WalletDto createWallet(User owner) {
            String iban = generateIban();
            Wallet wallet = new Wallet();

            wallet.setIban(iban);
            wallet.setBalance(BigDecimal.ZERO);
            syncWalletAndOwner(owner, wallet);

            return this.modelMapper.map(this.walletRepository.save(wallet), WalletDto.class);
        }

        @Override
        public Wallet getEntityByIdForUpdate(Long id) {
            return null;
        }

        @Override
        @Transactional
        public WalletDto topUpWallet(BigDecimal amount) {
            if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
                throw new InvalidInputException("Amount must be greater than zero!");
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String userEmail = authentication.getName();

            User user = this.authRepository.findByEmailForUpdate(userEmail).orElseThrow(
                    () -> new ObjectNotFoundException("User not found!")
            );

            BigDecimal newBalance = user.getWallet().getBalance().add(amount);

            user.getWallet().setBalance(newBalance);

            return this.modelMapper.map(user.getWallet(), WalletDto.class);
        }

        private String generateIban(){
            StringBuilder sb = new StringBuilder();

            Random random = new Random();
            char randomChar;
            for(int i = 0; i < 35; i++){
                if(random.nextBoolean()){
                    randomChar = (char) (random.nextInt(26) + 65);
                }else{
                    randomChar = (char) (random.nextInt(9) + 49);
                }

                sb.append(randomChar);
            }

            return sb.toString();
        }

        private void syncWalletAndOwner(User owner, Wallet wallet){
            owner.setWallet(wallet);
            wallet.setOwner(owner);
        }

    }
