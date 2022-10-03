--Index Sequences
CREATE SEQUENCE "public".auth_id_seq START WITH 1 INCREMENT BY 1;

--Tables
CREATE TABLE "public".auth (
    id           integer DEFAULT nextval('auth_id_seq'::regclass) NOT NULL  ,
	username             varchar(20)  NOT NULL  ,
	hashed_password      varchar(100)    ,
	email                varchar(100)  NOT NULL    ,
	access_token         varchar(20)    ,
	refresh_token        varchar(100)   ,
	CONSTRAINT pk_auth PRIMARY KEY ( id ),
	CONSTRAINT unq_auth_username UNIQUE ( username ) ,
	CONSTRAINT unq_auth_email UNIQUE ( email )  ,
	CONSTRAINT check_username CHECK (username ~* '^(?=.{4,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$')  ,
	CONSTRAINT check_email CHECK (email ~* '^[A-Za-z0-9._+%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$')
 );
