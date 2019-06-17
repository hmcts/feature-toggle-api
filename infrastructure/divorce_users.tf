data "azurerm_key_vault_secret" "admin_username_divorce" {
  name         = "admin-username-divorce"
  key_vault_id = "${module.feature-toggle-key-vault.key_vault_id}"
  depends_on   = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "admin_password_divorce" {
  name         = "admin-password-divorce"
  key_vault_id = "${module.feature-toggle-key-vault.key_vault_id}"
  depends_on   = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "editor_username_divorce" {
  name         = "editor-username-divorce"
  key_vault_id = "${module.feature-toggle-key-vault.key_vault_id}"
  depends_on   = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "editor_password_divorce" {
  name         = "editor-password-divorce"
  key_vault_id = "${module.feature-toggle-key-vault.key_vault_id}"
  depends_on   = ["module.feature-toggle-key-vault"]
}
