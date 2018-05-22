-- Main Table to store Features
CREATE TABLE FF4J_FEATURES (
  "feat_uid" VARCHAR(100),
  "enable" INTEGER NOT NULL,
  "description" VARCHAR(1000),
  "strategy" VARCHAR(1000),
  "expression" VARCHAR(255),
  "groupname" VARCHAR(100),
  PRIMARY KEY("feat_uid")
);

-- Roles to store ACL, FK to main table
CREATE TABLE FF4J_ROLES (
  "feat_uid" VARCHAR(100) REFERENCES FF4J_FEATURES("feat_uid"),
  "role_name" VARCHAR(100),
  PRIMARY KEY("feat_uid", "role_name")
);

-- Feature Internal Custom Properties
CREATE TABLE FF4J_CUSTOM_PROPERTIES (
  "property_id" VARCHAR(100) NOT NULL,
  "clazz" VARCHAR(255) NOT NULL,
  "currentvalue" VARCHAR(255),
  "fixedvalues" VARCHAR(1000),
  "description" VARCHAR(1000),
  "feat_uid" VARCHAR(100) REFERENCES FF4J_FEATURES("feat_uid"),
  PRIMARY KEY("property_id", "feat_uid")
);

-- @PropertyStore (edit general properties)
CREATE TABLE FF4J_PROPERTIES (
  "property_id" VARCHAR(100) NOT NULL,
  "clazz" VARCHAR(255) NOT NULL,
  "currentvalue" VARCHAR(255),
  "fixedvalues" VARCHAR(1000),
  "description" VARCHAR(1000),
  PRIMARY KEY("property_id")
);

-- @see JdbcEventRepository (audit event)
CREATE TABLE FF4J_AUDIT (
  "evt_uuid" VARCHAR(40) NOT NULL,
  "evt_time" TIMESTAMP NOT NULL,
  "evt_type" VARCHAR(30) NOT NULL,
  "evt_name" VARCHAR(100) NOT NULL,
  "evt_action" VARCHAR(100) NOT NULL,
  "evt_hostname" VARCHAR(100) NOT NULL,
  "evt_source" VARCHAR(30) NOT NULL,
  "evt_duration" INTEGER,
  "evt_user" VARCHAR(30),
  "evt_value" VARCHAR(100),
  "evt_keys" VARCHAR(255),
  PRIMARY KEY("evt_uuid", "evt_time")
);
