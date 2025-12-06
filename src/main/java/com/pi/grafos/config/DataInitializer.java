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

import com.pi.grafos.model.Ambulancia;
import com.pi.grafos.model.Cidade;
import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.Rua;
import com.pi.grafos.model.enums.AmbulanciaStatus;
import com.pi.grafos.model.enums.TipoAmbulancia;
import com.pi.grafos.model.enums.TipoLocalizacao;
import com.pi.grafos.repository.AmbulanciaRepository;
import com.pi.grafos.repository.CidadeRepository;
import com.pi.grafos.repository.LocalizacaoRepository;
import com.pi.grafos.repository.RuaRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CidadeRepository cidadeRepository;
    private final LocalizacaoRepository localizacaoRepository;
    private final RuaRepository ruaRepository;
    private final AmbulanciaRepository ambulanciaRepository;

    // IDs do CSV que representam BASES DE AMBULÂNCIA (Hospitais ou Postos)
    private final List<Long> idsBases = Arrays.asList(2L, 9L, 11L, 13L, 14L, 15L, 17L, 20L);

    public DataInitializer(CidadeRepository cidadeRepository, 
                           LocalizacaoRepository localizacaoRepository, 
                           RuaRepository ruaRepository,
                           AmbulanciaRepository ambulanciaRepository) {
        this.cidadeRepository = cidadeRepository;
        this.localizacaoRepository = localizacaoRepository;
        this.ruaRepository = ruaRepository;
        this.ambulanciaRepository = ambulanciaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Limpar o banco de dados (Ordem importa devido às FKs)
        System.out.println("--- Limpando banco de dados... ---");
        ruaRepository.deleteAll();
        ambulanciaRepository.deleteAll(); // Limpa frota antiga
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

        // 5. Criar Frota de Ambulâncias (CRUCIAL para o algoritmo funcionar)
        criarFrotaAmbulancias(mapaBairros);
        
        System.out.println("--- Inicialização de dados concluída com sucesso! ---");
    }

    private Map<Long, Localizacao> importBairros(Cidade cidade) throws Exception {
        System.out.println("Importando bairros...");
        Map<Long, Localizacao> map = new HashMap<>();
        
        ClassPathResource resource = new ClassPathResource("data/bairros.csv");
        
        // Adicionei StandardCharsets.UTF_8 para evitar problemas de acentuação
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
                    
                    // CORREÇÃO AQUI: Setamos o ID manualmente pois não tem @GeneratedValue na Entidade
                    loc.setIdLocal(idCsv); 
                    
                    loc.setNome(nome);
                    loc.setCidade(cidade);
    
                    // Define se é Base ou Bairro comum
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
                // CSV: id,bairro_origem_id,bairro_destino_id,distancia_km
                
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

    private void criarFrotaAmbulancias(Map<Long, Localizacao> mapaBairros) {
        System.out.println("Criando frota de ambulâncias...");

        // Cria uma UTI no Centro (ID 2)
        criarAmbulancia("UTI-001", TipoAmbulancia.UTI, mapaBairros.get(2L));

        // Cria uma Básica no Recanto Verde (ID 9)
        criarAmbulancia("USB-101", TipoAmbulancia.BASICA, mapaBairros.get(9L));

        // Cria uma UTI na Bela Vista (ID 14)
        criarAmbulancia("UTI-002", TipoAmbulancia.UTI, mapaBairros.get(14L));

        // Cria uma Básica no Lago Azul (ID 17)
        criarAmbulancia("USB-102", TipoAmbulancia.BASICA, mapaBairros.get(17L));
        
        // Cria uma Básica na Morada do Sol (ID 15)
        criarAmbulancia("USB-103", TipoAmbulancia.BASICA, mapaBairros.get(15L));
    }

    private void criarAmbulancia(String placa, TipoAmbulancia tipo, Localizacao base) {
        if (base != null) {
            Ambulancia amb = new Ambulancia();
            amb.setPlaca(placa);
            amb.setTipoAmbulancia(tipo);
            amb.setStatusAmbulancia(AmbulanciaStatus.DISPONIVEL);
            amb.setIsAtivo(true);
            amb.setUnidade(base); // Define onde ela está parada
            ambulanciaRepository.save(amb);
            System.out.println("Ambulância " + placa + " criada na base " + base.getNome());
        }
    }
}