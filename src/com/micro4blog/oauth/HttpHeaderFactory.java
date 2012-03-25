/*
 * Copyright 2011 Sina.
 *
 * Licensed under the Apache License and Weibo License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.open.weibo.com
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.micro4blog.oauth;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Random;

import android.os.Bundle;
import android.util.Log;

/**
 * Encapsulation a abstract weibo http headers base class.
 * 
 */

public abstract class HttpHeaderFactory {
    public static final String CONST_HMAC_SHA1 = "HmacSHA1";
    public static final String CONST_SIGNATURE_METHOD = "HMAC-SHA1";
    public static final String CONST_OAUTH_VERSION = "1.0";

    public HttpHeaderFactory() {
    }

    public String getMicro4blogAuthHeader(Micro4blog micro4blog, String method, String url, Micro4blogParameters params,
            String app_key, String app_secret, Token token) throws Micro4blogException {
        // step 1: generate timestamp and nonce
        final long timestamp = System.currentTimeMillis() / 1000;
//        final long nonce = timestamp + (new Random()).nextInt();
        Random random = new Random();
        long nonce = (random.nextInt(9876599) + 123400);
        // step 2: authParams有两个用处：1.加密串一部分 2.生成最后Authorization头域
        Micro4blogParameters authParams = this.generateAuthParameters(micro4blog, nonce, timestamp, token);
        // 生成用于计算signature的，参数串  thom 如果外面传过来参数的话，那params即可覆盖authparams，不过现在处理的是内部，不需要params，注释掉方法中的这个处理
        // 真正的原因是没有实现addAll方法
        Micro4blogParameters signatureParams = this.generateSignatureParameters(micro4blog, authParams, params, url);
        // step 3: 生成用于签名的base String
        String oauthBaseString = this.generateAuthSignature(method, signatureParams, url, token);
        // step 4: 生成oauth_signature
        String signature = generateSignature(micro4blog, oauthBaseString, token);
        authParams.add("oauth_signature", signature);
        // step 5: for additional parameters
        this.addAdditionalParams(authParams, params);
        
//        return "OAuth " + encodeParameters(authParams, ",", true);
        return encodeParameters(authParams, "&", true);
    }

    private String generateAuthSignature(final String method, Micro4blogParameters signatureParams,
            final String url, Token token) {
        StringBuffer base = new StringBuffer(method).append("&")
                .append(encode(constructRequestURL(url))).append("&");
        base.append(encode(encodeParameters(signatureParams, "&", false)));
        String oauthBaseString = base.toString();
        return oauthBaseString;
    }

    private Micro4blogParameters generateSignatureParameters(Micro4blog micro4blog, Micro4blogParameters authParams,
    		Micro4blogParameters params, String url) throws Micro4blogException {
    	Micro4blogParameters signatureParams = new Micro4blogParameters();
        signatureParams.addAll(authParams);
//        signatureParams.add("source", micro4blog.getAppKey());
        signatureParams.addAll(params);
        this.parseUrlParameters(url, signatureParams);
        Micro4blogParameters lsp = generateSignatureList(signatureParams);
        return lsp;
    }

    private Micro4blogParameters generateAuthParameters(Micro4blog micro4blog, long nonce, long timestamp, Token token) {
    	Micro4blogParameters authParams = new Micro4blogParameters();
        authParams.add("oauth_callback", "null");
    	authParams.add("oauth_consumer_key", micro4blog.getAppKey());
        authParams.add("oauth_nonce", String.valueOf(nonce));
        authParams.add("oauth_signature_method", HttpHeaderFactory.CONST_SIGNATURE_METHOD);
        authParams.add("oauth_timestamp", String.valueOf(timestamp));
        authParams.add("oauth_version", HttpHeaderFactory.CONST_OAUTH_VERSION);
        if (token != null) {
            authParams.add("oauth_token", token.getTokenOauthOrAccess());
        } else {
//            authParams.add("source", micro4blog.getAppKey());
        }
        
       
        return authParams;
    }

    // 生成用于哈希的base string串，注意要按顺序，按需文档需求参数生成，否则40107错误
    public abstract Micro4blogParameters generateSignatureList(Micro4blogParameters bundle);

    // add additional parameters to des key-value pairs,support to expanding
    // params
    public abstract void addAdditionalParams(Micro4blogParameters des, Micro4blogParameters src);

    // 解析url中参数对,存储到signatureBaseParams
    public void parseUrlParameters(String url, Micro4blogParameters signatureBaseParams)
            throws Micro4blogException {
        int queryStart = url.indexOf("?");
        if (-1 != queryStart) {
            String[] queryStrs = url.substring(queryStart + 1).split("&");
            try {
                for (String query : queryStrs) {
                    String[] split = query.split("=");
                    if (split.length == 2) {
                        signatureBaseParams.add(URLDecoder.decode(split[0], "UTF-8"),
                                URLDecoder.decode(split[1], "UTF-8"));
                    } else {
                        signatureBaseParams.add(URLDecoder.decode(split[0], "UTF-8"), "");
                    }
                }
            } catch (UnsupportedEncodingException e) {
                throw new Micro4blogException(e);
            }

        }

    }

    public abstract String generateSignature(Micro4blog micro4blog, String data, Token token) throws Micro4blogException;

    public static String encodeParameters(Micro4blogParameters postParams, String splitter, boolean quot) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < postParams.size(); i++) {
            if (buf.length() != 0) {
                if (quot) {
                    buf.append("");
                }
                buf.append(splitter);
            }
            buf.append(encode(postParams.getKey(i))).append("=");
            if (quot) {
                buf.append("");
            }
            buf.append(encode(postParams.getValue(i)));
        }
        if (buf.length() != 0) {
            if (quot) {
                buf.append("");
            }
        }
        return buf.toString();
    }

    public static String encodeParameters(Bundle postParams, String split, boolean quot) {
        final String splitter = split;
        StringBuffer buf = new StringBuffer();
        for (String key : postParams.keySet()) {
            if (buf.length() != 0) {
                if (quot) {
                    buf.append("\"");
                }
                buf.append(splitter);
            }
            buf.append(encode(key)).append("=");
            if (quot) {
                buf.append("\"");
            }
            buf.append(encode(postParams.getString(key)));
        }
        if (buf.length() != 0) {
            if (quot) {
                buf.append("\"");
            }
        }
        return buf.toString();
    }

    //
    public static String constructRequestURL(String url) {
        int index = url.indexOf("?");
        if (-1 != index) {
            url = url.substring(0, index);
        }
        int slashIndex = url.indexOf("/", 8);
        String baseURL = url.substring(0, slashIndex).toLowerCase();
        int colonIndex = baseURL.indexOf(":", 8);
        if (-1 != colonIndex) {
            // url contains port number
            if (baseURL.startsWith("http://") && baseURL.endsWith(":80")) {
                // http default port 80 MUST be excluded
                baseURL = baseURL.substring(0, colonIndex);
            } else if (baseURL.startsWith("https://") && baseURL.endsWith(":443")) {
                // http default port 443 MUST be excluded
                baseURL = baseURL.substring(0, colonIndex);
            }
        }
        url = baseURL + url.substring(slashIndex);

        return url;
    }

    /**
     * @param value
     *            string to be encoded
     * @return encoded parameters string
     */
    public static String encode(String value) {
        String encoded = null;
                      
        Log.i("thom", "value " + value);
        
        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }
        StringBuffer buf = new StringBuffer(encoded.length());
        char focus;
        for (int i = 0; i < encoded.length(); i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                buf.append("%2A");
            } else if (focus == '+') {
                buf.append("%20");
            } else if (focus == '%' && (i + 1) < encoded.length() && encoded.charAt(i + 1) == '7'
                    && encoded.charAt(i + 2) == 'E') {
                buf.append('~');
                i += 2;
            } else {
                buf.append(focus);
            }
        }
        return buf.toString();
    }


}
