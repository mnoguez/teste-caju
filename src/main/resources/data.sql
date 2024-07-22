-- Insert into users
INSERT INTO users (id, name) VALUES
('1e0fa047-7f57-41f5-873e-12c31e7c74e4', 'João da Silva'),
('2b0fa047-7f57-41f5-873e-12c31e7c74e4', 'Maria da Silva');

-- Insert into merchants
INSERT INTO merchants (id, name, mcc, wallet) VALUES
('4d0fa047-7f57-41f5-873e-12c31e7c74e4', 'PADARIA DO ZE               SAO PAULO BR', 'FOOD', 0.00),
('3c0fa047-7f57-41f5-873e-12c31e7c74e4', 'UBER TRIP                   SAO PAULO BR', 'CASH', 0.00),
('5d0fa047-7f57-41f5-873e-12c31e7c74e4', 'UBER EATS                   SAO PAULO BR', 'MEAL', 0.00);

-- Insert into user_wallet
INSERT INTO user_wallet (user_id, mcc, amount) VALUES
('1e0fa047-7f57-41f5-873e-12c31e7c74e4', 'FOOD', 100.00),
('1e0fa047-7f57-41f5-873e-12c31e7c74e4', 'CASH', 200.00),
('1e0fa047-7f57-41f5-873e-12c31e7c74e4', 'MEAL', 50.00),
('2b0fa047-7f57-41f5-873e-12c31e7c74e4', 'FOOD', 200.00),
('2b0fa047-7f57-41f5-873e-12c31e7c74e4', 'CASH', 200.00),
('2b0fa047-7f57-41f5-873e-12c31e7c74e4', 'MEAL', 200.00);