CREATE TABLE `users` (
                                       `id` bigint NOT NULL AUTO_INCREMENT,
                                       `email` varchar(255) DEFAULT NULL,
    `password` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `wallets` (
                                         `id` bigint NOT NULL AUTO_INCREMENT,
                                         `current_balance` decimal(38,2) DEFAULT NULL,
    `iban` varchar(255) DEFAULT NULL,
    `user_id` bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UKaw094dk7xdwqefrf4ea5of8io` (`iban`),
    UNIQUE KEY `UKsswfdl9fq40xlkove1y5kc7kv` (`user_id`),
    CONSTRAINT `FKc1foyisidw7wqqrkamafuwn4e` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `transfers` (
                                           `id` bigint NOT NULL AUTO_INCREMENT,
                                           `amount` decimal(38,2) DEFAULT NULL,
    `created_at` datetime(6) DEFAULT NULL,
    `from_iban` varchar(255) DEFAULT NULL,
    `to_iban` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `wallet_entries` (
                                                `id` bigint NOT NULL AUTO_INCREMENT,
                                                `amount` decimal(38,2) DEFAULT NULL,
    `entry_type` enum('INCOMING','OUTCOMING') DEFAULT NULL,
    `transfer_id` bigint DEFAULT NULL,
    `wallet_id` bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FKdmsmaft08cn4sle2o17nd5vl0` (`transfer_id`),
    KEY `FK63s1f5sddswary03e2s7lnewb` (`wallet_id`),
    CONSTRAINT `FK63s1f5sddswary03e2s7lnewb` FOREIGN KEY (`wallet_id`) REFERENCES `wallets` (`id`),
    CONSTRAINT `FKdmsmaft08cn4sle2o17nd5vl0` FOREIGN KEY (`transfer_id`) REFERENCES `transfers` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;