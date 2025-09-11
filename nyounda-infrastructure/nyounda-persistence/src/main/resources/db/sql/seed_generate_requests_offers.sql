DO
$$
    DECLARE
        -- five selected ROOT categories (from your CSV)
        cat_roots     uuid[] := ARRAY [
            '2a6d0e2c-0001-0000-0000-000000000001', -- Petits travaux
            '2a6d0e2c-0002-0000-0000-000000000002', -- Aide ménagère
            '2a6d0e2c-0003-0000-0000-000000000003', -- Cours & Assistance
            '2a6d0e2c-0004-0000-0000-000000000004', -- Soins & bien-être
            '2a6d0e2c-0005-0000-0000-000000000005' -- Transport
            ];
        cli           RECORD;
        i             int;
        chosen_root   uuid;
        chosen_sub    uuid;
        sr_id         uuid;
        prov          RECORD;
        skill_cat_ids uuid[];
        req_ids       uuid[];
        off_id        uuid;
        amt           numeric;
    BEGIN
        -----------------------------------------------------------------
        -- 1) Create 10 requests per client (25 clients => 250 requests)
        -----------------------------------------------------------------
        FOR cli IN
            SELECT id, full_name
            FROM app_user
            WHERE roles::text LIKE '%CLIENT%'
            ORDER BY full_name
            LOOP
                FOR i IN 1..10
                    LOOP
                        chosen_root := cat_roots[1 + floor(random() * 5)::int];

                        SELECT c.id
                        INTO chosen_sub
                        FROM category c
                        WHERE c.parent_id = chosen_root
                        ORDER BY random()
                        LIMIT 1;

                        IF chosen_sub IS NULL THEN
                            chosen_sub := chosen_root;
                        END IF;

                        sr_id := gen_random_uuid();

                        INSERT INTO service_request(id, user_id, category_id, subcategory_id, title, description,
                                                    address,
                                                    status, created_at, updated_at, chosen_offer_id)
                        VALUES (sr_id,
                                cli.id,
                                chosen_root,
                                chosen_sub,
                                'Demande ' || cli.full_name || ' #' || i,
                                'Seeded (local/test) – auto-generated request.',
                                'Paris ' || (10 + (i % 10)),
                                'PENDING',
                                now() - ((i * (1 + floor(random() * 6)::int)) || ' hours')::interval,
                                now(),
                                NULL);
                    END LOOP;
            END LOOP;

        -----------------------------------------------------------------
        -- 2) For each provider, add PENDING offers to matching requests
        -----------------------------------------------------------------
        FOR prov IN
            SELECT p.id AS provider_id, p.user_id, p.full_name
            FROM provider p
            ORDER BY p.full_name
            LOOP
                SELECT ARRAY_AGG(ps.category_id)
                INTO skill_cat_ids
                FROM provider_skill ps
                WHERE ps.provider_id = prov.provider_id;

                IF skill_cat_ids IS NULL OR array_length(skill_cat_ids, 1) = 0 THEN
                    CONTINUE;
                END IF;

                -- up to 5 offers per skill on earliest requests
                FOR i IN 1..array_length(skill_cat_ids, 1)
                    LOOP
                        req_ids := ARRAY(
                                SELECT id
                                FROM service_request
                                WHERE category_id = skill_cat_ids[i]
                                ORDER BY created_at
                                LIMIT 5
                                   );

                        IF req_ids IS NULL THEN CONTINUE; END IF;

                        FOREACH sr_id IN ARRAY req_ids
                            LOOP
                                off_id := gen_random_uuid();
                                amt := round((10000 + (random() * 20000))::numeric, 2);

                                INSERT INTO offer(id, request_id, user_id, mode, amount, expenses, message, status,
                                                  created_at, assigned_at, decline_reason)
                                VALUES (off_id,
                                        sr_id,
                                        prov.user_id,
                                        CASE WHEN random() < 0.6 THEN 'HOURLY' ELSE 'FIXED' END,
                                        jsonb_build_object('amount', amt, 'currency', 'XAF'),
                                        NULL,
                                        'Disponible rapidement (seed).',
                                        'PENDING',
                                        now(),
                                        NULL,
                                        NULL);
                            END LOOP;
                    END LOOP;

                -- ensure at least one offer per provider
                PERFORM 1 FROM offer o WHERE o.user_id = prov.user_id LIMIT 1;
                IF NOT FOUND THEN
                    SELECT id
                    INTO sr_id
                    FROM service_request
                    WHERE category_id = ANY (skill_cat_ids)
                    ORDER BY created_at
                    LIMIT 1;

                    IF sr_id IS NOT NULL THEN
                        INSERT INTO offer(id, request_id, user_id, mode, amount, expenses, message, status,
                                          created_at, assigned_at, decline_reason)
                        VALUES (gen_random_uuid(),
                                sr_id,
                                prov.user_id,
                                'HOURLY',
                                jsonb_build_object('amount', 12000, 'currency', 'XAF'),
                                NULL,
                                'First offer (safety).',
                                'PENDING',
                                now(),
                                NULL,
                                NULL);
                    END IF;
                END IF;
            END LOOP;
    END
$$;
