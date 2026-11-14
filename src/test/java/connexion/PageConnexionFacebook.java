package connexion;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.AriaRole;

import java.util.regex.Pattern;

public class PageConnexionFacebook {
    private final Page page;

    public PageConnexionFacebook(Page page) {
        this.page = page;
    }

    public PageConnexionFacebook ouvrir() {
        page.navigate("https://www.facebook.com/login");
        fermerBanniereCookies();
        return this;
    }

    // Plusieurs sélecteurs possibles séparés par des virgules : le premier qui existe est utilisé
    public Locator champIdentifiant() {
        return page.locator("input[name='email'], #email, input[type='email'], input[type='text']").first();
    }

    public Locator champMotDePasse() {
        return page.locator("input[name='pass'], #pass, input[type='password']").first();
    }

    public Locator boutonConnexion() {
        return page.locator("button[name='login'], button[type='submit'], "
                + "[role='button']:has-text('Se connecter'), [role='button']:has-text('Log in')").first();
    }

    public void seConnecter(String identifiant, String motDePasse) {
        champIdentifiant().fill(identifiant);
        champMotDePasse().fill(motDePasse);
        boutonConnexion().click();
        page.waitForLoadState();
    }

    /** Facebook peut afficher une bannière de cookies : on la ferme si elle apparaît. */
    private void fermerBanniereCookies() {
        Locator bouton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                .setName(Pattern.compile("(Refuser|Decline|Autoriser|Allow).*cookies",
                        Pattern.CASE_INSENSITIVE)));
        try {
            bouton.first().click(new Locator.ClickOptions().setTimeout(5000));
        } catch (PlaywrightException pasDeBanniere) {
            // Pas de bannière : on continue normalement
        }
    }
}