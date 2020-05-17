package br.com.senior.challenge.resources.converters;

import org.springframework.context.ApplicationContext;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.repository.support.DefaultRepositoryInvokerFactory;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.UriToEntityConverter;
import org.springframework.format.support.DefaultFormattingConversionService;

/**
 * Registra um conversor de URI para entidades
 */
public class UriToEntityConversionService extends DefaultFormattingConversionService {

    private UriToEntityConverter converter;

    public UriToEntityConversionService(ApplicationContext applicationContext, PersistentEntities entities) {
        new DomainClassConverter<>(this).setApplicationContext(applicationContext);

        Repositories repositories = new Repositories(applicationContext);
        converter = new UriToEntityConverter(entities, new DefaultRepositoryInvokerFactory(repositories, this), repositories);

        addConverter(converter);
    }

    public UriToEntityConverter getConverter() {
        return converter;
    }

}