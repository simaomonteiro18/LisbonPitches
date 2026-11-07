DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint c
        JOIN pg_attribute a ON a.attrelid = c.conrelid AND a.attnum = ANY (c.conkey)
        WHERE c.conrelid = 'public.users'::regclass
          AND c.contype = 'u'
          AND a.attname = 'email'
          AND array_length(c.conkey, 1) = 1
    ) THEN
ALTER TABLE public.users ADD CONSTRAINT uk_users_email UNIQUE (email);
END IF;
END
$$;