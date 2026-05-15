document.addEventListener('DOMContentLoaded', function () {

  /* -------------------- Acessibilidade visual -------------------- */
  const fontPlus = document.getElementById('fontPlus');
  const fontMinus = document.getElementById('fontMinus');
  const contrastToggle = document.getElementById('contrastToggle');

  const savedFontSize = localStorage.getItem('cv_font_size');
  const savedContrast = localStorage.getItem('cv_high_contrast');

  if (savedFontSize) {
    document.documentElement.style.fontSize = savedFontSize + 'px';
  }

  if (savedContrast === 'true') {
    document.body.classList.add('high-contrast');
  }

  function getCurrentFontSize() {
    const size = window.getComputedStyle(document.documentElement).fontSize;
    return parseFloat(size) || 18;
  }

  if (fontPlus) {
    fontPlus.addEventListener('click', function () {
      const nextSize = Math.min(getCurrentFontSize() + 1, 24);
      document.documentElement.style.fontSize = nextSize + 'px';
      localStorage.setItem('cv_font_size', nextSize);
    });
  }

  if (fontMinus) {
    fontMinus.addEventListener('click', function () {
      const nextSize = Math.max(getCurrentFontSize() - 1, 15);
      document.documentElement.style.fontSize = nextSize + 'px';
      localStorage.setItem('cv_font_size', nextSize);
    });
  }

  if (contrastToggle) {
    contrastToggle.addEventListener('click', function () {
      document.body.classList.toggle('high-contrast');
      localStorage.setItem(
        'cv_high_contrast',
        document.body.classList.contains('high-contrast')
      );
    });
  }

  /* -------------------- Seleção do desafio -------------------- */
  const challengeSelector = document.getElementById('challengeSelector');
  const challengeExperience = document.getElementById('challengeExperience');
  const startChallenge = document.getElementById('startChallenge');
  const changeChallenge = document.getElementById('changeChallenge');

  if (startChallenge && challengeSelector && challengeExperience) {
    startChallenge.addEventListener('click', function () {
      challengeSelector.style.display = 'none';
      challengeExperience.style.display = 'block';
      challengeExperience.scrollIntoView({ behavior: 'smooth', block: 'start' });
    });
  }

  if (changeChallenge && challengeSelector && challengeExperience) {
    changeChallenge.addEventListener('click', function () {
      challengeExperience.style.display = 'none';
      challengeSelector.style.display = 'block';
      challengeSelector.scrollIntoView({ behavior: 'smooth', block: 'start' });
    });
  }

  /* -------------------- Seleção do treinamento -------------------- */
  const selector = document.getElementById('trainingSelector');
  const experience = document.getElementById('trainingExperience');
  const startTraining = document.getElementById('startTraining');
  const changeTraining = document.getElementById('changeTraining');

  if (startTraining && selector && experience) {
    startTraining.addEventListener('click', function () {
      selector.closest('.training-blue-zone').style.display = 'none';
      experience.style.display = 'block';
      experience.scrollIntoView({ behavior: 'smooth', block: 'start' });
    });
  }

  if (changeTraining && selector && experience) {
    changeTraining.addEventListener('click', function () {
      selector.closest('.training-blue-zone').style.display = 'block';
      experience.style.display = 'none';
      window.scrollTo({ top: 0, behavior: 'smooth' });
    });
  }

  /* -------------------- Treinamento guiado -------------------- */
  const trilha = window.CAIXAVERSO_TRILHA;
  const progressoInicial = window.CAIXAVERSO_PROGRESSO_INICIAL;

  if (!trilha || !Array.isArray(trilha) || trilha.length === 0) {
    return;
  }

  let etapaIndex = Math.min(
    Math.max(
      progressoInicial && progressoInicial.etapaAtual ? progressoInicial.etapaAtual : 0,
      0
    ),
    trilha.length - 1
  );

  const titulo = document.getElementById('etapaTitulo');
  const instrucao = document.getElementById('etapaInstrucao');
  const dica = document.getElementById('dicaEtapa');
  const contador = document.getElementById('etapaContador');
  const progressBar = document.getElementById('progress-bar');
  const acaoEsperadaTexto = document.getElementById('acaoEsperadaTexto');
  const supportFocus = document.getElementById('supportFocus');
  const simStatusBadge = document.getElementById('simStatusBadge');
  const guidedCalloutTitle = document.getElementById('guidedCalloutTitle');
  const guidedCalloutText = document.getElementById('guidedCalloutText');

  const checkObjetivo = document.getElementById('checkObjetivo');
  const checkCampo = document.getElementById('checkCampo');
  const checkAcao = document.getElementById('checkAcao');
  const checkConclusao = document.getElementById('checkConclusao');

  const prev = document.getElementById('prev');
  const next = document.getElementById('next');
  const repeat = document.getElementById('repeatInstruction');
  const complete = document.getElementById('completeStep');
  const toast = document.getElementById('trainingToast');
  const acaoSimulada = document.getElementById('acaoSimulada');

  function atualizarChecklist(passosConcluidos) {
    const itens = [checkObjetivo, checkCampo, checkAcao, checkConclusao];

    itens.forEach(function (item, index) {
      if (!item) return;

      if (index < passosConcluidos) {
        item.classList.add('done');
      } else {
        item.classList.remove('done');
      }
    });
  }

  function limparCamposAtivos() {
    document.querySelectorAll('.fake-form-field.active-step').forEach(function (el) {
      el.classList.remove('active-step');
    });
  }

  function destacarCampo(targetId) {
    limparCamposAtivos();
    const target = document.getElementById(targetId);
    if (target) {
      target.classList.add('active-step');
      target.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
  }

  function calcularPercentualVisual() {
    if (!trilha.length) {
      return 0;
    }
    return Math.round((etapaIndex / trilha.length) * 100);
  }

  function renderEtapa() {
    const etapa = trilha[etapaIndex];
    if (!etapa) return;

    if (titulo) titulo.textContent = etapa.titulo;
    if (instrucao) instrucao.textContent = etapa.instrucao;
    if (dica) dica.textContent = etapa.dica;
    if (contador) contador.textContent = `${etapa.ordem}/${trilha.length}`;
    if (acaoEsperadaTexto) acaoEsperadaTexto.textContent = etapa.acaoEsperada;
    if (supportFocus) supportFocus.textContent = etapa.titulo;
    if (simStatusBadge) simStatusBadge.textContent = `Etapa ${etapa.ordem} de ${trilha.length}`;
    if (guidedCalloutTitle) guidedCalloutTitle.textContent = etapa.calloutTitle || etapa.titulo;
    if (guidedCalloutText) guidedCalloutText.textContent = etapa.calloutText || etapa.instrucao;

    if (progressBar) {
      progressBar.style.width = `${calcularPercentualVisual()}%`;
    }

    destacarCampo(etapa.targetId);
    atualizarChecklist(1);
  }

  function mostrarToast(html) {
    if (!toast) return;
    toast.style.display = 'block';
    toast.innerHTML = html;
  }

  function falarTexto(texto) {
    if (!('speechSynthesis' in window)) {
      mostrarToast('<h3>Leitura por voz indisponível</h3><p>Seu navegador não suporta leitura por voz.</p>');
      return;
    }

    const utterance = new SpeechSynthesisUtterance(texto);
    utterance.lang = 'pt-BR';
    window.speechSynthesis.cancel();
    window.speechSynthesis.speak(utterance);
  }

  if (prev) {
    prev.addEventListener('click', function () {
      etapaIndex = Math.max(0, etapaIndex - 1);
      renderEtapa();
    });
  }

  if (next) {
    next.addEventListener('click', function () {
      etapaIndex = Math.min(trilha.length - 1, etapaIndex + 1);
      renderEtapa();
    });
  }

  if (repeat) {
    repeat.addEventListener('click', function () {
      const etapa = trilha[etapaIndex];
      if (!etapa) return;
      falarTexto(`${etapa.titulo}. ${etapa.instrucao}. Dica: ${etapa.dica}`);
    });
  }

  if (acaoSimulada) {
    acaoSimulada.addEventListener('click', function () {
      const etapa = trilha[etapaIndex];
      if (!etapa) return;

      atualizarChecklist(3);

      mostrarToast(`
        <h3>Ação simulada executada</h3>
        <p>${etapa.acaoEsperada}</p>
      `);
    });
  }

  if (complete) {
    complete.addEventListener('click', function () {
      atualizarChecklist(4);
      complete.disabled = true;
      complete.textContent = 'Salvando...';

      fetch('/treinamento/etapa-concluida', {
        method: 'POST'
      })
        .then(function (response) {
          if (!response.ok) {
            throw new Error('Não foi possível salvar a etapa.');
          }
          return response.json();
        })
        .then(function (data) {
          if (progressBar) {
            progressBar.style.width = `${data.progressoPercentual}%`;
          }

          if (data.desafioDesbloqueado) {
            mostrarToast(`
              <h3>Treinamento concluído!</h3>
              <p>Você concluiu a trilha de abertura de conta bancária e desbloqueou o quiz associado.</p>
              <a class="btn btn-primary" href="/desafio">Ir para o quiz</a>
            `);

            if (simStatusBadge) simStatusBadge.textContent = 'Trilha concluída';
            complete.textContent = 'Treinamento concluído';
            complete.disabled = true;
            atualizarChecklist(4);
            return;
          }

          etapaIndex = Math.min(trilha.length - 1, data.etapaAtual);
          renderEtapa();

          mostrarToast(`
            <h3>Etapa concluída!</h3>
            <p>Progresso atualizado: ${data.progressoPercentual}%</p>
          `);
        })
        .catch(function (error) {
          mostrarToast(`
            <h3>Erro ao salvar etapa</h3>
            <p>${error.message}</p>
          `);
        })
        .finally(function () {
          if (complete && complete.textContent !== 'Treinamento concluído') {
            complete.disabled = false;
            complete.textContent = 'Concluir etapa';
          }
        });
    });
  }

  renderEtapa();
});