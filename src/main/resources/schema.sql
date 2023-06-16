DROP TABLE IF EXISTS users, items, requests, bookings, comments cascade;

CREATE TABLE IF NOT EXISTS users
(
  id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(128) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items
(
  id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  description text NOT NULL,
  available boolean NOT NULL,
  user_id integer NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS requests
(
  id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  description text NOT NULL,
  user_id integer NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT pk_request PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
  id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  item_id integer NOT NULL REFERENCES items (id) ON DELETE CASCADE,
  user_id integer NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  status VARCHAR(50),
  CONSTRAINT pk_booking PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments
(
  id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  content text NOT NULL,
  item_id integer NOT NULL REFERENCES items (id) ON DELETE CASCADE,
  user_id integer NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  date_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT pk_comment PRIMARY KEY (id)
);




