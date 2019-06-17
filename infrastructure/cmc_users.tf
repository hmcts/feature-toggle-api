data "azurerm_key_vault_secret" "admin_username_cmc" {
  name         = "admin-username-cmc"
  key_vault_id = "${module.feature-toggle-key-vault.key_vault_id}"
  depends_on   = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "admin_password_cmc" {
  name         = "admin-password-cmc"
  key_vault_id = "${module.feature-toggle-key-vault.key_vault_id}"
  depends_on   = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "editor_username_cmc" {
  name         = "editor-username-cmc"
  key_vault_id = "${module.feature-toggle-key-vault.key_vault_id}"
  depends_on   = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "editor_password_cmc" {
  name         = "editor-password-cmc"
  key_vault_id = "${module.feature-toggle-key-vault.key_vault_id}"
  depends_on   = ["module.feature-toggle-key-vault"]
}
