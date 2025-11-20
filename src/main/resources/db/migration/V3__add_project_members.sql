-- ========================
-- V2: Додавання системи ролей для проектів
-- ========================

-- Таблиця членів проекту з ролями
CREATE TABLE project_member (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'MANAGER', 'TESTER', 'VIEWER')),
    CONSTRAINT fk_pm_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    CONSTRAINT fk_pm_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    CONSTRAINT uk_pm_project_user UNIQUE (project_id, user_id)
);

-- Індекси для оптимізації запитів
CREATE INDEX idx_pm_project_id ON project_member(project_id);
CREATE INDEX idx_pm_user_id ON project_member(user_id);
CREATE INDEX idx_pm_role ON project_member(role);

-- Коментарі для документації
COMMENT ON TABLE project_member IS 'Зв''язок користувачів з проектами та їх ролі';
COMMENT ON COLUMN project_member.role IS 'Роль користувача в проекті: ADMIN - повний доступ, MANAGER - управління тестами, TESTER - виконання тестів, VIEWER - тільки читання';