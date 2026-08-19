/*
 * Universidade Estadual de Maringá - UEM
 * Núcleo de Processamento de Dados - NPD
 * Copyright (c) 2020. All rights reserved.
 */
package application.service;


import application.model.Usuario;
import java.util.HashMap;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

/**
 *
 * @author Alison
 */
public class Sessao {

    private static Sessao instance;

    public static final String ALTERAR = "Atualizar";
    public static final String INCLUIR = "Incluir";
    public static final String EXCLUIR = "Excluir";
    public static final String CONSULTAR = "Consultar";

    private static final String ATRIBUTO_USUARIO = "usuario_logado_Us3r";
    private static final String ATRIBUTO_PERMISSOES = "permissoes_usuario_logado_Us3r";

    public static Sessao getInstance() {
        if (instance == null) {
            instance = new Sessao();
        }
        return instance;
    }

    /*
        SESSAO INICIALIZADA NO LOGIN:
     */
    public void setAtributo(String chave, Object valor) {
        Session session = Sessions.getCurrent();
        session.setAttribute(chave, valor);
    }

    public Object getAtributo(String chave) {
        Session session = Sessions.getCurrent();
        return session.getAttribute(chave);
    }

    public boolean validarSessao() {
        Session session = Sessions.getCurrent();
        Usuario u = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);
        if (u == null) {
            Executions.sendRedirect("/login");
            return false;
        }
        return true;
    }

    public void destruirSessao() {
        Session session = Sessions.getCurrent();
        session.invalidate();
        Executions.sendRedirect("/login");
    }

    /*
        PERMISSõES INICIALIZADA NO MENU:
     */
    public void setPermissao(String arquivoZul, String acao) {
        Session session = Sessions.getCurrent();
        arquivoZul = arquivoZul.toUpperCase();
        arquivoZul = arquivoZul.replace(".ZUL", "");
        HashMap permissoes = (HashMap) session.getAttribute(ATRIBUTO_PERMISSOES);
        if (permissoes == null) {
            permissoes = new HashMap();
        }
        permissoes.put(arquivoZul + "_" + acao, "S");
        session.setAttribute(ATRIBUTO_PERMISSOES, permissoes);
    }

    private boolean hasPermissao(String arquivoZul, String acao) {
        Session session = Sessions.getCurrent();
        arquivoZul = arquivoZul.toUpperCase();
        arquivoZul = arquivoZul.replace(".ZUL", "");
        HashMap permissoes = (HashMap) session.getAttribute(ATRIBUTO_PERMISSOES);
        if (permissoes == null) {
            return false;
        }
        if (permissoes.get(arquivoZul + "_" + acao) == null) {
            return false;
        }
        return permissoes.get(arquivoZul + "_" + acao).toString().equals("S");
    }

    public HashMap getPermissoesPagina(String pagina) {
        HashMap permissoesPagina = new HashMap();
        if (hasPermissao(pagina, CONSULTAR)) {
            permissoesPagina.put(CONSULTAR, "S");
        }
        if (hasPermissao(pagina, ALTERAR)) {
            permissoesPagina.put(ALTERAR, "S");
        }
        if (hasPermissao(pagina, INCLUIR)) {
            permissoesPagina.put(INCLUIR, "S");
        }
        if (hasPermissao(pagina, EXCLUIR)) {
            permissoesPagina.put(EXCLUIR, "S");
        }
        return permissoesPagina;
    }

    public HashMap getPermissoesTodas() {
        Session session = Sessions.getCurrent();
        return (HashMap) session.getAttribute(ATRIBUTO_PERMISSOES);
    }

    /*
        GET SETTER AUXILIARES
     */
    public Usuario getUsuario() {
        return (Usuario) this.getAtributo(ATRIBUTO_USUARIO);
    }

    public void setUsuario(Usuario u) {
        this.setAtributo(ATRIBUTO_USUARIO, u);
    }

}
