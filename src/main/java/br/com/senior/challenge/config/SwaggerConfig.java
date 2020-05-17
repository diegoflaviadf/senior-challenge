package br.com.senior.challenge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.DefaultPathProvider;
import springfox.documentation.spring.web.plugins.Docket;

import static springfox.documentation.spring.web.paths.Paths.removeAdjacentForwardSlashes;

@Configuration
public class SwaggerConfig {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .pathProvider(new MyPathProvider())
                .select()
                .apis(RequestHandlerSelectors.basePackage("br.com.senior.challenge.controllers"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(metaData());

    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title("Sennior Java Dev Challenge")
                .description("\"Back end de uma aplicação para cadastro de produto/serviço, pedido e itens de pedido\"")
                .version("1.0.0")
                .build();
    }

    private class MyPathProvider extends DefaultPathProvider {
        @Override
        public String getOperationPath(String operationPath) {
            if (operationPath.startsWith(contextPath)) {
                operationPath = operationPath.substring(contextPath.length());
            }
            return removeAdjacentForwardSlashes(UriComponentsBuilder.newInstance().replacePath(operationPath)
                    .build().toString());
        }
    }
}
