package br.ufpb.os.service;

import br.ufpb.os.domain.Cliente;
import br.ufpb.os.domain.OS;
import br.ufpb.os.dtos.ClienteDTO;
import br.ufpb.os.repositories.ClienteRepository;
import br.ufpb.os.repositories.PessoaRepository;
import br.ufpb.os.services.ClienteService;
import br.ufpb.os.services.exceptions.DataIntegratyViolationException;
import br.ufpb.os.services.exceptions.ObjectNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ClienteServiceTest {

    @InjectMocks
    private ClienteService clienteService;

    @Mock
    private ClienteRepository clienteRepositoryMock;

    @Mock
    private PessoaRepository pessoaRepositoryMock;


    @BeforeEach
    void setUp(){
        List<Cliente> clientes = new ArrayList<>() ;
        Cliente cliente = new Cliente(1, "test", "cpf", "telefone");
        clientes.add(cliente);


        BDDMockito.when(clienteRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Optional.of(cliente));

        BDDMockito.when(clienteRepositoryMock.findAll())
                .thenReturn(clientes);

        BDDMockito.when(clienteRepositoryMock.save(ArgumentMatchers.any(Cliente.class)))
                .thenReturn(cliente);

        BDDMockito.doNothing().when(clienteRepositoryMock).delete(ArgumentMatchers.any(Cliente.class));

    }

    @Test
    void findById_ReturnsCliente_WhenSuccessful(){
        int expectedId = 1;

        Cliente cliente = clienteService.findById(1);

        Assertions.assertThat(cliente).isNotNull();

        Assertions.assertThat(cliente.getId())
                .isNotNull()
                .isEqualTo(expectedId);
    }

    @Test
    void findById_ThrowsObjectNotFoundException_WhenClienteIsNotFound(){
        BDDMockito.when(clienteService.findById(ArgumentMatchers.anyInt()))
                .thenThrow(ObjectNotFoundException.class);

        int expectedId = 2;

        Assertions.assertThatThrownBy(() -> clienteService.findById(expectedId))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    void findById_doesNotThrowAnyException(){
        int expectedId = 1;

        Assertions.assertThatCode(() -> clienteService.findById(expectedId))
                .doesNotThrowAnyException();
    }

    @Test
    void findAll_ReturnsAllClientes_WhenSuccessful(){
        String expectedName = "test";

        List<Cliente> clientes = clienteService.findAll();

        Assertions.assertThat(clientes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(clientes.get(0).getNome())
                .isEqualTo(expectedName);

    }

    @Test
    void findAll_ReturnsEmptyListOfClientes_WhenClienteNotExist(){
        BDDMockito.when(clienteRepositoryMock.findAll())
                .thenReturn(List.of());

        List<Cliente> clientes = clienteService.findAll();

        Assertions.assertThat(clientes)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void create_ReturnsCliente_WhenSuccessful(){
        Cliente cliente = new Cliente(1, "test", "cpf", "telefone");

        ClienteDTO clienteToBeSave = new ClienteDTO(cliente);

        Cliente clienteCreated = clienteService.create(clienteToBeSave);

        Assertions.assertThat(clienteCreated)
                .isNotNull()
                .isEqualTo(cliente);

        Assertions.assertThat(clienteCreated.getId())
                .isNotNull()
                .isEqualTo(cliente.getId());

        Assertions.assertThat(clienteCreated.getNome())
                .isNotNull()
                .isEqualTo(cliente.getNome());

        Assertions.assertThat(clienteCreated.getCpf())
                .isNotNull()
                .isEqualTo(cliente.getCpf());

        Assertions.assertThat(clienteCreated.getTelefone())
                .isNotNull()
                .isEqualTo(cliente.getTelefone());
    }

    @Test
    void create_throwsDataIntegratyViolationException_WhenClienteWithCpfAlreadyRegistrated(){
        Cliente cliente = new Cliente(1, "test", "cpf", "telefone");

        BDDMockito.when(pessoaRepositoryMock.findByCPF(ArgumentMatchers.anyString()))
                .thenReturn(cliente);

        ClienteDTO clienteToBeSaved = new ClienteDTO(cliente);

        Assertions.assertThatThrownBy(() -> clienteService.create(clienteToBeSaved))
                .isInstanceOf(DataIntegratyViolationException.class)
                .hasMessage("CPF já cadastrado na base de dados!");
    }

    @Test
    void update_ReturnsCliente_WhenSuccessful() {
        Cliente cliente = new Cliente(1, "test", "cpf", "telefone");

        ClienteDTO clienteToBeUpdate = new ClienteDTO(cliente);

        Cliente clienteUpdated = clienteService.update(cliente.getId(), clienteToBeUpdate);

        Assertions.assertThat(clienteUpdated)
                .isNotNull()
                .isEqualTo(cliente);

        Assertions.assertThat(clienteUpdated.getId())
                .isNotNull()
                .isEqualTo(cliente.getId());
    }

    @Test
    void update_throwsDataIntegratyViolationException_WhenClienteCpfIsRegistratedAndIdsAreDifferent(){
        Cliente cliente = new Cliente(1, "test", "cpf", "telefone");
        Cliente cliente2 = new Cliente(2, "test", "cpf", "telefone");

        BDDMockito.when(pessoaRepositoryMock.findByCPF(ArgumentMatchers.anyString()))
                .thenReturn(cliente);

        ClienteDTO clienteToBeUpdate = new ClienteDTO(cliente);

        Assertions.assertThatThrownBy(() -> clienteService.update(cliente2.getId(), clienteToBeUpdate))
                .isInstanceOf(DataIntegratyViolationException.class)
                .hasMessage("Id diferentes");
    }

    @Test
    void delete_RemoveCliente_whenSuccessful(){

        Assertions.assertThatCode(() -> clienteService.delete(1))
                .doesNotThrowAnyException();
    }

    @Test
    void delete_throwsDataIntegratyViolationException_WhenClienteHasListOfOs(){
        Cliente cliente = new Cliente(1, "test", "cpf", "telefone");
        OS os = new OS();
        cliente.getList().add(os);

        BDDMockito.when(clienteRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Optional.of(cliente));

        Assertions.assertThatThrownBy(() -> clienteService.delete(1))
                .isInstanceOf(DataIntegratyViolationException.class)
                .hasMessage("Pessoa com o id:"
                        +cliente.getId()+" possui Ordens de Serviço, não pode ser deletada!");
    }
}
