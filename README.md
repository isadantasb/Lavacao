
# mavenbootstrap

Projeto em Maven para desenvolvimento em Java utilizando ZK Framework com Bootstrap para desenvolvimento *frontend* e Hibernate para comunicação com o banco de dados. 

#### ZK Framework e Bootstrap

-   Para desenvolvimento _front-end_ utiliza-se o [ZK Framework 9](https://www.zkoss.org) para manipulação dos elementos DOM, isso significa que, para criar os _inputs_ e _buttons_ (elementos HTML que contém valores e ações) em vez de utilizar as tags HTML você utilizará as tags XML do ZK. A vantagem é que você terá um desenvolvimento mais rápido sem precisar saber JavaScript, pois apenas com Java e ZK todas as manipulações dos elementos do _front-end_ acontecerá no _back-end_ utilizando-se [Ajax](https://pt.wikipedia.org/wiki/Ajax_%28programa%C3%A7%C3%A3o%29). Para saber todas as funcionalidades que o ZK oferece acesse o _demo_ em [https://www.zkoss.org/zkdemo/](https://www.zkoss.org/zkdemo/).
-   Para desenvolvimento de layouts responsivos, utiliza-se o [Bootstrap 5](https://getbootstrap.com), o qual tem um sistema de *grid* possibilitando especificar para cada tamanho de tela qual o espaço que cada componente irá ocupar. Utilizamos também o CSS do Bootstrap para aperfeiçoar os componentes ZK como *inputs*, *selectbox* e *buttons*, além de utilizar o JavaScript do Bootstrap para incrementar as funcionalidades.

#### Java

-   Para desenvolvimento utiliza-se o [JDK 11](https://www.oracle.com/java/technologies/downloads/#java11).

#### Servidor

-   Necessário o [Apache Tomcat 9](https://tomcat.apache.org/download-90.cgi) rodando com Java 11. Recomenda-se também adicionar os seguinte parâmetros em "VM Options" nas propriedades do Tomcat: `-Xms768m -Xmx1024m -XX:PermSize=256M -XX:MaxPermSize=512m`

#### Banco de Dados

-   Está sendo utilizado a base "desenvol1" do banco de dados DB2. Favor alterar o arquivo `src/main/resources/hibernate.cfg.xml` para a base de desenvolvimento de sua equipe.

#### IDE

-   Se usar Netbeans, apenas o [Apache Netbeans](https://netbeans.apache.org/) versão **[12](https://netbeans.apache.org/download/index.html)** ou superior suporta JDK 11 e Tomcat 9. Para instalar o [plugin do ZK](http://plugins.netbeans.org/plugin/52406/rem7-0-0ce) necessita deste [plugin adicional](http://137.254.56.27/nexus/content/groups/netbeans/org/netbeans/api/org-jdesktop-layout/RELEASE82/org-jdesktop-layout-RELEASE82.nbm).
-   Se usar Eclipse, utilize a versão "[Eclipse IDE for Java EE Developers](https://www.eclipse.org/downloads/packages/release/kepler/sr2/eclipse-ide-java-ee-developers)" ou a versão [tradicional](https://www.eclipse.org/downloads/) porém deve-se instalar todos os pacotes "m2e..." e "web..." em Help -> Install New Software. Em Help -> Eclipse Marktplace instale o [ZK Studio](https://www.zkoss.org/product/zkstudio).
-   É possível facilmente utilizar outros editores como [IntelliJ IDEA](https://www.jetbrains.com/idea/), [VSCode](https://code.visualstudio.com/), etc. pois o projeto Maven é independente de IDE e já está configurado no .gitignore para ignorar os arquivos de controle que são automaticamente gerados pelas principais ferramentas de desenvolvimento.

