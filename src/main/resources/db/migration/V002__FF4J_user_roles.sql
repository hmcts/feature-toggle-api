CREATE TABLE USERS(
  username VARCHAR(50) NOT NULL PRIMARY KEY,
  password VARCHAR(100) NOT NULL,
  enabled BOOLEAN NOT NULL
);

CREATE TABLE authorities (
  username VARCHAR(50) NOT NULL,
  authority VARCHAR(50) NOT NULL,
  constraint fk_authorities_users foreign KEY(username)references USERS(username)
);

CREATE unique INDEX ix_auth_username ON authorities(username, authority);
