package dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import javax.persistence.Id;
import javax.persistence.criteria.CriteriaQuery;
import org.hibernate.query.Query;
import org.hibernate.Session;

/**
 * DAO genérico. Esta classe não dá para ser usada diretamente. Todo DAO deve
 * extender desta classe.
 *
 * @author alison
 * @param <T>
 * @param <I>
 */
public abstract class GenericDAO<T, I extends Serializable> extends DAO {

    private final Class<T> classe;

    public GenericDAO(Class<T> classe) {
        this.classe = classe;
    }

    public Class<T> getClasse() {
        return classe;
    }

    /**
     * Salvar um objeto no banco, gerando um id para ele. Este método fará o
     * autoincremento do @Id.
     *
     * @param entidade objeto a ser inserido no banco
     * @return id do objeto inserido
     */
    public I incluirAutoincrementando(T entidade) {
        I id = null;
        Session session = null;
        try {
            session = openSession();
            beginTransaction(session);
            autoIncrementarId(session, entidade);
            id = incluir(session, entidade);
            commit(session);
        } catch (Exception e) {
            printException(e);
            rollback(session);
        } finally {
            closeSession(session);
        }
        return id;
    }

    /**
     * Salvar um objeto no banco. Este método NÃO fará o autoincremento do @Id,
     * utilize caso seu objeto utilize chave composta.
     *
     * @param entidade objeto a ser inserido no banco
     * @return id do objeto inserido
     */
    public I incluir(T entidade) {
        I id = null;
        Session session = null;
        try {
            session = openSession();
            beginTransaction(session);
            id = incluir(session, entidade);
            commit(session);
        } catch (Exception e) {
            printException(e);
            rollback(session);
        } finally {
            closeSession(session);
        }
        return id;
    }

    /**
     * Salvar um objeto no banco de forma transacional. Utilize este método caso
     * precise fazer várias alterações no banco na na mesma transação. Será
     * necessário pegar a sessão e iniciar a transação manualmente, passar a
     * sessão para este método, depois commitar ou fazer rollback, e fechar a
     * sessão.
     *
     * @param session sessão do hibernate
     * @param entidade objeto a ser inserido no banco
     * @return id do objeto inserido
     */
    public I incluir(Session session, T entidade) {
        I idEntidade = (I) session.save(entidade);
        session.flush();
        session.refresh(entidade);
        return idEntidade;
    }

    /**
     * Busca e preenche o próximo id para o objeto a ser salvo. Este método
     * utiliza a anotação @Id do objeto para saber qual é o atributo referente
     * ao id. Caso o id seja uma chave composta, esse método não funcionará.
     *
     * @param session sessão do hibernate
     * @param entidade objeto a ser inserido no banco
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public void autoIncrementarId(Session session, T entidade) throws Exception {
        Field fieldId = getIdField(entidade);
        if (fieldId != null) {
            Query q = session.createQuery("select coalesce(max(" + fieldId.getName() + "), 0) + 1 as id from " + entidade.getClass().getName());
            I idAutoincremento = (I) q.uniqueResult();
            fieldId.set(entidade, idAutoincremento);
        }
    }

    /**
     * Retorna o atributo do objeto que tem a anotação @id. Utilizado para pegar
     * o id do objeto e realizar o autoincremento ou o seu valor.
     *
     * @param entidade objeto a ser buscado o atributo id
     * @return field que tem o @id anotado na classe modelo
     */
    private Field getIdField(T entidade) {
        Field fieldId = null;
        for (Field field : entidade.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Id.class) != null) {
                field.setAccessible(true);
                fieldId = field;
                break;
            }
        }
        return fieldId;
    }

    /**
     * Exclui um objeto do banco.
     *
     * @param entidade objeto a ser excluído
     * @return true caso sucesso, false caso ocorra algum erro
     */
    public boolean excluir(T entidade) {
        boolean excluded = false;
        Session session = null;
        try {
            session = openSession();
            beginTransaction(session);
            excluir(session, entidade);
            commit(session);
            excluded = true;
        } catch (Exception e) {
            printException(e);
            rollback(session);
        } finally {
            closeSession(session);
        }
        return excluded;
    }

    /**
     * Exclui um objeto do banco de forma transacional. Utilize este método caso
     * precise fazer várias alterações no banco na na mesma transação. Será
     * necessário pegar a sessão e iniciar a transação manualmente, passar a
     * sessão para este método, depois commitar ou fazer rollback, e fechar a
     * sessão.
     *
     * @param session sessão do hibernate
     * @param entidade objeto a ser excluído
     */
    public void excluir(Session session, T entidade) {
        session.delete(entidade);
        session.flush();
    }

    /**
     * Exclui um objeto do banco passando um id. Utilize este método caso você
     * não tenho o objeto que veio do banco de dados, ou seja, tenha apenas o
     * valor do id.
     *
     * @param codigo id do objeto a ser excluído
     * @return true caso sucesso, false caso ocorra algum erro
     */
    public boolean excluir(I codigo) {
        boolean excluded = false;
        Session session = null;
        try {
            session = openSession();
            beginTransaction(session);
            excluir(session, codigo);
            commit(session);
            excluded = true;
        } catch (Exception e) {
            printException(e);
            rollback(session);
        } finally {
            closeSession(session);
        }
        return excluded;
    }

    /**
     * Exclui um objeto do banco de forma transacional, passando um id. Utilize
     * este método caso você não tenho o objeto que veio do banco de dados, ou
     * seja, tenha apenas o valdor do id. Utilize este método caso precise fazer
     * várias alterações no banco na na mesma transação. Será necessário pegar a
     * sessão e iniciar a transação manualmente, passar a sessão para este
     * método, depois commitar ou fazer rollback, e fechar a sessão.
     *
     * @param session sessão do hibernate
     * @param codigo id do objeto a ser excluído
     */
    public void excluir(Session session, I codigo) {
        T genericClass = (T) session.get(classe, codigo);
        if (genericClass.getClass().getName() != null) {
            session.delete(genericClass);
        }
    }

    /**
     * Atualiza um objeto no banco.
     *
     * @param entidade objeto a ser atualizado
     * @return true caso sucesso, false caso ocorra algum erro
     */
    public boolean atualizar(T entidade) {
        boolean updated = false;
        Session session = null;
        try {
            session = openSession();
            beginTransaction(session);
            atualizar(session, entidade);
            commit(session);
            updated = true;
        } catch (Exception e) {
            printException(e);
            rollback(session);
        } finally {
            closeSession(session);
        }
        return updated;
    }

    /**
     * Atualiza um objeto no banco de forma transacional. Utilize este método
     * caso precise fazer várias alterações no banco na na mesma transação. Será
     * necessário pegar a sessão e iniciar a transação manualmente, passar a
     * sessão para este método, depois commitar ou fazer rollback, e fechar a
     * sessão.
     *
     * @param session sessão do hibernate
     * @param entidade objeto a ser atualizado
     */
    public void atualizar(Session session, T entidade) {
        session.update(entidade);
        session.flush();
        session.refresh(entidade);
    }

    /**
     * Buscar um objeto no banco de dados.
     *
     * @param codigo id do objeto a ser encontrado
     * @return o objeto encontrado desejada
     */
    public T buscar(I codigo) {
        T object = null;
        Session session = null;
        try {
            session = openSession();
            object = (T) session.get(classe, codigo);
        } catch (Exception e) {
            printException(e);
        } finally {
            closeSession(session);
        }
        return object;
    }

    /**
     * Lista todos os objetos existentes no banco da classe desesejada.
     *
     * @return lista de objetos
     */
    public List<T> listar() {
        List<T> objs = null;
        Session session = null;
        try {
            session = openSession();
            CriteriaQuery<T> criteria = session.getCriteriaBuilder().createQuery(classe);
            criteria.select(criteria.from(classe));
            Query<T> query = session.createQuery(criteria);

            printQuery(query);

            objs = query.list();
        } catch (Exception e) {
            printException(e);
        } finally {
            closeSession(session);
        }
        return objs;
    }

    /**
     * Lista os objetos da classe Curso do banco de dados baseado em um filtro e
     * ordem.
     *
     * @param filtro condições que irá no where, em hql
     * @param ordem condições que irá no order by, em hql
     * @return lista de objetos
     */
    public List<T> listar(String filtro, String ordem) {
        List<T> objs = null;
        Session session = null;
        try {
            session = openSession();
            Query query = session.createQuery("from " + classe.getSimpleName() + " t where " + filtro + " " + ordem);

            printQuery(query);

            objs = query.list();
        } catch (Exception e) {
            printException(e);
        } finally {
            closeSession(session);
        }
        return objs;
    }

    /**
     * Lista os objetos da classe Curso do banco de dados baseado em um filtro e
     * ordem de forma paginada.
     *
     * @param filtro condições que irá no where, em hql
     * @param ordem condições que irá no order by, em hql
     * @param page é o offset, ou seja, inicio dos dados a serem buscados
     * @param pageSize é o limit, ou seja, quantidade de dados a serem buscados
     * @return lista de objetos
     */
    public List<T> listarPaginado(String filtro, String ordem, int page, int pageSize) {
        List<T> objs = null;
        Session session = null;
        try {
            session = openSession();
            Query query = session.createQuery("from " + classe.getSimpleName() + " t where " + filtro + " " + ordem);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);

            printQuery(query);

            objs = query.list();
        } catch (Exception e) {
            printException(e);
        } finally {
            closeSession(session);
        }
        return objs;
    }

    /**
     * Quantidade de dados encontrados na tabela.
     *
     * @param filtro condições que irá no where, em hql
     * @return quantidade de dados encontrados
     */
    public int contar(String filtro) {
        int count = 0;
        Session session = null;
        try {
            session = openSession();
            Query query = session.createQuery("select count(*) from " + classe.getSimpleName() + " t where " + filtro);

            printQuery(query);

            count = ((Long) query.uniqueResult()).intValue();
        } catch (Exception e) {
            printException(e);
        } finally {
            closeSession(session);
        }
        return count;
    }

    /**
     * Executa um Select em HQL.
     *
     * @param hql sql em hql a ser executado o select
     * @return lista de objetos
     */
    public List<Object[]> executarSelectHql(String hql) {
        List<Object[]> objs = null;
        Session session = null;
        try {
            session = openSession();
            Query query = session.createQuery(hql);

            printQuery(query);

            objs = query.list();
        } catch (Exception e) {
            printException(e);
        } finally {
            closeSession(session);
        }
        return objs;
    }

    /**
     * Executa um Select em SQL puro.
     *
     * @param sql sql puro a ser executado o select
     * @return lista de objetos
     */
    public List<Object[]> executarSelectSql(String sql) {
        List<Object[]> objs = null;
        Session session = null;
        try {
            session = openSession();
            Query query = session.createNativeQuery(sql);
            objs = query.list();
        } catch (Exception e) {
            printException(e);
        } finally {
            closeSession(session);
        }
        return objs;
    }

    /**
     * Executa um Update em SQL puro.
     *
     * @param sql sql puro a ser executado o update
     * @return número de linhas atualizadas pelo update
     */
    public int executarUpdateSql(String sql) {
        int result = 0;
        Session session = null;
        try {
            session = openSession();
            beginTransaction(session);
            result = executarUpdateSql(session, sql);
            commit(session);
        } catch (Exception e) {
            printException(e);
            rollback(session);
        } finally {
            closeSession(session);
        }
        return result;
    }

    /**
     * Executa um Update em SQL puro.
     *
     * @param session sessão do hibernate
     * @param sql sql puro a ser executado o update
     * @return número de linhas atualizadas pelo update
     */
    public int executarUpdateSql(Session session, String sql) {
        int result = 0;
        try {
            result = session.createNativeQuery(sql).executeUpdate();
        } catch (Exception e) {
            printException(e);
        }
        return result;
    }

}