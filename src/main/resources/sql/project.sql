CREATE TABLE usertest(
         id INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
         username VARCHAR(100) NOT NULL,
         password VARCHAR(255) NOT NULL,
         email VARCHAR(100),
         age INT CHECK ( age > 0 )
);

CREATE TABLE role(
         id INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
         name VARCHAR(50) NOT NULL
);

CREATE TABLE user_role(
          user_id INT REFERENCES usertest(id),
          role_id INT REFERENCES role(id),
          PRIMARY KEY(user_id, role_id)
);

INSERT INTO usertest (username, password, email, age) VALUES
  ('user1', 'password1', 'user1@example.com', 25),
  ('user2', 'password2', 'user2@example.com', 30),
  ('user3', 'password3', 'user3@example.com', 35),
  ('user4', 'password4', 'user4@example.com', 40),
  ('user5', 'password5', 'user5@example.com', 45);


INSERT INTO role(name) VALUES ('ROLE_ADMIN');
INSERT INTO role(name) VALUES ('ROLE_USER');

INSERT INTO user_role(user_id, role_id) VALUES (1, 1);
INSERT INTO user_role(user_id, role_id) VALUES (2, 2);

DROP TABLE user_role;
DROP TABLE role;
DROP TABLE usertest;