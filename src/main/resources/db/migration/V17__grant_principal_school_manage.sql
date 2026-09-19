WITH rp AS (
    SELECT r.id AS role_id, p.id AS permission_id, r.name AS role_name, p.name AS perm_name
    FROM roles r CROSS JOIN permissions p
)
INSERT INTO role_permissions (role_id, permission_id)
SELECT role_id, permission_id FROM rp
WHERE role_name = 'PRINCIPAL'
    ON CONFLICT (role_id, permission_id) DO NOTHING;