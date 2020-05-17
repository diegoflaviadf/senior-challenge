package br.com.senior.challenge.config;

import br.com.senior.challenge.resources.converters.AbstractBaseEntityFromUriDeserializer;
import br.com.senior.challenge.resources.converters.UriToEntityConversionService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.rest.core.UriToEntityConverter;

import java.util.Collections;

public class DefaultMapper extends ObjectMapper {

    public static final String DEFAULT_PACKAGE = "br.com.senior";

    private MappingContext<?, ?> mappingContext;
    private ApplicationContext applicationContext;

    public DefaultMapper(ApplicationContext applicationContext, MappingContext<?, ?> mappingContext) {
        super();
        defaultMapper(applicationContext, mappingContext);
    }

    public DefaultMapper(ObjectMapper src) {
        super(src);
        DefaultMapper defaultMapper = (DefaultMapper) src;
        defaultMapper(defaultMapper.applicationContext, defaultMapper.mappingContext);
    }

    @Override
    public ObjectMapper copy() {
        return new DefaultMapper(this);
    }

    private void defaultMapper(ApplicationContext applicationContext, MappingContext<?, ?> mappingContext) {
        this.applicationContext = applicationContext;
        this.mappingContext = mappingContext;

        registerModule(new Jdk8Module());
        registerModule(new JavaTimeModule());
        registerModule(new Hibernate5Module());
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        setSerializationInclusion(JsonInclude.Include.NON_NULL);
        registerEntity();

    }

    private void registerEntity() {
        PersistentEntities persistentEntities = new PersistentEntities(Collections.singletonList(mappingContext));
        UriToEntityConversionService uriToEntityConversionService = new UriToEntityConversionService(applicationContext, persistentEntities);

        registerModule(new SimpleModule("URIDeserializationModule") {
            @Override
            public void setupModule(SetupContext context) {
                UriToEntityConverter converter = uriToEntityConversionService.getConverter();

                AbstractBaseEntityFromUriDeserializer abstractBaseEntityFromUriDeserializer = new AbstractBaseEntityFromUriDeserializer(persistentEntities, converter);

                context.addBeanDeserializerModifier(abstractBaseEntityFromUriDeserializer);
            }
        });
    }

}
