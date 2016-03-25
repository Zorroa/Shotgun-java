package com.zorroa.shotgun;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collection;

/**
 * Shotgun Request
 */
public class SgRequest {

    private SgFilters filters = new SgFilters();
    private Collection<String> fields = ImmutableList.of("id");
    private String type;
    private int page = 1;
    private int count = 500;

    public SgRequest(String type) {
        this.type = type;
    }

    public SgRequest filter(String path, String relation, Collection<Object> values) {
        filters.filter(path, relation, values);
        return this;
    }

    public SgRequest filter(String path, String relation, Object value) {
        filters.filter(path, relation, value);
        return this;
    }

    public Collection<String> getFields() {
        return fields;
    }

    public SgRequest setFields(Collection<String> fields) {
        this.fields = fields;
        return this;
    }

    public SgRequest setFields(String ... field) {
        fields = Arrays.asList(field);
        return this;
    }

    public String getType() {
        return type;
    }

    public SgRequest setType(String type) {
        this.type = type;
        return this;
    }

    public int getPage() {
        return page;
    }

    public SgRequest setPage(int page) {
        this.page = page;
        return this;
    }

    public int getCount() {
        return count;
    }

    public SgRequest setCount(int count) {
        this.count = count;
        return this;
    }

    public SgFilters getFilters() {
        return filters;
    }

    public SgRequest setFilters(SgFilters filters) {
        this.filters = filters;
        return this;
    }
}
