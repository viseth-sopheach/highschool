-- V12: Grant VICE_PRINCIPAL day-to-day account administration
-- Deliberately USER_MANAGE only, not ROLE_MANAGE. USER_MANAGE covers
-- creating accounts and toggling status (lock/unlock/disable) — routine
-- admin work. ROLE_MANAGE stays PRINCIPAL-only (via the wildcard in V8)
-- because it can reassign any user's roles, including granting oneself
-- PRINCIPAL — that's a privilege-escalation path, not day-to-day admin,
-- and should stay a single-role capability.

WITH rp AS (
    SELECT r.id AS role_id, p.id AS permission_id, r.name AS role_name, p.name AS perm_name
    FROM roles r CROSS JOIN permissions p
)
INSERT INTO role_permissions (role_id, permission_id)
SELECT role_id, permission_id FROM rp
WHERE role_name = 'VICE_PRINCIPAL' AND perm_name = 'USER_MANAGE'
    ON CONFLICT (role_id, permission_id) DO NOTHING;