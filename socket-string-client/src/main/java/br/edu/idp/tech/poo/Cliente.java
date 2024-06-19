package br.edu.idp.tech.poo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    public void iniciar(String endereco, int porta) {
        ObjectOutputStream saida;
        ObjectInputStream entrada;
        Socket conexao;
        Scanner ler = new Scanner(System.in);
        String mensagem = "";
        try {
            conexao = new Socket(endereco, porta);
            System.out.println("Conectado ao servidor " + endereco + ", na porta: " + porta);
            System.out.println("Digite: FIM para encerrar a conexao");

            // ligando as conexoes de saida e de entrada
            saida = new ObjectOutputStream(conexao.getOutputStream());
            saida.flush();
            entrada = new ObjectInputStream(conexao.getInputStream());

            final ObjectInputStream entradaFinal = entrada; // variável final ou efetivamente final

            Thread receiverThread = new Thread(() -> {
                try {
                    String serverMessage;
                    do {
                        // obtendo a mensagem enviada pelo servidor
                        serverMessage = (String) entradaFinal.readObject();
                        if (!serverMessage.equals("FIM")) {
                            System.out.println(">> " + serverMessage);
                        }
                    } while (!serverMessage.equals("FIM"));
                } catch (Exception e) {
                    System.err.println("Erro no receiverThread: " + e.toString());
                }
            });

            receiverThread.start();

            do {
                System.out.print("Eru::: ");
                mensagem = ler.nextLine();
                saida.writeObject(mensagem);
                saida.flush();

                if (!mensagem.equals("FIM")) {
                    System.out.println("A sua mensagem foi enviada para Melkor e ele respondeu:");
                    System.out.println("(a mensagem original foi \"" + mensagem + "\")");
                }
            } while (!mensagem.equals("FIM"));

            receiverThread.join(); // esperar o thread do receiver terminar
            saida.close();
            entrada.close();
            conexao.close();

        } catch (Exception e) {
            System.err.println("erro: " + e.toString());
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Uso: java tcp.Cliente <endereco-IP> <porta>");
            System.exit(1);
        }

        Cliente c = new Cliente();
        c.iniciar(args[0], Integer.parseInt(args[1]));
    }

}
