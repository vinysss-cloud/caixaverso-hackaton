document.addEventListener('DOMContentLoaded', function () {

    /*
     * ==========================================================
     * ACESSIBILIDADE VISUAL
     * ==========================================================
     */

    const fontPlus = document.getElementById('fontPlus');
    const fontMinus = document.getElementById('fontMinus');
    const contrastToggle = document.getElementById('contrastToggle');
    const lowVisionToggle = document.getElementById('lowVisionToggle');
    const motionToggle = document.getElementById('motionToggle');
    const accessibilityStatus = document.getElementById('accessibilityStatus');

    const STORAGE_KEYS = {
        fontSize: 'cv_font_size',
        highContrast: 'cv_high_contrast',
        lowVision: 'cv_low_vision',
        reduceMotion: 'cv_reduce_motion'
    };

    const savedFontSize = localStorage.getItem(STORAGE_KEYS.fontSize);
    const savedContrast = localStorage.getItem(STORAGE_KEYS.highContrast);
    const savedLowVision = localStorage.getItem(STORAGE_KEYS.lowVision);
    const savedReduceMotion = localStorage.getItem(STORAGE_KEYS.reduceMotion);

    const prefersReducedMotion = window.matchMedia
        ? window.matchMedia('(prefers-reduced-motion: reduce)').matches
        : false;

    function announceAccessibility(message) {
        if (!accessibilityStatus || !message) {
            return;
        }

        accessibilityStatus.textContent = '';

        setTimeout(function () {
            accessibilityStatus.textContent = message;
        }, 50);
    }

    function setPressed(button, pressed) {
        if (button) {
            button.setAttribute('aria-pressed', String(Boolean(pressed)));
        }
    }

    function aplicarClasseAcessibilidade(nomeClasse, ativo) {
        document.body.classList.toggle(nomeClasse, Boolean(ativo));
    }

    function getPreferredScrollBehavior() {
        return document.body.classList.contains('reduce-motion') ? 'auto' : 'smooth';
    }

    if (savedFontSize) {
        document.documentElement.style.fontSize = savedFontSize + 'px';
    }

    if (savedContrast === 'true') {
        aplicarClasseAcessibilidade('high-contrast', true);
    }

    if (savedLowVision === 'true') {
        aplicarClasseAcessibilidade('low-vision', true);
    }

    /*
     * Se o usuário já configurou preferência no app, respeitamos o valor salvo.
     * Se não configurou, respeitamos a preferência do sistema operacional/navegador.
     */
    if (savedReduceMotion === 'true' || (savedReduceMotion === null && prefersReducedMotion)) {
        aplicarClasseAcessibilidade('reduce-motion', true);
    }

    setPressed(contrastToggle, document.body.classList.contains('high-contrast'));
    setPressed(lowVisionToggle, document.body.classList.contains('low-vision'));
    setPressed(motionToggle, document.body.classList.contains('reduce-motion'));

    function getCurrentFontSize() {
        const size = window.getComputedStyle(document.documentElement).fontSize;
        return parseFloat(size) || 18;
    }

    if (fontPlus) {
        fontPlus.addEventListener('click', function () {
            const nextSize = Math.min(getCurrentFontSize() + 1, 24);
            document.documentElement.style.fontSize = nextSize + 'px';
            localStorage.setItem(STORAGE_KEYS.fontSize, nextSize);
            announceAccessibility('Tamanho da fonte aumentado.');
        });
    }

    if (fontMinus) {
        fontMinus.addEventListener('click', function () {
            const nextSize = Math.max(getCurrentFontSize() - 1, 15);
            document.documentElement.style.fontSize = nextSize + 'px';
            localStorage.setItem(STORAGE_KEYS.fontSize, nextSize);
            announceAccessibility('Tamanho da fonte reduzido.');
        });
    }

    if (contrastToggle) {
        contrastToggle.addEventListener('click', function () {
            const ativo = !document.body.classList.contains('high-contrast');

            aplicarClasseAcessibilidade('high-contrast', ativo);
            localStorage.setItem(STORAGE_KEYS.highContrast, String(ativo));
            setPressed(contrastToggle, ativo);

            announceAccessibility(
                ativo
                    ? 'Alto contraste ativado.'
                    : 'Alto contraste desativado.'
            );
        });
    }

    if (lowVisionToggle) {
        lowVisionToggle.addEventListener('click', function () {
            const ativo = !document.body.classList.contains('low-vision');

            aplicarClasseAcessibilidade('low-vision', ativo);
            localStorage.setItem(STORAGE_KEYS.lowVision, String(ativo));
            setPressed(lowVisionToggle, ativo);

            announceAccessibility(
                ativo
                    ? 'Modo baixa visão ativado. Textos, campos e espaçamentos foram ampliados.'
                    : 'Modo baixa visão desativado.'
            );
        });
    }

    if (motionToggle) {
        motionToggle.addEventListener('click', function () {
            const ativo = !document.body.classList.contains('reduce-motion');

            aplicarClasseAcessibilidade('reduce-motion', ativo);
            localStorage.setItem(STORAGE_KEYS.reduceMotion, String(ativo));
            setPressed(motionToggle, ativo);

            announceAccessibility(
                ativo
                    ? 'Redução de animações ativada.'
                    : 'Redução de animações desativada.'
            );
        });
    }

    /*
     * ==========================================================
     * MODO DESAFIO
     * ==========================================================
     */

    const challengeSelector = document.getElementById('challengeSelector');
    const challengeExperience = document.getElementById('challengeExperience');
    const startChallenge = document.getElementById('startChallenge');
    const changeChallenge = document.getElementById('changeChallenge');

    if (startChallenge && challengeSelector && challengeExperience) {
        startChallenge.addEventListener('click', function () {
            challengeSelector.style.display = 'none';
            challengeExperience.style.display = 'block';
            challengeExperience.scrollIntoView({ behavior: getPreferredScrollBehavior(), block: 'start' });
        });
    }

    if (changeChallenge && challengeSelector && challengeExperience) {
        changeChallenge.addEventListener('click', function () {
            challengeExperience.style.display = 'none';
            challengeSelector.style.display = 'block';
            challengeSelector.scrollIntoView({ behavior: getPreferredScrollBehavior(), block: 'start' });
        });
    }

    /*
     * ==========================================================
     * SELEÇÃO DO TREINAMENTO
     * ==========================================================
     */

    const selector = document.getElementById('trainingSelector');
    const experience = document.getElementById('trainingExperience');
    const startTraining = document.getElementById('startTraining');
    const changeTraining = document.getElementById('changeTraining');

    if (startTraining && selector && experience) {
        startTraining.addEventListener('click', function () {
            const zone = selector.closest('.training-blue-zone');

            if (zone) {
                zone.style.display = 'none';
            }

            experience.style.display = 'block';
            experience.scrollIntoView({ behavior: getPreferredScrollBehavior(), block: 'start' });

            setTimeout(function () {
                treinamentoEmExecucao = true;
                etapaIndex = calcularEtapaInicial();
                mostrarPainelInstrucao();
                renderEtapa();

                const percentual = Number(progressoInicial?.progressoPercentual || 0);

                if (percentual > 0 && percentual < 100) {
                    exibirToast('Treinamento retomado de onde você parou.', 'success');
                }
            }, 150);
        });
    }

    if (changeTraining && selector && experience) {
        changeTraining.addEventListener('click', function () {
            const zone = selector.closest('.training-blue-zone');

            if (zone) {
                zone.style.display = 'block';
            }

            experience.style.display = 'none';
            treinamentoEmExecucao = false;
            esconderPainelInstrucao();
            window.scrollTo({ top: 0, behavior: getPreferredScrollBehavior() });
        });
    }

    /*
     * ==========================================================
     * TREINAMENTO GUIADO EM TELA ÚNICA COM POPUP
     * ==========================================================
     */

    const trilha = window.CAIXAVERSO_TRILHA;
    const progressoInicial = window.CAIXAVERSO_PROGRESSO_INICIAL;

    if (!trilha || !Array.isArray(trilha) || trilha.length === 0) {
        return;
    }

    function calcularEtapaInicial() {
        if (!progressoInicial) {
            return 0;
        }

        const percentual = Number(progressoInicial.progressoPercentual || 0);
        const etapaAtual = Number(progressoInicial.etapaAtual || 0);

        if (percentual <= 0) {
            return 0;
        }

        if (percentual >= 100) {
            return trilha.length - 1;
        }

        if (etapaAtual >= 1 && etapaAtual <= trilha.length) {
            return etapaAtual - 1;
        }

        const etapaPorPercentual = Math.ceil((percentual / 100) * trilha.length);

        return Math.min(
            Math.max(etapaPorPercentual - 1, 0),
            trilha.length - 1
        );
    }

    let etapaIndex = calcularEtapaInicial();

    const titulo = document.getElementById('etapaTitulo');
    const dica = document.getElementById('dicaEtapa');
    const contador = document.getElementById('etapaContador');
    const progressBar = document.getElementById('progress-bar');
    const simStatusBadge = document.getElementById('simStatusBadge');

    const guidedPopup = document.getElementById('guidedPopup');
    const popupStepBadge = document.getElementById('popupStepBadge');
    const guidedCalloutTitle = document.getElementById('guidedCalloutTitle');
    const guidedCalloutText = document.getElementById('guidedCalloutText');
    const voiceInstructionText = document.getElementById('voiceInstructionText');
    const cadastroSimBody = document.getElementById('cadastroSimBody');

    /*
     * Move o painel de instrução para fora do formulário.
     * Isso evita conflito com grid, overflow, sticky, absolute e containers internos.
     */
    if (guidedPopup && guidedPopup.parentElement !== document.body) {
        document.body.appendChild(guidedPopup);
    }

    let treinamentoEmExecucao = false;

    function esconderPainelInstrucao() {
        if (!guidedPopup) {
            return;
        }

        guidedPopup.classList.remove('coach-docked-panel', 'is-visible');
        guidedPopup.style.setProperty('display', 'none', 'important');
    }

    function mostrarPainelInstrucao() {
        if (!guidedPopup) {
            return;
        }

        guidedPopup.classList.add('coach-docked-panel', 'is-visible');
        guidedPopup.style.setProperty('display', 'block', 'important');
    }

    esconderPainelInstrucao();

    const prev = document.getElementById('prev');
    const next = document.getElementById('next');
    const repeat = document.getElementById('repeatInstruction');
    const complete = document.getElementById('completeStep');
    const toast = document.getElementById('trainingToast');
    const acaoSimulada = document.getElementById('acaoSimulada');
    const minimizePopup = document.getElementById('minimizePopup');
    const showPopupButton = document.getElementById('showPopupButton');
    const validationMessage = document.getElementById('validationMessage');
    const popupTextPlus = document.getElementById('popupTextPlus');
    const popupTextMinus = document.getElementById('popupTextMinus');

    let popupFontScale = Number(localStorage.getItem('cv_popup_font_scale')) || 1;

    function limparCamposAtivos() {
        document.querySelectorAll('.fake-form-field.active-step').forEach(function (el) {
            el.classList.remove('active-step');
            el.removeAttribute('aria-current');
        });
    }

    function calcularPercentualVisual() {
        if (!trilha.length) {
            return 0;
        }

        return Math.round(((etapaIndex + 1) / trilha.length) * 100);
    }

    function montarTextoVoz(etapa) {
        return `${etapa.titulo}. ${etapa.instrucao}. Dica de acessibilidade: ${etapa.dica}`;
    }

    function destacarCampo(targetId) {

        if (!treinamentoEmExecucao) {
            esconderPainelInstrucao();
            return;
        }

        limparCamposAtivos();

        const etapa = trilha[etapaIndex];
        const target = document.getElementById(targetId);

        if (!target) {
            return;
        }

        target.classList.add('active-step');
        target.setAttribute('aria-current', 'step');

        if (etapa && etapa.ordem === 3) {
            const dataNascimentoTarget = document.getElementById('step-target-dataNascimento');

            if (dataNascimentoTarget) {
                dataNascimentoTarget.classList.add('active-step');
                dataNascimentoTarget.setAttribute('aria-current', 'step');
            }
        }

        if (etapa && etapa.ordem === 5) {
            [
                'step-target-cep',
                'step-target-endereco',
                'step-target-numero',
                'step-target-bairro',
                'step-target-cidade',
                'step-target-uf'
            ].forEach(function (id) {
                const enderecoTarget = document.getElementById(id);

                if (enderecoTarget) {
                    enderecoTarget.classList.add('active-step');
                    enderecoTarget.setAttribute('aria-current', 'step');
                }
            });
        }

        if (etapa && etapa.ordem === 8) {
            const resumoTarget = document.getElementById('step-target-revisao');
            const confirmacaoTarget = document.getElementById('step-target-confirmacao');

            if (resumoTarget) {
                resumoTarget.classList.add('active-step');
                resumoTarget.setAttribute('aria-current', 'step');
            }

            if (confirmacaoTarget) {
                confirmacaoTarget.classList.add('active-step');
                confirmacaoTarget.setAttribute('aria-current', 'step');
            }
        }

        mostrarPainelInstrucao();

        if (showPopupButton) {
            showPopupButton.style.display = 'none';
        }

        let targetParaRolar = target;

        if (etapa && etapa.ordem === 5) {
            targetParaRolar = document.getElementById('step-target-endereco') || target;
        }

        if (etapa && etapa.ordem === 7) {
            targetParaRolar = document.getElementById('step-target-documentos') || target;
        }

        if (etapa && etapa.ordem === 8) {
            targetParaRolar = document.getElementById('step-target-confirmacao') || target;
        }

        targetParaRolar.scrollIntoView({
            behavior: getPreferredScrollBehavior(),
            block: 'center'
        });

        setTimeout(function () {
            posicionarPopup(targetParaRolar);
        }, 500);
    }

    function posicionarPopup(target) {
        if (!treinamentoEmExecucao) {
            esconderPainelInstrucao();
            return;
        }

        if (!guidedPopup || !target) {
            return;
        }

        const isMobile = window.innerWidth <= 1100;

        guidedPopup.classList.remove('popup-left', 'popup-right', 'popup-below', 'mobile-popup');
        guidedPopup.classList.add('coach-docked-panel');

        if (isMobile) {
            guidedPopup.style.setProperty('position', 'relative', 'important');
            guidedPopup.style.setProperty('top', 'auto', 'important');
            guidedPopup.style.setProperty('left', 'auto', 'important');
            guidedPopup.style.setProperty('right', 'auto', 'important');
            guidedPopup.style.setProperty('width', '100%', 'important');
            guidedPopup.style.setProperty('max-width', 'none', 'important');
            return;
        }

        const etapa = trilha[etapaIndex];

        let referenceTarget = target;

        if (etapa && etapa.ordem === 5) {
            referenceTarget = document.getElementById('step-target-endereco') || target;
        }

        if (etapa && etapa.ordem === 7) {
            referenceTarget = document.getElementById('step-target-documentos') || target;
        }

        if (etapa && etapa.ordem === 8) {
            referenceTarget = document.getElementById('step-target-confirmacao') || target;
        }

        const targetRect = referenceTarget.getBoundingClientRect();

        const panelWidth = 320;
        const rightMargin = 42;
        const headerSafeTop = 98;
        const bottomSafe = 24;

        const panelHeight = guidedPopup.offsetHeight || 260;

        let top = targetRect.top;

        /*
         * Ajuste visual por etapa.
         * A ideia é o painel acompanhar a área ativa, sem sumir e sem cobrir campo.
         */
        if (etapa && etapa.ordem === 1) {
            top = targetRect.top - 8;
        }

        if (etapa && etapa.ordem === 2) {
            top = targetRect.top - 8;
        }

        if (etapa && etapa.ordem === 3) {
            top = targetRect.top - 8;
        }

        if (etapa && etapa.ordem === 4) {
            top = targetRect.top - 8;
        }

        if (etapa && etapa.ordem === 5) {
            top = targetRect.top - 10;
        }

        if (etapa && etapa.ordem === 6) {
            top = targetRect.top - 10;
        }

        if (etapa && etapa.ordem === 7) {
            top = targetRect.top - 10;
        }

        if (etapa && etapa.ordem === 8) {
            top = targetRect.top - 10;
        }

        const maxTop = window.innerHeight - panelHeight - bottomSafe;

        if (top < headerSafeTop) {
            top = headerSafeTop;
        }

        if (top > maxTop) {
            top = Math.max(headerSafeTop, maxTop);
        }

        guidedPopup.style.setProperty('display', 'block', 'important');
        guidedPopup.style.setProperty('position', 'fixed', 'important');
        guidedPopup.style.setProperty('width', `${panelWidth}px`, 'important');
        guidedPopup.style.setProperty('max-width', `${panelWidth}px`, 'important');
        guidedPopup.style.setProperty('right', `${rightMargin}px`, 'important');
        guidedPopup.style.setProperty('left', 'auto', 'important');
        guidedPopup.style.setProperty('top', `${top}px`, 'important');
        guidedPopup.style.setProperty('z-index', '9999', 'important');
    }

    function renderEtapa() {
        const etapa = trilha[etapaIndex];

        if (!etapa) {
            return;
        }

        const textoVoz = etapa.voiceText || etapa.instrucaoVoz || montarTextoVoz(etapa);

        if (titulo) {
            titulo.textContent = etapa.titulo;
        }

        if (dica) {
            dica.textContent = etapa.dica;
        }

        if (contador) {
            contador.textContent = `${etapa.ordem}/${trilha.length}`;
        }

        if (simStatusBadge) {
            simStatusBadge.textContent = `Etapa ${etapa.ordem} de ${trilha.length}`;
        }

        if (popupStepBadge) {
            popupStepBadge.textContent = `Etapa ${etapa.ordem} de ${trilha.length}`;
        }

        if (guidedCalloutTitle) {
            guidedCalloutTitle.textContent = etapa.calloutTitle || etapa.titulo;
        }

        if (guidedCalloutText) {
            guidedCalloutText.textContent = etapa.calloutText || etapa.instrucao;
        }

        if (voiceInstructionText) {
            voiceInstructionText.textContent = textoVoz;
        }

        if (progressBar) {
            progressBar.style.width = `${calcularPercentualVisual()}%`;
        }

        if (prev) {
            prev.disabled = etapaIndex === 0;
        }

        if (next) {
            next.style.display = etapaIndex === trilha.length - 1 ? 'none' : 'inline-flex';
            next.disabled = etapaIndex === trilha.length - 1;
        }

        if (complete) {
            complete.textContent = etapaIndex === trilha.length - 1
                ? 'Finalizar treinamento'
                : 'Concluir etapa';
        }

        limparMensagemValidacao();
        atualizarResumoProposta();
        aplicarTamanhoTextoPopup();
        destacarCampo(etapa.targetId);
    }

    function falarInstrucaoAtual() {
        const etapa = trilha[etapaIndex];

        if (!etapa) {
            return;
        }

        const texto = etapa.voiceText || etapa.instrucaoVoz || montarTextoVoz(etapa);

        if (!('speechSynthesis' in window)) {
            exibirToast('Seu navegador não possui suporte nativo para leitura por voz.', 'warning');
            return;
        }

        window.speechSynthesis.cancel();

        const utterance = new SpeechSynthesisUtterance(texto);
        utterance.lang = 'pt-BR';
        utterance.rate = 0.92;
        utterance.pitch = 1;
        utterance.volume = 1;

        window.speechSynthesis.speak(utterance);
    }

    function exibirToast(mensagem, tipo) {
        if (!toast) {
            return;
        }

        toast.textContent = mensagem;
        toast.className = 'feedback-card training-toast';

        if (tipo) {
            toast.classList.add(tipo);
        }

        toast.style.display = 'block';

        setTimeout(function () {
            toast.style.display = 'none';
        }, 4200);
    }

    function enviarConclusaoEtapa(etapa) {
        const payload = {
            ordem: etapa.ordem,
            titulo: etapa.titulo,
            campoSimulado: etapa.campoSimulado,
            acaoEsperada: etapa.acaoEsperada
        };

        fetch('/treinamento/etapa-concluida', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        }).catch(function () {
            console.warn('Não foi possível registrar a conclusão da etapa no servidor.');
        });
    }

function limparMensagemValidacao() {
    if (!validationMessage) {
        return;
    }

    validationMessage.style.display = 'none';
    validationMessage.textContent = '';
}

function exibirMensagemValidacao(mensagem) {
    if (!validationMessage) {
        return;
    }

    validationMessage.textContent = mensagem;
    validationMessage.style.display = 'block';
}

function campoPreenchidoPorId(id) {
    const campo = document.getElementById(id);

    if (!campo) {
        return false;
    }

    if (campo.type === 'checkbox' || campo.type === 'radio') {
        return campo.checked;
    }

    return campo.value && campo.value.trim().length > 0;
}

function algumCheckboxMarcadoPorName(name) {
    return Array.from(document.querySelectorAll(`input[name="${name}"]`))
        .some(function (input) {
            return input.checked;
        });
}

function cpfValidoBasico(valor) {
    if (!valor) {
        return false;
    }

    const numeros = valor.replace(/\D/g, '');
    return numeros.length === 11;
}

function dataValidaBasica(valor) {
    if (!valor) {
        return false;
    }

    const regex = /^(\d{2})\/(\d{2})\/(\d{4})$/;
    const match = valor.match(regex);

    if (!match) {
        return false;
    }

    const dia = Number(match[1]);
    const mes = Number(match[2]);
    const ano = Number(match[3]);

    return dia >= 1 &&
        dia <= 31 &&
        mes >= 1 &&
        mes <= 12 &&
        ano >= 1900 &&
        ano <= new Date().getFullYear();
}

function telefoneValidoBasico(valor) {
    if (!valor) {
        return false;
    }

    const numeros = valor.replace(/\D/g, '');
    return numeros.length === 10 || numeros.length === 11;
}

function cepValidoBasico(valor) {
    if (!valor) {
        return false;
    }

    const numeros = valor.replace(/\D/g, '');
    return numeros.length === 8;
}

function validarEtapaAtual() {
    const etapa = trilha[etapaIndex];

    if (!etapa) {
        return false;
    }

    limparMensagemValidacao();

    switch (etapa.ordem) {
        case 1:
            if (!campoPreenchidoPorId('tipoContaFake')) {
                exibirMensagemValidacao('Selecione o tipo de conta para avançar.');
                return false;
            }
            return true;

        case 2:
            if (!campoPreenchidoPorId('nomeFake')) {
                exibirMensagemValidacao('Digite o nome completo do cliente para avançar.');
                return false;
            }

            if (document.getElementById('nomeFake').value.trim().split(' ').length < 2) {
                exibirMensagemValidacao('Informe nome e sobrenome do cliente.');
                return false;
            }

            return true;

        case 3:
            if (!cpfValidoBasico(document.getElementById('cpfFake').value)) {
                exibirMensagemValidacao('Informe um CPF com 11 números para avançar.');
                return false;
            }

            if (!dataValidaBasica(document.getElementById('dataNascimentoFake').value)) {
                exibirMensagemValidacao('Informe a data de nascimento no formato dd/mm/aaaa.');
                return false;
            }

            return true;

        case 4:
            if (!telefoneValidoBasico(document.getElementById('telefoneFake').value)) {
                exibirMensagemValidacao('Informe um telefone válido com DDD para avançar.');
                return false;
            }

            return true;

        case 5:
            if (!cepValidoBasico(document.getElementById('cepFake').value)) {
                exibirMensagemValidacao('Informe um CEP com 8 números para avançar.');
                return false;
            }

            if (!campoPreenchidoPorId('enderecoFake')) {
                exibirMensagemValidacao('Informe o endereço para avançar.');
                return false;
            }

            if (!campoPreenchidoPorId('numeroFake')) {
                exibirMensagemValidacao('Informe o número do endereço para avançar.');
                return false;
            }

            if (!campoPreenchidoPorId('bairroFake')) {
                exibirMensagemValidacao('Informe o bairro para avançar.');
                return false;
            }

            if (!campoPreenchidoPorId('cidadeFake')) {
                exibirMensagemValidacao('Informe a cidade para avançar.');
                return false;
            }

            if (!campoPreenchidoPorId('ufFake') || document.getElementById('ufFake').value.trim().length !== 2) {
                exibirMensagemValidacao('Informe a UF com 2 letras para avançar.');
                return false;
            }

            return true;

        case 6:
            if (!algumCheckboxMarcadoPorName('acessibilidadeFake')) {
                exibirMensagemValidacao('Selecione pelo menos uma opção de acessibilidade para avançar.');
                return false;
            }
            return true;

        case 7:
            if (!campoPreenchidoPorId('rgValidadoFake')) {
                exibirMensagemValidacao('Marque o RG como validado para avançar.');
                return false;
            }

            if (!campoPreenchidoPorId('cpfValidadoFake')) {
                exibirMensagemValidacao('Marque o CPF como validado para avançar.');
                return false;
            }

            if (!campoPreenchidoPorId('comprovanteValidadoFake')) {
                exibirMensagemValidacao('Marque o comprovante de residência como validado para avançar.');
                return false;
            }

            return true;

        case 8:
            if (!campoPreenchidoPorId('confirmacaoFake')) {
                exibirMensagemValidacao('Confirme a revisão dos dados para finalizar o treinamento.');
                return false;
            }
            return true;

        default:
            return true;
    }
}

    function atualizarResumoProposta() {
        const resumo = document.getElementById('resumoPropostaFake');

        if (!resumo) {
            return;
        }

        const tipoConta = document.getElementById('tipoContaFake')?.value || 'Tipo de conta não selecionado';
        const nome = document.getElementById('nomeFake')?.value || 'Nome não informado';
        const telefone = document.getElementById('telefoneFake')?.value || 'Telefone não informado';

        const cep = document.getElementById('cepFake')?.value || 'CEP não informado';
        const endereco = document.getElementById('enderecoFake')?.value || 'Endereço não informado';
        const numero = document.getElementById('numeroFake')?.value || 's/n';
        const complemento = document.getElementById('complementoFake')?.value || '';
        const bairro = document.getElementById('bairroFake')?.value || 'Bairro não informado';
        const cidade = document.getElementById('cidadeFake')?.value || 'Cidade não informada';
        const uf = document.getElementById('ufFake')?.value || 'UF não informada';

        const acessibilidades = Array.from(document.querySelectorAll('input[name="acessibilidadeFake"]:checked'))
            .map(function (input) {
                return input.value;
            });

        const textoAcessibilidade = acessibilidades.length > 0
            ? acessibilidades.join(', ')
            : 'Acessibilidade não informada';

        const textoComplemento = complemento ? ` • ${complemento}` : '';

        resumo.textContent =
            `${tipoConta} • ${nome} • ${telefone} • ${endereco}, ${numero}${textoComplemento} • ${bairro} • ${cidade}/${uf} • CEP ${cep} • ${textoAcessibilidade}`;
    }

    function aplicarMascaraCpf(valor) {
        return valor
            .replace(/\D/g, '')
            .slice(0, 11)
            .replace(/(\d{3})(\d)/, '$1.$2')
            .replace(/(\d{3})(\d)/, '$1.$2')
            .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
    }

    function aplicarMascaraData(valor) {
        return valor
            .replace(/\D/g, '')
            .slice(0, 8)
            .replace(/(\d{2})(\d)/, '$1/$2')
            .replace(/(\d{2})(\d)/, '$1/$2');
    }

    function aplicarMascaraTelefone(valor) {
        const numeros = valor.replace(/\D/g, '').slice(0, 11);

        if (numeros.length <= 10) {
            return numeros
                .replace(/(\d{2})(\d)/, '($1) $2')
                .replace(/(\d{4})(\d)/, '$1-$2');
        }

        return numeros
            .replace(/(\d{2})(\d)/, '($1) $2')
            .replace(/(\d{5})(\d)/, '$1-$2');
    }

    function aplicarMascaraCep(valor) {
        return valor
            .replace(/\D/g, '')
            .slice(0, 8)
            .replace(/(\d{5})(\d)/, '$1-$2');
    }

    function aplicarTamanhoTextoPopup() {
        if (!guidedPopup) {
            return;
        }

        guidedPopup.style.setProperty('--popup-font-scale', popupFontScale);
    }

    function aumentarTextoPopup() {
        popupFontScale = Math.min(popupFontScale + 0.1, 1.6);
        localStorage.setItem('cv_popup_font_scale', popupFontScale);
        aplicarTamanhoTextoPopup();
    }

    function diminuirTextoPopup() {
        popupFontScale = Math.max(popupFontScale - 0.1, 0.9);
        localStorage.setItem('cv_popup_font_scale', popupFontScale);
        aplicarTamanhoTextoPopup();
    }
    function concluirEtapaAtual() {
         const etapa = trilha[etapaIndex];

         if (!etapa) {
             return;
         }

         if (!validarEtapaAtual()) {
             falarInstrucaoAtual();
             return;
         }

         enviarConclusaoEtapa(etapa);

         if (etapaIndex < trilha.length - 1) {
             exibirToast(`Etapa ${etapa.ordem} concluída. Avançando para a próxima orientação.`, 'success');
             etapaIndex += 1;
             renderEtapa();
             return;
         }

         exibirToast('Jornada Conta Fácil concluída! A validação foi desbloqueada.', 'success');

         setTimeout(function () {
             window.location.href = '/treinamento';
         }, 1200);
     }

    if (prev) {
        prev.addEventListener('click', function () {
            if (etapaIndex > 0) {
                etapaIndex -= 1;
                renderEtapa();
            }
        });
    }

    if (next) {
    next.addEventListener('click', function () {
        if (!validarEtapaAtual()) {
            falarInstrucaoAtual();
            return;
        }

        if (etapaIndex < trilha.length - 1) {
            etapaIndex += 1;
            renderEtapa();
        }
    });
}

    if (repeat) {
        repeat.addEventListener('click', function () {
            falarInstrucaoAtual();
        });
    }

    if (complete) {
        complete.addEventListener('click', function () {
            concluirEtapaAtual();
        });
    }

    if (acaoSimulada) {
        acaoSimulada.addEventListener('click', function () {
            concluirEtapaAtual();
        });
    }

    if (minimizePopup) {
        minimizePopup.addEventListener('click', function () {
            esconderPainelInstrucao();

            if (showPopupButton && treinamentoEmExecucao) {
                showPopupButton.style.display = 'inline-flex';
            }
        });
    }

    if (showPopupButton) {
        showPopupButton.addEventListener('click', function () {
            const etapa = trilha[etapaIndex];

            if (!etapa) {
                return;
            }

            mostrarPainelInstrucao();

            showPopupButton.style.display = 'none';
            destacarCampo(etapa.targetId);
        });
    }

    const cpfFake = document.getElementById('cpfFake');
    const dataNascimentoFake = document.getElementById('dataNascimentoFake');
    const telefoneFake = document.getElementById('telefoneFake');
    const cepFake = document.getElementById('cepFake');
    const ufFake = document.getElementById('ufFake');

    if (cpfFake) {
        cpfFake.addEventListener('input', function () {
            cpfFake.value = aplicarMascaraCpf(cpfFake.value);
            atualizarResumoProposta();
        });
    }

    if (dataNascimentoFake) {
        dataNascimentoFake.addEventListener('input', function () {
            dataNascimentoFake.value = aplicarMascaraData(dataNascimentoFake.value);
            atualizarResumoProposta();
        });
    }

    if (telefoneFake) {
        telefoneFake.addEventListener('input', function () {
            telefoneFake.value = aplicarMascaraTelefone(telefoneFake.value);
            atualizarResumoProposta();
        });
    }

    if (cepFake) {
        cepFake.addEventListener('input', function () {
            cepFake.value = aplicarMascaraCep(cepFake.value);
            atualizarResumoProposta();
        });
    }

    if (ufFake) {
        ufFake.addEventListener('input', function () {
            ufFake.value = ufFake.value
                .replace(/[^a-zA-Z]/g, '')
                .slice(0, 2)
                .toUpperCase();

            atualizarResumoProposta();
        });
    }

    document.querySelectorAll('input, select').forEach(function (campo) {
        campo.addEventListener('change', function () {
            limparMensagemValidacao();
            atualizarResumoProposta();
        });

        campo.addEventListener('input', function () {
            limparMensagemValidacao();
            atualizarResumoProposta();
        });
    });

    if (popupTextPlus) {
        popupTextPlus.addEventListener('click', function () {
            aumentarTextoPopup();
        });
    }

    if (popupTextMinus) {
        popupTextMinus.addEventListener('click', function () {
            diminuirTextoPopup();
        });
    }

    function obterTargetDaEtapaAtual() {
        const etapa = trilha[etapaIndex];

        if (!etapa) {
            return null;
        }

        if (etapa.ordem === 5) {
            return document.getElementById('step-target-endereco') ||
                document.getElementById('step-target-cep') ||
                document.getElementById(etapa.targetId);
        }

        if (etapa.ordem === 7) {
            return document.getElementById('step-target-documentos') ||
                document.getElementById(etapa.targetId);
        }

        if (etapa.ordem === 8) {
            return document.getElementById('step-target-confirmacao') ||
                document.getElementById(etapa.targetId);
        }

        return document.getElementById(etapa.targetId);
    }

    function reposicionarPainelAtual() {
        const target = obterTargetDaEtapaAtual();

        if (target) {
            posicionarPopup(target);
        }
    }

    window.addEventListener('resize', function () {
        reposicionarPainelAtual();
    });

    window.addEventListener('scroll', function () {
        reposicionarPainelAtual();
    }, { passive: true });

    const startTrainingButton = document.getElementById('startTraining');
    const restartTrainingButton = document.getElementById('restartTrainingButton');
    const restartTrainingForm = document.getElementById('restartTrainingForm');

    function lerProgressoCard(botao) {
        if (!botao) {
            return 0;
        }

        const valor = String(botao.getAttribute('data-progresso') || '0')
            .replace('%', '')
            .replace(',', '.')
            .trim();

        const numero = parseFloat(valor);

        return Number.isNaN(numero) ? 0 : numero;
    }

    if (startTrainingButton) {
        const progressoCard = lerProgressoCard(startTrainingButton);

        startTrainingButton.classList.remove('btn-finished');

        if (progressoCard <= 0) {
            startTrainingButton.textContent = 'Iniciar treinamento';
            startTrainingButton.disabled = false;
            startTrainingButton.setAttribute('aria-disabled', 'false');
        } else if (progressoCard > 0 && progressoCard < 100) {
            startTrainingButton.textContent = 'Continuar treinamento';
            startTrainingButton.disabled = false;
            startTrainingButton.setAttribute('aria-disabled', 'false');
        } else {
            startTrainingButton.textContent = 'Treinamento concluído';
            startTrainingButton.disabled = true;
            startTrainingButton.setAttribute('aria-disabled', 'true');
            startTrainingButton.classList.add('btn-finished');
        }
    }

    if (restartTrainingButton && restartTrainingForm) {
        const progressoCard = lerProgressoCard(restartTrainingButton);

        restartTrainingButton.classList.remove('btn-disabled-state');

        if (progressoCard < 100) {
            restartTrainingButton.disabled = true;
            restartTrainingButton.setAttribute('aria-disabled', 'true');
            restartTrainingButton.classList.add('btn-disabled-state');

            restartTrainingForm.addEventListener('submit', function (event) {
                event.preventDefault();
                return false;
            });
        } else {
            restartTrainingButton.disabled = false;
            restartTrainingButton.setAttribute('aria-disabled', 'false');
        }
    }
    });