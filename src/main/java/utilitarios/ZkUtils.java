package utilitarios;

import org.zkoss.zul.Messagebox;
import zk.custom.CustomMessagebox;


public class ZkUtils {
    public static final String SISTEMA = "Sistema";

    public static void MensagemErro(String mensagem) {
        if (!mensagem.isEmpty()) {
            CustomMessagebox.show(mensagem, SISTEMA, CustomMessagebox.OK, Messagebox.ERROR, false);
        }
    }
}
