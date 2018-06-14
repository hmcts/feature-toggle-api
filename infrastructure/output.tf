output "vaultUri" {
  value = "${module.feature-toggle-key-vault.key_vault_uri}"
}

output "vaultName" {
  value = "${local.vaultName}"
}

output "microserviceName" {
  value = "${var.component}"
}

output "idam_url_for_tests" {
  value = "${var.idam_api_url}"
}

output "use_idam_testing_support" {
  value = "${var.use_idam_testing_support}"
}
