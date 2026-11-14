package connexion;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Soit;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ConnexionSteps {

    private Playwright playwright;
    private Page page;
    private PageConnexionGitHub connexion;

    // ---------- Avant / après chaque scénario GitHub uniquement ----------

    @Before("@github")
    public void ouvrirNavigateur() {
        playwright = Playwright.create();
        boolean cache = !"false".equalsIgnoreCase(System.getenv("HEADLESS"));
        Browser navigateur = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(cache));
        page = navigateur.newPage();
        connexion = new PageConnexionGitHub(page);
        PlaywrightAssertions.setDefaultAssertionTimeout(15000);
    }

    @After("@github")
    public void fermerNavigateur(Scenario scenario) {
        if (scenario.isFailed()) {
            // Capture d'écran jointe au rapport en cas d'échec (cours p. 284)
            scenario.attach(page.screenshot(), "image/png", "capture-echec-github");
        }
        playwright.close();
    }

    // ---------- Soit ----------

    @Soit("je suis sur la page de connexion de GitHub")
    public void jeSuisSurLaPageDeConnexion() {
        connexion.ouvrir();
        assertThat(connexion.champIdentifiant()).isVisible();
    }

    // ---------- Quand ----------

    @Quand("je me connecte avec les identifiants de mon compte")
    public void jeMeConnecteAvecMesIdentifiants() {
        String login = System.getenv("GITHUB_LOGIN");
        String motDePasse = System.getenv("GITHUB_MOTDEPASSE");
        if (login == null || motDePasse == null) {
            throw new IllegalStateException(
                "Définir les variables GITHUB_LOGIN et GITHUB_MOTDEPASSE");
        }
        connexion.seConnecter(login, motDePasse);
    }

    @Quand("je tente de me connecter en tant que {string} avec le mot de passe {string}")
    public void jeTenteDeMeConnecter(String identifiant, String motDePasse) {
        connexion.seConnecter(identifiant, motDePasse);
    }

    // ---------- Alors ----------

    @Alors("je suis connecté à mon compte")
    public void jeSuisConnecte() {
        // GitHub place le login de l'utilisateur connecté dans <meta name="user-login">.
        // Délai de 2 minutes : laisse le temps de saisir à la main le code 2FA si GitHub le demande.
        assertThat(page.locator("meta[name='user-login']"))
                .hasAttribute("content", Pattern.compile(".+"),
                        new LocatorAssertions.HasAttributeOptions().setTimeout(120000));
    }

    @Alors("GitHub affiche le message {string}")
    public void gitHubAfficheLeMessage(String message) {
        assertThat(connexion.message(message)).isVisible();
    }

    @Alors("je reste sur la page de connexion")
    public void jeResteSurLaPageDeConnexion() {
        assertThat(page).hasURL(Pattern.compile(".*github\\.com/(login|session).*"));
    }

    @Alors("le champ {string} est signalé comme obligatoire")
    public void leChampEstSignaleObligatoire(String champ) {
        String idAttendu = champ.equals("identifiant") ? "login_field" : "password";
        String idObtenu = connexion.premierChampInvalide();
        if (!idAttendu.equals(idObtenu)) {
            throw new AssertionError("Champ signalé attendu : " + idAttendu
                    + ", obtenu : " + idObtenu);
        }
    }
}