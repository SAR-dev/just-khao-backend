--Index Sequences
CREATE SEQUENCE "public".auth_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE "public".profile_id_seq START WITH 1 INCREMENT BY 1;

--Tables
CREATE TABLE "public".auth (
    id                   integer DEFAULT nextval('auth_id_seq'::regclass) NOT NULL  ,
	username             varchar(50)  DEFAULT gen_random_uuid()  ,
	hashed_password      varchar(100)    ,
	email                varchar(100)  NOT NULL    ,
	verified             boolean DEFAULT false  ,
	refresh_token        varchar(100)   ,
	refreshed_at         timestamp   ,
	created_at           timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL  ,
	updated_at           timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL  ,
	CONSTRAINT pk_auth PRIMARY KEY ( id ),
	CONSTRAINT unq_auth_username UNIQUE ( username ) ,
	CONSTRAINT unq_auth_email UNIQUE ( email )  ,
	CONSTRAINT check_username CHECK (username ~* '^(?=.{4,50}$)(?![_.-])(?!.*[_.-]{2})[a-zA-Z0-9._-]+(?<![_.-])$')  ,
	CONSTRAINT check_email CHECK (email ~* '^[A-Za-z0-9._+%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$')
 );

CREATE TABLE "public".profile (
    id                   integer DEFAULT nextval('profile_id_seq'::regclass) NOT NULL  ,
	issuer               varchar(10) DEFAULT 'NATIVE'   ,
	issuer_user_id       varchar(100)    ,
	first_name           varchar(20)    ,
	last_name            varchar(20)    ,
	full_name            varchar(50)    ,
	avatar               varchar(200)    ,
	contact_email        varchar(100)   ,
	email_verified       boolean DEFAULT false  ,
	auth_id              integer    ,
	created_at           timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL  ,
	updated_at           timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL  ,
	CONSTRAINT pk_profile PRIMARY KEY ( id ),
	CONSTRAINT fk_profile_auth FOREIGN KEY ( auth_id ) REFERENCES "public".auth( id )   ,
	CONSTRAINT check_contact_email CHECK (contact_email ~* '^[A-Za-z0-9._+%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$')
 );

--Update Function

CREATE OR REPLACE FUNCTION "public".update_updated_at()
RETURNS trigger
LANGUAGE plpgsql
AS $function$
BEGIN
   NEW.updated_at = now();
   RETURN NEW;
END;
$function$
;

CREATE TRIGGER update_updated_at_auth BEFORE UPDATE ON "public".auth FOR EACH ROW EXECUTE FUNCTION update_updated_at();
CREATE TRIGGER update_updated_at_profile BEFORE UPDATE ON "public".profile FOR EACH ROW EXECUTE FUNCTION update_updated_at();