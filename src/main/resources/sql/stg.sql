--
-- PostgreSQL database dump
--

-- Dumped from database version 12.2 (Ubuntu 12.2-1.pgdg18.04+1)
-- Dumped by pg_dump version 12.2 (Ubuntu 12.2-1.pgdg18.04+1)

-- Started on 2020-02-26 21:37:04 MSK

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 13 (class 2615 OID 18958)
-- Name: stg; Type: SCHEMA; Schema: -; Owner: dns_admin
--

CREATE SCHEMA stg;


ALTER SCHEMA stg OWNER TO dns_admin;

SET default_tablespace = upg_dns;

SET default_table_access_method = heap;

--
-- TOC entry 217 (class 1259 OID 19028)
-- Name: category; Type: TABLE; Schema: stg; Owner: dns_admin; Tablespace: upg_dns
--

CREATE TABLE stg.category (
    id integer NOT NULL,
    title character varying(128),
    hierarchy character varying(1028),
    load_date date
);


ALTER TABLE stg.category OWNER TO dns_admin;

--
-- TOC entry 216 (class 1259 OID 19026)
-- Name: category_id_seq; Type: SEQUENCE; Schema: stg; Owner: dns_admin
--

CREATE SEQUENCE stg.category_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE stg.category_id_seq OWNER TO dns_admin;

--
-- TOC entry 3001 (class 0 OID 0)
-- Dependencies: 216
-- Name: category_id_seq; Type: SEQUENCE OWNED BY; Schema: stg; Owner: dns_admin
--

ALTER SEQUENCE stg.category_id_seq OWNED BY stg.category.id;


--
-- TOC entry 219 (class 1259 OID 19039)
-- Name: city; Type: TABLE; Schema: stg; Owner: dns_admin; Tablespace: upg_dns
--

CREATE TABLE stg.city (
    id integer NOT NULL,
    title character varying(256) NOT NULL,
    load_date date
);


ALTER TABLE stg.city OWNER TO dns_admin;

--
-- TOC entry 218 (class 1259 OID 19037)
-- Name: city_id_seq; Type: SEQUENCE; Schema: stg; Owner: dns_admin
--

CREATE SEQUENCE stg.city_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE stg.city_id_seq OWNER TO dns_admin;

--
-- TOC entry 3002 (class 0 OID 0)
-- Dependencies: 218
-- Name: city_id_seq; Type: SEQUENCE OWNED BY; Schema: stg; Owner: dns_admin
--

ALTER SEQUENCE stg.city_id_seq OWNED BY stg.city.id;


--
-- TOC entry 225 (class 1259 OID 6176680)
-- Name: product_item; Type: TABLE; Schema: stg; Owner: dns_admin; Tablespace: upg_dns
--

CREATE TABLE stg.product_item (
    id bigint NOT NULL,
    item json NOT NULL,
    load_date date
);


ALTER TABLE stg.product_item OWNER TO dns_admin;

--
-- TOC entry 224 (class 1259 OID 6176678)
-- Name: product_item_id_seq; Type: SEQUENCE; Schema: stg; Owner: dns_admin
--

CREATE SEQUENCE stg.product_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE stg.product_item_id_seq OWNER TO dns_admin;

--
-- TOC entry 3003 (class 0 OID 0)
-- Dependencies: 224
-- Name: product_item_id_seq; Type: SEQUENCE OWNED BY; Schema: stg; Owner: dns_admin
--

ALTER SEQUENCE stg.product_item_id_seq OWNED BY stg.product_item.id;


--
-- TOC entry 2861 (class 2604 OID 19031)
-- Name: category id; Type: DEFAULT; Schema: stg; Owner: dns_admin
--

ALTER TABLE ONLY stg.category ALTER COLUMN id SET DEFAULT nextval('stg.category_id_seq'::regclass);


--
-- TOC entry 2862 (class 2604 OID 19042)
-- Name: city id; Type: DEFAULT; Schema: stg; Owner: dns_admin
--

ALTER TABLE ONLY stg.city ALTER COLUMN id SET DEFAULT nextval('stg.city_id_seq'::regclass);


--
-- TOC entry 2863 (class 2604 OID 6176683)
-- Name: product_item id; Type: DEFAULT; Schema: stg; Owner: dns_admin
--

ALTER TABLE ONLY stg.product_item ALTER COLUMN id SET DEFAULT nextval('stg.product_item_id_seq'::regclass);


SET default_tablespace = '';

--
-- TOC entry 2865 (class 2606 OID 19036)
-- Name: category category_pkey; Type: CONSTRAINT; Schema: stg; Owner: dns_admin
--

ALTER TABLE ONLY stg.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);


--
-- TOC entry 2867 (class 2606 OID 19044)
-- Name: city city_pkey; Type: CONSTRAINT; Schema: stg; Owner: dns_admin
--

ALTER TABLE ONLY stg.city
    ADD CONSTRAINT city_pkey PRIMARY KEY (id);


SET default_tablespace = upg_dns;

--
-- TOC entry 2868 (class 1259 OID 6176715)
-- Name: ci_stg_product_item; Type: INDEX; Schema: stg; Owner: dns_admin; Tablespace: upg_dns
--

CREATE INDEX ci_stg_product_item ON stg.product_item USING btree (id);

ALTER TABLE stg.product_item CLUSTER ON ci_stg_product_item;


-- Completed on 2020-02-26 21:37:04 MSK

--
-- PostgreSQL database dump complete
--

