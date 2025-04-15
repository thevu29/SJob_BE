CREATE TABLE fields (
                        id VARCHAR(255) PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        description VARCHAR(255)
);

CREATE TABLE field_details (
                               id VARCHAR(255) PRIMARY KEY,
                               field_id VARCHAR(255) NOT NULL,
                               name VARCHAR(255) NOT NULL,
                               FOREIGN KEY (field_id) REFERENCES fields(id)
);

CREATE TABLE jobs (
                      id VARCHAR(255) PRIMARY KEY,
                      recruiter_id VARCHAR(255) NOT NULL,
                      name VARCHAR(255) NOT NULL,
                      description VARCHAR(255) NOT NULL,
                      salary DOUBLE PRECISION NOT NULL,
                      requirement VARCHAR(255) NOT NULL,
                      benefit VARCHAR(255) NOT NULL,
                      deadline DATE NOT NULL,
                      slots INTEGER NOT NULL,
                      type VARCHAR(50) NOT NULL,
                      date DATE NOT NULL,
                      education VARCHAR(255) NOT NULL,
                      experience VARCHAR(255) NOT NULL,
                      close_when_full BOOLEAN NOT NULL DEFAULT false,
                      status VARCHAR(50) NOT NULL DEFAULT 'OPEN'
);

CREATE TABLE job_field (
                           id VARCHAR(255) PRIMARY KEY,
                           job_id VARCHAR(255) NOT NULL,
                           field_detail_id VARCHAR(255) NOT NULL,
                           FOREIGN KEY (job_id) REFERENCES jobs(id),
                           FOREIGN KEY (field_detail_id) REFERENCES field_details(id)
);