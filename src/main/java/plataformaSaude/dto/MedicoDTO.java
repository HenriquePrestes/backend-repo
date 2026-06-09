// src/main/java/plataformaSaude/dto/MedicoDTO.java
package plataformaSaude.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para o Admin criar ou atualizar um Medico.
 * Inclui dados do Usuario, dados do Medico e a grade de horários.
 */
public class MedicoDTO {

    // Dados do Usuario
    private String nomeCompleto;
    private String cpf;
    private String email;
    private String senha; // Apenas para criação
    private String celular;
    private LocalDate dataNascimento;
    
    // Dados de endereço
    private String cep;
    private String rua;
    private String numero;
    private String complemento;
    private String cidade;
    private String estado;

    // Dados do Medico
    private String crm;
    private Long especialidadeId;
    private String foto; // URL da foto
    private int duracaoConsultaMinutos; // O novo campo!

    // A nova grade de horários
    private List<HorarioDTO> horariosTrabalho;

    // Getters e Setters para todos os campos
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    
    // Getters e Setters para endereço
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
    
    public String getCrm() { return crm; }
    public void setCrm(String crm) { this.crm = crm; }
    public Long getEspecialidadeId() { return especialidadeId; }
    public void setEspecialidadeId(Long especialidadeId) { this.especialidadeId = especialidadeId; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    public int getDuracaoConsultaMinutos() { return duracaoConsultaMinutos; }
    public void setDuracaoConsultaMinutos(int duracaoConsultaMinutos) { this.duracaoConsultaMinutos = duracaoConsultaMinutos; }
    public List<HorarioDTO> getHorariosTrabalho() { return horariosTrabalho; }
    public void setHorariosTrabalho(List<HorarioDTO> horariosTrabalho) { this.horariosTrabalho = horariosTrabalho; }
}