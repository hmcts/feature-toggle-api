data "azurerm_key_vault_secret" "admin_username_probate" {
  name         = "admin-username-probate"
  key_vault_id = "${module.feature-toggle-key-vault.key_vault_id}"
  depends_on   = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "admin_password_probate" {
  name         = "admin-password-probate"
  key_vault_id = "${module.feature-toggle-key-vault.key_vault_id}"
  depends_on   = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "editor_username_probate" {
  name         = "editor-username-probate"
  key_vault_id = "${module.feature-toggle-key-vault.key_vault_id}"
  depends_on   = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "editor_password_probate" {
  name         = "editor-password-probate"
  key_vault_id = "${module.feature-toggle-key-vault.key_vault_id}"
  depends_on   = ["module.feature-toggle-key-vault"]
}
