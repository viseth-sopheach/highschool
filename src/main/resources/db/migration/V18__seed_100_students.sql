-- 100 STUDENT users + student profiles (codes S-0101..S-0200) for the DEFAULT school.
-- Password hash is the same one V16 uses for the other seeded accounts.

INSERT INTO users (school_id, username, email, password_hash, status)
SELECT s.id,
       format('student%s@school.kh', lpad(g::text, 3, '0')),
       format('student%s@school.kh', lpad(g::text, 3, '0')),
       '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewKNmPeoJ0FoJvSK',
       'ACTIVE'
FROM schools s
         CROSS JOIN generate_series(1, 100) AS g
WHERE s.code = 'DEFAULT';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'STUDENT'
WHERE u.username ~ '^student[0-9]{3}@school\.kh$';

WITH names AS (
    SELECT
        ARRAY['ចាន់','ពេជ្រ','សុខ','មាស','លី','ជា','ឈឹម','ឡុង','អ៊ុក','គង់']            AS fam_km,
    ARRAY['Chan','Pich','Sok','Meas','Ly','Chea','Chhim','Long','Ouk','Kong']       AS fam_en,
    ARRAY['វុទ្ធី','រិទ្ធី','ដារា','សុភ័ក្ត្រ','វិសាល','សោភ័ណ','បូរ៉ា','ចាន់ធី','សម្បត្តិ','ពិសិដ្ឋ'] AS male_km,
    ARRAY['Vuthy','Rithy','Dara','Sophak','Visal','Sophon','Bora','Chanthy','Sambath','Pisith'] AS male_en,
    ARRAY['ស្រីមុំ','ពិសី','ចន្ទដា','ស្រីនាង','សុជាតា','ធីតា','ម៉ាលីន','សុវណ្ណា','ស្រីពៅ','កល្យាណ'] AS female_km,
    ARRAY['Sreymom','Pisey','Chanda','Sreyneang','Sochheata','Thida','Maline','Sovanna','Sreypov','Kalyan'] AS female_en
    )
INSERT INTO students (school_id, user_id, student_code, khmer_name, english_name, dob, gender, phone)
SELECT u.school_id,
       u.id,
       'S-' || lpad((100 + g)::text, 4, '0'),
       n.fam_km[1 + (g % 10)] || ' ' ||
       CASE WHEN g % 2 = 0 THEN n.female_km[1 + ((g / 10) % 10)]
                ELSE n.male_km[1 + ((g / 10) % 10)] END,
       n.fam_en[1 + (g % 10)] || ' ' ||
           CASE WHEN g % 2 = 0 THEN n.female_en[1 + ((g / 10) % 10)]
                ELSE n.male_en[1 + ((g / 10) % 10)] END,
       DATE '2008-01-01' + ((g * 37) % 1800),
       CASE WHEN g % 2 = 0 THEN 'FEMALE' ELSE 'MALE' END,
       '098' || lpad((200000 + g)::text, 6, '0')
FROM generate_series(1, 100) AS g
         CROSS JOIN names n
         JOIN users u ON u.username = format('student%s@school.kh', lpad(g::text, 3, '0'));