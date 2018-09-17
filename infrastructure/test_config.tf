# Secrets for tests are stored in permanent (long-lived) Azure Key Vault instances.
# With the exception of (s)preview all Vault instances are long-lived. For preview, however,
# test secrets (not created during deployment) need to be copied over from a permanent vault -
# that's what the code below does.
data "azurerm_key_vault_secret" "source-test-admin-user" {
  name = "test-admin-user"
  vault_uri = "${local.permanent_vault_uri}"
}

data "azurerm_key_vault_secret" "source-test-admin-password" {
  name = "test-admin-password"
  vault_uri = "${local.permanent_vault_uri}"
}

data "azurerm_key_vault_secret" "source-test-editor-user" {
  name = "test-editor-user"
  vault_uri = "${local.permanent_vault_uri}"
}

data "azurerm_key_vault_secret" "source-test-editor-password" {
  name = "test-editor-password"
  vault_uri = "${local.permanent_vault_uri}"
}

resource "azurerm_key_vault_secret" "test-admin-user" {
  name      = "test-admin-user"
  value     = "${local.test_admin_user}"
  vault_uri = "${module.feature-toggle-key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "test-admin-password" {
  name      = "test-admin-password"
  value     = "${local.test_admin_password}"
  vault_uri = "${module.feature-toggle-key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "test-editor-user" {
  name      = "test-editor-user"
  value     = "${local.test_editor_user}"
  vault_uri = "${module.feature-toggle-key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "test-editor-password" {
  name      = "test-editor-password"
  value     = "${local.test_editor_password}"
  vault_uri = "${module.feature-toggle-key-vault.key_vault_uri}"
}
