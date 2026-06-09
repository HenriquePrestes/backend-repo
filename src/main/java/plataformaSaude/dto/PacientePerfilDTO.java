package plataformaSaude.dto;

import jakarta.persistence.Column;

import java.time.LocalDate;

// Este DTO não precisa de validações (@NotBlank) pois os campos
// são opcionais na atualização. A validação do Yup (frontend) já ajuda.
public class PacientePerfilDTO {

    // Dados Pessoais
    private String nomeCompleto;
    private String cpf;
    private String email;
    private String celular;
    private LocalDate dataNascimento;
    private String profissao;

    // Endereço
    private String cep;
    private String rua;
    private String numero;
    private String complemento;
    private String cidade;
    private String estado;

    // Senha (opcional, só atualiza se for enviado)
    private String senha;

    // Notificações
    private Boolean notificacoes;

    private String temaPreferido;

    // --- Getters e Setters ---


    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getProfissao() { return profissao; }
    public void setProfissao(String profissao) { this.profissao = profissao; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Boolean getNotificacoes() { return notificacoes; }
    public void setNotificacoes(Boolean notificacoes) { this.notificacoes = notificacoes; }

    public String getTemaPreferido() {
        return temaPreferido;
    }
}
