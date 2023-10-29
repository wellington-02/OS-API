package br.ufpb.os.integration;

import br.ufpb.os.config.ContainersEnvironment;
import br.ufpb.os.domain.Cliente;
import br.ufpb.os.dtos.ClienteDTO;
import br.ufpb.os.repositories.ClienteRepository;
import br.ufpb.os.util.ClienteCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClienteControllerTest extends ContainersEnvironment {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ClienteRepository clienteRepository;


    private static final String BASE_URL = "/clientes";

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();
    }

    @Test
    void findById_ReturnsStatusOk_WhenSuccessful(){
        Cliente cliente = clienteRepository.save(ClienteCreator.defaulCliente());

        int expectedId = cliente.getId();

        ResponseEntity<Cliente> response = testRestTemplate.getForEntity
                (BASE_URL + "/{id}", Cliente.class, expectedId);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK)
        ;

    }

    @Test
    void findById_ReturnsStatusNotFound_WhenSuccessful(){
        int expectedId = 1;

        ResponseEntity<Cliente> response = testRestTemplate.getForEntity
                (BASE_URL + "/{id}", Cliente.class, expectedId);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
        ;

    }

    @Test
    void findAll_ReturnsListOfClientes_WhenSuccessful(){
        Cliente cliente = clienteRepository.save(ClienteCreator.defaulCliente());

        String expectedName = cliente.getNome();

        List<Cliente> response = testRestTemplate.exchange(BASE_URL, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Cliente>>() {
                }).getBody();

        Assertions.assertThat(response).isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(response.get(0).getNome()).isEqualTo(expectedName);
    }

    @Test
    void findAll_ReturnsEmptyListOfClientes_WhenNotFoundClientes(){

        List<Cliente> response = testRestTemplate.exchange(BASE_URL, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Cliente>>() {
                }).getBody();

        Assertions.assertThat(response).isNotNull()
                .isEmpty();
    }


    @Test
    void findAll_ReturnsStatusCodeOk_WhenSuccessful(){
        clienteRepository.save(ClienteCreator.defaulCliente());

        ResponseEntity<List<ClienteDTO>> response = testRestTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ClienteDTO>>() {}
        );

        Assertions.assertThat(response)
                .isNotNull();

        Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    void create_ReturnsStatusCodeCreated_WhenSuccessfu(){
        ClienteDTO clienteDTO = new ClienteDTO(ClienteCreator.defaulCliente());

        ResponseEntity<ClienteDTO> response = testRestTemplate.postForEntity(
                BASE_URL,
                clienteDTO,
                ClienteDTO.class);

        Assertions.assertThat(response)
                .isNotNull();

        Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void create_ReturnsStatusCodeBadRequest_WhenClienteWithCpfAlreadyRegistrated(){
        clienteRepository.save(ClienteCreator.defaulCliente());

        ClienteDTO clienteWithSameCpf = new ClienteDTO(ClienteCreator.defaulCliente());

        ResponseEntity<ClienteDTO> response = testRestTemplate.postForEntity(
                BASE_URL,
                clienteWithSameCpf,
                ClienteDTO.class);

        Assertions.assertThat(response)
                .isNotNull();

        Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void create_ReturnsStatusCodeBadRequest_WhenCpfIsNotValid(){
        Cliente clienteWithInavalidCpf = new Cliente(1, "test", "000.000.000-00", "telefone");

        ResponseEntity<ClienteDTO> response = testRestTemplate.postForEntity(
                BASE_URL,
                clienteWithInavalidCpf,
                ClienteDTO.class);

        Assertions.assertThat(response)
                .isNotNull();

        Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void create_ReturnsStatusCodeBadRequest_WhenNomeIsEmpty(){
        Cliente clienteWithEmptyNome = new Cliente(1, "", "598.508.200-80", "telefone");

        ResponseEntity<ClienteDTO> response = testRestTemplate.postForEntity(
                BASE_URL,
                clienteWithEmptyNome,
                ClienteDTO.class);

        Assertions.assertThat(response)
                .isNotNull();

        Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void create_ReturnsStatusCodeBadRequest_WhenCpfIsEmpty(){
        Cliente clienteWithEmptyCpf = new Cliente(1, "test", "", "telefone");

        ResponseEntity<ClienteDTO> response = testRestTemplate.postForEntity(
                BASE_URL,
                clienteWithEmptyCpf,
                ClienteDTO.class);

        Assertions.assertThat(response)
                .isNotNull();

        Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void create_ReturnsStatusCodeBadRequest_WhenTelefoneIsEmpty(){
        Cliente clienteWithEmptyTelefone = new Cliente(1, "test", "598.508.200-80", "");

        ResponseEntity<ClienteDTO> response = testRestTemplate.postForEntity(
                BASE_URL,
                clienteWithEmptyTelefone,
                ClienteDTO.class);

        Assertions.assertThat(response)
                .isNotNull();

        Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void update_ReturnsStatusCodeOk_WhenSuccessfu(){
        Cliente cliente = clienteRepository.save(ClienteCreator.defaulCliente());
        Cliente clienteToBeUpdate = new Cliente(1, "testing", "598.508.200-80", "telefone2");

        ClienteDTO clienteDTO = new ClienteDTO(clienteToBeUpdate);
        String id = String.valueOf(cliente.getId());

        ResponseEntity<ClienteDTO> response = testRestTemplate.exchange(
                BASE_URL + "/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(clienteDTO),
                ClienteDTO.class);

        Assertions.assertThat(response)
                .isNotNull();

        Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        Assertions.assertThat(response.getBody())
                .isNotNull();

        Assertions.assertThat(response.getBody().getNome())
                .isNotNull()
                .isNotEqualTo(cliente.getNome());
    }

    @Test
    void update_ReturnsStatusCodeNotFound_WhenIdIsDifferent(){
        clienteRepository.save(ClienteCreator.defaulCliente());
        Cliente clienteToBeUpdate = new Cliente(1, "test", "598.508.200-80", "telefone");

        ClienteDTO clienteDTO = new ClienteDTO(clienteToBeUpdate);
        String id = "10";

        ResponseEntity<ClienteDTO> response = testRestTemplate.exchange(
                BASE_URL + "/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(clienteDTO),
                ClienteDTO.class);

        Assertions.assertThat(response)
                .isNotNull();

        Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void update_ReturnsStatusCodeNotFound_WhenIdIsNull(){
        Cliente cliente = clienteRepository.save(ClienteCreator.defaulCliente());
        Cliente clienteToBeUpdate = new Cliente(1, "test", null, "telefone");

        ClienteDTO clienteDTO = new ClienteDTO(clienteToBeUpdate);
        String id = String.valueOf(cliente.getId());

        ResponseEntity<ClienteDTO> response = testRestTemplate.exchange(
                BASE_URL + "/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(clienteDTO),
                ClienteDTO.class);

        Assertions.assertThat(response)
                .isNotNull();

        Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void delete_ReturnsStatusCodeNoContent_WhenSuccessful(){
        Cliente cliente = clienteRepository.save(ClienteCreator.defaulCliente());

        String id = String.valueOf(cliente.getId());

        ResponseEntity<Void> response = testRestTemplate.exchange(
                BASE_URL + "/" + id,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        Assertions.assertThat(response)
                .isNotNull();

        Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void delete_ReturnsStatusCodeNotFound_WhenIdIsNotFound(){
        String id = "1";

        ResponseEntity<Void> response = testRestTemplate.exchange(
                BASE_URL + "/" + id,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        Assertions.assertThat(response)
                .isNotNull();

        Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
