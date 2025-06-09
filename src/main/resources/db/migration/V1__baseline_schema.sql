
-- ========================
-- SCHEMA BASELINE (V1)
-- ========================

-- Проєкти
CREATE TABLE project (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    code VARCHAR(50) UNIQUE
);

-- Користувачі (якщо потрібно для assignedTo)
CREATE TABLE app_user (
    id UUID PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255),
    full_name VARCHAR(255)
);

-- Словники
CREATE TABLE environment (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    UNIQUE(project_id, name)
);

CREATE TABLE configuration (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    UNIQUE(project_id, name)
);

CREATE TABLE milestone (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    UNIQUE(project_id, name)
);

-- Code генератор
CREATE TABLE code_sequence (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    project_id UUID,
    last_number INTEGER NOT NULL,
    UNIQUE (entity_type, project_id)
);

-- Сюїти
CREATE TABLE test_suite (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    description TEXT,
    parent_id UUID,
    code VARCHAR(50),
    UNIQUE (project_id, code)
);

-- Тест-кейси
-- Тест-кейси
CREATE TABLE test_case (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    suite_id UUID,
    code VARCHAR(50),
    title VARCHAR(255) NOT NULL,
    preconditions TEXT,
    description TEXT,
    steps TEXT,
    expected_result TEXT,
    priority VARCHAR(50),
    state VARCHAR(50),
    type VARCHAR(50),
    component VARCHAR(100),
    automation_status VARCHAR(50),
    requirement VARCHAR(100),
    owner VARCHAR(100),
    UNIQUE (project_id, code)
);

-- Запуски тестів
CREATE TABLE test_run (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50),
    status VARCHAR(50),
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    assigned_to UUID,
    description TEXT,
    environment_id UUID,
    configuration_id UUID,
    milestone_id UUID
);

-- Прив’язка кейсів до запуску
CREATE TABLE test_case_instance (
    id UUID PRIMARY KEY,
    run_id UUID NOT NULL,
    case_id UUID NOT NULL,
    status VARCHAR(50)
);

-- Результати кроків
CREATE TABLE test_step_result (
    id UUID PRIMARY KEY,
    instance_id UUID NOT NULL,
    step_number INT,
    result VARCHAR(50),
    message TEXT
);

-- FOREIGN KEYS
ALTER TABLE test_suite ADD CONSTRAINT fk_suite_project FOREIGN KEY (project_id) REFERENCES project(id);
ALTER TABLE test_suite ADD CONSTRAINT fk_suite_parent FOREIGN KEY (parent_id) REFERENCES test_suite(id);

ALTER TABLE test_case ADD CONSTRAINT fk_case_project FOREIGN KEY (project_id) REFERENCES project(id);
ALTER TABLE test_case ADD CONSTRAINT fk_case_suite FOREIGN KEY (suite_id) REFERENCES test_suite(id);

ALTER TABLE test_run ADD CONSTRAINT fk_run_project FOREIGN KEY (project_id) REFERENCES project(id);
ALTER TABLE test_run ADD CONSTRAINT fk_run_user FOREIGN KEY (assigned_to) REFERENCES app_user(id);
ALTER TABLE test_run ADD CONSTRAINT fk_run_environment FOREIGN KEY (environment_id) REFERENCES environment(id);
ALTER TABLE test_run ADD CONSTRAINT fk_run_configuration FOREIGN KEY (configuration_id) REFERENCES configuration(id);
ALTER TABLE test_run ADD CONSTRAINT fk_run_milestone FOREIGN KEY (milestone_id) REFERENCES milestone(id);

ALTER TABLE test_case_instance ADD CONSTRAINT fk_instance_run FOREIGN KEY (run_id) REFERENCES test_run(id);
ALTER TABLE test_case_instance ADD CONSTRAINT fk_instance_case FOREIGN KEY (case_id) REFERENCES test_case(id);

ALTER TABLE test_step_result ADD CONSTRAINT fk_result_instance FOREIGN KEY (instance_id) REFERENCES test_case_instance(id);

ALTER TABLE environment ADD CONSTRAINT fk_env_project FOREIGN KEY (project_id) REFERENCES project(id);
ALTER TABLE configuration ADD CONSTRAINT fk_conf_project FOREIGN KEY (project_id) REFERENCES project(id);
ALTER TABLE milestone ADD CONSTRAINT fk_mile_project FOREIGN KEY (project_id) REFERENCES project(id);
