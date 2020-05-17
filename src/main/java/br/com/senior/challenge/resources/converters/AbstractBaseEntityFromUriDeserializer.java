package br.com.senior.challenge.resources.converters;

import br.com.senior.challenge.entities.AbstractBaseEntity;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.StdValueInstantiator;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.rest.core.UriToEntityConverter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Deserializer para converter URI em Entidades
 */
public class AbstractBaseEntityFromUriDeserializer extends BeanDeserializerModifier {

    private final UriToEntityConverter converter;
    private final PersistentEntities repositories;

    public AbstractBaseEntityFromUriDeserializer(PersistentEntities repositories, UriToEntityConverter converter) {
        this.repositories = repositories;
        this.converter = converter;
    }

    @Override
    public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc, BeanDeserializerBuilder builder) {
        repositories.getPersistentEntity(beanDesc.getBeanClass())
                .filter(entity -> AbstractBaseEntity.class.isAssignableFrom(entity.getType()))
                .ifPresent(entity -> replaceValueInstantiator(builder, entity));
        return builder;
    }

    private void replaceValueInstantiator(BeanDeserializerBuilder builder, PersistentEntity<?, ?> entity) {
        final ValueInstantiator currentValueInstantiator = builder.getValueInstantiator();

        if (currentValueInstantiator instanceof StdValueInstantiator) {
            EntityFromUriInstantiator entityFromUriInstantiator =
                    new EntityFromUriInstantiator((StdValueInstantiator) currentValueInstantiator, entity.getType(), converter);
            builder.setValueInstantiator(entityFromUriInstantiator);
        }
    }

    private static class EntityFromUriInstantiator extends StdValueInstantiator {
        private final Class entityType;
        private final UriToEntityConverter converter;

        private EntityFromUriInstantiator(StdValueInstantiator src, Class entityType, UriToEntityConverter converter) {
            super(src);
            this.entityType = entityType;
            this.converter = converter;
        }

        @Override
        public Object createFromString(DeserializationContext ctxt, String value) throws IOException {
            URI uri;
            try {
                uri = new URI(value);
            } catch (URISyntaxException e) {
                return super.createFromString(ctxt, value);
            }

            return converter.convert(uri, TypeDescriptor.valueOf(URI.class), TypeDescriptor.valueOf(entityType));
        }
    }

}
