# TFT AI Coach — Android + Backend

## Android Overlay
- Overlay sobre outros apps + captura de tela (MediaProjection)
- Template matching de Augments (NCC em Kotlin)
- Ajuste regiões em `TemplateMatcher.kt::augRegions`
- Substitua ícones em `android/app/src/main/res/drawable/`

### Build local
```
cd android
./gradlew assembleRelease
```
APK em `android/app/build/outputs/apk/release/`.

### Assinatura
- Gere um keystore:
```
keytool -genkeypair -v -keystore release.keystore -alias tftcoach -keyalg RSA -keysize 2048 -validity 10000
```
- Coloque em `android/app/release.keystore` ou use CI com secret `SIGNING_STORE_BASE64` (base64 do arquivo).  
  Também defina `SIGNING_STORE_PASSWORD`, `SIGNING_KEY_ALIAS`, `SIGNING_KEY_PASSWORD` nos **Secrets** do repo.

## Backend (FastAPI)
```
cd backend
pip install -r requirements.txt
uvicorn main:app --reload
```
- Atualize meta:
```
curl -X POST http://localhost:8000/meta/push -H "Content-Type: application/json" -d '{"celestial_blessing":3,"second_wind":2}'
```
- Recomende:
```
curl -X POST http://localhost:8000/recommend -H "Content-Type: application/json" -d '{"augments":["celestial_blessing","second_wind"]}'
```

## GitHub Actions (APK pronto para download)
1. Faça upload desta pasta para um repositório.
2. Em **Settings > Secrets and variables > Actions**, crie:
   - `SIGNING_STORE_BASE64` = conteúdo base64 do seu `release.keystore` (opcional; sem isso sai *unsigned*).
   - `SIGNING_STORE_PASSWORD`
   - `SIGNING_KEY_ALIAS`
   - `SIGNING_KEY_PASSWORD`
3. Dispare o workflow em **Actions > Android CI > Run workflow**.
4. Baixe o `tft-ai-coach-apk` em **Artifacts**.

## Observação iOS
iOS não permite overlay sobre outros apps; esta solução é Android-only.
