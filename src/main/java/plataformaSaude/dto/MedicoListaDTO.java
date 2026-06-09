package plataformaSaude.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.List;
import java.util.ArrayList;

public class MedicoListaDTO {
    private Long id;
    private String nomeCompleto;
    private String cpf;
    private String email;
    private String celular;
    private LocalDate dataNascimento;
    private LocalDate dataCadastro;
    
    // Dados de endereço
    private String cep;
    private String rua;
    private String numero;
    private String complemento;
    private String cidade;
    private String estado;
    
    // Dados profissionais
    private String crm;
    private String especialidade; // Mantemos o nome da especialidade para exibição na lista
    private Long especialidadeId; // ID para edição
    private String foto;
    private int duracaoConsultaMinutos;
    private List<HorarioDTO> horariosTrabalho = new ArrayList<>();

    // Construtor vazio
    public MedicoListaDTO() {}

    // Construtor completo
    public MedicoListaDTO(Long id, String nomeCompleto, String email, String celular, 
                         String crm, String especialidade, String foto, 
                         int duracaoConsultaMinutos, LocalDate dataCadastro) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.celular = celular;
        this.crm = crm;
        this.especialidade = especialidade;
        this.foto = foto;
        this.duracaoConsultaMinutos = duracaoConsultaMinutos;
        this.dataCadastro = dataCadastro;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
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

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public String getCrm() { return crm; }
    public void setCrm(String crm) { this.crm = crm; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public Long getEspecialidadeId() { return especialidadeId; }
    public void setEspecialidadeId(Long especialidadeId) { this.especialidadeId = especialidadeId; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public int getDuracaoConsultaMinutos() { return duracaoConsultaMinutos; }
    public void setDuracaoConsultaMinutos(int duracaoConsultaMinutos) { this.duracaoConsultaMinutos = duracaoConsultaMinutos; }

    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }

    public List<HorarioDTO> getHorariosTrabalho() { return horariosTrabalho; }
    public void setHorariosTrabalho(List<HorarioDTO> horariosTrabalho) { this.horariosTrabalho = horariosTrabalho; }

    // Classe interna para horários
    public static class HorarioDTO {
        private Long id;
        private String descricao;
        private DayOfWeek diaSemana;
        private LocalTime horaInicio;
        private LocalTime horaFim;

        public HorarioDTO() {}

        public HorarioDTO(Long id, String descricao, DayOfWeek diaSemana, 
                         LocalTime horaInicio, LocalTime horaFim) {
            this.id = id;
            this.descricao = descricao;
            this.diaSemana = diaSemana;
            this.horaInicio = horaInicio;
            this.horaFim = horaFim;
        }

        // Getters e Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }

        public DayOfWeek getDiaSemana() { return diaSemana; }
        public void setDiaSemana(DayOfWeek diaSemana) { this.diaSemana = diaSemana; }

        public LocalTime getHoraInicio() { return horaInicio; }
        public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

        public LocalTime getHoraFim() { return horaFim; }
        public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
    }
}