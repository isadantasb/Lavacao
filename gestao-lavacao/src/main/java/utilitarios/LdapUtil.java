package utilitarios;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Conecta e busca atributos do LDAP da UEM. O LDAP é o servidor de autenticação
 * da UEM e é utilizado para autenticar os usuários que tem e-mail institucional
 * com o mesmo login e senha em todos os sistemas da UEM. Utiliza a biblioteca
 * Apache Directory API, sendo necessário a seguinte biblioteca de dependência: {@code
 * <dependency>
 * <groupId>org.apache.directory.api</groupId>
 * <artifactId>apache-ldap-api</artifactId>
 * <version>2.1.6</version>
 * <type>pom</type>
 * </dependency>
 * }
 *
 * @see
 * <a href="https://directory.apache.org/api/">https://directory.apache.org/api/</a>,
 * <a href="https://github.com/apache/directory-ldap-api">https://github.com/apache/directory-ldap-api</a>
 *
 * @author alison
 */
public class LdapUtil {

    private String usuario;
    private List<String> emails = new ArrayList();
    private String nomeCompleto;
    private String primeiroNome;
    private String ultimoNome;
    private String lotacao;
    private String funcao;
    private String tipo;
    private String matricula;
    private String seeAlso;

    private final String LDAP_URL = "ldap://ldap.uem.br:389";
    private final String LDAP_GROUP = "ou=People,dc=uem,dc=br";

    /**
     * Realiza a autenticação no LDAP, pegando os atributos. Esta autenticação é
     * utilizada para todos os usuários que tem e-mail institucional @uem.br.
     *
     * @param user Nome de usuário ou e-mail da UEM
     * @param password Senha
     * @throws Exception com a descrição do erro
     */
    public void bind(String user, String password) throws Exception {
        if (user == null || user.isBlank()) {
            throw new Exception("Nenhum nome de usuário foi digitado!");
        } else if (password == null || password.isEmpty()) {
            throw new Exception("Nenhuma senha foi digitada!");
        }

        //trata nome de usuário e remove @uem.br caso exista
        user = user.toLowerCase().trim();
        user = StringUtils.substringBefore(user, "@uem.br").trim();

        //conecta anonimamente no ldap
        Hashtable<String, String> envAnonymous = createAnonymousEnvironment();
        DirContext contextAnonymous = connectToServer(envAnonymous);

        //pesquisa se usuário existe no ldap
        searchUser(contextAnonymous, "uid=" + user);
        closeConnection(contextAnonymous);

        //conecta com usuário e senha no ldap para validar "usuário e senha"
        Hashtable<String, String> envUser = createUserEnvironment(user, password);
        DirContext contextUser = connectToServer(envUser);

        //busca atributos do usuário autenticado
        readAttributes(contextUser, envUser.get(Context.SECURITY_PRINCIPAL));
        closeConnection(contextUser);
    }

    /**
     * Realiza a pesquisa de um usuário no LDAP, pegando os atributos. Esta
     * pesquisa pode ser usada para pegar dados de um usuário qualquer.
     *
     * @param user Nome de usuário ou e-mail da UEM
     * @throws Exception com a descrição do erro
     */
    public void search(String user) throws Exception {
        if (user == null || user.isBlank()) {
            throw new Exception("Nenhum nome de usuário foi digitado!");
        }

        //remove @uem.br caso exista
        user = StringUtils.substringBefore(user, "@uem.br").trim();

        //conecta anonimamente no ldap
        Hashtable<String, String> envAnonymous = createAnonymousEnvironment();
        DirContext contextAnonymous = connectToServer(envAnonymous);

        //pesquisa se usuário existe no ldap e busca atributos do usuário
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        try {
            NamingEnumeration<SearchResult> searchResults = contextAnonymous.search(LDAP_GROUP, "uid=" + user, searchControls);
            if (searchResults.hasMore()) {
                SearchResult result = (SearchResult) searchResults.next();
                Attributes attribs = result.getAttributes();
                if (null != attribs) {
                    try {
                        NamingEnumeration<? extends Attribute> allAttributes = attribs.getAll();
                        while (allAttributes.hasMoreElements()) {
                            Attribute nextAttribute = allAttributes.next();
                            String key = nextAttribute.getID();
                            NamingEnumeration<?> attributeValues = nextAttribute.getAll();
                            while (attributeValues.hasMoreElements()) {
                                Object value = attributeValues.next();
                                populaAtributo(key, value);
                                //System.out.println(key + "=" + value);
                            }
                        }
                    } catch (NamingException ex) {
                        throw new Exception("Erro ao obter atributos do usuário ao pesquisar no LDAP. " + showErrorMessage(ex));
                    }
                } else {
                    throw new Exception("Atributos do usuário não encontrado ao pesquisar no LDAP!");
                }
            } else {
                throw new Exception("Usuário não encontrado ao pesquisar no LDAP!");
            }
        } catch (NamingException ex) {
            throw new Exception("Não foi possível pesquisar os dados do usuário. " + showErrorMessage(ex));
        }

        closeConnection(contextAnonymous);
    }

    private DirContext connectToServer(Hashtable<String, String> env) throws Exception {
        DirContext context;
        try {
            context = new InitialDirContext(env);
        } catch (NamingException ex) {
            if (ex.getClass().equals(AuthenticationException.class)) {
                throw new Exception("Usuário ou senha inválida!");
            } else {
                throw new Exception("Erro ao conectar no servidor de autenticação LDAP. " + showErrorMessage(ex));
            }
        }
        return context;
    }

    private void closeConnection(DirContext context) throws Exception {
        if (context != null) {
            try {
                context.close();
            } catch (NamingException ex) {
                throw new Exception("Erro ao desconectar do servidor de autenticação LDAP. " + showErrorMessage(ex));
            }
        }
    }

    private void searchUser(DirContext context, String filter) throws Exception {
        String[] attrIDs = {"uid"};
        SearchControls searchControls = new SearchControls();
        searchControls.setReturningAttributes(attrIDs);
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        try {
            NamingEnumeration<SearchResult> searchResults = context.search(LDAP_GROUP, filter, searchControls);
            if (!searchResults.hasMore()) {
                throw new Exception("Usuário não encontrado no LDAP!");
            }
        } catch (NamingException ex) {
            throw new Exception("Não foi possível pesquisar o usuário no LDAP. " + showErrorMessage(ex));
        }
    }

    private void readAttributes(DirContext context, String query) throws Exception {
        Attributes attributes = null;
        try {
            attributes = context.getAttributes(query);
        } catch (NamingException ex) {
            throw new Exception("Detalhes do usuário não encontrado no LDAP. " + showErrorMessage(ex));
        }
        if (attributes != null) {
            try {
                NamingEnumeration<? extends Attribute> allAttributes = attributes.getAll();
                while (allAttributes.hasMoreElements()) {
                    Attribute nextAttribute = allAttributes.next();
                    String key = nextAttribute.getID();
                    NamingEnumeration<?> attributeValues = nextAttribute.getAll();
                    while (attributeValues.hasMoreElements()) {
                        Object value = attributeValues.next();
                        populaAtributo(key, value);
                        //System.out.println(key + "=" + value);
                    }
                }
            } catch (NamingException ex) {
                throw new Exception("Erro ao obter atributos do usuário no LDAP. " + showErrorMessage(ex));
            }
        }
    }

    private Hashtable<String, String> createUserEnvironment(String user, String password) {
        Hashtable<String, String> envUser = createBaseEnvironment();
        envUser.put(Context.SECURITY_AUTHENTICATION, "simple");
        envUser.put(Context.SECURITY_PRINCIPAL, "uid=" + user + "," + LDAP_GROUP);
        envUser.put(Context.SECURITY_CREDENTIALS, password);
        return envUser;
    }

    private Hashtable<String, String> createAnonymousEnvironment() {
        Hashtable<String, String> envAnonymous = createBaseEnvironment();
        envAnonymous.put(Context.SECURITY_AUTHENTICATION, "none");
        return envAnonymous;
    }

    private Hashtable<String, String> createBaseEnvironment() {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put("com.sun.jndi.ldap.read.timeout", "5000");
        env.put("com.sun.jndi.ldap.connect.timeout", "5000");
        env.put(Context.PROVIDER_URL, LDAP_URL);
        return env;
    }

    /**
     * Faz um tratamento nas mensagens de erro e imprime no log. Mais
     * informações sobre os tipos de NamingException em
     * https://docs.oracle.com/javase/tutorial/jndi/ldap/exceptions.html
     *
     * @param NamingException exceção
     * @return String com o erro
     */
    private String showErrorMessage(NamingException ex) {
        System.out.println("Erro no LDAP: " + ExceptionUtils.getStackTrace(ex));
        String simpleErrorMessage = ex.getClass().getSimpleName() + ": " + ex.getMessage();
        return simpleErrorMessage;
    }

    /**
     * Popula os atributos com os valores do LDAP.
     *
     * @param key chave do atributo no LDAP
     * @param value valor do atributo no LDAP
     */
    private void populaAtributo(String key, Object value) {
        if (value != null) {
            if (key.equalsIgnoreCase("cn")) {
                nomeCompleto = value.toString();
            } else if (key.equalsIgnoreCase("givenName")) {
                primeiroNome = value.toString();
            } else if (key.equalsIgnoreCase("sn")) {
                ultimoNome = value.toString();
            } else if (key.equalsIgnoreCase("mail")) {
                emails.add(value.toString());
            } else if (key.equalsIgnoreCase("uid")) {
                usuario = value.toString();
            } else if (key.equalsIgnoreCase("departmentNumber")) {
                lotacao = value.toString();
            } else if (key.equalsIgnoreCase("title")) {
                funcao = value.toString();
            } else if (key.equalsIgnoreCase("employeeType")) {
                tipo = value.toString();
            } else if (key.equalsIgnoreCase("employeeNumber")) {
                matricula = value.toString();
            } else if (key.equalsIgnoreCase("seeAlso")) {
                seeAlso = value.toString();
            }
        }
    }

    /**
     * E-mails do usuário autenticado. Podem existir vários e-mails cadastrados
     * no LDAP.
     *
     * @return List com todos os e-mails
     */
    public List<String> getEmails() {
        return emails;
    }

    /**
     * @deprecated Subtituído por {@link #getEmail()}.
     *
     * @return String com o e-mail ou nulo
     */
    @Deprecated
    public String getMail() {
        return getEmail();
    }

    /**
     * Algum e-mail do usuário autenticado. Caso exista mais de um e-mail
     * cadastrado, será escolhido aleatoriamente um desses e-mails pois a ordem
     * que os atributos vem do LDAP é aleatória. Utilize {@link #getEmails()}
     * para obter todos os e-mails.
     *
     * @return String com o e-mail ou nulo
     */
    public String getEmail() {
        return !emails.isEmpty() ? emails.get(0) : null;
    }

    /**
     * E-mail institucional do usuário autenticado. É o nome de usuário seguido
     * de @uem.br.
     *
     * @return String com o e-mail institucional ou nulo
     */
    public String getEmailInstitucional() {
        return getUsuario() + "@uem.br";
    }

    /**
     * Nome de usuário (login) do usuário autenticado. É o nome que vem antes do
     * arroba do e-mail institucional.
     *
     * @return String com o nome de usuário (login) ou nulo
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Nome completo do usuário autenticado.
     *
     * @return String com o nome completo ou nulo
     */
    public String getNomeCompleto() {
        return nomeCompleto;
    }

    /**
     * Primeiro nome do usuário autenticado.
     *
     * @return String com o primeiro nome ou nulo
     */
    public String getPrimeiroNome() {
        return primeiroNome;
    }

    /**
     * Último nome do usuário autenticado.
     *
     * @return String com o último nome ou nulo
     */
    public String getUltimoNome() {
        return ultimoNome;
    }

    /**
     * @deprecated Subtituído por {@link #getUltimoNome()}. O sobrenome não está
     * corretamente cadastrado no LDAP pois não existe correta divisão de nomes
     * compostos e sobrenome, por isso retornamos o último nome.
     *
     * @return String com o último nome ou nulo
     */
    @Deprecated
    public String getSobrenome() {
        return getUltimoNome();
    }

    /**
     * Lotação do usuário autenticado.
     *
     * @return String descrevendo a lotação ou nulo
     */
    public String getLotacao() {
        return lotacao;
    }

    /**
     * Função do usuário autenticado.
     *
     * @return String descrevendo a função ou nulo
     */
    public String getFuncao() {
        return funcao;
    }

    /**
     * Tipo do usuário autenticado. Com esse valor é possível identificar se é
     * funcionário ou aluno. Os valores possíveis são:<br>
     * FA: Funcionário Nível Apoio<br>
     * FM: Funcionário Nível Médio<br>
     * FS: Funcionário Nível Superior<br>
     * FD: Funcionário Docente<br>
     * AP: Aposentados<br>
     * GD: Graduação<br>
     * PG: Pós-graduação<br>
     * ST: Setores<br>
     * UN: Unati<br>
     * CT: Convênios/Temporários<br>
     * OU: Outros<br>
     *
     * @return String com a sigla do tipo ou nulo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Matrícula do usuário autenticado. O valor será a matrícula caso
     * funcionário ou o RA caso aluno.
     *
     * @return String com a matrícula ou nulo
     */
    public String getMatricula() {
        return matricula;
    }

    /**
     * CPF do usuário autenticado.
     *
     * @return String com o cpf ou nulo
     */
    public String getCpf() {
        String cpf = null;
        String decodedString = decodeSeeAlso();
        if (decodedString.contains(":")) {
            String[] parts = decodedString.split(":");
            if (parts.length > 0) {
                cpf = parts[0];
            }
        }
        return cpf;
    }

    /**
     * Data de nascimento do usuário autenticado.
     *
     * @return String com a data de nascimento ou nulo
     */
    public Date getDataNascimento() {
        Date date = null;
        String decodedString = decodeSeeAlso();
        if (decodedString.contains(":")) {
            String[] parts = decodedString.split(":");
            if (parts.length > 1) {
                try {
                    date = new SimpleDateFormat("yyyyMMdd").parse(parts[1]);
                } catch (ParseException ex) {
                }
            }
        }
        return date;
    }

    /**
     * Este método decodifica o valor no campo seeAlso, o qual contém dados
     * adicionais como CPF e Data de Nascimento.
     *
     * @return String com o valor decodificado
     */
    private String decodeSeeAlso() {
        String decodedString = "";
        if (seeAlso != null && !seeAlso.isBlank()) {
            byte[] decodedBytes = Base64.getDecoder().decode(seeAlso);
            decodedString = new String(decodedBytes);
        }
        return decodedString;
    }

}
