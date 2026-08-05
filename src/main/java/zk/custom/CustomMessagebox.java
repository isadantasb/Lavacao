/*
 * Universidade Estadual de Maringá - UEM
 * Núcleo de Processamento de Dados - NPD
 * Copyright (c) 2021. All rights reserved.
 */
package zk.custom;

import java.io.Serializable;

import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;

/**
 * Extended messagebox que permite mensagem com quebra de linha e tem visual
 * customizado com Bootstrap. <br>
 * Linhas podem ser quebradas com \n . <br>
 * <br>
 *
 * @author alison
 */
public class CustomMessagebox extends Messagebox implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient static String _templ = "/zk_custom/customMessagebox.zul";

    public CustomMessagebox() {
    }

    public static void doSetTemplate() {
        setTemplate(_templ);
    }

    /**
     * Shows a message box and returns what button is pressed.
     *
     * @param title the title. If null, {@link WebApp#getAppName} is used.
     * @param buttons a combination of {@link #OK}, {@link #CANCEL},
     * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY}, and
     * {@link #IGNORE}. If zero, {@link #OK} is assumed
     * @param icon one of predefined images: {@link #QUESTION},
     * {@link #EXCLAMATION}, {@link #ERROR}, {@link #NONE}, or any style class
     * name(s) to show an image.
     * @param focus one of button to have to focus. If 0, the first button will
     * gain the focus. One of {@link #OK}, {@link #CANCEL},
     * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY}, and
     * {@link #IGNORE}.
     * @param listener the event listener which is invoked when a button is
     * clicked. Ignored if null. It is useful if the event processing thread is
     * disabled ({@link org.zkoss.zk.ui.util.Configuration#enableEventThread}).
     * If the event processing thread is disabled, this method always return
     * {@link #OK}. To know which button is pressed, you have to pass an event
     * listener. Then, when the user clicks a button, the event listener is
     * invoked. You can identify which button is clicked by examining the event
     * name ({@link org.zkoss.zk.ui.event.Event#getName}) as shown in the
     * following table. Alternatively, you can examine the value of
     * {@link org.zkoss.zk.ui.event.Event#getData}, which must be an integer
     * representing the button, such as {@link #OK}, {@link #YES} and so on.
     * <table border="1">
     * <tr><td>Button</td><td>Event Name</td></tr>
     * <tr><td>OK</td><td>onOK ({@link #ON_OK})</td></tr>
     * <tr><td>Cancel</td><td>onCancel ({@link #ON_CANCEL})</td></tr>
     * <tr><td>Yes</td><td>onYes ({@link #ON_YES})</td></tr>
     * <tr><td>No</td><td>onNo ({@link #ON_NO})</td></tr>
     * <tr><td>Retry</td><td>onRetry ({@link #ON_RETRY})</td></tr>
     * <tr><td>Abort</td><td>onAbort ({@link #ON_ABORT})</td></tr>
     * <tr><td>Ignore</td><td>onIgnore ({@link #ON_IGNORE})</td></tr>
     * </table>
     * @return the button being pressed (one of {@link #OK}, {@link #CANCEL},
     * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY}, and
     * {@link #IGNORE}). Note: if the event processing thread is disabled, it
     * always returns {@link #OK}.
     * @since 3.0.4
     */
    public static final int show(String message, String title, int buttons, String icon, boolean padding) {

        doSetTemplate();
        message = setPadding(message, padding);
        icon = setIcon(icon);

        return show(message, title, buttons, icon, 0, null);
    }

    /**
     * Shows a message box and returns what button is pressed.
     *
     * @param title the title. If null, {@link WebApp#getAppName} is used.
     * @param buttons a combination of {@link #OK}, {@link #CANCEL},
     * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY}, and
     * {@link #IGNORE}. If zero, {@link #OK} is assumed
     * @param icon one of predefined images: {@link #QUESTION},
     * {@link #EXCLAMATION}, {@link #ERROR}, {@link #NONE}, or any style class
     * name(s) to show an image.
     * @param focus one of button to have to focus. If 0, the first button will
     * gain the focus. One of {@link #OK}, {@link #CANCEL},
     * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY}, and
     * {@link #IGNORE}.
     * @param listener the event listener which is invoked when a button is
     * clicked. Ignored if null. It is useful if the event processing thread is
     * disabled ({@link org.zkoss.zk.ui.util.Configuration#enableEventThread}).
     * If the event processing thread is disabled, this method always return
     * {@link #OK}. To know which button is pressed, you have to pass an event
     * listener. Then, when the user clicks a button, the event listener is
     * invoked. You can identify which button is clicked by examining the event
     * name ({@link org.zkoss.zk.ui.event.Event#getName}) as shown in the
     * following table. Alternatively, you can examine the value of
     * {@link org.zkoss.zk.ui.event.Event#getData}, which must be an integer
     * representing the button, such as {@link #OK}, {@link #YES} and so on.
     * <table border="1">
     * <tr><td>Button</td><td>Event Name</td></tr>
     * <tr><td>OK</td><td>onOK ({@link #ON_OK})</td></tr>
     * <tr><td>Cancel</td><td>onCancel ({@link #ON_CANCEL})</td></tr>
     * <tr><td>Yes</td><td>onYes ({@link #ON_YES})</td></tr>
     * <tr><td>No</td><td>onNo ({@link #ON_NO})</td></tr>
     * <tr><td>Retry</td><td>onRetry ({@link #ON_RETRY})</td></tr>
     * <tr><td>Abort</td><td>onAbort ({@link #ON_ABORT})</td></tr>
     * <tr><td>Ignore</td><td>onIgnore ({@link #ON_IGNORE})</td></tr>
     * </table>
     * @return the button being pressed (one of {@link #OK}, {@link #CANCEL},
     * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY}, and
     * {@link #IGNORE}). Note: if the event processing thread is disabled, it
     * always returns {@link #OK}.
     * @since 3.0.4
     */
    public static final int show(String message, String title, int buttons, String icon, boolean padding, EventListener listener) {

        doSetTemplate();
        message = setPadding(message, padding);
        icon = setIcon(icon);

        return show(message, title, buttons, icon, 0, listener);
    }

    private static String setIcon(String iconType) {
        if (iconType.equals(Messagebox.QUESTION)) {
            return "z-messagebox-icon z-icon-question-circle fa-3x text-primary";
        } else if (iconType.equals(Messagebox.EXCLAMATION)) {
            return "z-messagebox-icon z-icon-exclamation-triangle fa-3x text-warning";
        } else if (iconType.equals(Messagebox.INFORMATION)) {
            return "z-messagebox-icon z-icon-exclamation-circle fa-3x text-primary";
        } else if (iconType.equals(Messagebox.ERROR)) {
            return "z-messagebox-icon z-icon-ban fa-3x text-danger";
        } else {
            return "";
        }
    }

    private static String setPadding(String message, Boolean padding) {
        if (padding == true) {
            message = "\n" + message + "\n\n\n";
        } else {
            message = message + "\n\n";
        }
        return message;
    }

}
