CREATE TABLE "user" (
  "user_id" bigint PRIMARY KEY NOT NULL,
  "nickname" varchar(64) NOT NULL,
  "username" varchar(64) UNIQUE NOT NULL,
  "password" varchar(256) NOT NULL,
  "status" varchar(16) NOT NULL,
  "created_at" timestamptz NOT NULL,
  "last_access_at" timestamptz NOT NULL,
  "role" varchar(16) NOT NULL
);
