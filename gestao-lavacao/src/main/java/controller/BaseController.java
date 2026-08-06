package controller;

import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

public abstract class BaseController extends Window {


    public abstract void onCreate();

    protected void fecharModal() {
        this.detach();
    }

    protected void mostrarErro(String mensagem) {
        Messagebox.show(mensagem, "Erro", Messagebox.OK, Messagebox.ERROR);
    }

    protected void mostrarAviso(String mensagem) {
        Messagebox.show(mensagem, "Atenção", Messagebox.OK, Messagebox.EXCLAMATION);
    }
}