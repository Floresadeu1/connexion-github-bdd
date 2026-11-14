package connexion;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Soit;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class FacebookSteps {

    private Playwright playwright;
    private BrowserContext contexte;
    private Page page;
    private PageConnexionFacebook connexion;

    @Before("@facebook")
    public void ouvrirNavigateur() {
        playwright = Playwright.create();
        boolean cache = !"false".equalsIgnoreCase(System.getenv("HEADLESS"));
        Browser navigateur = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(cache));
        contexte = navigateur.newContext(new Browser.NewContextOptions().setLocale("fr-FR"));
        page = contexte.newPage();
        connexion = new PageConnexionFacebook(page);
        PlaywrightAssertions.setDefaultAssertionTimeout(15000);
    }

    @After("@facebook")
    public void fermerNavigateur(Scenario scenario) {
        if (scenario.isFailed()) {
            scenario.attach(page.screenshot(), "image/png", "capture-echec-facebook");
        }
        playwright.close();
    }

    @Soit("je suis sur la page de connexion de Facebook")
    public void jeSuisSurLaPageDeConnexionFacebook() {
        connexion.ouvrir();
        assertThat(connexion.champIdentifiant()).isVisible();
    }

    @Quand("je tente de me connecter à Facebook en tant que {string} avec le mot de passe {string}")
    public void jeTenteDeMeConnecterAFacebook(String identifiant, String motDePasse) {
        connexion.seConnecter(identifiant, motDePasse);
    }

    @Alors("je ne suis pas connecté à Facebook")
    public void jeNeSuisPasConnecte() {
        // Facebook crée le cookie c_user uniquement pour un utilisateur connecté
        boolean connecte = contexte.cookies().stream()
                .anyMatch(c -> c.name.equals("c_user"));
        if (connecte) {
            throw new AssertionError("Le cookie c_user existe : l'utilisateur est connecté");
        }
    }

    @Alors("je reste sur la page de connexion de Facebook")
    public void jeResteSurLaPageDeConnexionFacebook() {
        assertThat(page).hasURL(Pattern.compile(".*facebook\\.com/.*login.*"));
    }
}