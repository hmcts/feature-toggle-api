data "azurerm_key_vault" "divorce_key_vault" {
  name = "${local.divorceVaultName}"
  resource_group_name = "${local.divorceVaultName}"
}

data "azurerm_key_vault_secret" "divorce_admin_username" {
  name = "feature-toggle-admin-username"
  vault_uri = "${data.azurerm_key_vault.divorce_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "divorce_admin_password" {
  name = "feature-toggle-admin-password"
  vault_uri = "${data.azurerm_key_vault.divorce_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "divorce_editor_username" {
  name = "feature-toggle-editor-username"
  vault_uri = "${data.azurerm_key_vault.divorce_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "divorce_editor_password" {
  name = "feature-toggle-editor-password"
  vault_uri = "${data.azurerm_key_vault.divorce_key_vault.vault_uri}"
}
