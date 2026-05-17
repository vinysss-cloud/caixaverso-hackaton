# CaixaVerso Assistivo

**Plataforma assistiva, acessível e gamificada para ampliar a autonomia digital de empregados CAIXA em jornadas internas.**

O **CaixaVerso Assistivo** é um protótipo desenvolvido para o **Hackathon CAIXAVERSO - 1ª Onda**, com foco no **Desafio 1 — Inclusão e Acessibilidade Digital do Empregado CAIXA**.

A proposta vai além de um treinamento digital tradicional. O projeto funciona como uma **camada assistiva de autonomia**, capaz de transformar fluxos internos complexos em **jornadas guiadas, simples, acessíveis, auditáveis e progressivas**, apoiando empregados PcD, empregados com baixa familiaridade digital e profissionais que precisam lidar diariamente com múltiplos sistemas corporativos.

> **Frase central do projeto:**  
> O CaixaVerso Assistivo transforma sistemas internos complexos em jornadas simples, acessíveis e humanas.

---

## 1. Contexto do desafio

A CAIXA possui um ambiente digital amplo, composto por sistemas, canais internos, normativos, ferramentas de gestão, atendimento e processos operacionais. Parte dos empregados pode enfrentar barreiras para compreender, navegar e executar tarefas de forma autônoma nesses ambientes.

Essas barreiras podem afetar especialmente:

- empregados PcD;
- empregados com baixa familiaridade digital;
- empregados em unidades com menor apoio presencial;
- profissionais que utilizam muitos sistemas ao mesmo tempo;
- empregados em fase de aprendizagem de novos fluxos;
- equipes que dependem de orientação rápida, clara e padronizada.

O resultado pode ser aumento de dúvidas recorrentes, dependência de suporte, retrabalho, perda de produtividade e maior esforço operacional.

---

## 2. Problema que o projeto resolve

O problema atacado pelo **CaixaVerso Assistivo** é a dificuldade de navegação autônoma em jornadas digitais internas.

Em vez de entregar apenas manuais, PDFs ou treinamentos passivos, a solução cria uma experiência prática, guiada e acessível, na qual o empregado aprende executando uma jornada simulada, com apoio contextual em cada etapa.

O projeto busca responder à seguinte pergunta:

> **Como permitir que empregados com diferentes necessidades, ritmos e níveis de familiaridade digital consigam aprender e navegar por fluxos internos com mais autonomia, clareza e segurança?**

---

## 3. Solução proposta

O **CaixaVerso Assistivo** oferece uma plataforma de trilhas assistivas, com recursos de acessibilidade, orientação por etapas, feedback visual, apoio por voz, validação de aprendizagem e elementos de gamificação.

A trilha piloto implementada é:

> **Conta Fácil: Jornada Assistiva PcD**  
> **Fluxo piloto:** abertura de conta bancária em ambiente simulado.

A abertura de conta foi usada apenas como **caso demonstrativo**. A arquitetura foi pensada para permitir expansão para novas jornadas internas, como:

- SISRH;
- intranet;
- atender.caixa;
- ferramentas de atendimento;
- consulta a normativos;
- fluxos de suporte interno;
- processos operacionais;
- trilhas de capacitação assistida;
- integração futura com canais corporativos.

---

## 4. Objetivo do projeto

Ampliar a autonomia digital de empregados CAIXA por meio de uma solução que combina:

- acessibilidade;
- linguagem simples;
- orientação contextual;
- trilhas práticas;
- apoio por voz;
- progressão por etapas;
- validação de aprendizagem;
- gamificação leve;
- dashboard de evolução;
- segurança mínima aplicada ao MVP;
- respeito à privacidade e à LGPD.

---

## 5. Público-alvo

O projeto foi pensado para apoiar principalmente:

- empregados PcD;
- empregados com baixa familiaridade digital;
- empregados em aprendizagem de novos sistemas;
- equipes de atendimento que operam múltiplos sistemas;
- gestores que precisam acompanhar evolução e conclusão de trilhas;
- áreas de capacitação, inovação, tecnologia e experiência do empregado.

---

## 6. Enquadramento no Desafio 1

O projeto se enquadra no **Desafio 1 — Inclusão e Acessibilidade Digital do Empregado CAIXA**, pois busca ampliar o acesso, a clareza, a usabilidade e a autonomia de empregados em jornadas digitais internas.

| Necessidade do desafio | Como o CaixaVerso Assistivo responde |
|---|---|
| Autonomia do empregado | Trilhas guiadas com instruções passo a passo |
| Acessibilidade digital | Alto contraste, ajuste de fonte, modo baixa visão, redução de animações e apoio por voz |
| Clareza no uso dos sistemas | Linguagem simples, etapas curtas e orientação contextual |
| Baixa familiaridade digital | Experiência progressiva, com menos informações por tela |
| Apoio a empregados PcD | Recursos visuais, sonoros e estrutura simplificada |
| Redução de dependência de suporte | Aprendizagem prática e autônoma |
| Melhoria da experiência do empregado | Interface mais acolhedora, assistiva e padronizada |
| Escalabilidade para sistemas internos | Arquitetura preparada para novas trilhas e fluxos corporativos |

---

## 7. Funcionalidades implementadas

### 7.1 Cadastro e autenticação

- Cadastro de usuário.
- Login com matrícula e senha.
- Validação de senha forte.
- Armazenamento de senha com **BCrypt**.
- Sessão por token aleatório.
- Armazenamento de hash do token de sessão.
- Cookie de sessão com `HttpOnly`.
- Cookie de sessão com `SameSite=Strict`.
- Filtro de autenticação para proteção de rotas internas.
- Mensagens amigáveis em caso de usuário não cadastrado ou senha inválida.

### 7.2 Preferências de acessibilidade

O cadastro foi pensado para registrar preferências de uso e adaptação da experiência, evitando exposição indevida de informações sensíveis.

Exemplos de preferências que podem orientar a experiência:

- necessidade de fonte ampliada;
- preferência por leitura em voz;
- preferência por alto contraste;
- redução de animações;
- navegação simplificada;
- apoio visual mais evidente.

> Em evolução para ambiente corporativo, essas preferências devem seguir princípios de minimização de dados, consentimento, finalidade específica e governança LGPD.

### 7.3 Trilhas assistivas

- Listagem de trilhas disponíveis.
- Trilha piloto: **Conta Fácil: Jornada Assistiva PcD**.
- Fluxo piloto: abertura de conta bancária.
- Cards futuros com status **“Em breve”**.
- Controle de início, andamento e conclusão.
- Botão para refazer trilha.
- Reinício do progresso ao refazer a jornada.

### 7.4 Jornada guiada

- Assistente contextual durante a execução da trilha.
- Orientações simples por etapa.
- Destaque visual do campo ou ação principal.
- Avanço progressivo.
- Feedback de conclusão.
- Exibição do assistente apenas no contexto correto da trilha.
- Redução de sobrecarga visual.

### 7.5 Assistente de voz

- Leitura de textos explicativos com `speechSynthesis`.
- Botões de áudio para apoio sob demanda.
- Resumo de telas e instruções principais.
- Apoio para usuários com baixa visão ou dificuldade de leitura.
- Estrutura preparada para futura integração com serviços corporativos de voz ou IA responsável.

> No MVP, o assistente de voz é baseado em recursos nativos do navegador e textos orientados por contexto. Não utiliza dados reais, deepfake, biometria vocal ou processamento sensível.

### 7.6 Acessibilidade visual e interação

- Aumento de fonte.
- Redução de fonte.
- Alto contraste.
- Modo baixa visão.
- Redução de animações.
- Foco visual mais evidente.
- Linguagem simples.
- Menos informação por etapa.
- Botões destacados.
- Organização visual voltada à clareza.

### 7.7 Gamificação leve

A gamificação é usada como mecanismo de engajamento, não como objetivo principal.

- Pontuação por avanço.
- XP acumulado.
- Nível do usuário.
- Medalhas/badges.
- Progresso da trilha.
- Ranking demonstrativo.
- Regras de pontuação por conclusão de etapas.

### 7.8 Validação de aprendizagem

- Validação liberada após conclusão da trilha.
- Perguntas de fixação contextualizadas.
- Respostas avaliadas.
- Pontuação conforme desempenho.
- Reforço do aprendizado prático.

### 7.9 Dashboard

- Visão de progresso.
- Pontuação do usuário.
- Medalhas conquistadas.
- Ranking demonstrativo.
- Indicadores de evolução.
- Estrutura preparada para métricas gerenciais e acompanhamento por trilha.

---

## 8. Evidências de acessibilidade

O projeto foi estruturado para demonstrar acessibilidade prática no MVP. Abaixo está o checklist de recursos previstos e/ou implementados.

| Recurso | Situação | Evidência esperada |
|---|---|---|
| Navegação por teclado | Previsto/implementável | Campos, botões e links acessíveis via `Tab` |
| Foco visível | Implementado/melhorado | Destaque visual ao navegar por teclado |
| Skip link | Implementado/melhorado | Link para pular ao conteúdo principal |
| Alto contraste | Implementado | Alternância visual para melhorar leitura |
| Modo baixa visão | Implementado | Interface com maior legibilidade |
| Ajuste de fonte | Implementado | Botões para aumentar e reduzir texto |
| Redução de animações | Implementado | Opção para reduzir movimento na interface |
| Apoio por voz | Implementado no MVP | Leitura de textos por `speechSynthesis` |
| Transcrição textual | Implementado/conceito aplicado | Textos disponíveis em tela para conteúdos narrados |
| Linguagem simples | Implementado | Instruções curtas e objetivas |
| ARIA e semântica | Em evolução | Melhorias em `aria-label`, `aria-live` e estrutura semântica |
| Compatibilidade com leitor de tela | Em evolução | Testes recomendados com NVDA, Narrador do Windows ou similares |
| Contraste WCAG | Em evolução | Validação recomendada por ferramenta de contraste |

### Próximos testes recomendados de acessibilidade

Antes da banca ou evolução corporativa, recomenda-se validar:

- fluxo completo usando apenas teclado;
- leitura das telas com leitor de tela;
- contraste de textos e botões;
- comportamento com zoom de 125%, 150% e 175%;
- uso do modo baixa visão;
- uso com redução de movimento ativada;
- clareza das mensagens de erro;
- acessibilidade dos formulários;
- foco inicial e foco após ações;
- consistência dos `aria-labels`.

---

## 9. Diferenciais da solução

O diferencial do **CaixaVerso Assistivo** está na combinação de recursos que normalmente aparecem separados:

1. **Camada assistiva reutilizável**, não apenas uma tela de treinamento.
2. **Aprendizagem prática**, na qual o empregado aprende executando.
3. **Orientação contextual**, mostrando o que fazer em cada etapa.
4. **Acessibilidade integrada**, tratada como requisito central.
5. **Apoio por voz**, auxiliando usuários com baixa visão ou dificuldade de leitura.
6. **Gamificação leve**, para engajamento e continuidade.
7. **Dashboard**, permitindo acompanhar evolução e conclusão.
8. **Arquitetura extensível**, preparada para novas trilhas.
9. **Segurança mínima aplicada ao MVP**, com BCrypt e sessão por token.
10. **Foco na experiência do empregado**, com impacto indireto na experiência do cliente.

---

## 10. Arquitetura técnica

O projeto foi desenvolvido como aplicação web Java utilizando Quarkus.

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

## 11. Fluxo principal da aplicação

1. O usuário acessa a plataforma.
2. Realiza cadastro com dados básicos e preferências opcionais de acessibilidade.
3. Efetua login com matrícula e senha.
4. Acessa o painel de trilhas.
5. Visualiza a trilha **Conta Fácil: Jornada Assistiva PcD**.
6. Inicia a jornada assistida.
7. Recebe orientação contextual passo a passo.
8. Pode ativar áudio, ajustar fonte, alto contraste, baixa visão ou reduzir animações.
9. Executa o fluxo piloto em ambiente simulado.
10. Conclui etapas e acumula pontos.
11. Desbloqueia a validação final.
12. Responde às perguntas de fixação.
13. Recebe pontuação, nível e medalhas.
14. Acompanha sua evolução no dashboard.
15. Pode refazer a trilha, reiniciando o progresso.

---

## 12. Segurança e privacidade

O projeto é um MVP de hackathon, mas já contempla preocupações básicas de segurança e privacidade.

### Implementado no MVP

- Senhas armazenadas com BCrypt.
- Regras de senha forte.
- Sessão baseada em token aleatório.
- Hash do token de sessão.
- Cookie com `HttpOnly`.
- Cookie com `SameSite=Strict`.
- Filtro de autenticação.
- Dados fictícios na jornada demonstrativa.
- Ausência de dados reais de clientes ou empregados.
- Preferências de acessibilidade tratadas como opcionais.

### Cuidados para evolução corporativa

Em ambiente de produção, a solução deve evoluir para:

- integração com SSO corporativo;
- autorização por perfil;
- autenticação federada;
- logs estruturados e auditáveis;
- controle CSRF;
- cookies com `Secure` em HTTPS;
- rate limit em login;
- proteção contra força bruta;
- segregação de ambientes;
- criptografia de dados sensíveis;
- mascaramento de dados;
- trilhas homologadas por área responsável;
- governança LGPD;
- revisão de segurança antes de integração com sistemas reais.

---

## 13. LGPD, ética e uso responsável

A solução foi pensada para respeitar os princípios de privacidade, minimização de dados, finalidade específica, segurança e não discriminação.

As preferências de acessibilidade devem ser:

- opcionais;
- usadas apenas para adaptar a experiência;
- não exibidas em ranking público;
- não usadas para restringir acesso;
- não usadas para avaliação discriminatória;
- protegidas em ambiente corporativo;
- visíveis apenas a perfis autorizados, quando necessário.

O projeto não utiliza dados reais de clientes, dados sigilosos da CAIXA, deepfakes, biometria ou modelos de IA generativa no MVP.

---

## 14. Valor de negócio para a CAIXA

O projeto pode gerar valor institucional em três dimensões principais.

### Para o empregado

- Mais autonomia digital.
- Menos dependência de suporte presencial.
- Aprendizagem prática e acessível.
- Redução de ansiedade diante de sistemas complexos.
- Experiência adaptada ao ritmo do usuário.
- Apoio a diferentes necessidades de acessibilidade.

### Para gestores e áreas de capacitação

- Acompanhamento de evolução por trilha.
- Identificação de gargalos de aprendizagem.
- Visualização de engajamento.
- Indicadores de conclusão.
- Base para priorizar novos treinamentos.
- Padronização da aprendizagem operacional.

### Para a organização

- Redução de dúvidas recorrentes.
- Redução de retrabalho.
- Ganho de produtividade.
- Fortalecimento da cultura de inclusão.
- Apoio à transformação digital interna.
- Melhoria da experiência do empregado.
- Impacto indireto na qualidade do atendimento ao cliente.

---

## 15. Indicadores de impacto sugeridos

Para demonstrar valor de negócio em evolução futura, a solução pode acompanhar:

| Indicador | Objetivo |
|---|---|
| Taxa de conclusão de trilhas | Medir adesão e finalização |
| Tempo médio por jornada | Identificar pontos de dificuldade |
| Quantidade de refações | Avaliar necessidade de reforço |
| Pontuação média por trilha | Medir assimilação do conteúdo |
| Etapas com maior abandono | Localizar gargalos de usabilidade |
| Uso de recursos de acessibilidade | Entender preferências de apoio |
| Redução de chamados recorrentes | Medir impacto operacional |
| Satisfação do empregado | Avaliar experiência e clareza |

---

## 16. Critérios oficiais de avaliação

| Critério | Como o projeto se posiciona |
|---|---|
| Aderência ao desafio | Foca inclusão digital, acessibilidade e autonomia do empregado CAIXA |
| Inovação e criatividade | Propõe camada assistiva reutilizável com voz, trilhas, dashboard e gamificação leve |
| Valor de negócio | Reduz dependência de suporte, acelera aprendizagem e melhora produtividade |
| Ética e segurança | Usa dados fictícios no MVP, BCrypt, sessão por token e visão de governança LGPD |
| Viabilidade técnica | Protótipo funcional em Java/Quarkus, com banco, telas, fluxo completo e arquitetura extensível |

---

## 17. Roadmap de evolução

### Curto prazo

- Refinar testes com teclado e leitor de tela.
- Validar contraste por critérios WCAG.
- Fortalecer semântica HTML e ARIA.
- Melhorar responsividade em diferentes níveis de zoom.
- Implementar CSRF nos formulários.
- Adicionar rate limit no login.
- Remover dados fixos de demonstração.
- Tornar o dashboard totalmente baseado em dados persistidos.

### Médio prazo

- Criar motor parametrizável de trilhas.
- Permitir cadastro administrativo de novas jornadas.
- Criar trilhas para SISRH, intranet, atender.caixa e normativos.
- Adaptar automaticamente a experiência por preferência de acessibilidade.
- Adicionar relatórios gerenciais.
- Integrar com base de conhecimento autorizada.
- Criar biblioteca de componentes acessíveis reutilizáveis.

### Longo prazo

- Integração com SSO corporativo.
- Integração com sistemas internos autorizados.
- Assistente contextual para dúvidas frequentes.
- Motor inteligente de recomendação de trilhas.
- Analytics de aprendizagem.
- Auditoria corporativa.
- Deploy em ambiente institucional seguro.
- Governança de conteúdo por áreas responsáveis.

---

## 18. Como executar o projeto

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

## 19. Observações sobre ambiente de demonstração

Este projeto utiliza banco H2 para facilitar a execução local durante o hackathon.

O ambiente atual é exclusivamente demonstrativo e não utiliza dados reais da CAIXA, clientes ou empregados. Qualquer integração com sistemas corporativos deverá seguir as políticas internas de segurança, privacidade, arquitetura, governança e conformidade.

Recomenda-se que o pacote final do projeto não inclua:

- pasta `target`;
- banco local gerado;
- arquivos temporários;
- credenciais;
- dados reais;
- histórico `.git`, caso a entrega seja feita por arquivo compactado.

---

## 20. Pitch de 5 minutos

### Abertura

O **CaixaVerso Assistivo** nasceu para resolver uma dor real: muitos empregados precisam navegar por sistemas internos complexos, com diferentes níveis de familiaridade digital e diferentes necessidades de acessibilidade.

### Problema

Quando a jornada digital é confusa, o empregado depende mais de suporte, perde autonomia, gasta mais tempo e pode ter dificuldade para executar tarefas com segurança e confiança.

### Solução

Nossa solução transforma jornadas internas em trilhas guiadas, acessíveis e práticas. O empregado aprende executando, com instruções simples, apoio por voz, recursos de acessibilidade, feedback visual e validação de aprendizagem.

### Protótipo

A trilha piloto é a **Conta Fácil: Jornada Assistiva PcD**, usando abertura de conta bancária como fluxo demonstrativo. Mas a arquitetura permite evoluir para SISRH, intranet, atender.caixa, normativos e outros sistemas internos.

### Valor para a CAIXA

A solução aumenta autonomia, reduz dependência de suporte, padroniza capacitação, fortalece inclusão e melhora a experiência do empregado, que é base para melhorar também a experiência do cliente.

### Fechamento

O CaixaVerso Assistivo não é apenas um treinamento. É uma camada assistiva reutilizável para transformar sistemas complexos em jornadas simples, acessíveis e humanas.

---

## 21. Resposta curta para perguntas da banca

### O projeto usa IA?

No MVP, o projeto usa um assistente contextual baseado em regras e leitura por voz via navegador. A arquitetura está preparada para futura integração com IA responsável, respeitando segurança, privacidade e políticas internas da CAIXA.

### Por que abertura de conta?

A abertura de conta foi usada apenas como fluxo piloto por ser uma jornada conhecida, com etapas claras e fácil demonstração. A proposta principal é a camada assistiva, que pode ser aplicada a outras jornadas internas.

### Como o projeto ajuda empregados PcD?

A solução oferece alto contraste, ajuste de fonte, modo baixa visão, redução de animações, apoio por voz, linguagem simples, foco visual e jornada guiada por etapas.

### Como evita risco LGPD?

O MVP não usa dados reais. As preferências de acessibilidade são opcionais e devem ser usadas apenas para adaptar a experiência, sem exposição pública ou uso discriminatório.

### Como gera valor para a CAIXA?

Aumenta autonomia, reduz suporte recorrente, acelera aprendizagem, melhora produtividade e fortalece a cultura de inclusão digital.

---

## 22. Frase de impacto

**CaixaVerso Assistivo: inclusão digital na prática, transformando sistemas complexos em jornadas simples, acessíveis e humanas.**


### Reforço de segurança e privacidade aplicado nesta versão

Esta versão reforça explicitamente que o CaixaVerso Assistivo é um MVP demonstrativo e não deve receber dados reais de clientes, empregados, operações bancárias ou sistemas corporativos. O fluxo de abertura de conta é apenas uma simulação para demonstrar a camada assistiva reutilizável em jornadas como intranet, SISRH, atender.caixa e demais plataformas internas.

Medidas adicionadas:

- proteção CSRF nos formulários sensíveis (`login`, `cadastro`, `desafio` e `refazer trilha`);
- envio de token CSRF também nas chamadas assíncronas de conclusão de etapa;
- cookie de sessão e cookie CSRF com `HttpOnly`, `SameSite=Strict` e `Secure` ativo por padrão;
- rate limit em memória para tentativas de login, reduzindo força bruta no MVP;
- remoção do campo técnico antigo relacionado a condição pessoal, substituído por preferências opcionais de acessibilidade;
- remoção de usuário/senha H2 fixos em produção, usando variáveis de ambiente;
- manutenção de credenciais locais apenas nos perfis `dev` e `test`, marcadas como demonstrativas.
