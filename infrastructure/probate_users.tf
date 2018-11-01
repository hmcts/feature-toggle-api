data "azurerm_key_vault_secret" "admin_username_probate" {
  name = "admin-username-probate"
  vault_uri = "${local.permanent_vault_uri}"
  depends_on = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "admin_password_probate" {
  name = "admin-password-probate"
  vault_uri = "${local.permanent_vault_uri}"
  depends_on = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "editor_username_probate" {
  name = "editor-username-probate"
  vault_uri = "${local.permanent_vault_uri}"
  depends_on = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "editor_password_probate" {
  name = "editor-password-probate"
  vault_uri = "${local.permanent_vault_uri}"
  depends_on = ["module.feature-toggle-key-vault"]
}
