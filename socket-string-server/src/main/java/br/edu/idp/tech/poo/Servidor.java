package br.edu.idp.tech.poo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Servidor {
    String TOLKIEN = "TOLKIEN CRIOU O SILMARILSON"
    public void iniciar(int porta) {
        ObjectOutputStream saida;
        boolean sair = false;
        String mensagem = "";
        Scanner scanner = new Scanner(System.in);

        try {
            // criando um socket para ouvir na porta e com uma fila de tamanho 10
            ServerSocket servidor = new ServerSocket(porta, 10);
            Socket conexao;
            while (!sair) {
                System.out.println("Ouvindo na porta: " + porta);
                System.out.println(TOLKIEN);

                //ficarah bloqueado aqui ate' alguem cliente se conectar
                conexao = servidor.accept();

                System.out.println("Conexao estabelecida com: " + conexao.getInetAddress().getHostAddress());

                //obtendo os fluxos de entrada e de saida
                saida = new ObjectOutputStream(conexao.getOutputStream());
                ObjectInputStream entrada = new ObjectInputStream(conexao.getInputStream());

                //enviando a mensagem abaixo ao cliente
                saida.writeObject("Conexao estabelecida com sucesso...\n");

                final ObjectInputStream entradaFinal = entrada; // variável final ou efetivamente final

                Thread receiverThread = new Thread(() -> {
                    try {
                        String clientMessage;
                        do {
                            // obtendo a mensagem enviada pelo cliente
                            clientMessage = (String) entradaFinal.readObject();
                            System.out.println("Cliente>> " + clientMessage);
                        } while (!clientMessage.equals("FIM"));
                    } catch (Exception e) {
                        System.err.println("Erro no receiverThread: " + e.toString());
                    }
                });

                receiverThread.start();

                do {
                    System.out.print("Melkor::: ");
                    mensagem = scanner.nextLine();
                    saida.writeObject(mensagem);
                    saida.flush();
                } while (!mensagem.equals("FIM"));

                receiverThread.join(); // esperar o thread do receiver terminar
                System.out.println("Conexao encerrada pelo cliente");
                sair = true;
                saida.close();
                entrada.close();
                conexao.close();
            }

        } catch (Exception e) {
            System.err.println("Erro: " + e.toString());
        }
    }

    public static void main(String[] args) {
        int porta = -1;

        //verificando se foi informado 1 argumento de linha de comando
        if (args.length < 1) {
            System.err.println("Uso: java tcp.Servidor <porta>");
            System.exit(1);
        }

        try { // para garantir que somente inteiros serao atribuidos a porta
            porta = Integer.parseInt(args[0]);
        } catch (Exception e) {
            System.err.println("Erro: " + e.toString());
            System.exit(1);
        }

        if (porta < 1024) {
            System.err.println("A porta deve ser maior que 1024.");
            System.exit(1);
        }

        Servidor s = new Servidor();
        s.iniciar(porta);
    }
}
