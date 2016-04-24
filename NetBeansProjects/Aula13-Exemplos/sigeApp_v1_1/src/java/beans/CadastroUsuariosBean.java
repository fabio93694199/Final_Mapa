package beans;

import java.util.List;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import modelo.Usuario;
import persistencia.UsuarioDAO;

@ManagedBean(name="usuarioBean")
@RequestScoped
public class CadastroUsuariosBean {
    private Usuario usuario = new Usuario();
    private UsuarioDAO dao = new UsuarioDAO();
    private List<Usuario> listaUsuarios;

    public CadastroUsuariosBean() {
        listaUsuarios = dao.listar();
    }
    
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public List<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }
    
    public void salvar() {
        dao.salvar(usuario);
        enviarMensagem(FacesMessage.SEVERITY_INFO, "Usuário cadastrado com sucesso");
        usuario = new Usuario();
    }
    
    public void carregar(int id) {
        usuario = dao.carregar(id);
    }
    
    public void remover(Usuario usuario) {
        dao.remover(usuario);
        enviarMensagem(FacesMessage.SEVERITY_INFO, "Usuário removido com sucesso");
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
