# CaixaVerso Assistivo

**Plataforma assistiva, gamificada e adaptativa para apoiar empregados CAIXA na aprendizagem e navegação de jornadas digitais internas.**

O **CaixaVerso Assistivo** é um protótipo desenvolvido para o **Hackathon CAIXAVERSO - 1ª Onda**, com foco em **inclusão e acessibilidade digital do empregado CAIXA**.

A proposta não é apenas criar um treinamento digital. A solução funciona como uma **camada de autonomia digital**, capaz de transformar fluxos internos complexos em **trilhas guiadas, acessíveis, simples, auditáveis e gamificadas**, respeitando diferentes perfis de empregados, incluindo pessoas com deficiência, empregados com baixo letramento digital e profissionais que lidam com múltiplos sistemas corporativos.

---

## 1. Problema

A CAIXA possui um ambiente digital amplo, com múltiplos sistemas, fluxos operacionais, ferramentas internas e jornadas corporativas. Parte dos empregados pode enfrentar barreiras para acessar, compreender ou navegar de forma autônoma nesses ambientes, especialmente quando há:

- deficiência visual, auditiva, motora, cognitiva ou neurodivergência;
- baixa familiaridade com ferramentas digitais;
- dificuldade de adaptação a múltiplos sistemas internos;
- dependência de suporte presencial para execução de tarefas;
- excesso de informações em telas complexas;
- baixa padronização no processo de aprendizagem operacional.

Essas barreiras podem gerar perda de produtividade, retrabalho, aumento do esforço operacional, dependência de apoio externo e impacto indireto na qualidade do atendimento ao cliente.

---

## 2. Solução

O **CaixaVerso Assistivo** propõe uma experiência de aprendizagem prática, acessível e orientada por etapas.

A solução guia o empregado dentro de uma jornada simulada, apresentando instruções simples, textos explicativos, apoio por áudio, recursos de acessibilidade, validações, progresso, pontuação, medalhas e ranking gamificado.

A trilha de **Abertura de Conta Bancária** foi utilizada como **caso piloto** para demonstrar a solução. A arquitetura, porém, foi pensada para evoluir e permitir novas trilhas relacionadas a sistemas internos, como:

- intranet;
- SISRH;
- atender.caixa;
- ferramentas de atendimento;
- fluxos de suporte interno;
- processos operacionais;
- consulta a normativos;
- integração com canais corporativos.

---

## 3. Objetivo do projeto

Ampliar a autonomia digital de empregados CAIXA por meio de uma plataforma que combina:

- acessibilidade;
- orientação contextual;
- gamificação;
- trilhas de aprendizagem;
- feedback visual;
- apoio por voz;
- linguagem simples;
- progressão por níveis;
- dashboard gerencial;
- boas práticas de segurança;
- respeito à privacidade e à LGPD.

---

## 4. Público-alvo

O projeto foi pensado principalmente para:

- empregados PcD;
- empregados com baixa familiaridade digital;
- empregados em fase de aprendizagem de novos sistemas;
- equipes de atendimento que precisam operar múltiplos sistemas;
- gestores que desejam acompanhar evolução, engajamento e conclusão de trilhas;
- áreas de capacitação, inovação, tecnologia e experiência do empregado.

---

## 5. Como o projeto atende ao Desafio 1

O projeto se enquadra no desafio de **Inclusão e Acessibilidade Digital do Empregado CAIXA**, pois busca ampliar o acesso e a autonomia de empregados com necessidades especiais ou limitações tecnológicas no uso de ferramentas digitais internas.

A solução atua diretamente sobre os seguintes pontos:

| Necessidade do desafio | Como o projeto responde |
|---|---|
| Autonomia do empregado | Trilhas guiadas com instruções passo a passo |
| Acessibilidade | Alto contraste, ajuste de fonte, textos claros e áudio explicativo |
| Clareza | Linguagem objetiva e orientação por etapas |
| Usabilidade | Interface simples, botões destacados e fluxo progressivo |
| Inclusão digital | Treinamento prático para empregados com diferentes níveis de familiaridade |
| Apoio a PcD | Recursos visuais, sonoros e estrutura simplificada |
| Redução de dependência de suporte | Aprendizagem autônoma dentro da jornada |
| Melhoria da experiência do empregado | Ambiente mais acolhedor, simples e assistivo |

---

## 6. Principais funcionalidades

### Cadastro e autenticação

- Cadastro de usuário.
- Login com matrícula e senha.
- Validação de senha forte.
- Armazenamento de senha com BCrypt.
- Sessão por token aleatório.
- Cookie de sessão com `HttpOnly` e `SameSite=Strict`.
- Filtro de autenticação para proteger rotas internas.

### Perfil de acessibilidade

- Registro opcional de necessidades de acessibilidade.
- Uso da informação apenas para adaptação da experiência.
- Estrutura preparada para evoluir para personalização automática por perfil.

### Trilhas de aprendizagem

- Cards de treinamento.
- Treinamento disponível para Abertura de Conta Bancária.
- Cards futuros com status “Em breve”.
- Controle de início, andamento, conclusão e refazer trilha.
- Botão para reiniciar treinamento do zero.

### Jornada guiada

- Popup orientador contextual.
- Destaque visual do campo atual.
- Instruções simples.
- Avanço por etapas.
- Feedback de conclusão.
- Controle para exibir o assistente apenas durante a execução da trilha.

### Acessibilidade

- Botão para aumentar fonte.
- Botão para reduzir fonte.
- Modo alto contraste.
- Botão de áudio para leitura de textos explicativos.
- Uso de `speechSynthesis` no navegador.
- Interface com foco em simplicidade.
- Menos informação por etapa.
- Elementos visuais destacados.

### Gamificação

- Pontuação por avanço.
- XP acumulado.
- Nível do usuário.
- Medalhas/badges.
- Progresso da trilha.
- Ranking fictício para demonstração.
- Regras de evolução por conclusão de etapas e desafios.

### Quiz e validação de aprendizado

- Desafio liberado após conclusão da trilha.
- Perguntas de fixação.
- Validação de respostas.
- Pontuação conforme desempenho.
- Reforço do aprendizado prático.

### Dashboard

- Visão de progresso.
- Pontuação do usuário.
- Ranking.
- Medalhas.
- Indicadores de evolução.
- Estrutura preparada para métricas gerenciais.

---

## 7. Diferenciais da solução

O diferencial do **CaixaVerso Assistivo** está na combinação de recursos que normalmente aparecem separados:

1. **Treinamento prático**, não apenas conteúdo teórico.
2. **Orientação contextual**, mostrando onde o empregado deve atuar.
3. **Acessibilidade integrada**, não tratada como recurso secundário.
4. **Gamificação**, aumentando engajamento e continuidade.
5. **Dashboard**, permitindo visão gerencial da evolução.
6. **Arquitetura extensível**, preparada para novas trilhas.
7. **Segurança mínima aplicada ao MVP**, com BCrypt e sessão por token.
8. **Foco na experiência do empregado**, impactando indiretamente a experiência do cliente.

---

## 8. Arquitetura técnica

O projeto foi desenvolvido como uma aplicação web utilizando Java e Quarkus.

### Tecnologias utilizadas

- Java 17
- Quarkus
- Qute Templates
- RESTEasy Reactive
- Hibernate ORM com Panache
- H2 Database
- HTML
- CSS
- JavaScript
- BCrypt
- Web Speech API via `speechSynthesis`

### Estrutura geral

```text
src
├── main
│   ├── java
│   │   └── br/gov/caixa/caixaverso
│   │       ├── config
│   │       ├── controller
│   │       ├── entity
│   │       ├── filter
│   │       ├── repository
│   │       └── service
│   └── resources
│       ├── META-INF/resources
│       │   ├── css
│       │   ├── img
│       │   └── js
│       ├── templates
│       └── application.properties
```

---

## 9. Fluxo principal da aplicação

1. Usuário acessa a aplicação.
2. Realiza cadastro informando dados básicos e, opcionalmente, necessidades de acessibilidade.
3. Efetua login.
4. Visualiza as trilhas disponíveis.
5. Inicia a trilha de Abertura de Conta Bancária.
6. Recebe orientação passo a passo.
7. Pode ativar áudio, aumentar fonte ou usar alto contraste.
8. Preenche campos simulados da jornada.
9. Conclui etapas e acumula pontos.
10. Desbloqueia o desafio final.
11. Responde ao quiz.
12. Recebe pontuação, nível e medalhas.
13. Visualiza sua evolução no dashboard.
14. Pode refazer a trilha, zerando o progresso anterior.

---

## 10. Segurança e privacidade

O projeto é um MVP para hackathon, mas já contempla algumas preocupações importantes:

- Senhas armazenadas com BCrypt.
- Regras de senha forte no cadastro.
- Sessão baseada em token aleatório.
- Armazenamento de hash do token de sessão.
- Cookie com `HttpOnly`.
- Cookie com `SameSite=Strict`.
- Rotas internas protegidas por filtro de autenticação.
- Dados de acessibilidade tratados como opcionais.
- Ambiente de demonstração sem uso de dados reais de clientes.
- Trilha com dados fictícios e finalidade educacional.

### Evolução prevista para ambiente corporativo

Em um cenário real de produção, a solução deveria evoluir para:

- integração com SSO corporativo;
- autenticação federada;
- autorização por perfil;
- trilhas vinculadas a papéis organizacionais;
- criptografia de dados sensíveis;
- auditoria de acessos;
- logs estruturados;
- controle CSRF;
- cookies com `Secure` em HTTPS;
- segregação de ambientes;
- governança LGPD;
- mascaramento de dados;
- integração com sistemas corporativos autorizados.

---

## 11. LGPD e ética

A solução foi pensada para respeitar princípios de privacidade, minimização de dados e não discriminação.

As informações de acessibilidade devem ser:

- opcionais;
- utilizadas apenas para adaptar a experiência;
- não expostas individualmente em dashboards públicos;
- não utilizadas para restringir acesso;
- não utilizadas para tomada de decisão discriminatória;
- protegidas em ambiente corporativo adequado.

O objetivo da coleta é exclusivamente assistivo: melhorar a experiência, reduzir barreiras e ampliar autonomia.

---

## 12. Valor de negócio para a CAIXA

O projeto pode gerar valor institucional em diferentes frentes:

### Para o empregado

- Maior autonomia digital.
- Menor dependência de suporte.
- Aprendizagem mais simples e prática.
- Experiência mais inclusiva.
- Redução da ansiedade ao usar sistemas complexos.
- Jornada adaptada ao seu ritmo.

### Para gestores

- Acompanhamento de evolução por trilha.
- Identificação de gargalos de aprendizagem.
- Visão de engajamento.
- Indicadores de conclusão.
- Apoio à gestão de capacitação.

### Para a organização

- Redução de retrabalho.
- Redução de esforço operacional.
- Padronização de treinamento.
- Melhoria da experiência do empregado.
- Fortalecimento da cultura de inclusão.
- Apoio à transformação digital interna.
- Impacto indireto na experiência do cliente.

---

## 13. Critérios de pontuação atendidos

| Critério | Como o projeto se posiciona |
|---|---|
| Aderência ao desafio | Foca inclusão digital, acessibilidade e autonomia do empregado CAIXA |
| Inovação e criatividade | Combina trilhas, gamificação, popup guiado, voz, badges e dashboard |
| Valor de negócio | Reduz suporte, acelera aprendizagem e melhora produtividade |
| Ética e segurança | Usa sessão com token, BCrypt e dados fictícios no MVP |
| Viabilidade técnica | Protótipo funcional em Java/Quarkus com banco, telas e fluxo completo |

---

## 14. Roadmap de evolução

### Curto prazo

- Tornar o dashboard totalmente baseado em dados reais do banco.
- Implementar ranking real por pontuação.
- Adicionar testes automatizados.
- Melhorar cobertura de segurança nos formulários.
- Remover dados fixos de demonstração.
- Refinar CSS e responsividade.

### Médio prazo

- Criar motor parametrizável de trilhas.
- Permitir cadastro administrativo de novas jornadas.
- Adaptar automaticamente a experiência por perfil de acessibilidade.
- Adicionar relatórios gerenciais.
- Implementar trilhas para SISRH, intranet, atendimento e normativos.
- Integrar com base de conhecimento autorizada.

### Longo prazo

- Integração com SSO corporativo.
- Integração com plataformas internas autorizadas.
- Motor inteligente de recomendação de trilhas.
- Assistente contextual para dúvidas frequentes.
- Analytics de aprendizagem.
- Auditoria corporativa.
- Deploy em ambiente institucional seguro.

---

## 15. Como executar o projeto

### Pré-requisitos

- Java 17+
- Maven 3.8+
- Navegador moderno

### Executar em modo desenvolvimento

```bash
mvn quarkus:dev
```

Acesse:

```text
http://localhost:8080
```

### Gerar build

```bash
mvn clean package
```

### Executar JAR gerado

```bash
java -jar target/quarkus-app/quarkus-run.jar
```

---

## 16. Observações sobre ambiente de demonstração

Este projeto utiliza banco H2 para facilitar execução local durante o hackathon.

O ambiente atual é exclusivamente demonstrativo e não utiliza dados reais da CAIXA, clientes ou empregados. Qualquer integração com sistemas corporativos deverá seguir políticas internas de segurança, privacidade, arquitetura e governança.

---

## 17. Pitch resumido

O **CaixaVerso Assistivo** é uma plataforma de autonomia digital para empregados CAIXA.

Ele transforma jornadas internas complexas em trilhas guiadas, acessíveis e gamificadas, com apoio por voz, linguagem simples, progresso, pontuação, medalhas e dashboard.

A abertura de conta é a trilha piloto. A proposta é permitir que a CAIXA evolua para uma biblioteca de trilhas assistivas aplicáveis a diferentes sistemas internos, apoiando empregados PcD, profissionais com baixo letramento digital e equipes que precisam operar múltiplas ferramentas no atendimento.

Mais do que treinar, a solução reduz barreiras, aumenta autonomia, fortalece a inclusão e melhora a experiência do empregado — base essencial para uma melhor experiência do cliente.

---

## 18. Frase de impacto

**CaixaVerso Assistivo: inclusão digital na prática, transformando sistemas complexos em jornadas simples, acessíveis e humanas.**
