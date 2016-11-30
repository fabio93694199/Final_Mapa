package beans;

import java.util.List;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import modelo.Evento;
import persistencia.EventoDAO;

@ManagedBean(name="EventoBean")
@RequestScoped
public class CadastroEventosBean {
    private Evento evento = new Evento();
    private EventoDAO dao = new EventoDAO();
    private List<Evento> listaEventos;

    public CadastroEventosBean() {
        listaEventos = dao.listar();
    }
    
    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }
    
    public List<Evento> getListaEventos() {
        return listaEventos;
    }
        
    public void salvar() {
        dao.salvar(evento);
        enviarMensagem(FacesMessage.SEVERITY_INFO, "Evento cadastrado com sucesso");
        evento = new Evento();
    }
    
    public void carregar(int id) {
        evento = dao.carregar(id);
    }
    
    public void remover(Evento evento) {
        dao.remover(evento);
        enviarMensagem(FacesMessage.SEVERITY_INFO, "Evento removido com sucesso");
    }
    
    private void enviarMensagem(Severity sev, String msg) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(sev, msg, ""));
    }
    
    @PreDestroy
    public void encerrar() {
        dao.encerrar();
    }
}
