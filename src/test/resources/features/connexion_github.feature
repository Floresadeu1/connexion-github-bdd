# language: fr
@github
Fonctionnalité: Connexion à un compte GitHub
  Afin de gérer mes dépôts de code
  En tant qu'étudiant inscrit sur GitHub
  Je veux me connecter avec mon identifiant et mon mot de passe

  Contexte:
    Soit je suis sur la page de connexion de GitHub

  @positif
  Scénario: Connexion réussie avec les identifiants de mon compte
    Quand je me connecte avec les identifiants de mon compte
    Alors je suis connecté à mon compte

  # Classes d'équivalence : un identifiant au format login, un au format e-mail.
  # Toujours des comptes INEXISTANTS, pour ne jamais bloquer un vrai compte.
  @negatif
  Plan du scénario: Connexion refusée pour un compte inexistant
    Quand je tente de me connecter en tant que "<identifiant>" avec le mot de passe "<mot_de_passe>"
    Alors GitHub affiche le message "Incorrect username or password."
    Et je reste sur la page de connexion

    Exemples:
      | identifiant                      | mot_de_passe |
      | compte-inexistant-inf563-2026    | Azerty123!   |
      | personne.inexistante@example.com | Azerty123!   |

  # Classes d'équivalence : identifiant vide, mot de passe vide, les deux vides.
  # Les champs sont obligatoires : le navigateur bloque l'envoi, GitHub n'affiche rien.
  @negatif @champs-vides
  Plan du scénario: Envoi bloqué quand un champ obligatoire est vide
    Quand je tente de me connecter en tant que "<identifiant>" avec le mot de passe "<mot_de_passe>"
    Alors le champ "<champ_signale>" est signalé comme obligatoire
    Et je reste sur la page de connexion

    Exemples:
      | identifiant       | mot_de_passe | champ_signale |
      |                   | Azerty123!   | identifiant   |
      | compte-inexistant |              | mot de passe  |
      |                   |              | identifiant   |