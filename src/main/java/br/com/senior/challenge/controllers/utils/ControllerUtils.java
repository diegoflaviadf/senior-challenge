package br.com.senior.challenge.controllers.utils;

import org.springframework.hateoas.Link;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Classe utilitária para Controllers
 */
public final class ControllerUtils {

    private ControllerUtils() {
        //Utility class
    }

    /**
     * Adjust the {@link Link} such that it starts at {@literal basePath}.
     *
     * @param link     - link presumably supplied via Spring HATEOAS
     * @param basePath - base path provided by Spring Data REST
     * @return new {@link Link} with these two values melded together
     */
    public static Link applyBasePath(Link link, String basePath) {

        URI uri = link.toUri();

        URI newUri = null;
        try {
            newUri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), //
                    uri.getPort(), basePath + uri.getPath(), uri.getQuery(), uri.getFragment());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Link não é válido " + uri);
        }

        return new Link(newUri.toString(), link.getRel());
    }

}
