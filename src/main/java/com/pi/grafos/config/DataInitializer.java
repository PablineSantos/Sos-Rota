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
import com.pi.grafos.repository.AmbulanciaRepository;
import com.pi.grafos.repository.CidadeRepository;
import com.pi.grafos.repository.EquipeRepository;
import com.pi.grafos.repository.LocalizacaoRepository;
import com.pi.grafos.repository.OcorrenciaRepository;
import com.pi.grafos.repository.RuaRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CidadeRepository cidadeRepository;
    private final LocalizacaoRepository localizacaoRepository;
    private final RuaRepository ruaRepository;
    private final AmbulanciaRepository ambulanciaRepository;
    private final EquipeRepository equipeRepository;       // Adicionado
    private final OcorrenciaRepository ocorrenciaRepository; // Adicionado para evitar erro se tiver ocorrência salva

    // IDs do CSV que representam BASES DE AMBULÂNCIA (Hospitais ou Postos)
    private final List<Long> idsBases = Arrays.asList(2L, 9L, 11L, 13L, 14L, 15L, 17L, 20L);

    public DataInitializer(CidadeRepository cidadeRepository, 
                           LocalizacaoRepository localizacaoRepository, 
                           RuaRepository ruaRepository,
                           AmbulanciaRepository ambulanciaRepository,
                           EquipeRepository equipeRepository,
                           OcorrenciaRepository ocorrenciaRepository) {
        this.cidadeRepository = cidadeRepository;
        this.localizacaoRepository = localizacaoRepository;
        this.ruaRepository = ruaRepository;
        this.ambulanciaRepository = ambulanciaRepository;
        this.equipeRepository = equipeRepository;
        this.ocorrenciaRepository = ocorrenciaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Limpar o banco de dados NA ORDEM CORRETA (Filhos -> Pais)
        System.out.println("--- Limpando banco de dados... ---");
        
        // Limpa Ocorrencias (pois dependem de Localizacao)
        ocorrenciaRepository.deleteAll();
        
        // Limpa Equipes (pois dependem de Ambulancia) -> ISSO RESOLVE SEU ERRO
        equipeRepository.deleteAll();
        
        // Limpa Ruas (dependem de Localizacao)
        ruaRepository.deleteAll();
        
        // Limpa Ambulancias (agora seguro, pois não tem equipes vinculadas)
        ambulanciaRepository.deleteAll(); 
        
        // Limpa Localizacao e Cidade
        localizacaoRepository.deleteAll();
        cidadeRepository.deleteAll();

        // 2. Criar a cidade base
        Cidade cidade = new Cidade();
        cidade.setNomeCidade("Cidália"); 
        cidade = cidadeRepository.save(cidade);
        System.out.println("Cidade criada: " + cidade.getNomeCidade());

        // 3. Importar Bairros e criar Mapa de IDs (CSV ID -> Entidade Banco)
        Map<Long, Localizacao> mapaBairros = importBairros(cidade);

        // 4. Importar Ruas usando o mapa
        importRuas(cidade, mapaBairros);

        // 5. REMOVIDO: "criarFrotaAmbulancias"
        // O sistema agora inicia SEM ambulâncias para você cadastrar manualmente no fluxo.
        
        System.out.println("--- Inicialização de dados concluída com sucesso! ---");
    }

    private Map<Long, Localizacao> importBairros(Cidade cidade) throws Exception {
        System.out.println("Importando bairros...");
        Map<Long, Localizacao> map = new HashMap<>();
        
        ClassPathResource resource = new ClassPathResource("data/bairros.csv");
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) { header = false; continue; } 

                String[] data = line.split(",");
                // CSV: id,nome_bairro
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
                    
                    Localizacao salvo = localizacaoRepository.save(loc);
                    map.put(idCsv, salvo);
                }
            }
        }
        return map;
    }

    private void importRuas(Cidade cidade, Map<Long, Localizacao> mapaBairros) throws Exception {
        System.out.println("Importando conexões (ruas)...");
        
        ClassPathResource resource = new ClassPathResource("data/ruas_conexoes.csv");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) { header = false; continue; } 

                String[] data = line.split(",");
                if (data.length >= 4) {
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
                        rua.setNomeRua("Rota " + origem.getNome() + " -> " + destino.getNome());
    
                        ruaRepository.save(rua);
                    }
                }
            }
        }
    }
}