package persistencia;

import java.util.List;
import modelo.Evento;
import org.hibernate.Session;

public class EventoDAO {
    private Session sessao;
    
    public EventoDAO() {
        sessao = HibernateUtil.getSessionFactory().getCurrentSession();
        sessao.beginTransaction();
    }
    
    public void salvar(Evento e) {
        sessao.saveOrUpdate(e);
    }
    
    public Evento carregar(int id) {
        return (Evento) sessao.load(Evento.class, id);
    }
    
    public void remover(Evento e) {
        sessao.delete(e);
    }
    
    public List<Evento> listar() {
        return sessao.createCriteria(Evento.class).list();
    } 
    
    public void encerrar() {
        sessao.getTransaction().commit();
    }
}
