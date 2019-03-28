{{- define "importer.vault" }}
  {{- if eq .Values.global.subscriptionId "bf308a5c-0624-4334-8ff8-8dca9fd43783"}}
  {{- "rpe-ft-api-saat" -}}
  {{- else }}
  {{- "rpe-ft-api-aat" -}}
  {{- end }}
{{- end }}

{{- define "importer.resourcegroup" }}
  {{- if eq .Values.global.subscriptionId "bf308a5c-0624-4334-8ff8-8dca9fd43783"}}
  {{- "rpe-feature-toggle-api-saat" -}}
  {{- else }}
  {{- "rpe-feature-toggle-api-aat" -}}
  {{- end }}
{{- end }}
