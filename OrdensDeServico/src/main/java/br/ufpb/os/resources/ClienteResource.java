package br.ufpb.os.resources;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import br.ufpb.os.domain.Cliente;
import br.ufpb.os.dtos.ClienteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.ufpb.os.services.ClienteService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/clientes")
public class ClienteResource {
	
	private static final Logger LOG = LoggerFactory.getLogger(ClienteResource.class);

	@Autowired
	private ClienteService service;

	/*
	 * Busca pelo ID
	 */
	@GetMapping(value = "/{id}")
	public ResponseEntity<ClienteDTO> findById(@PathVariable Integer id) {
		LOG.info("Resource - BUSCANDO CLIENTE POR ID");
		ClienteDTO objDTO = new ClienteDTO(service.findById(id));
		return ResponseEntity.ok().body(objDTO);
	}

	/*
	 * Lista todos objetos do tipo Cliente na base
	 */
	@GetMapping
	public ResponseEntity<List<ClienteDTO>> findAll() {
		LOG.info("Resource - BUSCANDO TODOS OS CLIENTES DO BANCO");
		List<ClienteDTO> listDTO = service.findAll().stream().map(obj -> new ClienteDTO(obj))
				.collect(Collectors.toList());

		return ResponseEntity.ok().body(listDTO);
	}

	/*
	 * Cria um novo Cliente
	 */
	@PostMapping
	public ResponseEntity<ClienteDTO> create(@Valid @RequestBody ClienteDTO objDTO) {
		LOG.info("Resource - CRIANDO NOVO CLIENTE");
		Cliente newObj = service.create(objDTO);

		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newObj.getId()).toUri();

		return ResponseEntity.created(uri).build();
	}

	/*
	 * Atualiza um Cliente
	 */
	@PutMapping(value = "/{id}")
	public ResponseEntity<ClienteDTO> update(@PathVariable Integer id, @Valid @RequestBody ClienteDTO objDTO) {
		LOG.info("Resource - ATUALIZANDO CLIENTE");
		ClienteDTO newObj = new ClienteDTO(service.update(id, objDTO));
		return ResponseEntity.ok().body(newObj);
	}

	/*
	 * Delete Cliente
	 */
//	@PreAuthorize("hasAnyRole('ADMIN')")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		LOG.info("Resource - DELETANDO CLIENTE");
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

}
