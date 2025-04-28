ALTER TABLE job_seeker_service.job_seekers
    ALTER COLUMN gender DROP NOT NULL,
    ALTER COLUMN address DROP NOT NULL,
    ALTER COLUMN phone DROP NOT NULL;