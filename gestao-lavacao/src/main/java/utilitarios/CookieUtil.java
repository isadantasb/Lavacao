/*
 * Universidade Estadual de Maringá - UEM
 * Núcleo de Processamento de Dados - NPD
 * Copyright (c) 2020. All rights reserved.
 */
package utilitarios;

import com.itextpdf.xmp.impl.Base64;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.zkoss.zk.ui.Executions;

/**
 *
 * @author alison
 */
public class CookieUtil {

    private static Integer MAX_AGE = 60 * 60 * 24 * 365;
    private static String COMMENT = "Cookie de controle do sistema UEM";
    private static String COOKIE_PREFIX = "siga";

    /**
     * Salva um cookie para o cliente. Por padrão esse cookie irá durar 1 ano
     * caso nunca seja atualizado.
     *
     * @param name Nome do cookie. Ao salvar o nome será encodado em base58 e
     * adicionao "siga" na frente
     * @param value Valor da informação que será guardada no cookie. O valor
     * será encodado em base64
     */
    public static void setCookie(String name, String value) {
        Cookie cookie = new Cookie(encodeName(name), encodeValue(value));
        configDefalts(cookie);
        ((HttpServletResponse) Executions.getCurrent().getNativeResponse()).addCookie(cookie);
    }

    /**
     * Salva um cookie para o cliente
     *
     * @param name Nome do cookie. Ao salvar o nome será encodado em base58 e
     * adicionao "siga" na frente
     * @param value Valor da informação que será guardada no cookie. O valor
     * será encodado em base64
     * @param maxAge O tempo que o cookie irá durar até expirar, em segundos. O
     * valor 0 significa que irá expirar imediatamente. Um valor negativo
     * significa que o cookie irá expirar assim que o navegador for fechado.
     * Para durar para sempre coloque um valor grande, por exemplo, 1 ano = 60 *
     * 60 * 24 * 365 = 31536000
     */
    public static void setCookie(String name, String value, Integer maxAge) {
        Cookie cookie = new Cookie(encodeName(name), encodeValue(value));
        configDefalts(cookie);
        cookie.setMaxAge(maxAge);
        ((HttpServletResponse) Executions.getCurrent().getNativeResponse()).addCookie(cookie);
    }

    /**
     * Retorna o valor de um cookie salvo no cliente
     *
     * @param name Nome do cookie
     * @return String com o valor do cookie
     */
    public static String getCookie(String name) {
        Cookie[] cookies = ((HttpServletRequest) Executions.getCurrent().getNativeRequest()).getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(encodeName(name))) {
                    return decodeValue(cookie.getValue());
                }
            }
        }
        return null;
    }

    /**
     * Coloca os valores padrões de configuração do cookie, como por exemplo o
     * tempo de vida dele
     *
     * @param cookie O cookie a ser configurado
     */
    private static void configDefalts(Cookie cookie) {
        cookie.setComment(COMMENT);
        cookie.setMaxAge(MAX_AGE);
        cookie.setSecure(true);
    }

    private static String encodeName(String name) {
        return COOKIE_PREFIX +java.util.Base64.getEncoder().encodeToString(name.getBytes());
    }

    private static String encodeValue(String value) {
        return Base64.encode(value);
    }

    private static String decodeValue(String value) {
        return Base64.decode(value);
    }
}
