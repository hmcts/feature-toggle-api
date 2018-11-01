data "azurerm_key_vault_secret" "admin_username_cmc" {
  name = "admin-username-cmc"
  vault_uri = "${local.permanent_vault_uri}"
  depends_on = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "admin_password_cmc" {
  name = "admin-password-cmc"
  vault_uri = "${local.permanent_vault_uri}"
  depends_on = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "editor_username_cmc" {
  name = "editor-username-cmc"
  vault_uri = "${local.permanent_vault_uri}"
  depends_on = ["module.feature-toggle-key-vault"]
}

data "azurerm_key_vault_secret" "editor_password_cmc" {
  name = "editor-password-cmc"
  vault_uri = "${local.permanent_vault_uri}"
  depends_on = ["module.feature-toggle-key-vault"]
}
