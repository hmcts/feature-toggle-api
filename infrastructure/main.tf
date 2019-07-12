provider "azurerm" {
  version = "=1.22.1"
}

# Make sure the resource group exists
resource "azurerm_resource_group" "rg" {
  name     = "${var.product}-${var.component}-${var.env}"
  location = "${var.location_app}"
}

locals {
  ase_name  = "core-compute-${var.env}"
  s2s_url   = "http://rpe-service-auth-provider-${var.env}.service.${local.ase_name}.internal"
  vaultName = "${var.product}-ft-api-${var.env}"

  db_connection_options = "?sslmode=require"

  test_admin_user      = "${data.azurerm_key_vault_secret.source-test-admin-user.value}"
  test_admin_password  = "${data.azurerm_key_vault_secret.source-test-admin-password.value}"
  test_editor_user     = "${data.azurerm_key_vault_secret.source-test-editor-user.value}"
  test_editor_password = "${data.azurerm_key_vault_secret.source-test-editor-password.value}"

  sku_size = "${var.env == "prod" || var.env == "sprod" || var.env == "aat" ? "I2" : "I1"}"
}

module "feature-toggle-db" {
  source          = "git@github.com:hmcts/cnp-module-postgres?ref=master"
  product         = "${var.product}-${var.component}-db"
  location        = "${var.location_db}"
  env             = "${var.env}"
  database_name   = "feature_toggle"
  postgresql_user = "feature_toggler"
  sku_name        = "GP_Gen5_2"
  sku_tier        = "GeneralPurpose"
  common_tags     = "${var.common_tags}"
  subscription    = "${var.subscription}"
}

module "feature-toggle-api" {
  source               = "git@github.com:hmcts/cnp-module-webapp?ref=master"
  product              = "${var.product}-${var.component}"
  location             = "${var.location_app}"
  env                  = "${var.env}"
  ilbIp                = "${var.ilbIp}"
  resource_group_name  = "${azurerm_resource_group.rg.name}"
  subscription         = "${var.subscription}"
  capacity             = "${var.capacity}"
  additional_host_name = "${var.env != "preview" ? var.external_host_name : "null"}"
  is_frontend          = "${var.env != "preview" ? 1 : 0}"
  common_tags          = "${var.common_tags}"
  asp_name             = "${var.product}-${var.component}-${var.env}"
  asp_rg               = "${var.product}-${var.component}-${var.env}"
  instance_size        = "${local.sku_size}"

  app_settings = {
    FEATURES_DB_HOST         = "${module.feature-toggle-db.host_name}"
    FEATURES_DB_PORT         = "${module.feature-toggle-db.postgresql_listen_port}"
    FEATURES_DB_USER_NAME    = "${module.feature-toggle-db.user_name}"
    FEATURES_DB_PASSWORD     = "${module.feature-toggle-db.postgresql_password}"
    FEATURES_DB_NAME         = "${module.feature-toggle-db.postgresql_database}"
    FEATURES_DB_CONN_OPTIONS = "${local.db_connection_options}"
    FLYWAY_URL               = "jdbc:postgresql://${module.feature-toggle-db.host_name}:${module.feature-toggle-db.postgresql_listen_port}/${module.feature-toggle-db.postgresql_database}${local.db_connection_options}"
    FLYWAY_USER              = "${module.feature-toggle-db.user_name}"
    FLYWAY_PASSWORD          = "${module.feature-toggle-db.postgresql_password}"
    ADMIN_USERNAME_TEST      = "${local.test_admin_user}"
    ADMIN_PASSWORD_TEST      = "${local.test_admin_password}"
    EDITOR_USERNAME_TEST     = "${local.test_editor_user}"
    EDITOR_PASSWORD_TEST     = "${local.test_editor_password}"
    ADMIN_USERNAME_CMC       = "${data.azurerm_key_vault_secret.admin_username_cmc.value}"
    ADMIN_PASSWORD_CMC       = "${data.azurerm_key_vault_secret.admin_password_cmc.value}"
    EDITOR_USERNAME_CMC      = "${data.azurerm_key_vault_secret.editor_username_cmc.value}"
    EDITOR_PASSWORD_CMC      = "${data.azurerm_key_vault_secret.editor_password_cmc.value}"

    ADMIN_USERNAME_DIVORCE  = "${data.azurerm_key_vault_secret.admin_username_divorce.value}"
    ADMIN_PASSWORD_DIVORCE  = "${data.azurerm_key_vault_secret.admin_password_divorce.value}"
    EDITOR_USERNAME_DIVORCE = "${data.azurerm_key_vault_secret.editor_username_divorce.value}"
    EDITOR_PASSWORD_DIVORCE = "${data.azurerm_key_vault_secret.editor_password_divorce.value}"

    ADMIN_USERNAME_PROBATE  = "${data.azurerm_key_vault_secret.admin_username_probate.value}"
    ADMIN_PASSWORD_PROBATE  = "${data.azurerm_key_vault_secret.admin_password_probate.value}"
    EDITOR_USERNAME_PROBATE = "${data.azurerm_key_vault_secret.editor_username_probate.value}"
    EDITOR_PASSWORD_PROBATE = "${data.azurerm_key_vault_secret.editor_password_probate.value}"

    // silence the "bad implementation" logs
    LOGBACK_REQUIRE_ALERT_LEVEL = false
    LOGBACK_REQUIRE_ERROR_CODE  = false
  }
}

# region save DB details to Azure Key Vault
module "feature-toggle-key-vault" {
  source              = "git@github.com:hmcts/cnp-module-key-vault?ref=master"
  name                = "${local.vaultName}"
  product             = "${var.product}"
  env                 = "${var.env}"
  tenant_id           = "${var.tenant_id}"
  object_id           = "${var.jenkins_AAD_objectId}"
  resource_group_name = "${azurerm_resource_group.rg.name}"
  # dcd_cc-dev group object ID
  product_group_object_id = "38f9dea6-e861-4a50-9e73-21e64f563537"
  common_tags             = "${var.common_tags}"
}

resource "azurerm_key_vault_secret" "POSTGRES-USER" {
  name         = "${var.component}-POSTGRES-USER"
  value        = "${module.feature-toggle-db.user_name}"
  key_vault_id = "${module.feature-toggle-key-vault.key_vault_id}"
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name         = "${var.component}-POSTGRES-PASS"
  value        = "${module.feature-toggle-db.postgresql_password}"
  key_vault_id = "${module.feature-toggle-key-vault.key_vault_id}"
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST" {
  name         = "${var.component}-POSTGRES-HOST"
  value        = "${module.feature-toggle-db.host_name}"
  key_vault_id = "${module.feature-toggle-key-vault.key_vault_id}"
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT" {
  name         = "${var.component}-POSTGRES-PORT"
  value        = "${module.feature-toggle-db.postgresql_listen_port}"
  key_vault_id = "${module.feature-toggle-key-vault.key_vault_id}"
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE" {
  name         = "${var.component}-POSTGRES-DATABASE"
  value        = "${module.feature-toggle-db.postgresql_database}"
  key_vault_id = "${module.feature-toggle-key-vault.key_vault_id}"
}
# endregion
