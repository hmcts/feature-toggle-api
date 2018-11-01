# Secrets for tests are stored in permanent (long-lived) Azure Key Vault instances.
# With the exception of (s)preview all Vault instances are long-lived. For preview, however,
# test secrets (not created during deployment) need to be copied over from a permanent vault -
# that's what the code below does.
data "azurerm_key_vault_secret" "source-test-admin-user" {
  name = "admin-username-test"
  vault_uri = "${local.permanent_vault_uri}"
  depends_on = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "source-test-admin-password" {
  name = "admin-password-test"
  vault_uri = "${local.permanent_vault_uri}"
  depends_on = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "source-test-editor-user" {
  name = "editor-username-test"
  vault_uri = "${local.permanent_vault_uri}"
  depends_on = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "source-test-editor-password" {
  name = "editor-password-test"
  vault_uri = "${local.permanent_vault_uri}"
  depends_on = ["module.feature-toggle-key-vault"]
}

resource "azurerm_key_vault_secret" "test-admin-user" {
  name      = "admin-username-test"
  value     = "${local.test_admin_user}"
  vault_uri = "${module.feature-toggle-key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "test-admin-password" {
  name      = "admin-password-test"
  value     = "${local.test_admin_password}"
  vault_uri = "${module.feature-toggle-key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "test-editor-user" {
  name      = "editor-username-test"
  value     = "${local.test_editor_user}"
  vault_uri = "${module.feature-toggle-key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "test-editor-password" {
  name      = "editor-password-test"
  value     = "${local.test_editor_password}"
  vault_uri = "${module.feature-toggle-key-vault.key_vault_uri}"
}
