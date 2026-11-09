# Instructions pour corriger l'erreur MissingPluginException avec image_picker

## Étapes à suivre :

1. **Arrêtez complètement l'application** (pas juste hot restart)

2. **Nettoyez le projet Flutter :**
   ```bash
   flutter clean
   ```

3. **Réinstallez les dépendances :**
   ```bash
   flutter pub get
   ```

4. **Si vous êtes sur Android, nettoyez aussi le build Android :**
   ```bash
   cd android
   ./gradlew clean
   cd ..
   ```

5. **Reconstruisez complètement l'application :**
   ```bash
   flutter run
   ```
   **IMPORTANT :** Ne faites PAS juste un hot reload. Vous devez faire un **full rebuild**.

## Si le problème persiste :

### Pour Android :
- Vérifiez que vous avez les permissions dans `android/app/src/main/AndroidManifest.xml`
- Assurez-vous que `minSdkVersion` est au moins 21

### Pour iOS :
- Vérifiez que vous avez les permissions dans `ios/Runner/Info.plist`
- Exécutez `pod install` dans le dossier `ios/`

### Pour Web :
- `image_picker` a des limitations sur web. Utilisez plutôt `file_picker` pour web.

## Vérification :
Après avoir suivi ces étapes, l'erreur `MissingPluginException` devrait être résolue.

