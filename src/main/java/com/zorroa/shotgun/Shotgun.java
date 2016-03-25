package com.zorroa.shotgun;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Shotgun client.
 */
public class Shotgun {

    private static final Logger logger = LoggerFactory.getLogger(Shotgun.class);

    private final static ObjectMapper JSON = new ObjectMapper();
    static {
        JSON.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JSON.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        JSON.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        JSON.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true);
        JSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final String script;
    private final String key;
    private final HttpHost host;
    private final CloseableHttpClient client;

    public Shotgun(String server, String script, String key) {
        this.script = script;
        this.key = key;

        URI uri = URI.create(server);
        this.host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        this.client = HttpClients.createDefault();
    }

    public List<Map<String, Object>> find(SgRequest req) {
        Map<String, Object> paging = ImmutableMap.<String, Object>builder()
                // Paging info creates extra overhead, not sure we need it.
                .put("return_paging_info", false)
                .put("return_fields", req.getFields())
                .put("return_only", "active")
                .put("paging", ImmutableMap.of("current_page", req.getPage(), "entities_per_page", req.getCount()))
                .put("filters", ImmutableMap.of("logical_operator", "and", "conditions", req.getFilters().asList()))
                .put("api_return_image_urls", true)
                .put("type", req.getType())
                .build();

        return makeRequest("read", ImmutableList.of(paging));
    }

    public Map<String, Object> findOne(SgRequest req) {
        req.setCount(1);
        return find(req).get(0);
    }

    private List<Map<String, Object>> makeRequest(String method, List<Object> params) {

        RequestBody body = new RequestBody(method);
        body.addToParams(ImmutableMap.of("script_key", key, "script_name", script));
        body.addAllToParams(params);
        HttpPost req = new HttpPost("/api3/json");
        req.setHeader("Content-Type", "application/json; charset=utf-8");
        req.setHeader("Connection", "keep-alive");

        try {
            req.setEntity(new ByteArrayEntity(JSON.writeValueAsBytes(body)));
        } catch (Exception e) {
            throw new ShotgunException("failed request " + e);
        }

        return execute(req);
    }

    private List<Map<String, Object>> execute(HttpRequest req) {
        HttpResponse response;
        try {
            response = client.execute(host, req);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new ShotgunException(response.getStatusLine().getReasonPhrase());
            }

            Map<String, Object> result = JSON.readValue(response.getEntity().getContent(), Map.class);
            if (result.containsKey("exception")) {
                throw new ShotgunException((String) result.get("message"));
            }
            return (List<Map<String, Object>>)((Map<String, Object>) result.get("results")).get("entities");
        } catch (IOException e) {
            throw new ShotgunException("failed to execute request " + req + "," + e, e);
        }
    }

    private static class RequestBody {
        private String method_name;
        private List<Object> params = Lists.newArrayList();

        public RequestBody(String method) {
            this.method_name = method;
        }

        public void addToParams(Object object) {
            params.add(object);
        }

        public void addAllToParams(Collection<Object> object) {
            params.addAll(object);
        }

        public String getMethod_name() {
            return method_name;
        }

        public RequestBody setMethod_name(String method_name) {
            this.method_name = method_name;
            return this;
        }

        public List<Object> getParams() {
            return params;
        }

        public RequestBody setParams(List<Object> params) {
            this.params = params;
            return this;
        }
    }
}
