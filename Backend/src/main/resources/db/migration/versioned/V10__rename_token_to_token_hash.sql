ALTER TABLE password_reset_tokens RENAME COLUMN token TO token_hash;

-- Invalidate any existing plain-text tokens
UPDATE password_reset_tokens SET used = TRUE;
