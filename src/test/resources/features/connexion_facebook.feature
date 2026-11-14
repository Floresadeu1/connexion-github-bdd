# language: fr
@facebook
Fonctionnalité: Connexion à un compte Facebook
  Afin de retrouver mes contacts
  En tant qu'utilisateur de Facebook
  Je veux me connecter avec mon e-mail ou mon numéro de téléphone

  Contexte:
    Soit je suis sur la page de connexion de Facebook

  # Classes d'équivalence : identifiant au format e-mail, au format téléphone.
  # Toujours des comptes INEXISTANTS, pour ne jamais bloquer un vrai compte.
  @negatif
  Plan du scénario: Connexion refusée pour un compte Facebook inexistant
    Quand je tente de me connecter à Facebook en tant que "<identifiant>" avec le mot de passe "<mot_de_passe>"
    Alors je ne suis pas connecté à Facebook
    Et je reste sur la page de connexion de Facebook

    Exemples:
      | identifiant                      | mot_de_passe |
      | personne.inexistante@example.com | Azerty123!   |
      | +237600000000                    | Azerty123!   |

  # Classes d'équivalence : identifiant vide, mot de passe vide.
  @negatif @champs-vides
  Plan du scénario: Connexion refusée quand un champ Facebook est vide
    Quand je tente de me connecter à Facebook en tant que "<identifiant>" avec le mot de passe "<mot_de_passe>"
    Alors je ne suis pas connecté à Facebook
    Et je reste sur la page de connexion de Facebook

    Exemples:
      | identifiant                      | mot_de_passe |
      |                                  | Azerty123!   |
      | personne.inexistante@example.com |              |