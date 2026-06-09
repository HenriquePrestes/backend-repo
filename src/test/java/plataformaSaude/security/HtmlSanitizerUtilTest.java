package plataformaSaude.security;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class HtmlSanitizerUtilTest {

    @Test
    void deveRemoverTagsHtmlPeligrosas() {
        String sujo = "<script>alert('xss');</script>Olá";
        String limpo = HtmlSanitizerUtil.sanitize(sujo);
        assertThat(limpo).isEqualTo("Olá");
    }

    @Test
    void deveManterTextoNormal() {
        String texto = "Clinica MedFast";
        String limpo = HtmlSanitizerUtil.sanitize(texto);
        assertThat(limpo).isEqualTo(texto);
    }

    @Test
    void deveRetornarNullQuandoEntradaForNull() {
        assertThat(HtmlSanitizerUtil.sanitize(null)).isNull();
    }
}
