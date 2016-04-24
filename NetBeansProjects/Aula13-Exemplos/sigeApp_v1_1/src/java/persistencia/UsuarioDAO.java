package persistencia;

import java.util.List;
import modelo.Usuario;
import org.hibernate.Session;

public class UsuarioDAO {
    private Session sessao;
    
    public UsuarioDAO() {
        sessao = HibernateUtil.getSessionFactory().getCurrentSession();
        sessao.beginTransaction();
    }
    
    public void salvar(Usuario usuario) {
        sessao.saveOrUpdate(usuario);
    }
    
    public Usuario carregar(int id) {
        return (Usuario) sessao.load(Usuario.class, id);
    }
    
    public void remover(Usuario usuario) {
        sessao.delete(usuario);
    }
    
    public List<Usuario> listar() {
        return sessao.createCriteria(Usuario.class).list();
    } 
    
    public Usuario autentica(String login, String senha) {
        return (Usuario) sessao.createQuery("FROM Usuario WHERE login=:login AND senha=:senha").setString("login", login).setString("senha", senha).uniqueResult();
    }
    
    public void encerrar() {
        sessao.getTransaction().commit();
    }
}
