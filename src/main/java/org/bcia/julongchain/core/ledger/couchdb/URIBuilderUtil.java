package org.bcia.julongchain.core.ledger.couchdb;

import org.lightcouch.Params;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建 couchdb URI
 *
 * @author zhaiyihua
 * @date 2018/07/20
 * @company ShanghaiGeer
 */
public class URIBuilderUtil {
    private String scheme;
    private String host;
    private int port;
    private String path = "";
    private final List<String> params = new ArrayList<String>();

    public static URIBuilderUtil buildUri() {
        return new URIBuilderUtil();
    }

    public static URIBuilderUtil buildUri(URI uri) {
        URIBuilderUtil builder = URIBuilderUtil.buildUri().scheme(uri.getScheme()).
                host(uri.getHost()).port(uri.getPort()).path(uri.getPath());
        return builder;
    }

    public URIBuilderUtil scheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public URIBuilderUtil host(String host) {
        this.host = host;
        return this;
    }

    public URIBuilderUtil port(int port) {
        this.port = port;
        return this;
    }

    public URIBuilderUtil path(String path) {
        this.path += path;
        return this;
    }

    public URIBuilderUtil pathEncoded(String path) {
        try {
            this.path += URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    public URIBuilderUtil query(String name, Object value) {
        if (name != null && value != null) {
            try {
                name = URLEncoder.encode(name, "UTF-8");
                value = URLEncoder.encode(String.valueOf(value), "UTF-8");
                this.params.add(String.format("%s=%s", name, value));
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return this;
    }

    public URIBuilderUtil query(Params params) {
        if (params.getParams() != null){
            this.params.addAll(params.getParams());
        }
        return this;
    }

    public URI build() {
        final StringBuilder query = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            String amp = (i != params.size() - 1) ? "&" : "";
            query.append(params.get(i) + amp);
        }

        String q = (query.length() == 0) ? "" : "?" + query;
        String uri = String.format("%s://%s:%s%s%s", new Object[] { scheme, host, port, path, q });

        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }

    }
}
