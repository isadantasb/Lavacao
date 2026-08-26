package com.example.demo;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zk.ui.util.Clients;

public class PainelComposer extends SelectorComposer<Div> {

    private final List<Ambiente> ambientes = new ArrayList<>();
    private final List<RegistroLimpeza> registros = new ArrayList<>();

    @Wire
    private Div containerDeSalas;
    @Wire
    private Label expiradosCount;
    @Wire
    private Label urgenteCount;
    @Wire
    private Label atencaoCount;
    @Wire
    private Label emDiaCount;
    @Wire
    private Window modalNovoAmbiente;
    @Wire
    private Window modalNovaLimpeza;
    @Wire
    private Textbox salaInput;
    @Wire
    private Textbox setorInput;
    @Wire
    private Button parteBox;
    @Wire
    private Button parteBanheiro;
    @Wire
    private Button parteLavabo;
    @Wire
    private Combobox ambienteLimpeza;
    @Wire
    private Datebox dataLimpeza;

    @Override
    public void doAfterCompose(Div view) throws Exception {
        super.doAfterCompose(view);
        atualizarPainel();
    }

    @Listen("onClick = #salvarAmbiente")
    public void salvarAmbiente() {
        String sala = salaInput.getValue() == null ? "" : salaInput.getValue().trim();
        String setor = setorInput.getValue() == null ? "" : setorInput.getValue().trim();
        if (sala.isEmpty() || setor.isEmpty()) {
            Clients.showNotification("Informe o número da sala e o setor.", Clients.NOTIFICATION_TYPE_WARNING, salaInput, "after_center", 2500);
            return;
        }
        List<String> partes = new ArrayList<>();
        adicionarParteSeSelecionada(partes, parteBox, "Box");
        adicionarParteSeSelecionada(partes, parteBanheiro, "Banheiro");
        adicionarParteSeSelecionada(partes, parteLavabo, "Lavabo");
        Ambiente ambiente = new Ambiente(sala, setor, partes);
        ambientes.add(ambiente);
        Comboitem item = new Comboitem(ambiente.getNome());
        item.setValue(ambiente);
        ambienteLimpeza.appendChild(item);
        limparFormularioAmbiente();
        modalNovoAmbiente.setVisible(false);
        atualizarPainel();
    }

    @Listen("onClick = #salvarLimpeza")
    public void salvarLimpeza() {
        Comboitem item = ambienteLimpeza.getSelectedItem();
        if (item == null || item.getValue() == null) {
            Clients.showNotification("Cadastre e selecione um ambiente antes de registrar a limpeza.", Clients.NOTIFICATION_TYPE_WARNING, ambienteLimpeza, "after_center", 3000);
            return;
        }
        Date data = dataLimpeza.getValue();
        if (data == null) {
            Clients.showNotification("Informe a data da limpeza.", Clients.NOTIFICATION_TYPE_WARNING, dataLimpeza, "after_center", 2500);
            return;
        }
        Ambiente ambiente = (Ambiente) item.getValue();
        registros.add(new RegistroLimpeza(ambiente, paraLocalDate(data)));
        modalNovaLimpeza.setVisible(false);
        ambienteLimpeza.setSelectedItem(null);
        dataLimpeza.setValue(null);
        atualizarPainel();
    }

    @Listen("onClick = #parteBox")
    public void alternarBox() {
        alternar(parteBox);
    }

    @Listen("onClick = #parteBanheiro")
    public void alternarBanheiro() {
        alternar(parteBanheiro);
    }

    @Listen("onClick = #parteLavabo")
    public void alternarLavabo() {
        alternar(parteLavabo);
    }

    private void atualizarPainel() {
        if (containerDeSalas == null) {
            return;
        }
        containerDeSalas.getChildren().clear();
        int expirados = 0;
        int urgentes = 0;
        int atencao = 0;
        int emDia = 0;
        for (Ambiente ambiente : ambientes) {
            RegistroLimpeza registro = ultimaLimpeza(ambiente);
            String status = "room-status-empty";
            String statusTexto = "Sem limpezas registradas";
            if (registro != null) {
                long dias = ChronoUnit.DAYS.between(registro.data(), LocalDate.now());
                if (dias >= 31) {
                    status = "room-status-black";
                    statusTexto = "Expirado";
                    expirados++;
                } else if (dias >= 22) {
                    status = "room-status-red";
                    statusTexto = "Urgente";
                    urgentes++;
                } else if (dias >= 15) {
                    status = "room-status-yellow";
                    statusTexto = "Atenção";
                    atencao++;
                } else {
                    status = "room-status-green";
                    statusTexto = "Em dia";
                    emDia++;
                }
            }
            containerDeSalas.appendChild(criarCartao(ambiente, registro, status, statusTexto));
        }
        expiradosCount.setValue(String.valueOf(expirados));
        urgenteCount.setValue(String.valueOf(urgentes));
        atencaoCount.setValue(String.valueOf(atencao));
        emDiaCount.setValue(String.valueOf(emDia));
    }

    private Div criarCartao(Ambiente ambiente, RegistroLimpeza registro, String status, String statusTexto) {
        Div cartao = new Div();
        cartao.setSclass("room-card " + status);
        Label tipo = new Label("Sala");
        tipo.setSclass("room-label");
        cartao.appendChild(tipo);
        Label sala = new Label(ambiente.getNome());
        sala.setSclass("room-name");
        cartao.appendChild(sala);
        Label setor = new Label("· " + ambiente.setor());
        setor.setSclass("room-sector");
        cartao.appendChild(setor);
        Div partes = new Div();
        partes.setSclass("room-tags");
        for (String parte : ambiente.partes()) {
            Label tag = new Label(parte);
            tag.setSclass("room-tag");
            partes.appendChild(tag);
        }
        cartao.appendChild(partes);
        Label statusLabel = new Label(statusTexto);
        statusLabel.setSclass("room-status-label");
        cartao.appendChild(statusLabel);
        if (registro != null) {
            Label ultima = new Label("Última: " + registro.data().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            ultima.setSclass("room-last-cleaning");
            cartao.appendChild(ultima);
        }
        return cartao;
    }

    private RegistroLimpeza ultimaLimpeza(Ambiente ambiente) {
        RegistroLimpeza ultima = null;
        for (RegistroLimpeza registro : registros) {
            if (registro.ambiente() == ambiente && (ultima == null || registro.data().isAfter(ultima.data()))) {
                ultima = registro;
            }
        }
        return ultima;
    }

    private void adicionarParteSeSelecionada(List<String> partes, Button botao, String parte) {
        if (botao.getSclass().contains("selected")) {
            partes.add(parte);
        }
    }

    private void alternar(Button botao) {
        botao.setSclass(botao.getSclass().contains("selected") ? "btn-toggle" : "btn-toggle selected");
    }

    private void limparFormularioAmbiente() {
        salaInput.setValue("");
        setorInput.setValue("");
        parteBox.setSclass("btn-toggle");
        parteBanheiro.setSclass("btn-toggle");
        parteLavabo.setSclass("btn-toggle");
    }

    private LocalDate paraLocalDate(Date data) {
        return Instant.ofEpochMilli(data.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private record Ambiente(String sala, String setor, List<String> partes) {
        String getNome() {
            return "Sala " + sala;
        }
    }

    private record RegistroLimpeza(Ambiente ambiente, LocalDate data) {
    }
}
