package com.pi.grafos.config;

import com.pi.grafos.model.Cidade;
import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.enums.TipoLocalizacao;
import com.pi.grafos.repository.CidadeRepository;
import com.pi.grafos.repository.LocalizacaoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
public class SistemaSeeder implements CommandLineRunner {

    private final LocalizacaoRepository localizacaoRepository;
    private final CidadeRepository cidadeRepository;

    // LISTA DE IDs QUE SÃO BASES_AMBULANCIA
    // 2, 9, 11, 13, 14, 15, 17, 20
    private final List<Long> idsBases = Arrays.asList(2L, 9L, 11L, 13L, 14L, 15L, 17L, 20L);

    public SistemaSeeder(LocalizacaoRepository locRepo, CidadeRepository cidRepo) {
        this.localizacaoRepository = locRepo;
        this.cidadeRepository = cidRepo;
    }

    @Override
    public void run(String... args) {
        if (localizacaoRepository.count() == 0) {
            carregarGrafo();
        }
    }

    private void carregarGrafo() {
        System.out.println("Carregando e Classificando mapa de Cidália...");

        Cidade cidade = cidadeRepository.findByNomeCidade("Cidália")
                .orElseGet(() -> cidadeRepository.save(new Cidade("Cidália")));

        try {
            InputStream is = getClass().getResourceAsStream("/bairros.csv");
            if (is == null) return;

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String linha;

            reader.readLine(); // Descomente se tiver cabeçalho

            while ((linha = reader.readLine()) != null) {
                try {
                    String[] partes = linha.split(",");
                    if (partes.length >= 2) {
                        Long id = Long.parseLong(partes[0].trim());
                        String nome = partes[1].trim();

                        Localizacao local = new Localizacao();
                        local.setIdLocal(id);
                        local.setNome(nome);
                        local.setCidade(cidade);

                        // --- LÓGICA DE CLASSIFICAÇÃO AUTOMÁTICA ---
                        if (idsBases.contains(id)) {
                            local.setTipo(TipoLocalizacao.BASE_AMBULANCIA); // É Base (Cinza)
                        } else {
                            local.setTipo(TipoLocalizacao.BAIRRO); // É Bairro Comum (Azul)
                        }

                        localizacaoRepository.save(local);
                         System.out.println("Salvo: " + nome + " como " + local.getTipo());
                    }
                } catch (Exception ex) {
                    System.err.println("Erro linha: " + linha);
                }
            }
            System.out.println("Mapa carregado e classificado com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}