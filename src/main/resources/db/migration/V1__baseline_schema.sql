-- ========================
-- SCHEMA BASELINE (V2) TMS STRIKER
-- ========================

-- Проєкти
CREATE TABLE project (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    code VARCHAR(50) UNIQUE
);

-- Користувачі
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
    UNIQUE(project_id, name),
    CONSTRAINT fk_env_project FOREIGN KEY (project_id) REFERENCES project(id)
);

CREATE TABLE configuration (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    UNIQUE(project_id, name),
    CONSTRAINT fk_conf_project FOREIGN KEY (project_id) REFERENCES project(id)
);

CREATE TABLE version (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(50),
    UNIQUE(project_id, name),
    CONSTRAINT fk_ver_project FOREIGN KEY (project_id) REFERENCES project(id)
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
    UNIQUE (project_id, code),
    CONSTRAINT fk_suite_project FOREIGN KEY (project_id) REFERENCES project(id),
    CONSTRAINT fk_suite_parent FOREIGN KEY (parent_id) REFERENCES test_suite(id)
);

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
    UNIQUE (project_id, code),
    CONSTRAINT fk_case_project FOREIGN KEY (project_id) REFERENCES project(id),
    CONSTRAINT fk_case_suite FOREIGN KEY (suite_id) REFERENCES test_suite(id)
);

-- Прогони тестів (Test Runs) — ONLY сучасна структура!
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
    configuration_id UUID,
    version_id UUID,
    CONSTRAINT fk_run_project FOREIGN KEY (project_id) REFERENCES project(id),
    CONSTRAINT fk_run_user FOREIGN KEY (assigned_to) REFERENCES app_user(id),
    CONSTRAINT fk_run_configuration FOREIGN KEY (configuration_id) REFERENCES configuration(id),
    CONSTRAINT fk_run_version FOREIGN KEY (version_id) REFERENCES version(id)
);

-- Many-to-many: прогони <-> кейси
CREATE TABLE test_run_cases (
    test_run_id UUID NOT NULL,
    test_case_id UUID NOT NULL,
    PRIMARY KEY (test_run_id, test_case_id),
    CONSTRAINT fk_trc_run FOREIGN KEY (test_run_id) REFERENCES test_run(id),
    CONSTRAINT fk_trc_case FOREIGN KEY (test_case_id) REFERENCES test_case(id)
);

-- Many-to-many: прогони <-> env
CREATE TABLE test_run_environments (
    test_run_id UUID NOT NULL,
    environment_id UUID NOT NULL,
    PRIMARY KEY (test_run_id, environment_id),
    CONSTRAINT fk_tre_run FOREIGN KEY (test_run_id) REFERENCES test_run(id),
    CONSTRAINT fk_tre_env FOREIGN KEY (environment_id) REFERENCES environment(id)
);

-- Результати виконання кейсів у прогоні
CREATE TABLE test_run_case_results (
    id UUID PRIMARY KEY,
    test_run_id UUID NOT NULL,
    test_case_id UUID NOT NULL,
    status VARCHAR(50),
    executed_by UUID,
    comment TEXT,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    CONSTRAINT fk_trcr_run FOREIGN KEY (test_run_id) REFERENCES test_run(id),
    CONSTRAINT fk_trcr_case FOREIGN KEY (test_case_id) REFERENCES test_case(id),
    CONSTRAINT fk_trcr_executed_by FOREIGN KEY (executed_by) REFERENCES app_user(id)
);

-- Аудит результатів кейсів (optional)
CREATE TABLE test_run_case_result_audit (
    id UUID PRIMARY KEY,
    result_id UUID NOT NULL,
    changed_by UUID,
    changed_by_name VARCHAR(255),
    old_status VARCHAR(50),
    new_status VARCHAR(50),
    comment TEXT,
    changed_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_audit_result FOREIGN KEY (result_id) REFERENCES test_run_case_results(id)
);

-- Всі старі таблиці milestone, test_case_instance, test_step_result, test_step_result_audit — ВИДАЛЯЄШ!

-- (Бонус) Індекси для швидких JOIN
CREATE INDEX idx_tr_project ON test_run (project_id);
CREATE INDEX idx_tr_assigned_to ON test_run (assigned_to);
CREATE INDEX idx_tr_version ON test_run (version_id);
CREATE INDEX idx_tr_config ON test_run (configuration_id);
CREATE INDEX idx_trcr_run_case ON test_run_case_results (test_run_id, test_case_id);
CREATE INDEX idx_tre_run ON test_run_environments (test_run_id);
CREATE INDEX idx_trc_run ON test_run_cases (test_run_id);


-- V2__cascade_delete.sql

-- Видаляємо старий зв'язок (без CASCADE)
ALTER TABLE test_suite DROP CONSTRAINT IF EXISTS fk_test_suite_project;
-- Додаємо новий зв'язок з CASCADE
ALTER TABLE test_suite ADD CONSTRAINT fk_test_suite_project
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE;

-- Повторити для інших таблиць:

ALTER TABLE test_case DROP CONSTRAINT IF EXISTS fk_test_case_suite;
ALTER TABLE test_case ADD CONSTRAINT fk_test_case_suite
    FOREIGN KEY (suite_id) REFERENCES test_suite(id) ON DELETE CASCADE;

ALTER TABLE test_run DROP CONSTRAINT IF EXISTS fk_test_run_project;
ALTER TABLE test_run ADD CONSTRAINT fk_test_run_project
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE;

ALTER TABLE environment DROP CONSTRAINT IF EXISTS fk_environment_project;
ALTER TABLE environment ADD CONSTRAINT fk_environment_project
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE;

ALTER TABLE configuration DROP CONSTRAINT IF EXISTS fk_configuration_project;
ALTER TABLE configuration ADD CONSTRAINT fk_configuration_project
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE;

ALTER TABLE version DROP CONSTRAINT IF EXISTS fk_version_project;
ALTER TABLE version ADD CONSTRAINT fk_version_project
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE;

-- Повторити для інших FK, які потрібно зробити каскадними.
