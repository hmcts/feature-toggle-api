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
