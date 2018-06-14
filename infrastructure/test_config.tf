data "vault_generic_secret" "test-admin-user" {
  path = "secret/${var.vault_section}/cc/ff4j/webconsole/test-admin-user"
}

data "vault_generic_secret" "test-admin-password" {
  path = "secret/${var.vault_section}/cc/ff4j/webconsole/test-admin-password"
}

data "vault_generic_secret" "test-editor-user" {
  path = "secret/${var.vault_section}/cc/ff4j/webconsole/test-editor-user"
}

data "vault_generic_secret" "test-editor-password" {
  path = "secret/${var.vault_section}/cc/ff4j/webconsole/test-editor-password"
}

data "vault_generic_secret" "test-read-user" {
  path = "secret/${var.vault_section}/cc/ff4j/webconsole/test-read-user"
}

data "vault_generic_secret" "test-read-password" {
  path = "secret/${var.vault_section}/cc/ff4j/webconsole/test-read-password"
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

resource "azurerm_key_vault_secret" "test-read-user" {
  name      = "test-read-user"
  value     = "${local.test_read_user}"
  vault_uri = "${module.feature-toggle-key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "test-read-password" {
  name      = "test-read-password"
  value     = "${local.test_read_password}"
  vault_uri = "${module.feature-toggle-key-vault.key_vault_uri}"
}

data "vault_generic_secret" "test_idam_client_secret" {
  path = "secret/${var.vault_section}/ccidam/idam-api/oauth2/client-secrets/cmc-citizen"
}

resource "azurerm_key_vault_secret" "idam-client-secret-for-tests" {
  name      = "idam-client-secret-for-tests"
  value     = "${data.vault_generic_secret.test_idam_client_secret.data["value"]}"
  vault_uri = "${module.feature-toggle-key-vault.key_vault_uri}"
}

#region IdAM test user's password

resource "random_string" "idam_password" {
  length = 16
  special = false
}

# This is set only for environments where IdAM testing support is on.
# In other environments (e.g. prod) real IdAM password has to be manually set in Azure Vault
resource "azurerm_key_vault_secret" "idam_password_for_tests" {
  name      = "idam-password-for-tests"
  value     = "${random_string.idam_password.result}"
  vault_uri = "${module.feature-toggle-key-vault.key_vault_uri}"
  count     = "${var.use_idam_testing_support == "true" ? 1 : 0}"
}
