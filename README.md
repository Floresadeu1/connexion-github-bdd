# Connexion à GitHub : tests BDD avec Cucumber et Playwright

TP du cours INF563 Vérification et Validation du logiciel (Université de Yaoundé I).

Le comportement attendu de la page de connexion de GitHub est décrit en Gherkin
(spécification exécutable), puis automatisé avec Cucumber (JUnit 5) et Playwright.

## Structure

| Fichier | Rôle |
|---|---|
| `src/test/resources/features/connexion_github.feature` | Scénarios en Gherkin (français) |
| `src/test/java/connexion/RunCucumberTest.java` | Lanceur : relie Maven à Cucumber |
| `src/test/java/connexion/ConnexionSteps.java` | Définitions d'étapes : une méthode par phrase |
| `src/test/java/connexion/PageConnexionGitHub.java` | Page object : sélecteurs de la page de connexion |

## Scénarios

| Scénario | Tag | Classes d'équivalence | Résultat |
|---|---|---|---|
| Connexion réussie | `@positif` | Identifiants valides | Non automatisable (voir lacunes) |
| Compte inexistant (2 exemples) | `@negatif` | Identifiant au format login / au format e-mail | Vert |
| Champ obligatoire vide (3 exemples) | `@negatif @champs-vides` | Identifiant vide / mot de passe vide / les deux | Vert |

## Lancer les tests

    mvn test "-Dcucumber.filter.tags=@negatif"

Rapport : `target/cucumber-report.html`. Une capture d'écran y est jointe en cas d'échec.

Pour le scénario positif, les identifiants sont lus dans des variables
d'environnement et n'apparaissent dans aucun fichier :

    $env:GITHUB_LOGIN="..."
    $env:GITHUB_MOTDEPASSE="..."
    mvn test

## Historique rouge → vert

1. `Scénarios Gherkin : étapes non définies (rouge)` : Cucumber lit les scénarios
   mais ne trouve aucun code (undefined).
2. `Étapes Playwright : scénarios négatifs verts` : les définitions d'étapes
   sont écrites, les scénarios négatifs passent.

## Lacunes de spécification rencontrées

1. **Double authentification (2FA).** Après un identifiant et un mot de passe
   corrects, GitHub demande un code à 6 chiffres. La spécification
   « je suis connecté à mon compte » est donc incomplète : la connexion réussie
   comporte une étape que le test ne peut pas automatiser. Le scénario positif
   n'est pas exécuté dans la suite automatique.
2. **Vérification d'appareil.** Même sans 2FA, GitHub peut envoyer un code par
   e-mail lors d'une connexion depuis un navigateur inconnu, ce qui est le cas
   du navigateur lancé par Playwright.
3. **Champs vides : qui signale l'erreur ?** Les champs sont obligatoires
   (attribut `required`). C'est le navigateur qui bloque l'envoi, pas GitHub :
   aucun message de GitHub n'est affiché. L'oracle vérifie donc que le champ
   est invalide pour le navigateur. Quand les deux champs sont vides, seul le
   premier (identifiant) est signalé.
4. **Message d'erreur.** L'oracle dépend du texte anglais
   « Incorrect username or password. ». Rien ne garantit qu'il restera
   identique : le test casserait sans que le comportement ait changé.
5. **Mot de passe erroné sur un vrai compte.** Il n'a pas été testé
   volontairement : des échecs répétés peuvent bloquer le compte ou déclencher
   un CAPTCHA. Seuls des comptes inexistants sont utilisés.

## Limites

Les tests dépendent d'un site réel (réseau, changements d'interface de GitHub,
CAPTCHA) : ce sont des tests de bout en bout, lents et potentiellement
instables. Ils restent donc peu nombreux et limités au parcours critique.