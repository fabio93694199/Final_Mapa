package beans;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import modelo.Usuario;
import persistencia.UsuarioDAO;

@ManagedBean
@SessionScoped
public class LoginBean {
    private Usuario usuario = new Usuario();

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public String autenticar() {
        UsuarioDAO dao = new UsuarioDAO();
        usuario = dao.autentica(usuario.getLogin(), usuario.getSenha());
        if(usuario == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login ou senha incorretos.", ""));
            usuario = new Usuario();
            return "index";
        } else {
            return "principal";
        }
    }

}
