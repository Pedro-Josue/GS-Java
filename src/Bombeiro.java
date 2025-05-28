import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Bombeiro {

    // Busca JSON com estações de bombeiro próximas da lat/lon (raio em km)
    public String buscarEstacoesJson(double lat, double lon, double raioKm) {
        try {
            // Consulta Overpass API para "amenity=fire_station" num círculo
            String query = "[out:json];node[\"amenity\"=\"fire_station\"](around:" + (raioKm * 1000) + "," + lat + "," + lon + ");out;";

            String urlStr = "https://overpass-api.de/api/interpreter?data=" + java.net.URLEncoder.encode(query, "UTF-8");

            URL url = new URL(urlStr);
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("GET");
            conexao.setRequestProperty("User-Agent", "Mozilla/5.0");

            int codigoResposta = conexao.getResponseCode();
            if (codigoResposta != 200) {
                System.out.println("Erro na API Overpass: código " + codigoResposta);
                return null;
            }

            BufferedReader leitor = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
            StringBuilder resposta = new StringBuilder();
            String linha;
            while ((linha = leitor.readLine()) != null) {
                resposta.append(linha);
            }
            leitor.close();

            return resposta.toString();

        } catch (Exception e) {
            System.out.println("Erro ao buscar estações: " + e.getMessage());
            return null;
        }
    }

    // Calcula distância em km entre dois pontos
    public double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Raio da Terra em km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // Recebe JSON da Overpass e retorna a estação mais próxima como string formatada
    public String encontrarEstacaoMaisProxima(String json, double latRef, double lonRef) {
        if (json == null || json.isEmpty()) {
            return "Nenhuma estação encontrada.";
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode raiz = mapper.readTree(json);
            JsonNode elementos = raiz.path("elements");

            double menorDistancia = Double.MAX_VALUE;
            String estacaoMaisProxima = null;

            for (JsonNode node : elementos) {
                double lat = node.path("lat").asDouble();
                double lon = node.path("lon").asDouble();
                double distancia = calcularDistancia(latRef, lonRef, lat, lon);

                if (distancia < menorDistancia) {
                    menorDistancia = distancia;

                    JsonNode tags = node.path("tags");
                    String nome = tags.has("name") ? tags.get("name").asText() : "Desconhecido";
                    String rua = tags.has("addr:street") ? tags.get("addr:street").asText() : "";
                    String numero = tags.has("addr:housenumber") ? tags.get("addr:housenumber").asText() : "";
                    String cidade = tags.has("addr:city") ? tags.get("addr:city").asText() : "";
                    String cep = tags.has("addr:postcode") ? tags.get("addr:postcode").asText() : "";

                    String endereco = (rua + " " + numero + ", " + cidade + " " + cep).trim();

                    estacaoMaisProxima = String.format(
                            "Nome: %s\nEndereço: %s\nLatitude: %.6f\nLongitude: %.6f\nDistância: %.2f km",
                            nome,
                            endereco.isEmpty() ? "Desconhecido" : endereco,
                            lat,
                            lon,
                            distancia);
                }
            }

            return estacaoMaisProxima == null ? "Nenhuma estação encontrada." : estacaoMaisProxima;

        } catch (Exception e) {
            return "Erro ao processar dados da estação: " + e.getMessage();
        }
    }
}
