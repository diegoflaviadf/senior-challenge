package br.com.senior.challenge.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class JacksonConfig implements WebMvcConfigurer {

    private MappingContext<?, ?> mappingContext;
    private ApplicationContext applicationContext;

    public JacksonConfig(ApplicationContext applicationContext, MappingContext<?, ?> mappingContext) {
        this.applicationContext = applicationContext;
        this.mappingContext = mappingContext;
    }

    @Override
    public void configureMessageConverters(
            List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(defaultMapper()));
    }

    @Bean
    public ObjectMapper defaultMapper() {
        return new DefaultMapper(applicationContext, mappingContext);
    }

}
