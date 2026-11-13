package connexion;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class PageConnexionGitHub {
    private final Page page;

    public PageConnexionGitHub(Page page) {
        this.page = page;
    }

    public PageConnexionGitHub ouvrir() {
        page.navigate("https://github.com/login");
        return this;
    }

    public Locator champIdentifiant() {
        return page.locator("#login_field");
    }

    public Locator champMotDePasse() {
        return page.locator("#password");
    }

    public Locator boutonConnexion() {
        return page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Sign in").setExact(true));
    }

    public void seConnecter(String identifiant, String motDePasse) {
        champIdentifiant().fill(identifiant);
        champMotDePasse().fill(motDePasse);
        boutonConnexion().click();
    }

    public Locator message(String texte) {
        return page.getByText(texte).first();
    }

    /** Renvoie l'id du premier champ que le navigateur juge invalide, ou null. */
    public String premierChampInvalide() {
        return (String) page.evaluate(
            "() => { const i = document.querySelector('#login_field').form"
          + ".querySelector('input:invalid'); return i ? i.id : null; }");
    }
}