CREATE TABLE tenants
(
    id   UUID NOT NULL,
    name VARCHAR(1000) NOT NULL,
    CONSTRAINT pk_tenants PRIMARY KEY (id)
);

CREATE TABLE tenant_users
(
    tenant_id UUID NOT NULL,
    user_sub  VARCHAR(255) NOT NULL,
    CONSTRAINT pk_tenant_users PRIMARY KEY (tenant_id, user_sub),
    CONSTRAINT fk_tenant_users__tenant_id FOREIGN KEY (tenant_id) REFERENCES tenants (id)
);
