DROP TABLE IF EXISTS  users_roles;

DROP TABLE IF EXISTS  user_role;

CREATE TABLE user_role
  (
     role_id  BIGINT NOT NULL,
     name VARCHAR(256) NOT NULL,
     PRIMARY KEY (role_id)
  );
DROP TABLE IF EXISTS user_secured;

CREATE TABLE user_secured
  (
     user_id BIGINT NOT NULL,
     enabled         BIT NOT NULL,
     password        VARCHAR(100) NOT NULL,
     username        VARCHAR(45) NOT NULL,
     PRIMARY KEY (user_id)
  );

ALTER TABLE user_secured
  ADD CONSTRAINT uk_to66vp9rd474vj8shusuq4j7t UNIQUE (username);

CREATE TABLE users_roles (
     role_id  BIGINT NOT NULL,
     user_id BIGINT NOT NULL,
     PRIMARY KEY (role_id, user_id)
  );

ALTER TABLE users_roles
  ADD CONSTRAINT fki5wg0x936i4npqp94nqc4mmyw FOREIGN KEY (user_id) REFERENCES
  user_secured (user_id);

ALTER TABLE users_roles
  ADD CONSTRAINT fki5wg0x936i4npqp94nqc4mmyw2 FOREIGN KEY (role_id) REFERENCES
  user_role (role_id);

DROP TABLE IF EXISTS oauth_client_details;

CREATE TABLE oauth_client_details
  (
     client_id               VARCHAR(256) PRIMARY KEY,
     resource_ids            VARCHAR(256),
     client_secret           VARCHAR(256),
     scope                   VARCHAR(256),
     authorized_grant_types  VARCHAR(256),
     web_server_redirect_uri VARCHAR(256),
     authorities             VARCHAR(256),
     access_token_validity   INTEGER,
     refresh_token_validity  INTEGER,
     additional_information  VARCHAR(4096),
     autoapprove             VARCHAR(256)
  );

DROP TABLE IF EXISTS oauth_client_token;

CREATE TABLE oauth_client_token
  (
     authentication_id VARCHAR(256) PRIMARY KEY,
     token_id          VARCHAR(256),
     token             LONG VARBINARY,
     user_name         VARCHAR(256),
     client_id         VARCHAR(256)
  );

DROP TABLE IF EXISTS oauth_access_token;

CREATE TABLE oauth_access_token
  (
     authentication_id VARCHAR(256) PRIMARY KEY,
     token_id          VARCHAR(256),
     token             LONG VARBINARY,
     user_name         VARCHAR(256),
     client_id         VARCHAR(256),
     authentication    LONG VARBINARY,
     refresh_token     VARCHAR(256)
  );

DROP TABLE IF EXISTS oauth_refresh_token;

CREATE TABLE oauth_refresh_token
  (
     token_id       VARCHAR(256) PRIMARY KEY,
     token          LONG VARBINARY,
     authentication LONG VARBINARY
  );
/*
  drop table if exists oauth_code;
create table oauth_code (
  code VARCHAR(255), authentication LONG VARBINARY
);*/