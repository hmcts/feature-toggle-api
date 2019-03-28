{{- define "importer.vault" }}
  {{- if eq .Values.global.subscriptionId "bf308a5c-0624-4334-8ff8-8dca9fd43783"}}
  {{- "ccd-saat" -}}
  {{- else }}
  {{- "ccd-aat" -}}
  {{- end }}
{{- end }}

{{- define "importer.resourcegroup" }}
  {{- if eq .Values.global.subscriptionId "bf308a5c-0624-4334-8ff8-8dca9fd43783"}}
  {{- "ccd-shared-saat" -}}
  {{- else }}
  {{- "ccd-shared-aat" -}}
  {{- end }}
{{- end }}
