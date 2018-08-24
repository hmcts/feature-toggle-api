data "azurerm_key_vault" "probate_key_vault" {
  name = "${local.probateVaultName}"
  resource_group_name = "${local.probateVaultName}"
}

data "azurerm_key_vault_secret" "probate_admin_username" {
  name = "feature-toggle-admin-username"
  vault_uri = "${data.azurerm_key_vault.probate_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "probate_admin_password" {
  name = "feature-toggle-admin-password"
  vault_uri = "${data.azurerm_key_vault.probate_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "probate_editor_username" {
  name = "feature-toggle-editor-username"
  vault_uri = "${data.azurerm_key_vault.probate_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "probate_editor_password" {
  name = "feature-toggle-editor-password"
  vault_uri = "${data.azurerm_key_vault.probate_key_vault.vault_uri}"
}
