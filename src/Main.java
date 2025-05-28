import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Geolocalizacao geo = new Geolocalizacao();
        Bombeiro bombeiro = new Bombeiro();
        AnaliseRisco risco = new AnaliseRisco();

        String opcao = "";

        while (!opcao.equals("0")) {
            System.out.println("Bem vindo ao analisador de risco de queimadas");
            System.out.println("1 - Buscar estação de bombeiros mais próxima");
            System.out.println("2 - Analisar risco de incêndio na cidade");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    System.out.print("Digite o nome da cidade para buscar a estação de bombeiro mais próxima: ");
                    String cidadeBombeiro = scanner.nextLine();

                    if (geo.buscarCoordenadas(cidadeBombeiro)) {
                        double lat = geo.getLatitude();
                        double lon = geo.getLongitude();

                        System.out.print("Digite o raio de busca (km): ");
                        double raio;
                        try {
                            raio = Double.parseDouble(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Raio inválido. Usando valor padrão 5 km.");
                            raio = 5.0;
                        }

                        String jsonEstacoes = bombeiro.buscarEstacoesJson(lat, lon, raio);
                        String resultado = bombeiro.encontrarEstacaoMaisProxima(jsonEstacoes, lat, lon);
                        System.out.println(resultado);
                    } else {
                        System.out.println("Cidade não encontrada.");
                    }
                    break;

                case "2":
                    System.out.print("Digite o nome da cidade para analisar o risco de incêndio: ");
                    String cidadeRisco = scanner.nextLine();

                    if (geo.buscarCoordenadas(cidadeRisco)) {
                        double lat = geo.getLatitude();
                        double lon = geo.getLongitude();

                        String resultado = risco.calcularRiscoIncendio(lat, lon);
                        System.out.println(resultado);
                    } else {
                        System.out.println("Cidade não encontrada.");
                    }
                    break;

                case "0":
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção inválida, tente novamente.");
            }
        }
        //fechando o scanner
        scanner.close();

    }
}
