import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AnaliseRisco {

    private static final String API_KEY = System.getenv("API_KEY_OPENWEATHER");

    // Consulta API OpenWeather para obter risco de incêndio (usamos índice de umidade e vento como exemplo)
    public String calcularRiscoIncendio(double lat, double lon) {
        try {
            String urlStr = String.format(
                    "https://api.openweathermap.org/data/2.5/weather?lat=%.6f&lon=%.6f&appid=%s&units=metric&lang=pt_br",
                    lat, lon, API_KEY);

            URL url = new URL(urlStr);
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("GET");
            conexao.setRequestProperty("User-Agent", "Mozilla/5.0");

            int codigo = conexao.getResponseCode();
            if (codigo != 200) {
                return "Erro na API OpenWeather: código " + codigo;
            }

            BufferedReader leitor = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
            StringBuilder resposta = new StringBuilder();
            String linha;
            while ((linha = leitor.readLine()) != null) {
                resposta.append(linha);
            }
            leitor.close();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode raiz = mapper.readTree(resposta.toString());

            double umidade = raiz.path("main").path("humidity").asDouble();
            double vento = raiz.path("wind").path("speed").asDouble();

            // Lógica simples para risco (quanto menor umidade e maior vento, maior risco)
            String risco;
            if (umidade < 30 && vento > 10) {
                risco = "ALTO";
            } else if (umidade < 50 && vento > 5) {
                risco = "MÉDIO";
            } else {
                risco = "BAIXO";
            }

            return String.format("Risco de incêndio na área: %s\nUmidade: %.1f%%\nVelocidade do vento: %.1f m/s",
                    risco, umidade, vento);

        } catch (Exception e) {
            return "Erro ao consultar risco de incêndio: " + e.getMessage();
        }
    }
}
