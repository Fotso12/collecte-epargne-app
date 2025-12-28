# TODO - Implémentation de l'envoi d'email lors de la création d'utilisateur

## Étapes complétées
- [x] Créer le service EmailService pour gérer l'envoi d'emails via Gmail SMTP
- [x] Modifier UtilisateurService pour injecter EmailService
- [x] Ajouter l'appel à l'envoi d'email dans la méthode save après la sauvegarde de l'utilisateur
- [x] Ajouter la gestion d'erreurs pour l'envoi d'email (ne pas échouer la création si l'email échoue)
- [x] Ajouter des commentaires clairs et nets dans EmailService.java et UtilisateurService.java

## Étapes suivantes
- [ ] Tester l'envoi d'email lors de la création d'un nouvel utilisateur via l'API
- [ ] Vérifier que l'email est bien envoyé avec les identifiants en clair
- [ ] Améliorer la gestion d'erreurs (logging avec SLF4J au lieu de System.err)
