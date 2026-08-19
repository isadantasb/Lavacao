package controller;

/**
 *
 * @author alison
 */
import application.service.Application;
import application.service.Sessao;
import application.model.Usuario;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Div;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import utilitarios.CookieUtil;
import utilitarios.LdapUtil;

/**
 *
 * @author Alison
 */
public class LoginController extends Window {


    // Esses campos tem que ser alterados dps de acordo com a tela de Login
    private Textbox usuarioDigitado;
    private Textbox senhaDigitada;
    private Include conteudo;
    private Label lbVersao;
    private Label lbErro;
    private Div divDesenv;
    private Window janela;

    //ATENCAO: EXCLUA OU ALTERE ESSA SENHA MESTRA
    private final String SENHA_MESTRA = "senhamestra!";

    public void onCreate() {

        this.conteudo = (Include) getFellowIfAny("conteudo", true);
        this.janela = (Window) getFellow("login");
        this.usuarioDigitado = (Textbox) getFellow("usuario");
        this.senhaDigitada = (Textbox) getFellow("senha");
        this.lbVersao = (Label) getFellow("lb_versao");
        this.lbErro = (Label) getFellow("msg_erro");
        this.divDesenv = (Div) getFellow("div_desenv");

        String versao = Application.getInstance().getVersion();
        this.lbVersao.setValue("Versão " + versao);

        this.mensagemDesenvol();
        Application.getInstance().insereDadosNoConsole(this.janela);
        Application.getInstance().atualizaTituloDaPagina();

        verificaCookie();

        //acesso automático para teste
        //this.usuarioDigitado.setValue("arpfreitas");
        //this.senhaDigitada.setValue(SENHA_MESTRA);
        //acessar();
    }

    /**
     * Executado quando clica no botão para entrar.
     */
    public void acessar() {
        validarSessao();
    }

    /**
     * Verifica se tem o último usuário que logou salvo em cookie, se tiver
     * preenche o campo automaticamente.
     */
    private void verificaCookie() {
        String ultimoUsuario = CookieUtil.getCookie("usuariologin");
        if (ultimoUsuario != null) {
            this.usuarioDigitado.setValue(ultimoUsuario);
        }
    }

    /**
     * Salva em cookie o último usuário que logou.
     */
    private void atualizaCookie() {
        CookieUtil.setCookie("usuariologin", this.usuarioDigitado.getValue());
    }

    /**
     * Valida a senha no LDAP da UEM. Funciona para todos que tiver um e-mail
     * válido @uem.br
     *
     * @param login é o e-mail ou apenas o nome do usuário
     * @param senha senha
     * @return
     */
    private boolean isSenhaValidaLdap(String login, String senha) {

        LdapUtil ldapUtil = new LdapUtil();

        try {
            ldapUtil.bind(login, senha);
        } catch (Exception e) {
            this.showErro(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Valida a senha conforme cadastrado no banco de dados. Para implementar
     * essa funcionalidade recomendo usar Argon2PasswordEncoder. Veja tutorial
     * https://foojay.io/today/how-to-do-password-hashing-in-java-applications-the-right-way/
     *
     * @param login é o e-mail ou apenas o nome do usuário
     * @param senha senha
     * @return
     */
    private boolean isSenhaValidaBanco(String login, String senha) {

        //TODO
        return false;
    }

    private boolean isSenhaMestra(String login, String senha) {

        return senha.equals(SENHA_MESTRA);
    }

    /**
     * Se validar a senha, salva na sessão o usuário que acessou com todos os
     * dados necessários para o sistema funcionar, suas permissões, perfis, etc.
     */
    private void validarSessao() {

        String login = this.usuarioDigitado.getValue();
        String senha = this.senhaDigitada.getValue();

        if (isSenhaMestra(login, senha) || isSenhaValidaLdap(login, senha) || isSenhaValidaBanco(login, senha)) {

            //aqui estamos colocando @uem.br porem tem que ver se essa regra faz sentido,
            //ou seja, se seu sistema somente aceita usuarios @uem.br ou se aceita usuarios externos
            String email = "";
            int i = login.indexOf("@");
            if (i < 1) {
                email = login + "@uem.br";
            } else {
                email = login;
            }

            Usuario usuario = new Usuario();
            usuario.setEmail(email);
            //aqui pode ter tratativa de ir buscar esse usuário no banco de dados usando o e-mail, para pegar as permissões do usuário, caso você tenha um cadastro de usuário e permissão
            //pode neste momento verificar também se o usuário pode ter acesso ou não ao sistema
            //se tiver algum erro utilize o this.showErro(mensagem)

            //GUARDE O USUÁRIO NA SESSÃO
            //assim você pode pegar ele na sessão toda vez que entrar em alguma página
            //e assim pegar as permissões do usuário logado para saber se realmente ele tem permissão para acessar aquela página
            //ou qual permissão ele tem naquele página (alterar, incluuir, ver, etc.)
            Sessao.getInstance().setUsuario(usuario);
            atualizaCookie();
            Executions.sendRedirect("/");

        }

    }

    /**
     * Mostra erro na tela de login
     *
     * @param msg
     */
    public void showErro(String msg) {
        this.lbErro.setValue(msg);
        this.lbErro.setVisible(true);
    }

    /**
     * Esconde o erro na tela de login.
     */
    public void hideErro() {
        this.lbErro.setValue("");
        this.lbErro.setVisible(false);
    }

    /**
     * Caso o ambiente de desenvolvimento seja de testes (não esteja conectado
     * em produção), então mostra um aviso.
     */
    private void mensagemDesenvol() {
        if (Application.getInstance().getEnviroment().equals(Application.DEVELOP)) {
            this.divDesenv.setVisible(true);
        }
    }

}
