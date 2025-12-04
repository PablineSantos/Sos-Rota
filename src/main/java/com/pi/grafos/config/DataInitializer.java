/*package com.pi.grafos.config;

import com.pi.grafos.model.Cidade;
import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.Rua;
import com.pi.grafos.repository.CidadeRepository;
import com.pi.grafos.repository.LocalizacaoRepository;
import com.pi.grafos.repository.RuaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CidadeRepository cidadeRepository;
    private final LocalizacaoRepository localizacaoRepository;
    private final RuaRepository ruaRepository;

    public DataInitializer(CidadeRepository cidadeRepository, 
                           LocalizacaoRepository localizacaoRepository, 
                           RuaRepository ruaRepository) {
        this.cidadeRepository = cidadeRepository;
        this.localizacaoRepository = localizacaoRepository;
        this.ruaRepository = ruaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Limpar o banco de dados (A ordem importa por causa das Foreign Keys)
        System.out.println("--- Limpando banco de dados... ---");
        ruaRepository.deleteAll();
        localizacaoRepository.deleteAll();
        cidadeRepository.deleteAll();

        // 2. Criar a cidade base (já que as tabelas exigem uma cidade)
        Cidade cidade = new Cidade();
        cidade.setNomeCidade("Goiânia"); // Ou o nome que preferir
        cidade = cidadeRepository.save(cidade);
        System.out.println("Cidade criada: " + cidade.getNomeCidade());

        // 3. Importar Bairros e criar Mapa de IDs
        // O Mapa serve para vincular o ID antigo (do CSV) com o novo objeto salvo (ID do Banco)
        Map<Long, Localizacao> mapaBairrosCsvParaEntidade = importBairros(cidade);

        // 4. Importar Ruas usando o mapa
        importRuas(cidade, mapaBairrosCsvParaEntidade);
        
        System.out.println("--- Inicialização de dados concluída! ---");
    }

    private Map<Long, Localizacao> importBairros(Cidade cidade) throws Exception {
        System.out.println("Importando bairros...");
        Map<Long, Localizacao> map = new HashMap<>();
        
        ClassPathResource resource = new ClassPathResource("data/bairros.csv");
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) { header = false; continue; } // Pula cabeçalho

                String[] data = line.split(",");
                // CSV formato: id,nome_bairro
                Long idCsv = Long.parseLong(data[0].trim());
                String nome = data[1].trim();

                Localizacao loc = new Localizacao();
                loc.setNome(nome);
                loc.setCidade(cidade);
                
                // Salva no banco (o JPA vai gerar um novo ID, ignoramos o ID do CSV para persistência)
                Localizacao salvo = localizacaoRepository.save(loc);
                
                // Guardamos no mapa: ID do CSV -> Objeto Real do Banco
                map.put(idCsv, salvo);
            }
        }
        return map;
    }

    private void importRuas(Cidade cidade, Map<Long, Localizacao> mapaBairros) throws Exception {
        System.out.println("Importando conexões (ruas)...");
        
        ClassPathResource resource = new ClassPathResource("data/ruas_conexoes.csv");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) { header = false; continue; } // Pula cabeçalho

                String[] data = line.split(",");
                // CSV formato: id,bairro_origem_id,bairro_destino_id,distancia_km
                
                // O ID da rua (data[0]) ignoramos pois o banco gera
                Long idOrigemCsv = Long.parseLong(data[1].trim());
                Long idDestinoCsv = Long.parseLong(data[2].trim());
                Double distancia = Double.parseDouble(data[3].trim());

                Localizacao origem = mapaBairros.get(idOrigemCsv);
                Localizacao destino = mapaBairros.get(idDestinoCsv);

                if (origem != null && destino != null) {
                    Rua rua = new Rua();
                    rua.setCidade(cidade);
                    rua.setOrigem(origem);
                    rua.setDestino(destino);
                    rua.setDistancia(distancia);
                    // Como o CSV não tem nome da rua, geramos um automático
                    rua.setNomeRua("Rota " + origem.getNome() + " -> " + destino.getNome());

                    ruaRepository.save(rua);
                } else {
                    System.err.println("Erro ao importar rua: Bairro não encontrado para linha: " + line);
                }
            }
        }
    }
}*/