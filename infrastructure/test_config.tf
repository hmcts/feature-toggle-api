resource "azurerm_key_vault_secret" "test-s2s-url" {
  name      = "test-s2s-url"
  value     = "${local.s2s_url}"
  vault_uri = "${module.feature-toggle-key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "test-s2s-name" {
  name      = "test-s2s-name"
  value     = "feature_toggle_tests"
  vault_uri = "${module.feature-toggle-key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "test-s2s-secret" {
  name      = "test-s2s-secret"
  value     = "${data.vault_generic_secret.tests_s2s_secret.data["value"]}"
  vault_uri = "${module.feature-toggle-key-vault.key_vault_uri}"
}
