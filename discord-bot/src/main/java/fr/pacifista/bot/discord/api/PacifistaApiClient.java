package fr.pacifista.bot.discord.api;

import com.funixproductions.core.crud.clients.CrudClient;
import com.funixproductions.core.crud.dtos.ApiDTO;
import com.funixproductions.core.crud.dtos.PageDTO;
import fr.pacifista.bot.discord.config.Config;
import jakarta.validation.Valid;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public abstract class PacifistaApiClient<DTO extends ApiDTO> implements CrudClient<DTO> {
    private static final String PACIFISTA_API_URL = "https://api.pacifista.fr";
    private final String url;
    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeaders defaultHeaders;

    protected PacifistaApiClient(Config config, String path) {
        this.url = PACIFISTA_API_URL + path;

        this.defaultHeaders = new HttpHeaders();
        defaultHeaders.set("Authorization", config.getPacifistaApiToken());
    }

    @Override
    public PageDTO<DTO> getAll(String page, String elemsPerPage, String search, String sort) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.url)
                .queryParam("page", page)
                .queryParam("elemsPerPage", elemsPerPage)
                .queryParam("search", search)
                .queryParam("sort", sort);

        HttpEntity<DTO> requestEntity = new HttpEntity<>(this.defaultHeaders);

        ResponseEntity<PageDTO<DTO>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    @Override
    public DTO findById(String id) {
        HttpEntity<DTO> requestEntity = new HttpEntity<>(this.defaultHeaders);

        ResponseEntity<DTO> response = restTemplate.exchange(this.url + "/" + id, HttpMethod.GET, requestEntity, getDtoClass());
        return response.getBody();
    }

    @Override
    public DTO create(@Valid DTO request) {
        HttpEntity<DTO> requestEntity = new HttpEntity<>(request, this.defaultHeaders);

        ResponseEntity<DTO> response = restTemplate.exchange(this.url, HttpMethod.POST, requestEntity, getDtoClass());
        return response.getBody();
    }

    @Override
    public List<DTO> create(@Valid List<@Valid DTO> request) {
        HttpEntity<List<DTO>> requestEntity = new HttpEntity<>(request, this.defaultHeaders);

        ResponseEntity<List<DTO>> response = restTemplate.exchange(
                this.url,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    @Override
    public DTO update(DTO request) {
        HttpEntity<DTO> requestEntity = new HttpEntity<>(request, this.defaultHeaders);

        ResponseEntity<DTO> response = restTemplate.exchange(this.url, HttpMethod.PATCH, requestEntity, getDtoClass());
        return response.getBody();
    }

    @Override
    public List<DTO> update(List<DTO> request) {
        HttpEntity<List<DTO>> requestEntity = new HttpEntity<>(request, this.defaultHeaders);

        ResponseEntity<List<DTO>> response = restTemplate.exchange(
                this.url,
                HttpMethod.PATCH,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    @Override
    public DTO updatePut(@Valid DTO request) {
        HttpEntity<DTO> requestEntity = new HttpEntity<>(request, this.defaultHeaders);

        ResponseEntity<DTO> response = restTemplate.exchange(this.url, HttpMethod.PUT, requestEntity, getDtoClass());
        return response.getBody();
    }

    @Override
    public List<DTO> updatePut(@Valid List<@Valid DTO> request) {
        HttpEntity<List<DTO>> requestEntity = new HttpEntity<>(request, this.defaultHeaders);

        ResponseEntity<List<DTO>> response = restTemplate.exchange(
                this.url,
                HttpMethod.PUT,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    @Override
    public void delete(String id) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.url)
                .queryParam("id", id);

        HttpEntity<DTO> requestEntity = new HttpEntity<>(this.defaultHeaders);

        restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
    }

    @Override
    public void delete(String... ids) {
        for (String id : ids) {
            delete(id);
        }
    }

    protected abstract Class<DTO> getDtoClass();
}
