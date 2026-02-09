🚑 SOS-Rota — Sistema Inteligente de Gestão e Despacho de Emergências
📌 Visão Geral

O SOS-Rota é um sistema desenvolvido em Java com foco na gestão e despacho otimizado de atendimentos de emergência em uma cidade fictícia chamada Cidália. Desenvolvi este sistema aplicando conceitos de Engenharia de Software, Estruturas de Dados e Banco de Dados Relacional, utilizando o algoritmo de Dijkstra para cálculo de rotas mínimas entre bases de ambulâncias e locais de ocorrência.

Este projeto foi desenvolvido como parte do Projeto Integrador 2025-2 do curso de Bacharelado em Engenharia de Software – SENAI FATESG.

🎯 Objetivo do Projeto

Meu objetivo com este projeto é apoiar a gestão de atendimentos de emergência, permitindo:

Cadastro e gerenciamento de ocorrências;

Gestão de ambulâncias, profissionais e equipes;

Cálculo automático da melhor rota entre base e ocorrência;

Despacho otimizado de ambulâncias respeitando SLAs por gravidade;

Registro e consulta de histórico de atendimentos.

🧠 Principais Funcionalidades

🔐 Autenticação de Usuários

🚑 Cadastro de Ambulâncias (tipo, status, base)

👨‍⚕️ Cadastro de Profissionais e Equipes

🗺️ Modelagem da Cidade como Grafo

📍 Cadastro de Ocorrências por Gravidade

🧮 Cálculo de Caminho Mínimo (Dijkstra)

⏱️ Validação de SLA por Gravidade

📊 Consultas e Relatórios Gerenciais

⚙️ Regras de Negócio Essenciais

Durante o desenvolvimento, implementei as seguintes regras de negócio principais:

Uma ambulância só pode ser despachada se estiver Disponível e com equipe completa;

Ocorrências de Gravidade Alta exigem ambulância UTI e SLA de 8 minutos;

Ocorrências de Gravidade Média exigem ambulância Básica e SLA de 15 minutos;

Uma ambulância não pode atender duas ocorrências simultaneamente;

Senhas são armazenadas utilizando hash criptográfico.

🗃️ Estrutura de Dados e Algoritmos

Neste projeto, utilizei:

A biblioteca java.util (List, Queue, etc.);

Implementação do algoritmo de Dijkstra para cálculo de rotas;

Modelagem da cidade como grafo ponderado.

🛢️ Banco de Dados

Implementei o sistema utilizando banco de dados relacional (PostgreSQL ou MySQL), com importação de dados a partir de arquivos CSV.

Principais entidades do sistema:

Bases

Ambulâncias

Profissionais

Equipes

Ocorrências

Atendimentos

Usuários

📂 Estrutura do Projeto

Organizei o projeto seguindo uma arquitetura em camadas, separando responsabilidades para facilitar manutenção, testes e evolução do sistema.

src/main/
├── java/com/pi/grafos/
│   ├── config/      
│   ├── controller/  
│   ├── model/     
│   ├── repository/  
│   ├── service/  
│   ├── view/        
│   ├── GrafosSpringApp.java
│   ├── JavaFxApp.java     
│   └── Main.java        
│
├── resources/
│   ├── data/         
│   ├── fonts/
│   │   └── Poppins/   
│   └── images/       
🚀 Tecnologias Utilizadas

Java

Spring Boot

JDBC

PostgreSQL

Git & GitHub

📄 Documentação

Documento de Requisitos do Sistema (ERS)

Histórias de Usuário

Requisitos Funcionais e Não Funcionais

Regras de Domínio

Toda a documentação está disponível na pasta /docs do repositório.

👥 Equipe

Hatus

Gabriel

Hyan

Pabline

📅 Período de Desenvolvimento

Início: 17/11/2025

Conclusão: 08/12/2025

📜 Licença

Este projeto é de uso acadêmico, desenvolvido exclusivamente para fins educacionais.
