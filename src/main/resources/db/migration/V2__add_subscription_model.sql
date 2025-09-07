CREATE TABLE subscription_plans
(
    id                  UUID NOT NULL,
    name                VARCHAR(1000) NOT NULL,
    billing_period_type VARCHAR(255) NOT NULL,
    CONSTRAINT pk_subscription_plans PRIMARY KEY (id)
);

CREATE TABLE plan_prices
(
    subscription_plan_id UUID NOT NULL,
    amount               DECIMAL(1000, 2) NOT NULL,
    currency             VARCHAR(3) NOT NULL,
    CONSTRAINT pk_plan_prices PRIMARY KEY (subscription_plan_id),
    CONSTRAINT fk_plan_prices_on_subscription_plan FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans (id)
);
COMMENT ON COLUMN plan_prices.currency IS 'ISO 4217 currency code';

CREATE TABLE trial_offerings
(
    subscription_plan_id UUID NOT NULL,
    available            BOOLEAN NOT NULL,
    trial_period         INTEGER,
    CONSTRAINT pk_trial_offerings PRIMARY KEY (subscription_plan_id),
    CONSTRAINT fk_trial_offerings_on_subscription_plan FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans (id),
    CONSTRAINT ck_trial_period_specified_if_available CHECK ( available = (trial_period IS NOT NULL) )
);
COMMENT ON COLUMN trial_offerings.trial_period IS 'Number of days for trial period. Should not be null if available=true';

CREATE TABLE plan_service_usage_limits
(
    subscription_plan_id UUID NOT NULL,
    service_type         VARCHAR(255) NOT NULL,
    "limit"              DECIMAL(1000, 3) NOT NULL,
    CONSTRAINT pk_plan_service_usage_limits PRIMARY KEY (subscription_plan_id, service_type),
    CONSTRAINT fk_plan_service_usage_limits_on_subscription_plan FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans (id)
);

CREATE TABLE plan_service_overuse_prices
(
    subscription_plan_id UUID NOT NULL,
    service_type         VARCHAR(255) NOT NULL,
    billed_usage         DECIMAL(1000, 3) NOT NULL,
    amount               DECIMAL(1000, 2) NOT NULL,
    currency             VARCHAR(3) NOT NULL,
    CONSTRAINT pk_plan_service_overuse_prices PRIMARY KEY (subscription_plan_id, service_type),
    CONSTRAINT fk_plan_service_overuse_prices_on_subscription_plan FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans (id)
);
COMMENT ON COLUMN plan_service_overuse_prices.currency IS 'ISO 4217 currency code';
