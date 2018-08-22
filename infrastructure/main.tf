provider "azurerm" {}

provider "vault" {
  address = "https://vault.reform.hmcts.net:6200"
}

# Make sure the resource group exists
resource "azurerm_resource_group" "rg" {
  name     = "${var.product}-${var.component}-${var.env}"
  location = "${var.location_app}"
}

locals {
  ase_name               = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"

  local_env              = "${(var.env == "preview" || var.env == "spreview") ? (var.env == "preview" ) ? "aat" : "saat" : var.env}"
  local_ase              = "${(var.env == "preview" || var.env == "spreview") ? (var.env == "preview" ) ? "core-compute-aat" : "core-compute-saat" : local.ase_name}"

  s2s_url                = "http://rpe-service-auth-provider-${local.local_env}.service.${local.local_ase}.internal"

  previewVaultName       = "${var.product}-ft-api"
  nonPreviewVaultName    = "${var.product}-ft-api-${var.env}"
  vaultName              = "${(var.env == "preview" || var.env == "spreview") ? local.previewVaultName : local.nonPreviewVaultName}"

  cmcPreviewVaultName = "cmc-aat"
  cmcNonPreviewVaultName = "cmc-${var.env}"
  cmcVaultName = "${(var.env == "preview" || var.env == "spreview") ? local.cmcPreviewVaultName : local.cmcNonPreviewVaultName}"

  divorcePreviewVaultName = "divorce-aat"
  divorceNonPreviewVaultName = "divorce-${var.env}"
  divorceVaultName = "${(var.env == "preview" || var.env == "spreview") ? local.divorcePreviewVaultName : local.divorceNonPreviewVaultName}"

  db_connection_options  = "?ssl=true"

  test_admin_user        = "${data.vault_generic_secret.test-admin-user.data["value"]}"
  test_admin_password    = "${data.vault_generic_secret.test-admin-password.data["value"]}"
  test_editor_user       = "${data.vault_generic_secret.test-editor-user.data["value"]}"
  test_editor_password   = "${data.vault_generic_secret.test-editor-password.data["value"]}"
}

module "feature-toggle-db" {
  source              = "git@github.com:hmcts/moj-module-postgres?ref=master"
  product             = "${var.product}-${var.component}-db"
  location            = "${var.location_db}"
  env                 = "${var.env}"
  database_name       = "feature_toggle"
  postgresql_user     = "feature_toggler"
  sku_name            = "GP_Gen5_2"
  sku_tier            = "GeneralPurpose"
  common_tags         = "${var.common_tags}"
}

module "feature-toggle-api" {
  source               = "git@github.com:hmcts/moj-module-webapp?ref=master"
  product              = "${var.product}-${var.component}"
  location             = "${var.location_app}"
  env                  = "${var.env}"
  ilbIp                = "${var.ilbIp}"
  resource_group_name  = "${azurerm_resource_group.rg.name}"
  subscription         = "${var.subscription}"
  capacity             = "${var.capacity}"
  additional_host_name = "${var.env != "preview" ? var.external_host_name : "null"}"
  is_frontend          = "${var.env != "preview" ? 1: 0}"
  common_tags          = "${var.common_tags}"

  app_settings = {
    FEATURES_DB_HOST            = "${module.feature-toggle-db.host_name}"
    FEATURES_DB_PORT            = "${module.feature-toggle-db.postgresql_listen_port}"
    FEATURES_DB_USER_NAME       = "${module.feature-toggle-db.user_name}"
    FEATURES_DB_PASSWORD        = "${module.feature-toggle-db.postgresql_password}"
    FEATURES_DB_NAME            = "${module.feature-toggle-db.postgresql_database}"
    FEATURES_DB_CONN_OPTIONS    = "${local.db_connection_options}"
    FLYWAY_URL                  = "jdbc:postgresql://${module.feature-toggle-db.host_name}:${module.feature-toggle-db.postgresql_listen_port}/${module.feature-toggle-db.postgresql_database}${local.db_connection_options}"
    FLYWAY_USER                 = "${module.feature-toggle-db.user_name}"
    FLYWAY_PASSWORD             = "${module.feature-toggle-db.postgresql_password}"
    TEST_ADMIN_USERNAME         = "${local.test_admin_user}"
    TEST_ADMIN_PASSWORD         = "${local.test_admin_password}"
    TEST_EDITOR_USERNAME        = "${local.test_editor_user}"
    TEST_EDITOR_PASSWORD        = "${local.test_editor_password}"
    CMC_ADMIN_USERNAME          = "${data.azurerm_key_vault_secret.cmc_admin_username.value}"
    CMC_ADMIN_PASSWORD          = "${data.azurerm_key_vault_secret.cmc_admin_password.value}"
    CMC_EDITOR_USERNAME         = "${data.azurerm_key_vault_secret.cmc_editor_username.value}"
    CMC_EDITOR_PASSWORD         = "${data.azurerm_key_vault_secret.cmc_editor_password.value}"
    DIVORCE_ADMIN_USERNAME      = "${data.azurerm_key_vault_secret.divorce_admin_username.value}"
    DIVORCE_ADMIN_PASSWORD      = "${data.azurerm_key_vault_secret.divorce_admin_password.value}"
    DIVORCE_EDITOR_USERNAME     = "${data.azurerm_key_vault_secret.divorce_editor_username.value}"
    DIVORCE_EDITOR_PASSWORD     = "${data.azurerm_key_vault_secret.divorce_editor_password.value}"
    // silence the "bad implementation" logs
    LOGBACK_REQUIRE_ALERT_LEVEL = false
    LOGBACK_REQUIRE_ERROR_CODE  = false
  }
}

# region save DB details to Azure Key Vault
module "feature-toggle-key-vault" {
  source              = "git@github.com:hmcts/moj-module-key-vault?ref=master"
  name                = "${local.vaultName}"
  product             = "${var.product}"
  env                 = "${var.env}"
  tenant_id           = "${var.tenant_id}"
  object_id           = "${var.jenkins_AAD_objectId}"
  resource_group_name = "${azurerm_resource_group.rg.name}"
  # dcd_cc-dev group object ID
  product_group_object_id = "38f9dea6-e861-4a50-9e73-21e64f563537"
}

resource "azurerm_key_vault_secret" "POSTGRES-USER" {
  name      = "${var.component}-POSTGRES-USER"
  value     = "${module.feature-toggle-db.user_name}"
  vault_uri = "${module.feature-toggle-key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name      = "${var.component}-POSTGRES-PASS"
  value     = "${module.feature-toggle-db.postgresql_password}"
  vault_uri = "${module.feature-toggle-key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST" {
  name      = "${var.component}-POSTGRES-HOST"
  value     = "${module.feature-toggle-db.host_name}"
  vault_uri = "${module.feature-toggle-key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT" {
  name      = "${var.component}-POSTGRES-PORT"
  value     = "${module.feature-toggle-db.postgresql_listen_port}"
  vault_uri = "${module.feature-toggle-key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE" {
  name      = "${var.component}-POSTGRES-DATABASE"
  value     = "${module.feature-toggle-db.postgresql_database}"
  vault_uri = "${module.feature-toggle-key-vault.key_vault_uri}"
}
# endregion
