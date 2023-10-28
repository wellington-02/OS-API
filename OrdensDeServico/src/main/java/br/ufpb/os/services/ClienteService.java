package br.ufpb.os.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import br.ufpb.os.domain.Cliente;
import br.ufpb.os.domain.Pessoa;
import br.ufpb.os.dtos.ClienteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.ufpb.os.repositories.PessoaRepository;
import br.ufpb.os.repositories.ClienteRepository;
import br.ufpb.os.services.exceptions.DataIntegratyViolationException;
import br.ufpb.os.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	private static final Logger LOG = LoggerFactory.getLogger(ClienteService.class);
	
	@Autowired
	private ClienteRepository repository;

	@Autowired
	private PessoaRepository pessoaRepository;


	public Cliente findById(Integer id) {
		LOG.info("Service - BUSCANDO CLIENTE POR ID");
		Optional<Cliente> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}

	public List<Cliente> findAll() {
		LOG.info("Service - BUSCANDO TODOS OS CLIENTES DO BANCO");
		return repository.findAll();
	}

	public Cliente create(ClienteDTO objDTO) {
		LOG.info("Service - CRIANDO NOVO CLIENTE");
		if (findByCPF(objDTO) != null) {
			throw new DataIntegratyViolationException("CPF já cadastrado na base de dados!");
		}

		return repository.save(new Cliente(null, objDTO.getNome(), objDTO.getCpf(), objDTO.getTelefone()));
	}

	public Cliente update(Integer id, @Valid ClienteDTO objDTO) {
		LOG.info("Service - ATUALIZANDO CLIENTE");
		Cliente oldObj = findById(id);

		if (findByCPF(objDTO) != null && findByCPF(objDTO).getId() != id) {
			throw new DataIntegratyViolationException("Id diferentes");
		}

		oldObj.setNome(objDTO.getNome());
		oldObj.setCpf(objDTO.getCpf());
		oldObj.setTelefone(objDTO.getTelefone());
		return repository.save(oldObj);
	}

	public void delete(Integer id) {
		LOG.info("Service - DELETANDO CLIENTE");
		Cliente obj = findById(id);

		if (obj.getList().size() > 0) {
			throw new DataIntegratyViolationException("Pessoa com o id:"
					+id+" possui Ordens de Serviço, não pode ser deletada!");
		}
		repository.deleteById(id);
	}

	private Pessoa findByCPF(ClienteDTO objDTO) {
		Pessoa obj = pessoaRepository.findByCPF(objDTO.getCpf());

		if (obj != null) {
			return obj;
		}
		return null;
	}

}
