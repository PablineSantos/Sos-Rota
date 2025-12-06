package com.pi.grafos.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pi.grafos.model.Cidade;
import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.Rua;
import com.pi.grafos.model.enums.TipoLocalizacao;
import com.pi.grafos.repository.CidadeRepository;
import com.pi.grafos.repository.LocalizacaoRepository;
import com.pi.grafos.repository.RuaRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CidadeRepository cidadeRepository;
    private final LocalizacaoRepository localizacaoRepository;
    private final RuaRepository ruaRepository;

    // IDs do CSV que representam BASES DE AMBULÂNCIA
    private final List<Long> idsBases = Arrays.asList(2L, 9L, 11L, 13L, 14L, 15L, 17L, 20L);

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
        // --- PROTEÇÃO: SÓ RODA SE O BANCO ESTIVER VAZIO ---
        if (cidadeRepository.count() > 0) {
            System.out.println(">>> Banco de dados já contem dados. Pulando inicialização para preservar seus cadastros.");
            return; 
        }

        // Se chegou aqui, o banco está vazio, então carrega o mapa inicial
        System.out.println(">>> Banco vazio detectado. Iniciando importação do Mapa...");

        // 1. Criar a cidade base
        Cidade cidade = new Cidade();
        cidade.setNomeCidade("Cidália"); 
        cidade = cidadeRepository.save(cidade);
        System.out.println("Cidade criada: " + cidade.getNomeCidade());

        // 2. Importar Bairros
        Map<Long, Localizacao> mapaBairros = importBairros(cidade);

        // 3. Importar Ruas
        importRuas(cidade, mapaBairros);

        System.out.println(">>> Mapa importado com sucesso! Agora você pode cadastrar as ambulâncias manualmente.");
    }

    private Map<Long, Localizacao> importBairros(Cidade cidade) throws Exception {
        Map<Long, Localizacao> map = new HashMap<>();
        ClassPathResource resource = new ClassPathResource("data/bairros.csv");
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) { header = false; continue; } 
                String[] data = line.split(",");
                if (data.length >= 2) {
                    Long idCsv = Long.parseLong(data[0].trim());
                    String nome = data[1].trim();
    
                    Localizacao loc = new Localizacao();
                    loc.setIdLocal(idCsv); 
                    loc.setNome(nome);
                    loc.setCidade(cidade);
                    if (idsBases.contains(idCsv)) {
                        loc.setTipo(TipoLocalizacao.BASE_AMBULANCIA);
                    } else {
                        loc.setTipo(TipoLocalizacao.BAIRRO);
                    }
                    map.put(idCsv, localizacaoRepository.save(loc));
                }
            }
        }
        return map;
    }

    private void importRuas(Cidade cidade, Map<Long, Localizacao> mapaBairros) throws Exception {
        ClassPathResource resource = new ClassPathResource("data/ruas_conexoes.csv");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) { header = false; continue; } 
                String[] data = line.split(",");
                if (data.length >= 4) {
                    Long idOrigem = Long.parseLong(data[1].trim());
                    Long idDestino = Long.parseLong(data[2].trim());
                    Double distancia = Double.parseDouble(data[3].trim());
    
                    Localizacao origem = mapaBairros.get(idOrigem);
                    Localizacao destino = mapaBairros.get(idDestino);
    
                    if (origem != null && destino != null) {
                        Rua rua = new Rua();
                        rua.setCidade(cidade);
                        rua.setOrigem(origem);
                        rua.setDestino(destino);
                        rua.setDistancia(distancia);
                        rua.setNomeRua("Rota " + origem.getNome() + " -> " + destino.getNome());
                        ruaRepository.save(rua);
                    }
                }
            }
        }
    }
}