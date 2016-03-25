package com.zorroa.shotgun;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

/**
 * Created by chambers on 3/23/16.
 */
public class SgFilters {

    private List<SgFilter> filters;

    public SgFilters() {
        filters = Lists.newArrayList();
    }

    public SgFilters(String path, String relation, List<Object> values) {
        this();
        filter(path, relation, values);
    }

    public SgFilters(String path, String relation, Object value) {
        this();
        filter(path, relation, value);
    }

    public SgFilters filter(String path, String relation, Collection<Object> values) {
        filters.add(new SgFilter(path, relation, values));
        return this;
    }

    public SgFilters filter(String path, String relation, Object value) {
        filters.add(new SgFilter(path, relation, value));
        return this;
    }

    public List<SgFilter> asList() {
        return filters;
    }

    public static class SgFilter {

        private String path;
        private Collection<Object> values;
        private String relation;

        public SgFilter(String path, String relation, Collection<Object> values) {
            this.path = path;
            this.values = values;
            this.relation = relation;
        }

        public SgFilter(String path, String relation, Object value) {
            this.path = path;
            this.values = ImmutableList.of(value);
            this.relation = relation;
        }

        public String getPath() {
            return path;
        }

        public SgFilter setPath(String path) {
            this.path = path;
            return this;
        }

        public Collection<Object> getValues() {
            return values;
        }

        public SgFilter setValues(List<Object> values) {
            this.values = values;
            return this;
        }

        public String getRelation() {
            return relation;
        }

        public SgFilter setRelation(String relation) {
            this.relation = relation;
            return this;
        }
    }

}
