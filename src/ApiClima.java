import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClima {
    //apikey open weather em variavel de ambiente
    private String apiKey = System.getenv("API_KEY_OPENWEATHER");

    public void obterDadosClima (String cidade){
        try {
            String urlString = String.format(
                    "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric&lang=pt_br",
                    cidade, apiKey);

            URL url = new URL(urlString);
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("GET");

            int codigoResposta = conexao.getResponseCode();
            if (codigoResposta == 200) {
                BufferedReader leitor = new BufferedReader(
                        new InputStreamReader(conexao.getInputStream()));
                String linha;
                StringBuilder resposta = new StringBuilder();

                while ((linha = leitor.readLine()) != null) {
                    resposta.append(linha);
                }
                leitor.close();

                // Interpretar JSON
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(resposta.toString());

                double temperatura = root.path("main").path("temp").asDouble();
                int umidade = root.path("main").path("humidity").asInt();
                double vento = root.path("wind").path("speed").asDouble();
                String nomeCidade = root.path("name").asText();

                System.out.println("📍 Cidade: " + nomeCidade);
                System.out.println("🌡️ Temperatura: " + temperatura + " °C");
                System.out.println("💧 Umidade: " + umidade + " %");
                System.out.println("💨 Vento: " + vento + " m/s");

            } else {
                System.out.println("❌ Erro na conexão: Código " + codigoResposta);
            }

        } catch (Exception e) {
                e.printStackTrace();
        }
    }
}

