package br.ufpb.os.util;

import br.ufpb.os.domain.Cliente;

public class ClienteCreator {

    private static final int id = 1;
    private static final String nome = "test";
    private static final String cpf = "598.508.200-80";
    private static final String telefone = "telefone";

    public static Cliente defaulCliente() {

        return new Cliente(id, nome, cpf, telefone);
    }
}
