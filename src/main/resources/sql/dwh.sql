--
-- PostgreSQL database dump
--

-- Dumped from database version 12.2 (Ubuntu 12.2-1.pgdg18.04+1)
-- Dumped by pg_dump version 12.2 (Ubuntu 12.2-1.pgdg18.04+1)

-- Started on 2020-02-26 21:38:42 MSK

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
-- TOC entry 3031 (class 1262 OID 18941)
-- Name: dns_dwh; Type: DATABASE; Schema: -; Owner: dns_admin
--

CREATE DATABASE dns_dwh WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';


ALTER DATABASE dns_dwh OWNER TO dns_admin;

\connect dns_dwh

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
-- TOC entry 7 (class 2615 OID 18943)
-- Name: dwh; Type: SCHEMA; Schema: -; Owner: dns_admin
--

CREATE SCHEMA dwh;


ALTER SCHEMA dwh OWNER TO dns_admin;

--
-- TOC entry 240 (class 1255 OID 6176719)
-- Name: fact_insert_product_price(); Type: PROCEDURE; Schema: dwh; Owner: dns_admin
--

CREATE PROCEDURE dwh.fact_insert_product_price()
    LANGUAGE plpgsql
    AS $$
declare
	cur_id bigint;
	id_to bigint;
	step bigint;
begin

	step := 20000;

	select
		min(id)
		, max(id)
	into
		cur_id
		, id_to
	from
		stg.product_item
	;
	
	raise notice '% - %', cur_id, id_to;
	
	while cur_id < (id_to + step)
	loop

		/*
		* merge products
		*/

		with product_info as
		(
			select
				lower((item ->> 'category')::varchar(1024)) category_title
				, (item ->> 'title')::varchar(256) title
				, (item ->> 'code')::varchar(32) code
			from
				stg.product_item
			where
				id >= cur_id
				and
				id < cur_id + step
		)
		, product_info_nummed as
		(
			select
				category_title
				, title
				, code
				, row_number() over (partition by category_title, title, code) rn
			from
				product_info
		)
		insert into
			dwh.product
			(title, category_id, code)
		select
			prod_info.title
			, cat.id
			, prod_info.code
		from
			product_info_nummed prod_info

			inner join dwh.category cat on
				cat.nature_hierarchy = prod_info.category_title

			left join dwh.product dwh_prod on
				dwh_prod.code = prod_info.code
				and
				dwh_prod.title = prod_info.title
				and
				dwh_prod.category_id = cat.id
		where
			rn = 1
			and
			dwh_prod.id is null
		;
		
		/*
			* fact insert
		*/
		
		/*
			* new records
		*/
		
		drop table if exists product_stg_info;

		create local temp table product_stg_info as
		select
			cast((_item.item ->> 'date')::timestamptz(0) as date) as date
			, (_item.item ->> 'city')::varchar(256) city
			, lower((_item.item ->> 'category')::varchar(1024)) category_title
			, (_item.item ->> 'title')::varchar(256) title
			, (_item.item ->> 'code')::varchar(32) code
			, (_item.item ->> 'price')::integer price
		from
			stg.product_item _item
		where
			id >= cur_id
			and
			id < cur_id + step
		;

		with product_dwh_info as
		(
			select
				calend.date_id as date_id
				, calend.date as "date"
				, prod.id as product_id
				, ci.id as city_id
				, _item.price as price
			from
				product_stg_info _item

				inner join dwh.calendar calend on
					calend.date = _item.date

				inner join dwh.category cat on
					cat.nature_hierarchy = _item.category_title

				inner join dwh.product prod on
					prod.code = _item.code
					and
					prod.title = _item.title
					and
					prod.category_id = cat.id

				inner join dwh.city ci on
					ci.title = _item.city
		)
		insert into
			dwh.product_price
			(date_id_from, date_id_to, product_id, city_id, price)
		select
			new_one.date_id as date_id_from
			, new_one.date_id as date_id_to
			, new_one.product_id
			, new_one.city_id
			, new_one.price
		from
			product_dwh_info new_one

			left join dwh.product_price existing on
				existing.product_id = new_one.product_id
				and
				to_char(new_one.date - interval '1 day', 'yyyyMMdd')::integer between existing.date_id_from and existing.date_id_to
				and
				existing.city_id = new_one.city_id
				and
				existing.price = new_one.price
		where
			existing.id is null
		;
		
		/*
			* updated records
		*/
		
		with product_dwh_info as
		(
			select
				calend.date_id as date_id
				, calend.date as "date"
				, prod.id as product_id
				, ci.id as city_id
				, _item.price as price
			from
				product_stg_info _item

				inner join dwh.calendar calend on
					calend.date = _item.date

				inner join dwh.category cat on
					cat.nature_hierarchy = _item.category_title

				inner join dwh.product prod on
					prod.code = _item.code
					and
					prod.title = _item.title
					and
					prod.category_id = cat.id

				inner join dwh.city ci on
					ci.title = _item.city
		)
		update
			dwh.product_price existing
		set
			date_id_to = new_one.date_id
		from
			product_dwh_info new_one
		where
			existing.product_id = new_one.product_id
			and
			existing.city_id = new_one.city_id
			and
			existing.price = new_one.price
			and
			existing.date_id_to = to_char(new_one.date - interval '1 day', 'yyyyMMdd')::integer
		;
		
		raise notice 'DONE: %', cur_id;
		
		cur_id := cur_id + step;
	
	end loop;

end;
$$;


ALTER PROCEDURE dwh.fact_insert_product_price() OWNER TO dns_admin;

--
-- TOC entry 226 (class 1255 OID 19094)
-- Name: merge_categories(); Type: PROCEDURE; Schema: dwh; Owner: dns_admin
--

CREATE PROCEDURE dwh.merge_categories()
    LANGUAGE plpgsql
    AS $$
begin

	with non_existing as 
	( 
	  select 
		stg_cat.title 
		, stg_cat.hierarchy 
		, replace(stg_cat.hierarchy || ' / ' || stg_cat.title, 'all / ', '') nature_hierarchy
	  from 
		stg.category stg_cat 

		left join dwh.category dwh_cat on 
		  dwh_cat.title = stg_cat.title 
		  and 
		  dwh_cat.hierarchy = stg_cat.hierarchy 
	  where 
		dwh_cat.id is null 
	) 
	insert into 
	  dwh.category 
	  (title, hierarchy, nature_hierarchy) 
	select 
	  title 
	  , hierarchy 
	  , nature_hierarchy
	from 
	  non_existing;
	  
	update dwh.category as c 
		set 
		high_id = 
		( 
			select 
				id 
			from 
				dwh.category c1 
			where 
				c.hierarchy != 'all' and c.hierarchy = c1.hierarchy || ' / ' || c1.title 
				or 
				c.hierarchy = 'all' and c1.title = c.hierarchy 
		) 
	where 
		high_id is null 
		and 
		title != 'all'
	;
end
;
$$;


ALTER PROCEDURE dwh.merge_categories() OWNER TO dns_admin;

--
-- TOC entry 227 (class 1255 OID 6176689)
-- Name: merge_cities(); Type: PROCEDURE; Schema: dwh; Owner: dns_admin
--

CREATE PROCEDURE dwh.merge_cities()
    LANGUAGE plpgsql
    AS $$
begin
	with non_existing as
	(
	  select
		stg_city.title as title
	  from
		stg.city stg_city

		left join dwh.city dwh_city on
		  dwh_city.title = stg_city.title
	  where
		dwh_city.id is null
	)
	insert into
	  dwh.city (title)
	select
	  title
	from
	  non_existing
	;
end
;
$$;


ALTER PROCEDURE dwh.merge_cities() OWNER TO dns_admin;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 221 (class 1259 OID 19060)
-- Name: calendar; Type: TABLE; Schema: dwh; Owner: dns_admin
--

CREATE TABLE dwh.calendar (
    date_id double precision NOT NULL,
    date date,
    year double precision,
    month_id double precision,
    month double precision,
    chr_month text,
    day_of_week double precision,
    chr_day_of_week text
);


ALTER TABLE dwh.calendar OWNER TO dns_admin;

SET default_tablespace = upg_dns;

--
-- TOC entry 215 (class 1259 OID 19007)
-- Name: category; Type: TABLE; Schema: dwh; Owner: dns_admin; Tablespace: upg_dns
--

CREATE TABLE dwh.category (
    id integer NOT NULL,
    high_id integer,
    title character varying(256) NOT NULL,
    hierarchy character varying(1024),
    nature_hierarchy character varying(1024)
);


ALTER TABLE dwh.category OWNER TO dns_admin;

--
-- TOC entry 214 (class 1259 OID 19005)
-- Name: category_id_seq; Type: SEQUENCE; Schema: dwh; Owner: dns_admin
--

CREATE SEQUENCE dwh.category_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE dwh.category_id_seq OWNER TO dns_admin;

--
-- TOC entry 3032 (class 0 OID 0)
-- Dependencies: 214
-- Name: category_id_seq; Type: SEQUENCE OWNED BY; Schema: dwh; Owner: dns_admin
--

ALTER SEQUENCE dwh.category_id_seq OWNED BY dwh.category.id;


--
-- TOC entry 211 (class 1259 OID 18991)
-- Name: city; Type: TABLE; Schema: dwh; Owner: dns_admin; Tablespace: upg_dns
--

CREATE TABLE dwh.city (
    id integer NOT NULL,
    title character varying(256) NOT NULL
);


ALTER TABLE dwh.city OWNER TO dns_admin;

--
-- TOC entry 210 (class 1259 OID 18989)
-- Name: city_id_seq; Type: SEQUENCE; Schema: dwh; Owner: dns_admin
--

CREATE SEQUENCE dwh.city_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE dwh.city_id_seq OWNER TO dns_admin;

--
-- TOC entry 3033 (class 0 OID 0)
-- Dependencies: 210
-- Name: city_id_seq; Type: SEQUENCE OWNED BY; Schema: dwh; Owner: dns_admin
--

ALTER SEQUENCE dwh.city_id_seq OWNED BY dwh.city.id;


--
-- TOC entry 213 (class 1259 OID 18999)
-- Name: product; Type: TABLE; Schema: dwh; Owner: dns_admin; Tablespace: upg_dns
--

CREATE TABLE dwh.product (
    id integer NOT NULL,
    category_id integer,
    title character varying(256) NOT NULL,
    code character varying(32)
);


ALTER TABLE dwh.product OWNER TO dns_admin;

--
-- TOC entry 212 (class 1259 OID 18997)
-- Name: product_id_seq; Type: SEQUENCE; Schema: dwh; Owner: dns_admin
--

CREATE SEQUENCE dwh.product_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE dwh.product_id_seq OWNER TO dns_admin;

--
-- TOC entry 3034 (class 0 OID 0)
-- Dependencies: 212
-- Name: product_id_seq; Type: SEQUENCE OWNED BY; Schema: dwh; Owner: dns_admin
--

ALTER SEQUENCE dwh.product_id_seq OWNED BY dwh.product.id;


--
-- TOC entry 223 (class 1259 OID 6176660)
-- Name: product_price; Type: TABLE; Schema: dwh; Owner: dns_admin; Tablespace: upg_dns
--

CREATE TABLE dwh.product_price (
    id bigint NOT NULL,
    date_id_from integer NOT NULL,
    date_id_to integer NOT NULL,
    product_id integer NOT NULL,
    city_id integer NOT NULL,
    price integer DEFAULT 0 NOT NULL
);


ALTER TABLE dwh.product_price OWNER TO dns_admin;

--
-- TOC entry 222 (class 1259 OID 6176658)
-- Name: product_price_id_seq; Type: SEQUENCE; Schema: dwh; Owner: dns_admin
--

CREATE SEQUENCE dwh.product_price_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE dwh.product_price_id_seq OWNER TO dns_admin;

--
-- TOC entry 3035 (class 0 OID 0)
-- Dependencies: 222
-- Name: product_price_id_seq; Type: SEQUENCE OWNED BY; Schema: dwh; Owner: dns_admin
--

ALTER SEQUENCE dwh.product_price_id_seq OWNED BY dwh.product_price.id;


--
-- TOC entry 209 (class 1259 OID 18981)
-- Name: sku; Type: TABLE; Schema: dwh; Owner: dns_admin; Tablespace: upg_dns
--

CREATE TABLE dwh.sku (
    id integer NOT NULL,
    date_id integer NOT NULL,
    product_id integer NOT NULL,
    city_id integer NOT NULL,
    price integer DEFAULT 0 NOT NULL,
    bonus integer DEFAULT 0 NOT NULL,
    code character varying(32),
    created_at date
);


ALTER TABLE dwh.sku OWNER TO dns_admin;

--
-- TOC entry 208 (class 1259 OID 18979)
-- Name: sku_id_seq; Type: SEQUENCE; Schema: dwh; Owner: dns_admin
--

CREATE SEQUENCE dwh.sku_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE dwh.sku_id_seq OWNER TO dns_admin;

--
-- TOC entry 3036 (class 0 OID 0)
-- Dependencies: 208
-- Name: sku_id_seq; Type: SEQUENCE OWNED BY; Schema: dwh; Owner: dns_admin
--

ALTER SEQUENCE dwh.sku_id_seq OWNED BY dwh.sku.id;


--
-- TOC entry 220 (class 1259 OID 19056)
-- Name: v_calendar; Type: VIEW; Schema: dwh; Owner: dns_admin
--

CREATE VIEW dwh.v_calendar AS
 SELECT (((date_part('year'::text, t.date) * (10000)::double precision) + (date_part('month'::text, t.date) * (100)::double precision)) + date_part('day'::text, t.date)) AS date_id,
    (t.date)::date AS date,
    date_part('year'::text, t.date) AS year,
    ((date_part('year'::text, t.date) * (100)::double precision) + date_part('month'::text, t.date)) AS month_id,
    date_part('month'::text, t.date) AS month,
    to_char(t.date, 'month'::text) AS chr_month,
    date_part('isodow'::text, t.date) AS day_of_week,
    to_char(t.date, 'day'::text) AS chr_day_of_week
   FROM generate_series(('2019-12-20'::date)::timestamp with time zone, ('2020-12-31'::date)::timestamp with time zone, '1 day'::interval) t(date);


ALTER TABLE dwh.v_calendar OWNER TO dns_admin;

--
-- TOC entry 2870 (class 2604 OID 19010)
-- Name: category id; Type: DEFAULT; Schema: dwh; Owner: dns_admin
--

ALTER TABLE ONLY dwh.category ALTER COLUMN id SET DEFAULT nextval('dwh.category_id_seq'::regclass);


--
-- TOC entry 2868 (class 2604 OID 18994)
-- Name: city id; Type: DEFAULT; Schema: dwh; Owner: dns_admin
--

ALTER TABLE ONLY dwh.city ALTER COLUMN id SET DEFAULT nextval('dwh.city_id_seq'::regclass);


--
-- TOC entry 2869 (class 2604 OID 19002)
-- Name: product id; Type: DEFAULT; Schema: dwh; Owner: dns_admin
--

ALTER TABLE ONLY dwh.product ALTER COLUMN id SET DEFAULT nextval('dwh.product_id_seq'::regclass);


--
-- TOC entry 2871 (class 2604 OID 6176663)
-- Name: product_price id; Type: DEFAULT; Schema: dwh; Owner: dns_admin
--

ALTER TABLE ONLY dwh.product_price ALTER COLUMN id SET DEFAULT nextval('dwh.product_price_id_seq'::regclass);


--
-- TOC entry 2865 (class 2604 OID 18984)
-- Name: sku id; Type: DEFAULT; Schema: dwh; Owner: dns_admin
--

ALTER TABLE ONLY dwh.sku ALTER COLUMN id SET DEFAULT nextval('dwh.sku_id_seq'::regclass);


SET default_tablespace = '';

--
-- TOC entry 2888 (class 2606 OID 19012)
-- Name: category category_pkey; Type: CONSTRAINT; Schema: dwh; Owner: dns_admin
--

ALTER TABLE ONLY dwh.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);


--
-- TOC entry 2880 (class 2606 OID 18996)
-- Name: city city_pkey; Type: CONSTRAINT; Schema: dwh; Owner: dns_admin
--

ALTER TABLE ONLY dwh.city
    ADD CONSTRAINT city_pkey PRIMARY KEY (id);


SET default_tablespace = upg_dns;

--
-- TOC entry 2895 (class 2606 OID 19114)
-- Name: calendar pk_calendar_date_id; Type: CONSTRAINT; Schema: dwh; Owner: dns_admin; Tablespace: upg_dns
--

ALTER TABLE ONLY dwh.calendar
    ADD CONSTRAINT pk_calendar_date_id PRIMARY KEY (date_id);

ALTER TABLE dwh.calendar CLUSTER ON pk_calendar_date_id;


SET default_tablespace = '';

--
-- TOC entry 2898 (class 2606 OID 6176666)
-- Name: product_price pk_product_price; Type: CONSTRAINT; Schema: dwh; Owner: dns_admin
--

ALTER TABLE ONLY dwh.product_price
    ADD CONSTRAINT pk_product_price PRIMARY KEY (id);


--
-- TOC entry 2886 (class 2606 OID 19004)
-- Name: product product_pkey; Type: CONSTRAINT; Schema: dwh; Owner: dns_admin
--

ALTER TABLE ONLY dwh.product
    ADD CONSTRAINT product_pkey PRIMARY KEY (id);


--
-- TOC entry 2878 (class 2606 OID 18988)
-- Name: sku sku_pkey; Type: CONSTRAINT; Schema: dwh; Owner: dns_admin
--

ALTER TABLE ONLY dwh.sku
    ADD CONSTRAINT sku_pkey PRIMARY KEY (id);


SET default_tablespace = upg_dns;

--
-- TOC entry 2873 (class 1259 OID 19107)
-- Name: ci_sku_id; Type: INDEX; Schema: dwh; Owner: dns_admin; Tablespace: upg_dns
--

CREATE INDEX ci_sku_id ON dwh.sku USING btree (id);

ALTER TABLE dwh.sku CLUSTER ON ci_sku_id;


--
-- TOC entry 2889 (class 1259 OID 19025)
-- Name: nci_category_hierarchy; Type: INDEX; Schema: dwh; Owner: dns_admin; Tablespace: upg_dns
--

CREATE INDEX nci_category_hierarchy ON dwh.category USING btree (hierarchy);


--
-- TOC entry 2890 (class 1259 OID 19014)
-- Name: nci_category_high_id; Type: INDEX; Schema: dwh; Owner: dns_admin; Tablespace: upg_dns
--

CREATE INDEX nci_category_high_id ON dwh.category USING btree (high_id) INCLUDE (id, title);


--
-- TOC entry 2891 (class 1259 OID 19116)
-- Name: nci_category_nature_hierarchy; Type: INDEX; Schema: dwh; Owner: dns_admin; Tablespace: upg_dns
--

CREATE INDEX nci_category_nature_hierarchy ON dwh.category USING btree (nature_hierarchy) INCLUDE (id);


--
-- TOC entry 2892 (class 1259 OID 19071)
-- Name: nci_category_title_hierarchy; Type: INDEX; Schema: dwh; Owner: dns_admin; Tablespace: upg_dns
--

CREATE INDEX nci_category_title_hierarchy ON dwh.category USING btree (title, hierarchy);


--
-- TOC entry 2881 (class 1259 OID 6176716)
-- Name: nci_city_title; Type: INDEX; Schema: dwh; Owner: dns_admin; Tablespace: upg_dns
--

CREATE INDEX nci_city_title ON dwh.city USING btree (title) INCLUDE (id);


--
-- TOC entry 2893 (class 1259 OID 6176717)
-- Name: nci_dwh_calendar_date; Type: INDEX; Schema: dwh; Owner: dns_admin; Tablespace: upg_dns
--

CREATE INDEX nci_dwh_calendar_date ON dwh.calendar USING btree (date) INCLUDE (date_id);


--
-- TOC entry 2896 (class 1259 OID 6176827)
-- Name: nci_dwh_product_price; Type: INDEX; Schema: dwh; Owner: dns_admin; Tablespace: upg_dns
--

CREATE INDEX nci_dwh_product_price ON dwh.product_price USING btree (city_id, product_id) INCLUDE (id, date_id_from, date_id_to, price);


--
-- TOC entry 2882 (class 1259 OID 19016)
-- Name: nci_product_category_id; Type: INDEX; Schema: dwh; Owner: dns_admin; Tablespace: upg_dns
--

CREATE INDEX nci_product_category_id ON dwh.product USING btree (category_id) INCLUDE (title);


--
-- TOC entry 2883 (class 1259 OID 6176718)
-- Name: nci_product_code; Type: INDEX; Schema: dwh; Owner: dns_admin; Tablespace: upg_dns
--

CREATE INDEX nci_product_code ON dwh.product USING btree (code) INCLUDE (id, category_id, title);


--
-- TOC entry 2884 (class 1259 OID 19017)
-- Name: nci_product_title; Type: INDEX; Schema: dwh; Owner: dns_admin; Tablespace: upg_dns
--

CREATE INDEX nci_product_title ON dwh.product USING btree (title) INCLUDE (id);


--
-- TOC entry 2874 (class 1259 OID 19169)
-- Name: nci_sku_city_id; Type: INDEX; Schema: dwh; Owner: dns_admin; Tablespace: upg_dns
--

CREATE INDEX nci_sku_city_id ON dwh.sku USING btree (city_id);


--
-- TOC entry 2875 (class 1259 OID 19170)
-- Name: nci_sku_date_id; Type: INDEX; Schema: dwh; Owner: dns_admin; Tablespace: upg_dns
--

CREATE INDEX nci_sku_date_id ON dwh.sku USING btree (date_id);


--
-- TOC entry 2876 (class 1259 OID 19168)
-- Name: nci_sku_product_id; Type: INDEX; Schema: dwh; Owner: dns_admin; Tablespace: upg_dns
--

CREATE INDEX nci_sku_product_id ON dwh.sku USING btree (product_id);


-- Completed on 2020-02-26 21:38:42 MSK

--
-- PostgreSQL database dump complete
--

