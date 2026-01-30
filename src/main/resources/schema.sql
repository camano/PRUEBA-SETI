
CREATE TABLE IF NOT EXISTS franchises (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS branches (
    id VARCHAR(20) PRIMARY KEY,
    franchise_id VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT fk_franchise
        FOREIGN KEY (franchise_id)
        REFERENCES franchises(id)
);

CREATE TABLE IF NOT EXISTS products (
    id VARCHAR(20) PRIMARY KEY,
    branch_id VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    stock INT NOT NULL,
    CONSTRAINT fk_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches(id)
);
CREATE TABLE IF NOT EXISTS sequences (
    name VARCHAR(50) PRIMARY KEY,
    value BIGINT NOT NULL
);

INSERT INTO sequences (name, value)
VALUES ('franchise', 0),('branch', 0),('product', 0)
ON CONFLICT (name) DO NOTHING;