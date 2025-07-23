INSERT INTO dog (id, name, description)
VALUES (97, 'Rocky', 'A brown Chihuahua known for being protective11.'),
       (87, 'Bailey', 'A tan Dachshund known for being playful.'),
       (89, 'Charlie', 'A black Bulldog known for being curious.'),
       (67, 'Cooper', 'A tan Boxer known for being affectionate.'),
       (73, 'Max', 'A brindle Dachshund known for being energetic.'),
       (3, 'Buddy', 'A Poodle known for being calm.'),
       (93, 'Duke', 'A white German Shepherd known for being friendly.'),
       (63, 'Jasper', 'A grey Shih Tzu known for being protective.'),
       (69, 'Toby', 'A grey Doberman known for being playful.'),
       (101, 'Nala', 'A spotted German Shepherd known for being loyal.'),
       (61, 'Penny', 'A white Great Dane known for being protective.'),
       (1, 'Bella', 'A golden Poodle known for being calm.'),
       (91, 'Willow', 'A brindle Great Dane known for being calm.'),
       (5, 'Daisy', 'A spotted Poodle known for being affectionate.'),
       (95, 'Mia', 'A grey Great Dane known for being loyal.'),
       (71, 'Molly', 'A golden Chihuahua known for being curious.'),
       (65, 'Ruby', 'A white Great Dane known for being protective.'),
       (45, 'Prancer', 'A demonic, neurotic, man hating, animal hating, children hating dogs that look like a gremlin.')
ON CONFLICT (id) DO UPDATE
    SET name        = EXCLUDED.name,
        description = EXCLUDED.description;