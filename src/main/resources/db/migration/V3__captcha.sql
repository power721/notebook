CREATE TABLE captcha
(
    id          int AUTO_INCREMENT PRIMARY KEY,
    code        varchar(255) NOT NULL,
    expire_time datetime     NOT NULL,
    name        varchar(255) NOT NULL
);
