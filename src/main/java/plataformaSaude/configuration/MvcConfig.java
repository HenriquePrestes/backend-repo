package plataformaSaude.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapeia a URL /uploads/** para a pasta física onde os arquivos estão
        // Ex: /uploads/meu-logo.png -> buscará o arquivo em ./uploads/meu-logo.png
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir);
    }
}

//Esta classe de configuração diz ao Spring Boot para "servir" arquivos estáticos da pasta
// ./uploads/ através da URL /uploads/**.