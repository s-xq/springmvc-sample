CREATE DATABASE IF NOT EXISTS ssm DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

use ssm;

CREATE TABLE IF NOT EXISTS t_role
(
  id        INT UNSIGNED AUTO_INCREMENT,
  role_name VARCHAR(10) NOT NULL ,
  note VARCHAR(100) NOT NULL ,
  PRIMARY KEY (id)
)ENGINE =InnoDB;

INSERT INTO t_role(role_name, note) VALUE  ('admin', 'Administration');
INSERT INTO t_role(role_name, note) VALUE  ('user', 'User');

