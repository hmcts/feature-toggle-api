data "azurerm_key_vault_secret" "admin_username_divorce" {
  name = "admin-username-divorce"
  vault_uri = "${local.permanent_vault_uri}"
  depends_on = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "admin_password_divorce" {
  name = "admin-password-divorce"
  vault_uri = "${local.permanent_vault_uri}"
  depends_on = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "editor_username_divorce" {
  name = "editor-username-divorce"
  vault_uri = "${local.permanent_vault_uri}"
  depends_on = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "editor_password_divorce" {
  name = "editor-password-divorce"
  vault_uri = "${local.permanent_vault_uri}"
  depends_on = ["module.feature-toggle-key-vault"]
}
