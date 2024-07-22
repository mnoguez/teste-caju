-- Create user table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

-- Create merchant table
CREATE TABLE merchants (
    id UUID PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    mcc VARCHAR(255) NOT NULL,
    wallet DECIMAL(19, 2) NOT NULL
);

-- Create user wallet table
CREATE TABLE user_wallet (
    user_id UUID,
    mcc VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    PRIMARY KEY (user_id, mcc),
    CONSTRAINT fk_user_wallet
        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create transaction table
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(19, 2) NOT NULL,
    mcc VARCHAR(255) NOT NULL,
    user_id VARCHAR(36),
    merchant_id VARCHAR(36),
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT fk_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_merchant
        FOREIGN KEY (merchant_id) REFERENCES merchants(id)
);