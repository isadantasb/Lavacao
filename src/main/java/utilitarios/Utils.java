package utilitarios;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author equipes
 */
public class Utils {

    private String caminhoReal = "";
    private static final String EMAIL_REGEX = "^[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]{2,7}$";
    private Pattern p = Pattern.compile(EMAIL_REGEX);

    /**
     * Cria a nova instancia do Utils
     */
    public Utils() {
    }

    public String hora() {
        Calendar c = Calendar.getInstance();
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        String numero = String.valueOf(hora) + "" + String.valueOf(min) + "" + String.valueOf(sec);

        return (numero);

    }

    //String para data
    public Date strToDate(String data_normal) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Date dt = null;
        try {
            dt = sdf.parse(data_normal);
        } catch (ParseException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return dt;
    }

    //String para data com horas completas
    public Date strToDateTime(String data_completa) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

        Date dt = null;
        try {
            dt = sdf.parse(data_completa);
        } catch (ParseException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return dt;
    }

    public String converteData(String data) {

        // converte data no formata dd-mm-aaaa para aaaa-mm-dd
        String[] sPartes = new String[3];

        if (data.length() == 10) {

            sPartes = data.split("[-./]");

            if (sPartes[2].length() < 4) { // se a última parte não for o ano
                return data;
            }
            return sPartes[2] + "-" + sPartes[1] + "-" + sPartes[0];
        }
        return null;
    }

    public int diferencaEntreData(String dt_inicio, String dt_termino) {

        // datas no formato aaaa-mm-dd
        int[] dia_do_mes = new int[13]; // array para conter qtos dias tem o mês
        int nu_dias = 0; // retorna o número de dias entre as datas
        String[] sPartes = new String[3]; // array para conter o ano, mes e dia da data
        int dia_i, mes_i, ano_i = 0;
        int dia_t, mes_t, ano_t = 0;

        dia_do_mes[1] = 31;
        dia_do_mes[2] = 28;
        dia_do_mes[3] = 31;
        dia_do_mes[4] = 30;
        dia_do_mes[5] = 31;
        dia_do_mes[6] = 30;
        dia_do_mes[7] = 31;
        dia_do_mes[8] = 31;
        dia_do_mes[9] = 30;
        dia_do_mes[10] = 31;
        dia_do_mes[11] = 30;
        dia_do_mes[12] = 31;

        dt_inicio = converteData(dt_inicio);
        dt_termino = converteData(dt_termino);

        if ((dt_inicio.length() == 10) && (dt_termino.length() == 10)) {

            sPartes = dt_inicio.split("[-./]"); // separa o ano, mês e dia da data inicial

            dia_i = Integer.parseInt(sPartes[2]); // converte o dia da data inicial para inteiro
            mes_i = Integer.parseInt(sPartes[1]); // converte o mês da data inicial para inteiro
            ano_i = Integer.parseInt(sPartes[0]); // converte o ano da data inicial para inteiro

            sPartes = dt_termino.split("[-./]"); // separa o ano, mês e dia da data final

            dia_t = Integer.parseInt(sPartes[2]); // converte o dia da data final para inteiro
            mes_t = Integer.parseInt(sPartes[1]); // converte o mês da data final para inteiro
            ano_t = Integer.parseInt(sPartes[0]); // converte o ano da data final para inteiro

            while (ano_i <= ano_t) {
                if (ano_i == ano_t) {
                    if (mes_i == mes_t) {
                        if ((nu_dias == 0) && (dia_i > dia_t)) {
                            return 0;
                        }
                        if (dia_t > dia_do_mes[mes_t]) {
                            if (((ano_i % 4) == 0) && (mes_i == 2)) {
                                nu_dias += 29;
                            } else {
                                nu_dias += dia_do_mes[mes_t];
                            }
                        } else {
                            nu_dias += dia_t - dia_i;
                        }
                        ano_i += 1;
                    } else if (mes_i > mes_t) {
                        return 0;
                    } else {
                        if (((ano_i % 4) == 0) && (mes_i == 2)) { // ano bissexto
                            if (nu_dias == 0) {
                                nu_dias = (29 - dia_i) + 1;
                            } else {
                                nu_dias += 29;
                            }
                        } else {
                            if (nu_dias == 0) {
                                nu_dias = (dia_do_mes[mes_i] - dia_i) + 1;
                            } else {
                                nu_dias += dia_do_mes[mes_i];
                            }
                        }
                    }
                } else if (ano_i < ano_t) {
                    if (((ano_i % 4) == 0) && (mes_i == 2)) { // ano bissexto
                        if (nu_dias == 0) {
                            nu_dias = (29 - dia_i) + 1;
                        } else {
                            nu_dias += 29;
                        }
                    } else {
                        if (nu_dias == 0) {
                            nu_dias = (dia_do_mes[mes_i] - dia_i) + 1;
                        } else {
                            nu_dias += dia_do_mes[mes_i];
                        }
                    }
                }
                mes_i += 1;
                if (mes_i == 13) {
                    mes_i = 1;
                    ano_i += 1;
                }
            }
        }
        return nu_dias;
    }

    public String criptografaMD5(String chave) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        BigInteger hash = new BigInteger(1, md.digest(chave.getBytes()));
        String s = hash.toString(16);
        if (s.length() % 2 != 0) {
            s = "0" + s;
        }
        return s;
    }

    public String normalizaTexto(String texto) {
        if (texto != null) {
            return maiusculas(removeEspacos(removeSignos(texto)));
        } else {
            return "";
        }

    }

    public String removeEspacos(String texto) {
        if (texto != null) {
            //texto = removeSignos(texto,false);
            texto = texto.replaceAll("\\s+", " ");
            // Remover o espaço no início
            texto = texto.replaceAll("^\\s+", "");
            // Remover o espaço no fim
            texto = texto.replaceAll("\\s+$", "");
            return texto;
        } else {
            return "";
        }
    }

    public String truncar(String texto, int tamanho) {
        if (texto != null) {
            if (texto.length() > tamanho) {
                texto = texto.substring(0, tamanho);
            }

            return texto;
        } else {
            return "";
        }
    }

    public String zerosEsquerda(String texto, int tamanho) {
        if (texto != null) {
            int tt = texto.length();

            if (tt < tamanho) {
                for (int i = 0; i < (tamanho - tt); i++) {
                    texto = "0" + texto;
                }
            }
            return texto;
        } else {
            return "";
        }
    }

    public String zerosDireita(String texto, int tamanho) {
        if (texto != null) {
            int tt = texto.length();

            if (tt < tamanho) {
                for (int i = 0; i < (tamanho - tt); i++) {
                    texto = texto + "0";
                }
            }
            return texto;
        } else {
            return "";
        }
    }

    public String removeSignos(String texto) {
        if (texto != null) {
            //String ConSignos ="áàäâãéèëêíìïîĩóòöôõúùüûũñÃÂÀÄÁÂÉÈËÊÎÍÌĨÔÓÒÖÕÚÙÜÛŨÑçÇ~'\"`!@#$%^&*()-_+=,./;:[]{}\\|?";
            //String SinSignos ="aaaaaeeeeiiiiiooooouuuuunAAAAAAEEEEIIIIOOOOOUUUUUNcC                              ";
            String ConSignos = "áàäâãéèëêíìïîĩóòöôõúùüûũñÃÂÀÄÁÂÉÈËÊÎÍÌĨÔÓÒÖÕÚÙÜÛŨÑçÇ~\"`!@#$%^&*_+=;:[]{}\\|?";
            String SinSignos = "aaaaaeeeeiiiiiooooouuuuunAAAAAAEEEEIIIIOOOOOUUUUUNcC                       ";
            int v;

            for (v = 0; v < SinSignos.length(); v++) {

                String i = ConSignos.substring(v, v + 1);
                String j = SinSignos.substring(v, v + 1);
                texto = texto.replace(i, j);
            }
            return removeCaracterIlegal(texto);
        } else {
            return "";
        }
    }

    public String removeCaracterIlegal(String frase) {
        //Retorna a Frase dada sem caracteres estranhos;
        String validos = " abcdefghijklmnopqrstuvxywzABCDEFGHIJKLMNOPQRSTUVXYWZ()/_.,;:+-%º\"*&@?0123457689àâêôûãõáéíóúçÀÂÊÔÛÃÕÁÉÍÓÚÇ'\\|";
        int i;
        String retorno = "";
        for (i = 0; i < frase.length(); i++) {
            if (validos.indexOf(frase.charAt(i)) > -1) {
                retorno += frase.charAt(i);
            }
        }
        return retorno;
    }

    public String removeSignos(String texto, boolean manterAcentos) {
        if (texto != null) {
            String ConSignos = "";
            String SinSignos = "";

            if (manterAcentos) {
                ConSignos = "áàäâéèëêíìïîóòöôúùüûÂÀÄÁÂÉÈËÊÎÍÌÔÓÒÖÚÙÜÛçÇ'\"`!@#$%^&*()-_+=,./;:[]{}\\|?";
                SinSignos = "aaaaeeeeiiiioooouuuuAAAAAEEEEIIIOOOOUUUUcC                             ";
            } else {
                ConSignos = "'\"`!#$%^&*()_+=,/;:[]{}\\|?";
                SinSignos = "                          ";
            }
            int v;

            for (v = 0; v < SinSignos.length(); v++) {

                String i = ConSignos.substring(v, v + 1);
                String j = SinSignos.substring(v, v + 1);
                texto = texto.replace(i, j);
            }
            return texto;
        } else {
            return "";
        }
    }

    public String maiusculas(String texto) {
        if (texto != null) {
            return texto.toUpperCase();
        } else {
            return "";
        }
    }

    public String plic(String s) {
        if (s == null) {
            return "''";
        } else {
            if (s.contains("'")) {
                s = s.replace("'", "''");
            }
            return "'" + s.trim() + "'";
        }
    }

    public String alerta(String s) {
        return "<script> alert(\"" + s + "\");</script>";
    }

    public String dataBanco(String dtNormal) {
        if (dtNormal != null && !dtNormal.equals("")) {
            int b1 = dtNormal.indexOf("/");
            int b2 = dtNormal.indexOf("/", b1 + 1);

            String dtBanco = dtNormal.substring(b2 + 1, b2 + 5) + "-"
                    + dtNormal.substring(b1 + 1, b2) + "-" + dtNormal.substring(0, 2) + " ";

            if (dtNormal.length() > 10) {
                dtBanco += dtNormal.substring(b2 + 6);
            } else if (dtBanco.length() == 10) {
                dtBanco += "00:00:00";
            }

            return dtBanco;
        }
        return dtNormal;
    }

    public String dataNormal(String dtBanco) {
        if (dtBanco != null && !dtBanco.equals("")) {
            int b1 = dtBanco.indexOf("-");
            int b2 = dtBanco.indexOf("-", b1 + 1);
            String dtNormal = "";
            if (dtBanco.length() > 10) {
                dtNormal = dtBanco.substring(b2 + 1, b2 + 3) + "/"
                        + dtBanco.substring(b1 + 1, b2) + "/" + dtBanco.substring(0, 4) + " "
                        + dtBanco.substring(b2 + 4, b2 + 12);
            } else if (dtBanco.length() == 10) {
                dtNormal = dtBanco.substring(b2 + 1, b2 + 3) + "/"
                        + dtBanco.substring(b1 + 1, b2) + "/" + dtBanco.substring(0, 4);

            }
            return dtNormal;
        }
        return dtBanco;

    }

    public String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String getDateTime(String mascara) {
        DateFormat dateFormat = new SimpleDateFormat(mascara);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public int strToIntDef(String valor, int def) {
        int vlr = 0;
        try {
            vlr = Integer.parseInt(valor);
        } catch (Exception ex) {
            return def;
        }
        return vlr;
    }

    public int strToInt(String valor) {
        int vlr = 0;
        try {
            vlr = Integer.parseInt(valor);
        } catch (Exception ex) {
            return 0;
        }
        return vlr;
    }

    public String getPathArquivos() {
        String path = caminhoReal + "/relatorios/";
        return path;
    }

//    public String getStringSeguranca(String tp_solicitacao, String ano_letivo, String cd_aluno,
//            String dt_ano_ingres, String dh_solicitacao) {
//        String seg = tp_solicitacao + ano_letivo + "acculolracnaig"
//                + cd_aluno + dt_ano_ingres + soNumeros(dh_solicitacao);
//        return seg;
//    }
    public String textoCapitulado(String frase) {
        /**
         * Retorna um texto com as iniciais maiúsculas
         */
        if (frase != null) {
            frase = frase.trim();
            frase = frase.replace(".", ". ");
            frase = frase.replace("  ", " ");
            frase = maiusculaMinuscula(frase);
            frase = frase.replace(" E ", " e ");
            frase = frase.replace(" A ", " a ");
            frase = frase.replace(" Da ", " da ");
            frase = frase.replace(" Das ", " das ");
            frase = frase.replace(" De ", " de ");
            frase = frase.replace(" Do ", " do ");
            frase = frase.replace(" Dos ", " dos ");
            frase = frase.replace("Ii", "II");
            frase = frase.replace("Iii", "III");
            frase = frase.replace(" Iv ", " IV ");
            frase = frase.replace(" Vi ", " VI ");
            frase = frase.replace(" Vii", " VII");
            frase = frase.replace(" Viii", " VIII");
            frase = frase.replace(" Ix ", " IX ");
            frase = frase.replace(" Xi ", " Xi ");
            frase = frase.replace(" Xii", " XII");
            frase = frase.replace(" Xiii", " XIII");
            return frase;
        } else {
            return "";
        }
    }

    private String maiusculaMinuscula(String frase) {
        /**
         * Converte uma string para todas minusculas e a primeira maiuscula
         */
        char[] retorno = null;
        String caracter = "";
        if (frase != null && !frase.equals("")) {

            try {
                frase = frase.toLowerCase();
                retorno = frase.toCharArray();
                caracter = String.copyValueOf(retorno, 0, 1);
                caracter = caracter.toUpperCase();
                    retorno[0] = caracter.charAt(0);
                for (int i = 1; i < retorno.length - 1; i++) {
                    if (retorno[i] == " ".charAt(0) && i != retorno.length - 1) {
                        caracter = String.copyValueOf(retorno, i + 1, 1);
                        caracter = caracter.toUpperCase();
                        retorno[i + 1] = caracter.charAt(0);
                    }
                }

            } catch (Exception ex) {
                retorno = null;
            }
            frase = String.copyValueOf(retorno, 0, retorno.length);
            return frase;
        } else {
            return "";
        }
    }

    public String getCaminhoReal() {
        return caminhoReal;
    }

    public String soNumeros(String frase) {
        String numeros = "0123456789";
        String retorno = "";
        int cont = 0;

        for (int i = 0; i < frase.length(); i++) {
            if (numeros.indexOf(frase.charAt(i)) >= 0) {
                retorno += frase.charAt(i);
            }
        }

        return retorno;
    }

    public void setCaminhoReal(String caminhoReal) {
        this.caminhoReal = caminhoReal;
    }

    public boolean verificaData(String data) {
        return false;
    }

    public boolean verificaEmail(String email) {
        return false;
    }

    /**
     * Realiza a validação do CPF.
     *
     * @param strCpf número de CPF a ser validado
     * @return true se o CPF é válido e false se não é válido
     */
    public boolean verificaCPF(String strCpf) {
        int d1, d2;
        int digito1, digito2, resto;
        int digitoCPF;
        String nDigResult;

        if (strCpf == null) {
            return false;
        }
        strCpf = soNumeros(strCpf);
        if (strCpf.length() != 11) {
            return false;
        }

        //Verificar se é uma sequencia de caracteres repetidos
        if (strCpf.equals("00000000000") || strCpf.equals("11111111111") || strCpf.equals("22222222222") || strCpf.equals("33333333333") || strCpf.equals("44444444444") || strCpf.equals("55555555555") || strCpf.equals("66666666666") || strCpf.equals("77777777777") || strCpf.equals("88888888888") || strCpf.equals("99999999999")) {
            return false;
        }

        d1 = d2 = 0;
        digito1 = digito2 = resto = 0;

        for (int nCount = 1; nCount < strCpf.length() - 1; nCount++) {
            digitoCPF = Integer.valueOf(strCpf.substring(nCount - 1, nCount)).intValue();

            //multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4 e assim por diante.
            d1 = d1 + (11 - nCount) * digitoCPF;

            //para o segundo digito repita o procedimento incluindo o primeiro digito calculado no passo anterior.
            d2 = d2 + (12 - nCount) * digitoCPF;
        }
        ;

        //Primeiro resto da divisão por 11.
        resto = (d1 % 11);

        //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
        if (resto < 2) {
            digito1 = 0;
        } else {
            digito1 = 11 - resto;
        }

        d2 += 2 * digito1;

        //Segundo resto da divisão por 11.
        resto = (d2 % 11);

        //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
        if (resto < 2) {
            digito2 = 0;
        } else {
            digito2 = 11 - resto;
        }

        //Digito verificador do CPF que está sendo validado.
        String nDigVerific = strCpf.substring(strCpf.length() - 2, strCpf.length());

        //Concatenando o primeiro resto com o segundo.
        nDigResult = String.valueOf(digito1) + String.valueOf(digito2);

        //comparar o digito verificador do cpf com o primeiro resto + o segundo resto.
        return nDigVerific.equals(nDigResult);
    }

    public String formatarData(Date dt, String mascara) {
        Format formatter;
        formatter = new SimpleDateFormat(mascara);
        return (formatter.format(dt));

    }

    public String formatarDecimal(String valor) {

        if (valor.equals("0")) {
            return "0,00";
        }
        DecimalFormat myformat = new DecimalFormat("#,###,###.00");
        //float fvalor = Float.parseFloat(valor);
        double dvalor = Double.parseDouble(valor);
        String retorno = myformat.format(dvalor);
//        NumberFormat z = NumberFormat.getCurrencyInstance();
//        String retorno = z.format(Double.parseDouble(valor));
        return retorno;

    }

    public String diferencaDias(String dataNormalInicio, String dataNormalFim) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date di = null;
        Date df = null;
        try {
            di = sdf.parse(dataNormalInicio);
            df = sdf.parse(dataNormalFim);
        } catch (java.text.ParseException e) {
            return "";
        }

        long diferencaMS = df.getTime() - di.getTime();
        long diferencaSegundos = diferencaMS / 1000;
        long diferencaMinutos = diferencaSegundos / 60;
        long diferencaHoras = diferencaMinutos / 60;
        long diferencaDias = diferencaHoras / 24;

        String retorno = Long.toString(diferencaDias);
        /*
         System.out.println(diferencaMS);
         System.out.println(diferencaSegundos);
         System.out.println(diferencaMinutos);
         System.out.println(diferencaHoras);
         System.out.println(diferencaDias);
         */

        return retorno;

    }

    public String formatarClob(java.sql.Clob s) throws SQLException, IOException {

        char[] buffer = new char[1024];
        Reader instream = s.getCharacterStream();
        StringBuffer sb = new StringBuffer();
        int length;
        while ((length = instream.read(buffer)) != -1) {
            sb.append(buffer, 0, length);
        }
        instream.close();
        String strFinal = sb.toString();

        return strFinal;
    }

    public String clobStringConversion(Clob clb) throws IOException, SQLException {
        if (clb == null) {
            return "";
        }

        StringBuffer str = new StringBuffer();
        String strng;

        BufferedReader bufferRead = new BufferedReader(clb.getCharacterStream());

        while ((strng = bufferRead.readLine()) != null) {
            str.append(strng);
        }

        return str.toString();
    }

    public String convertClobToString(Clob clob) {
        String toRet = "";
        if (clob != null) {
            try {
                long length = clob.length();
                toRet = clob.getSubString(1, (int) length);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return toRet;
    }

    public boolean validaEmail(String email) {
        return p.matcher(email).matches();
    }

    public byte[] gerarHash(String frase, String algoritmo) {
        try {
            MessageDigest md = MessageDigest.getInstance(algoritmo);
            md.update(frase.getBytes());
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public String valorPorExtenso(double vlr) {
        if (vlr == 0) {
            return ("zero");
        }

        long inteiro = (long) Math.abs(vlr); // parte inteira do valor
        double resto = vlr - inteiro;       // parte fracionária do valor

        String vlrS = String.valueOf(inteiro);
        if (vlrS.length() > 15) {
            return ("Erro: valor superior a 999 trilhões.");
        }

        String s = "", saux, vlrP;
        String centavos = String.valueOf((int) Math.round(resto * 100));

        String[] unidade = {"", "um", "dois", "três", "quatro", "cinco",
            "seis", "sete", "oito", "nove", "dez", "onze",
            "doze", "treze", "quatorze", "quinze", "dezesseis",
            "dezessete", "dezoito", "dezenove"};
        String[] centena = {"", "cento", "duzentos", "trezentos",
            "quatrocentos", "quinhentos", "seiscentos",
            "setecentos", "oitocentos", "novecentos"};
        String[] dezena = {"", "", "vinte", "trinta", "quarenta", "cinquenta",
            "sessenta", "setenta", "oitenta", "noventa"};
        String[] qualificaS = {"", "mil", "milhão", "bilhão", "trilhão"};
        String[] qualificaP = {"", "mil", "milhões", "bilhões", "trilhões"};

// definindo o extenso da parte inteira do valor
        int n, unid, dez, cent, tam, i = 0;
        boolean umReal = false, tem = false;
        while (!vlrS.equals("0")) {
            tam = vlrS.length();
// retira do valor a 1a. parte, 2a. parte, por exemplo, para 123456789:
// 1a. parte = 789 (centena)
// 2a. parte = 456 (mil)
// 3a. parte = 123 (milhões)
            if (tam > 3) {
                vlrP = vlrS.substring(tam - 3, tam);
                vlrS = vlrS.substring(0, tam - 3);
            } else { // última parte do valor
                vlrP = vlrS;
                vlrS = "0";
            }
            if (!vlrP.equals("000")) {
                saux = "";
                if (vlrP.equals("100")) {
                    saux = "cem";
                } else {
                    n = Integer.parseInt(vlrP, 10);  // para n = 371, tem-se:
                    cent = n / 100;                  // cent = 3 (centena trezentos)
                    dez = (n % 100) / 10;            // dez  = 7 (dezena setenta)
                    unid = (n % 100) % 10;           // unid = 1 (unidade um)
                    if (cent != 0) {
                        saux = centena[cent];
                    }
                    if ((n % 100) <= 19) {
                        if (saux.length() != 0) {
                            saux = saux + " e " + unidade[n % 100];
                        } else {
                            saux = unidade[n % 100];
                        }
                    } else {
                        if (saux.length() != 0) {
                            saux = saux + " e " + dezena[dez];
                        } else {
                            saux = dezena[dez];
                        }
                        if (unid != 0) {
                            if (saux.length() != 0) {
                                saux = saux + " e " + unidade[unid];
                            } else {
                                saux = unidade[unid];
                            }
                        }
                    }
                }
                if (vlrP.equals("1") || vlrP.equals("001")) {
                    if (i == 0) // 1a. parte do valor (um real)
                    {
                        umReal = true;
                    } else {
                        saux = saux + " " + qualificaS[i];
                    }
                } else if (i != 0) {
                    saux = saux + " " + qualificaP[i];
                }
                if (s.length() != 0) {
                    s = saux + ", " + s;
                } else {
                    s = saux;
                }
            }
            if (((i == 0) || (i == 1)) && s.length() != 0) {
                tem = true; // tem centena ou mil no valor
            }
            i = i + 1; // próximo qualificador: 1- mil, 2- milhão, 3- bilhão, ...
        }

        if (s.length() != 0) {
            if (umReal) {
                s = s + " real";
            } else if (tem) {
                s = s + " reais";
            } else {
                s = s + " de reais";
            }
        }

// definindo o extenso dos centavos do valor
        if (!centavos.equals("0")) { // valor com centavos
            if (s.length() != 0) // se não é valor somente com centavos
            {
                s = s + " e ";
            }
            if (centavos.equals("1")) {
                s = s + "um centavo";
            } else {
                n = Integer.parseInt(centavos, 10);
                if (n <= 19) {
                    s = s + unidade[n];
                } else {             // para n = 37, tem-se:
                    unid = n % 10;   // unid = 37 % 10 = 7 (unidade sete)
                    dez = n / 10;    // dez  = 37 / 10 = 3 (dezena trinta)
                    s = s + dezena[dez];
                    if (unid != 0) {
                        s = s + " e " + unidade[unid];
                    }
                }
                s = s + " centavos";
            }
        }
        return (s);
    }

    public void gravarArquivo(String origem, String destino) throws Exception, FileNotFoundException, IOException {

        File file = new File(destino); //Criamos um nome para o arquivo  

        if (!file.exists()) {

            InputStream inputStream = new FileInputStream(origem);

            OutputStream out = new FileOutputStream(file);
            byte buf[] = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();

        }
    }

    public void apagarArquivo(String caminhoDoArquivo) {
        File file = new File(caminhoDoArquivo);
        file.delete();
    }

    public String caracteresEspeciais(String str) {

        /**
         * Troca os caracteres especiais da string por "" *
         */
        String[] caracteresEspeciais = {"\\ ", ",", "-", ":", "\\(", "\\)", "ª", "\\|", "\\\\", "°", "° ", "ç", "á"};

        for (int i = 0; i < caracteresEspeciais.length; i++) {
            str = str.replaceAll(caracteresEspeciais[i], "");
        }

        /**
         * Troca os espaços no início por "" *
         */
        str = str.replaceAll("^\\s+", "");
        /**
         * Troca os espaços no início por "" *
         */
        str = str.replaceAll("\\s+$", "");
        /**
         * Troca os espaços duplicados, tabulações e etc por " " *
         */
        str = str.replaceAll("\\s+", " ");
        return trata(str);
    }

    public String trata(String passa) {
        passa = passa.replaceAll("[ÂÀÁÄÃ]", "A");
        passa = passa.replaceAll("[âãàáä]", "a");
        passa = passa.replaceAll("[ÊÈÉË]", "E");
        passa = passa.replaceAll("[êèéë]", "e");
        passa = passa.replaceAll("ÎÍÌÏ", "I");
        passa = passa.replaceAll("îíìï", "i");
        passa = passa.replaceAll("[ÔÕÒÓÖ]", "O");
        passa = passa.replaceAll("[ôõòóö]", "o");
        passa = passa.replaceAll("[ÛÙÚÜ]", "U");
        passa = passa.replaceAll("[ûúùü]", "u");
        passa = passa.replaceAll("Ç", "C");
        passa = passa.replaceAll("ç", "c");
        passa = passa.replaceAll("[ýÿ]", "y");
        passa = passa.replaceAll("Ý", "Y");
        passa = passa.replaceAll("ñ", "n");
        passa = passa.replaceAll("Ñ", "N");
        passa = passa.replaceAll("['<>\\|/]", "");
        return passa;
    }

    public double getMenorValor(double[] vetor) {
        double menorValor = vetor[0];
        for (double valor : vetor) {
            if (menorValor > valor && valor > 0) {
                menorValor = valor;
            }
        }
        return menorValor;
    }

    public double getMaiorValor(double[] vetor) {
        double maiorValor = vetor[0];
        for (double valor : vetor) {
            if (maiorValor < valor && valor > 0) {
                maiorValor = valor;
            }
        }
        return maiorValor;
    }

    //Parâmetros:  
    /**
     * 1 - Valor a arredondar. 2 - Quantidade de casas depois da vírgula. 3 -
     * Arredondar para cima ou para baixo? Para cima = 0 (ceil) Para baixo = 1
     * ou qualquer outro inteiro (floor)
     *
     */
    public double arredondar(double valor, int casas, int ceilOrFloor) {
        double arredondado = valor;
        arredondado *= (Math.pow(10, casas));
        if (ceilOrFloor == 0) {
            arredondado = Math.ceil(arredondado);
        } else {
            arredondado = Math.floor(arredondado);
        }
        arredondado /= (Math.pow(10, casas));
        return arredondado;
    }

    public java.util.Date getClassDateCompilation(Class clazz) throws IOException {
        String className = clazz.getName();
        className = className.replaceAll("\\.", "/");
        className = "/" + className + ".class";
        URL url = Class.class.getResource(className);
        URLConnection urlConnection = url.openConnection();
        java.util.Date lastModified = new java.util.Date(urlConnection.getLastModified());
        return lastModified;
    }

    public boolean verificaPIS(String pisOrPasep) {
        int digit_count = 11;
        if (pisOrPasep == null) {
            return false;
        }
        String n = pisOrPasep.replaceAll("[^0-9]*", "");
        if (n.length() != digit_count) {
            return false;
        }
        int i;          // just count 
        int digit;      // A number digit
        int coeficient; // A coeficient  
        int sum;        // The sum of (Digit * Coeficient)
        int foundDv;    // The found Dv (Chek Digit)
        int dv = Integer.parseInt(String.valueOf(n.charAt(n.length() - 1)));
        sum = 0;
        coeficient = 2;
        for (i = n.length() - 2; i >= 0; i--) {
            digit = Integer.parseInt(String.valueOf(n.charAt(i)));
            sum += digit * coeficient;
            coeficient++;
            if (coeficient > 9) {
                coeficient = 2;
            }
        }
        foundDv = 11 - sum % 11;
        if (foundDv >= 10) {
            foundDv = 0;
        }
        return dv == foundDv;
    }

    public FileOutputStream abrirArquivoTexto(String nomeArq) throws FileNotFoundException {
        File arquivo;
        arquivo = new File(nomeArq);
        return new FileOutputStream(arquivo);
    }

    public void fecharArquivoTexto(FileOutputStream arquivo) throws IOException {
        arquivo.close();
    }

    public String removeCaracterIlegalNumero(String frase) {
        //Retorna a Frase dada sem caracteres estranhos;
        String validos = "0123457689";
        int i;
        String retorno = "";
        for (i = 0; i < frase.length(); i++) {
            if (validos.indexOf(frase.charAt(i)) > -1) {
                retorno += frase.charAt(i);
            }
        }
        return retorno;
    }
}