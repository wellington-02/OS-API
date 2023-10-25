package br.ufpb.os.dtos;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import br.ufpb.os.domain.Tecnico;
import org.hibernate.validator.constraints.br.CPF;

public class TecnicoDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;

	@NotEmpty(message = "O campo NOME é requerido")
	private String nome;

	@CPF
	@NotEmpty(message = "O campo CPF é requerido")
	private String cpf;

	@NotEmpty(message = "O campo TELEFONE é requerido")
	private String telefone;

//	@JsonIgnore
//	@NotEmpty(message = "O campo SENHA é requerido")
//	private String senha;

//	private Set<Integer> perfis = new HashSet<>();

	public TecnicoDTO() {
		super();
	}

	public TecnicoDTO(Tecnico obj) {
		super();
		this.id = obj.getId();
		this.nome = obj.getNome();
		this.cpf = obj.getCpf();
		this.telefone = obj.getTelefone();
//		this.senha = obj.getSenha();
//		this.perfis = obj.getPerfis().stream().map(x -> x.getCod()).collect(Collectors.toSet());
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

//	public String getSenha() {
//		return senha;
//	}
//
//	public void setSenha(String senha) {
//		this.senha = senha;
//	}

//	public Set<Perfil> getPerfis() {
//		return perfis.stream().map(x -> Perfil.toEnum(x)).collect(Collectors.toSet());
//	}
//
//	public void addPerfil(Perfil perfil) {
//		perfis.add(perfil.getCod());
//	}

}
