DROP DATABASE IF EXISTS redpacket ;

CREATE DATABASE IF NOT EXISTS redpacket DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

USE redpacket;

CREATE TABLE T_RED_PACKET
(
  id INT(20) NOT NULL AUTO_INCREMENT,
  user_id INT(12) not NULL,
  amount DECIMAL(16,2) NOT NULL ,
  send_date TIMESTAMP NOT NULL ,
  total INT(12) NOT NULL ,
  unit_amount DECIMAL(12) NOT NULL ,
  stock INT(12) NOT NULL ,
  version INT(12) DEFAULT 0 NOT NULL ,
  note VARCHAR(256) NULL ,
  PRIMARY KEY CLUSTERED(id)
);

CREATE TABLE T_USER_RED_PACKET
(
  id INT(12) NOT NULL AUTO_INCREMENT,
  red_packet_id INT(12)NOT NULL ,
  user_id INT(12) NOT NULL ,
  amount DECIMAL(16,2) NOT NULL ,
  grap_time TIMESTAMP NOT NULL ,
  note VARCHAR(256) NULL ,
  PRIMARY KEY CLUSTERED(id)
);

INSERT INTO T_RED_PACKET(user_id, amount, send_date, total, unit_amount,stock, note)
    VALUES (1, 200000.00, NOW(), 2000, 100.00, 2000, '20万元金额，2千个小红包，每个100元');
