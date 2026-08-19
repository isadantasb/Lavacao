/*
 * Universidade Estadual de Maringá - UEM
 * Núcleo de Processamento de Dados - NPD
 * Copyright (c) 2020. All rights reserved.
 */
package application.service;

import dao.DAO;
import java.util.Date;
import java.util.Properties;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;
import utilitarios.Utils;

/**
 *
 * @author alison
 *
 * Application contém as propriedades do sistema.
 *
 * Para pegar os atributos de Application, deve-se utilizar o singleton
 * getInstance para pegar o objeto Application e posteriormente pegar os
 * atributos.
 */
public class Application {

    private Properties properties = new Properties();
    private String enviroment = "";
    private Window janela;

    private static Application instance;

    public static final String NAME = "app_name";
    public static final String DESCRIPTION = "app_description";
    public static final String VERSION = "app_version";
    public static final String DEVELOP = "desenvol";
    public static final String PRODUCTION = "producao";

    //Inicializa os atributos da classe
    private Application() {

        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("application.properties"));
        } catch (Exception ex) {
            System.out.println("Erro ao recuperar o application.properties: " + ex.getMessage());
        }

        verificaAmbiente();
    }

    private void verificaAmbiente() {

        String ambiente = DAO.getConnectionName();

        if (ambiente != null && (ambiente.contains("desenvol") || ambiente.contains("186.233.154.47"))) {
            this.setEnviroment(DEVELOP);
        } else {
            this.setEnviroment(PRODUCTION);
        }

    }

    //Retorna a instancia da classe utilizando singleton
    public static Application getInstance() {
        if (instance == null) {
            instance = new Application();
        }
        return instance;
    }

    /**
     * Retorna uma propriedade que consta no arquivo application.properties pelo
     * nome
     *
     * @param key Chave. Para versão, utilize o método getVersion().
     * @return String com o valor da propriedade
     */
    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    /**
     * Retorna o ambiente de conexao com o banco de dados
     *
     * @return String contendo Application.DEVELOP ou Application.PRODUCTION
     */
    public String getEnviroment() {
        return enviroment;
    }

    private void setEnviroment(String enviroment) {
        this.enviroment = enviroment;
    }

    /**
     * Retorna versão com a data do último build
     *
     * @return String com a versão e data concatenada no formato
     * appversion.yyyyMMdd.HHmm
     */
    public String getVersion() {
        try {
        Utils ut = new Utils();

        Date ultimaCompilacao = ut.getClassDateCompilation(this.getClass());
        String ultimaCompilacaoFormatada = ut.formatarData(ultimaCompilacao, "yyyyMMdd.HHmm");
        String versao = this.getProperty(VERSION);

        return versao + "." + ultimaCompilacaoFormatada;
        } catch (java.io.IOException e) {
            e.printStackTrace(); // Imprime o erro no console
            System.out.println("Erro ao ler ou gravar o arquivo!");
            return null;
        }

    }

    /**
     * Imprime informações no console e no código-fonte
     *
     * @param janela A mensagem será inserida antes do código-fonte da janela
     */
    public void insereDadosNoConsole(Window janela) {
        String organization = this.getProperty("app_organization");

        String uem = organization.split("I")[0].trim();
        String npd = organization.split("I")[1].trim();

        Clients.evalJavaScript(" console.log('%c " + uem.split("-")[0].trim() + " ' + '%c " + uem.split("-")[1].trim() + " ', 'font-weight: bold; background: #aaa; color: #980000', 'font-weight: normal; background: #222; color: white'); ");
        Clients.evalJavaScript(" console.log('%c " + npd.split("-")[0].trim() + " ' + '%c " + npd.split("-")[1].trim() + " ', 'font-weight: bold; background: white; color: #222', 'font-weight: normal; background: white; color: #222'); ");
        Clients.evalJavaScript(" console.log(' " + this.getProperty(Application.NAME) + " versão " + this.getVersion() + "'); ");

        PageCtrl pg = (PageCtrl) janela.getPage();
        pg.addBeforeHeadTags("<!-- \n"
                + uem + "\n"
                + npd + "\n"
                + " " + this.getProperty(Application.NAME) + " versão " + this.getVersion() + " \n"
                + "-->");
    }

    /**
     * Coloca o título da página no topo do navegador/browser.
     * Esse método pode ser extendido para receber uma string para personalizar o título
     */
    public void atualizaTituloDaPagina() {
        String tituloPadrao = this.getProperty(NAME) + " - " + this.getProperty(DESCRIPTION);
        Executions.getCurrent().getDesktop().getFirstPage().setTitle(tituloPadrao);
    }

}
