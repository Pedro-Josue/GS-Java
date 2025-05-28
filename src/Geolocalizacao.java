import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Geolocalizacao {

    private double latitude;
    private double longitude;

    // Busca lat e long da cidade via Nominatim
    public boolean buscarCoordenadas(String cidade) {
        try {
            String cidadeCodificada = URLEncoder.encode(cidade, "UTF-8");
            String urlStr = "https://nominatim.openstreetmap.org/search?format=json&limit=1&q=" + cidadeCodificada;

            URL url = new URL(urlStr);
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("GET");
            conexao.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader leitor = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
            StringBuilder resposta = new StringBuilder();
            String linha;
            while ((linha = leitor.readLine()) != null) {
                resposta.append(linha);
            }
            leitor.close();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode raiz = mapper.readTree(resposta.toString());

            if (raiz.isArray() && raiz.size() > 0) {
                JsonNode local = raiz.get(0);
                this.latitude = local.path("lat").asDouble();
                this.longitude = local.path("lon").asDouble();
                return true;
            } else {
                System.out.println("Cidade não encontrada.");
                return false;
            }

        } catch (Exception e) {
            System.out.println("Erro na geolocalização: " + e.getMessage());
            return false;
        }
    }

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
}
