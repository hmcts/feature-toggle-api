data "vault_generic_secret" "test-admin-user" {
  path = "secret/${var.vault_section}/cc/ff4j/webconsole/test-admin-user"
}

data "vault_generic_secret" "test-admin-password" {
  path = "secret/${var.vault_section}/cc/ff4j/webconsole/test-admin-password"
}

data "vault_generic_secret" "test-editor-user" {
  path = "secret/${var.vault_section}/cc/ff4j/webconsole/test-editor-user"
}

data "vault_generic_secret" "test-editor-password" {
  path = "secret/${var.vault_section}/cc/ff4j/webconsole/test-editor-password"
}

data "vault_generic_secret" "test-read-user" {
  path = "secret/${var.vault_section}/cc/ff4j/webconsole/test-read-user"
}

data "vault_generic_secret" "test-read-password" {
  path = "secret/${var.vault_section}/cc/ff4j/webconsole/test-read-password"
}
