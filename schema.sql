--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;


--
-- Name: EXTENSION "uuid-ossp"; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: code_sequence; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.code_sequence (
    id bigint NOT NULL,
    entity_type character varying(32) NOT NULL,
    project_id uuid NOT NULL,
    last_number integer DEFAULT 0 NOT NULL
);


--
-- Name: code_sequence_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.code_sequence_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: code_sequence_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.code_sequence_id_seq OWNED BY public.code_sequence.id;


--
-- Name: project; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.project (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    name character varying(255) NOT NULL,
    description text,
    code character varying(32) NOT NULL
);


--
-- Name: project_case_sequence; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.project_case_sequence (
    project_id uuid NOT NULL,
    next_number integer DEFAULT 1 NOT NULL,
    next_value integer NOT NULL
);


--
-- Name: test_case; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.test_case (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    title character varying(255),
    description character varying(5000),
    preconditions character varying(5000),
    steps character varying(5000),
    expected_result character varying(5000),
    priority character varying(255),
    tags character varying(255),
    state character varying(255),
    owner character varying(255),
    type character varying(255),
    automation_status character varying(255),
    use_case character varying(5000),
    component character varying(255),
    requirement character varying(255),
    suite_id uuid,
    project_id uuid NOT NULL,
    code character varying(35)
);


--
-- Name: test_case_instance; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.test_case_instance (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    case_id uuid,
    status character varying(255),
    run_id uuid,
    test_case_id uuid,
    test_run_id uuid
);


--
-- Name: test_run; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.test_run (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    name character varying(255),
    status character varying(255),
    started_at timestamp with time zone,
    completed_at timestamp with time zone,
    project_id uuid,
    finished_at timestamp(6) with time zone
);


--
-- Name: test_runs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.test_runs (
    id uuid NOT NULL,
    code character varying(255),
    completed_at timestamp(6) with time zone,
    name character varying(255),
    project_id uuid NOT NULL,
    started_at timestamp(6) with time zone,
    status character varying(255),
    assigned_to_id uuid,
    description character varying(255)
);


--
-- Name: test_step; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.test_step (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    test_case_id uuid NOT NULL,
    order_index integer NOT NULL,
    action text,
    expected_result text
);


--
-- Name: test_step_result; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.test_step_result (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    step_number integer,
    message character varying(255),
    result character varying(255),
    instance_id uuid
);


--
-- Name: test_suite; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.test_suite (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    name character varying(255),
    description character varying(255),
    parent_id uuid,
    project_id uuid,
    code character varying(255)
);


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id uuid NOT NULL,
    email character varying(255) NOT NULL,
    name character varying(255),
    password character varying(255),
    role character varying(255)
);


--
-- Name: code_sequence id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.code_sequence ALTER COLUMN id SET DEFAULT nextval('public.code_sequence_id_seq'::regclass);


--
-- Name: code_sequence code_sequence_entity_type_project_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.code_sequence
    ADD CONSTRAINT code_sequence_entity_type_project_id_key UNIQUE (entity_type, project_id);


--
-- Name: code_sequence code_sequence_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.code_sequence
    ADD CONSTRAINT code_sequence_pkey PRIMARY KEY (id);


--
-- Name: project_case_sequence project_case_sequence_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project_case_sequence
    ADD CONSTRAINT project_case_sequence_pkey PRIMARY KEY (project_id);


--
-- Name: project project_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project
    ADD CONSTRAINT project_code_key UNIQUE (code);


--
-- Name: project project_code_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project
    ADD CONSTRAINT project_code_unique UNIQUE (code);


--
-- Name: project project_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project
    ADD CONSTRAINT project_name_key UNIQUE (name);


--
-- Name: project project_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project
    ADD CONSTRAINT project_pkey PRIMARY KEY (id);


--
-- Name: test_case_instance test_case_instance_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.test_case_instance
    ADD CONSTRAINT test_case_instance_pkey PRIMARY KEY (id);


--
-- Name: test_case test_case_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.test_case
    ADD CONSTRAINT test_case_pkey PRIMARY KEY (id);


--
-- Name: test_run test_run_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.test_run
    ADD CONSTRAINT test_run_pkey PRIMARY KEY (id);


--
-- Name: test_runs test_runs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.test_runs
    ADD CONSTRAINT test_runs_pkey PRIMARY KEY (id);


--
-- Name: test_step test_step_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.test_step
    ADD CONSTRAINT test_step_pkey PRIMARY KEY (id);


--
-- Name: test_step_result test_step_result_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.test_step_result
    ADD CONSTRAINT test_step_result_pkey PRIMARY KEY (id);


--
-- Name: test_suite test_suite_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.test_suite
    ADD CONSTRAINT test_suite_pkey PRIMARY KEY (id);


--
-- Name: users uk_6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- Name: code_sequence uko62s7xj07oxlc65a6sycs87yu; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.code_sequence
    ADD CONSTRAINT uko62s7xj07oxlc65a6sycs87yu UNIQUE (entity_type, project_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: idx_test_step_test_case_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_test_step_test_case_id ON public.test_step USING btree (test_case_id);


--
-- Name: test_case_code_project_unique; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX test_case_code_project_unique ON public.test_case USING btree (project_id, code);


--
-- Name: test_suite fknlhm016gjf8gvev8d73k0l4hy; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.test_suite
    ADD CONSTRAINT fknlhm016gjf8gvev8d73k0l4hy FOREIGN KEY (parent_id) REFERENCES public.test_suite(id);


--
-- Name: test_runs fkqjr1xap8m4meykmvhi8i73upo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.test_runs
    ADD CONSTRAINT fkqjr1xap8m4meykmvhi8i73upo FOREIGN KEY (assigned_to_id) REFERENCES public.users(id);


--
-- Name: test_case_instance test_case_instance_case_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.test_case_instance
    ADD CONSTRAINT test_case_instance_case_id_fkey FOREIGN KEY (case_id) REFERENCES public.test_case(id);


--
-- Name: test_case_instance test_case_instance_run_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.test_case_instance
    ADD CONSTRAINT test_case_instance_run_id_fkey FOREIGN KEY (run_id) REFERENCES public.test_run(id);


--
-- Name: test_case test_case_project_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.test_case
    ADD CONSTRAINT test_case_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.project(id) ON DELETE SET NULL;


--
-- Name: test_case test_case_suite_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.test_case
    ADD CONSTRAINT test_case_suite_id_fkey FOREIGN KEY (suite_id) REFERENCES public.test_suite(id) ON DELETE SET NULL;


--
-- Name: test_run test_run_project_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.test_run
    ADD CONSTRAINT test_run_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.project(id);


--
-- Name: test_step_result test_step_result_instance_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.test_step_result
    ADD CONSTRAINT test_step_result_instance_id_fkey FOREIGN KEY (instance_id) REFERENCES public.test_case_instance(id);


--
-- Name: test_step test_step_test_case_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.test_step
    ADD CONSTRAINT test_step_test_case_id_fkey FOREIGN KEY (test_case_id) REFERENCES public.test_case(id) ON DELETE CASCADE;


--
-- Name: test_suite test_suite_project_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.test_suite
    ADD CONSTRAINT test_suite_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.project(id) ON DELETE SET NULL;


--
-- PostgreSQL database dump complete
--

