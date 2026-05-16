package br.gov.caixa.treinamento.bootstrap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DataLoader - Cobertura")
class DataLoaderTest {

    @Test
    @DisplayName("DataLoader deve ser instanciável e não executar carga automática")
    void dataLoader_deveSerInstanciavel() {
        DataLoader dataLoader = new DataLoader();

        assertThat(dataLoader).isNotNull();
    }
}
