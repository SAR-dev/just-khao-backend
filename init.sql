--Index Sequences
CREATE SEQUENCE "public".auth_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE "public".profile_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE "public".post_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE "public".reaction_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE "public".post_reaction_id_seq START WITH 1 INCREMENT BY 1;

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
	CONSTRAINT unq_profile_auth_id UNIQUE ( auth_id ) ,
	CONSTRAINT fk_profile_auth FOREIGN KEY ( auth_id ) REFERENCES "public".auth( id )   ,
	CONSTRAINT check_contact_email CHECK (contact_email ~* '^[A-Za-z0-9._+%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$')
 );

 CREATE TABLE "public".post (
    id                   integer DEFAULT nextval('post_id_seq'::regclass) NOT NULL  ,
    auth_id              integer    ,
	content              varchar(1000)    ,
	images               varchar(100)[]    ,
	video                varchar(100)    ,
	mentions             integer[]      ,
	created_at           timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL  ,
	updated_at           timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL  ,
	CONSTRAINT pk_post PRIMARY KEY ( id ),
	CONSTRAINT fk_post_auth FOREIGN KEY ( auth_id ) REFERENCES "public".auth( id )
 );

  CREATE TABLE "public".reaction (
    id                   integer DEFAULT nextval('reaction_id_seq'::regclass) NOT NULL  ,
	name                 varchar(10)    ,
	shorthand            varchar(10)    ,
	value                varchar(10)    ,
	source               varchar(100)    ,
	type                 varchar(10) DEFAULT 'FREE' CHECK (status IN ( 'FREE', 'PREMIUM' ))   ,
	created_at           timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL  ,
	updated_at           timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL  ,
	CONSTRAINT pk_reaction PRIMARY KEY ( id ),
	CONSTRAINT fk_reaction_auth FOREIGN KEY ( auth_id ) REFERENCES "public".auth( id )
 );

 CREATE TABLE "public".post_reaction (
    id                   integer DEFAULT nextval('post_reaction_id_seq'::regclass) NOT NULL  ,
	post_id              integer    ,
	reaction_id          integer    ,
	auth_id              integer    ,
	created_at           timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL  ,
	updated_at           timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL  ,
	CONSTRAINT pk_reaction PRIMARY KEY ( id ),
	CONSTRAINT fk_post_reaction_post FOREIGN KEY ( auth_id ) REFERENCES "public".post( id )  ,
	CONSTRAINT fk_post_reaction_reaction FOREIGN KEY ( auth_id ) REFERENCES "public".reaction( id )  ,
	CONSTRAINT fk_post_reaction_auth FOREIGN KEY ( auth_id ) REFERENCES "public".auth( id )  ,
	CONSTRAINT unq_reaction_auth_post UNIQUE ( post_id, reaction_id, auth_id )
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
CREATE TRIGGER update_updated_at_post BEFORE UPDATE ON "public".post FOR EACH ROW EXECUTE FUNCTION update_updated_at();
CREATE TRIGGER update_updated_at_reaction BEFORE UPDATE ON "public".reaction FOR EACH ROW EXECUTE FUNCTION update_updated_at();
CREATE TRIGGER update_updated_at_post_reaction BEFORE UPDATE ON "public".post_reaction FOR EACH ROW EXECUTE FUNCTION update_updated_at();