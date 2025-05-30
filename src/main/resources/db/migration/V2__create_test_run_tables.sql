-- таблиця запусків
CREATE TABLE test_run (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  project_id UUID NOT NULL,
  status VARCHAR(50),
  started_at TIMESTAMP,
  completed_at TIMESTAMP
);

-- таблиця екземплярів кейсів у конкретному запуску
CREATE TABLE test_case_instance (
  id UUID PRIMARY KEY,
  run_id UUID NOT NULL,
  case_id BIGINT NOT NULL,
  status VARCHAR(50),
  CONSTRAINT fk_run
    FOREIGN KEY(run_id)
    REFERENCES test_run(id)
);

-- таблиця результатів кроків
CREATE TABLE test_step_result (
  id UUID PRIMARY KEY,
  instance_id UUID NOT NULL,
  step_number INT,
  result VARCHAR(50),
  message TEXT,
  CONSTRAINT fk_instance
    FOREIGN KEY(instance_id)
    REFERENCES test_case_instance(id)
);
