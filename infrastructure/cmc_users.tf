data "azurerm_key_vault" "cmc_key_vault" {
  name = "${local.cmcVaultName}"
  resource_group_name = "${local.cmcVaultName}"
}

data "azurerm_key_vault_secret" "cmc_admin_username" {
  name = "feature-toggle-admin-username"
  vault_uri = "${data.azurerm_key_vault.cmc_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "cmc_admin_password" {
  name = "feature-toggle-admin-password"
  vault_uri = "${data.azurerm_key_vault.cmc_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "cmc_editor_username" {
  name = "feature-toggle-editor-username"
  vault_uri = "${data.azurerm_key_vault.cmc_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "cmc_editor_password" {
  name = "feature-toggle-editor-password"
  vault_uri = "${data.azurerm_key_vault.cmc_key_vault.vault_uri}"
}
