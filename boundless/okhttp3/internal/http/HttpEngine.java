package okhttp3.internal.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.Address;
import okhttp3.CertificatePinner;
import okhttp3.Connection;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;
import okhttp3.internal.Internal;
import okhttp3.internal.InternalCache;
import okhttp3.internal.Util;
import okhttp3.internal.Version;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.GzipSource;
import okio.Okio;
import okio.Sink;
import okio.Source;
import okio.Timeout;

public final class HttpEngine {
    private static final ResponseBody EMPTY_BODY = new ResponseBody() {
        public MediaType contentType() {
            return null;
        }

        public long contentLength() {
            return 0;
        }

        public BufferedSource source() {
            return new Buffer();
        }
    };
    public static final int MAX_FOLLOW_UPS = 20;
    public final boolean bufferRequestBody;
    private BufferedSink bufferedRequestBody;
    private Response cacheResponse;
    private CacheStrategy cacheStrategy;
    private final boolean callerWritesRequestBody;
    final OkHttpClient client;
    private final boolean forWebSocket;
    private HttpStream httpStream;
    private Request networkRequest;
    private final Response priorResponse;
    private Sink requestBodyOut;
    long sentRequestMillis = -1;
    private CacheRequest storeRequest;
    public final StreamAllocation streamAllocation;
    private boolean transparentGzip;
    private final Request userRequest;
    private Response userResponse;

    class NetworkInterceptorChain implements Chain {
        private int calls;
        private final Connection connection;
        private final int index;
        private final Request request;

        NetworkInterceptorChain(int index, Request request, Connection connection) {
            this.index = index;
            this.request = request;
            this.connection = connection;
        }

        public Connection connection() {
            return this.connection;
        }

        public Request request() {
            return this.request;
        }

        public Response proceed(Request request) throws IOException {
            this.calls++;
            if (this.index > 0) {
                Interceptor caller = (Interceptor) HttpEngine.this.client.networkInterceptors().get(this.index - 1);
                Address address = connection().route().address();
                if (!request.url().host().equals(address.url().host()) || request.url().port() != address.url().port()) {
                    throw new IllegalStateException("network interceptor " + caller + " must retain the same host and port");
                } else if (this.calls > 1) {
                    throw new IllegalStateException("network interceptor " + caller + " must call proceed() exactly once");
                }
            }
            if (this.index < HttpEngine.this.client.networkInterceptors().size()) {
                NetworkInterceptorChain chain = new NetworkInterceptorChain(this.index + 1, request, this.connection);
                Interceptor interceptor = (Interceptor) HttpEngine.this.client.networkInterceptors().get(this.index);
                Response intercept = interceptor.intercept(chain);
                if (chain.calls != 1) {
                    throw new IllegalStateException("network interceptor " + interceptor + " must call proceed() exactly once");
                } else if (intercept != null) {
                    return intercept;
                } else {
                    throw new NullPointerException("network interceptor " + interceptor + " returned null");
                }
            }
            HttpEngine.this.httpStream.writeRequestHeaders(request);
            HttpEngine.this.networkRequest = request;
            if (HttpEngine.this.permitsRequestBody(request) && request.body() != null) {
                BufferedSink bufferedRequestBody = Okio.buffer(HttpEngine.this.httpStream.createRequestBody(request, request.body().contentLength()));
                request.body().writeTo(bufferedRequestBody);
                bufferedRequestBody.close();
            }
            Response response = HttpEngine.this.readNetworkResponse();
            int code = response.code();
            if ((code != 204 && code != 205) || response.body().contentLength() <= 0) {
                return response;
            }
            throw new ProtocolException("HTTP " + code + " had non-zero Content-Length: " + response.body().contentLength());
        }
    }

    public void sendRequest() throws okhttp3.internal.http.RequestException, okhttp3.internal.http.RouteException, java.io.IOException {
        /* JADX: method processing error */
/*
Error: java.util.NoSuchElementException
	at java.util.HashMap$HashIterator.nextNode(HashMap.java:1439)
	at java.util.HashMap$KeyIterator.next(HashMap.java:1461)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.applyRemove(BlockFinallyExtract.java:535)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.extractFinally(BlockFinallyExtract.java:175)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.processExceptionHandler(BlockFinallyExtract.java:79)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:51)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
*/
        /*
        r12 = this;
        r8 = r12.cacheStrategy;
        if (r8 == 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r8 = r12.httpStream;
        if (r8 == 0) goto L_0x000f;
    L_0x0009:
        r8 = new java.lang.IllegalStateException;
        r8.<init>();
        throw r8;
    L_0x000f:
        r8 = r12.userRequest;
        r1 = r12.networkRequest(r8);
        r8 = okhttp3.internal.Internal.instance;
        r9 = r12.client;
        r6 = r8.internalCache(r9);
        if (r6 == 0) goto L_0x009d;
    L_0x001f:
        r0 = r6.get(r1);
    L_0x0023:
        r4 = java.lang.System.currentTimeMillis();
        r8 = new okhttp3.internal.http.CacheStrategy$Factory;
        r8.<init>(r4, r1, r0);
        r8 = r8.get();
        r12.cacheStrategy = r8;
        r8 = r12.cacheStrategy;
        r8 = r8.networkRequest;
        r12.networkRequest = r8;
        r8 = r12.cacheStrategy;
        r8 = r8.cacheResponse;
        r12.cacheResponse = r8;
        if (r6 == 0) goto L_0x0045;
    L_0x0040:
        r8 = r12.cacheStrategy;
        r6.trackResponse(r8);
    L_0x0045:
        if (r0 == 0) goto L_0x0052;
    L_0x0047:
        r8 = r12.cacheResponse;
        if (r8 != 0) goto L_0x0052;
    L_0x004b:
        r8 = r0.body();
        okhttp3.internal.Util.closeQuietly(r8);
    L_0x0052:
        r8 = r12.networkRequest;
        if (r8 != 0) goto L_0x009f;
    L_0x0056:
        r8 = r12.cacheResponse;
        if (r8 != 0) goto L_0x009f;
    L_0x005a:
        r8 = new okhttp3.Response$Builder;
        r8.<init>();
        r9 = r12.userRequest;
        r8 = r8.request(r9);
        r9 = r12.priorResponse;
        r9 = stripBody(r9);
        r8 = r8.priorResponse(r9);
        r9 = okhttp3.Protocol.HTTP_1_1;
        r8 = r8.protocol(r9);
        r9 = 504; // 0x1f8 float:7.06E-43 double:2.49E-321;
        r8 = r8.code(r9);
        r9 = "Unsatisfiable Request (only-if-cached)";
        r8 = r8.message(r9);
        r9 = EMPTY_BODY;
        r8 = r8.body(r9);
        r10 = r12.sentRequestMillis;
        r8 = r8.sentRequestAtMillis(r10);
        r10 = java.lang.System.currentTimeMillis();
        r8 = r8.receivedResponseAtMillis(r10);
        r8 = r8.build();
        r12.userResponse = r8;
        goto L_0x0004;
    L_0x009d:
        r0 = 0;
        goto L_0x0023;
    L_0x009f:
        r8 = r12.networkRequest;
        if (r8 != 0) goto L_0x00d3;
    L_0x00a3:
        r8 = r12.cacheResponse;
        r8 = r8.newBuilder();
        r9 = r12.userRequest;
        r8 = r8.request(r9);
        r9 = r12.priorResponse;
        r9 = stripBody(r9);
        r8 = r8.priorResponse(r9);
        r9 = r12.cacheResponse;
        r9 = stripBody(r9);
        r8 = r8.cacheResponse(r9);
        r8 = r8.build();
        r12.userResponse = r8;
        r8 = r12.userResponse;
        r8 = r12.unzip(r8);
        r12.userResponse = r8;
        goto L_0x0004;
    L_0x00d3:
        r7 = 0;
        r8 = r12.connect();	 Catch:{ all -> 0x00fc }
        r12.httpStream = r8;	 Catch:{ all -> 0x00fc }
        r8 = r12.httpStream;	 Catch:{ all -> 0x00fc }
        r8.setHttpEngine(r12);	 Catch:{ all -> 0x00fc }
        r8 = r12.writeRequestHeadersEagerly();	 Catch:{ all -> 0x00fc }
        if (r8 == 0) goto L_0x011e;	 Catch:{ all -> 0x00fc }
    L_0x00e5:
        r2 = okhttp3.internal.http.OkHeaders.contentLength(r1);	 Catch:{ all -> 0x00fc }
        r8 = r12.bufferRequestBody;	 Catch:{ all -> 0x00fc }
        if (r8 == 0) goto L_0x0134;	 Catch:{ all -> 0x00fc }
    L_0x00ed:
        r8 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;	 Catch:{ all -> 0x00fc }
        r8 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1));	 Catch:{ all -> 0x00fc }
        if (r8 <= 0) goto L_0x0109;	 Catch:{ all -> 0x00fc }
    L_0x00f4:
        r8 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x00fc }
        r9 = "Use setFixedLengthStreamingMode() or setChunkedStreamingMode() for requests larger than 2 GiB.";	 Catch:{ all -> 0x00fc }
        r8.<init>(r9);	 Catch:{ all -> 0x00fc }
        throw r8;	 Catch:{ all -> 0x00fc }
    L_0x00fc:
        r8 = move-exception;
        if (r7 != 0) goto L_0x0108;
    L_0x00ff:
        if (r0 == 0) goto L_0x0108;
    L_0x0101:
        r9 = r0.body();
        okhttp3.internal.Util.closeQuietly(r9);
    L_0x0108:
        throw r8;
    L_0x0109:
        r8 = -1;
        r8 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1));
        if (r8 == 0) goto L_0x012c;
    L_0x010f:
        r8 = r12.httpStream;	 Catch:{ all -> 0x00fc }
        r9 = r12.networkRequest;	 Catch:{ all -> 0x00fc }
        r8.writeRequestHeaders(r9);	 Catch:{ all -> 0x00fc }
        r8 = new okhttp3.internal.http.RetryableSink;	 Catch:{ all -> 0x00fc }
        r9 = (int) r2;	 Catch:{ all -> 0x00fc }
        r8.<init>(r9);	 Catch:{ all -> 0x00fc }
        r12.requestBodyOut = r8;	 Catch:{ all -> 0x00fc }
    L_0x011e:
        r7 = 1;
        if (r7 != 0) goto L_0x0004;
    L_0x0121:
        if (r0 == 0) goto L_0x0004;
    L_0x0123:
        r8 = r0.body();
        okhttp3.internal.Util.closeQuietly(r8);
        goto L_0x0004;
    L_0x012c:
        r8 = new okhttp3.internal.http.RetryableSink;	 Catch:{ all -> 0x00fc }
        r8.<init>();	 Catch:{ all -> 0x00fc }
        r12.requestBodyOut = r8;	 Catch:{ all -> 0x00fc }
        goto L_0x011e;	 Catch:{ all -> 0x00fc }
    L_0x0134:
        r8 = r12.httpStream;	 Catch:{ all -> 0x00fc }
        r9 = r12.networkRequest;	 Catch:{ all -> 0x00fc }
        r8.writeRequestHeaders(r9);	 Catch:{ all -> 0x00fc }
        r8 = r12.httpStream;	 Catch:{ all -> 0x00fc }
        r9 = r12.networkRequest;	 Catch:{ all -> 0x00fc }
        r8 = r8.createRequestBody(r9, r2);	 Catch:{ all -> 0x00fc }
        r12.requestBodyOut = r8;	 Catch:{ all -> 0x00fc }
        goto L_0x011e;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http.HttpEngine.sendRequest():void");
    }

    public HttpEngine(OkHttpClient client, Request request, boolean bufferRequestBody, boolean callerWritesRequestBody, boolean forWebSocket, StreamAllocation streamAllocation, RetryableSink requestBodyOut, Response priorResponse) {
        this.client = client;
        this.userRequest = request;
        this.bufferRequestBody = bufferRequestBody;
        this.callerWritesRequestBody = callerWritesRequestBody;
        this.forWebSocket = forWebSocket;
        if (streamAllocation == null) {
            streamAllocation = new StreamAllocation(client.connectionPool(), createAddress(client, request));
        }
        this.streamAllocation = streamAllocation;
        this.requestBodyOut = requestBodyOut;
        this.priorResponse = priorResponse;
    }

    private boolean writeRequestHeadersEagerly() {
        return this.callerWritesRequestBody && permitsRequestBody(this.networkRequest) && this.requestBodyOut == null;
    }

    private HttpStream connect() throws RouteException, RequestException, IOException {
        return this.streamAllocation.newStream(this.client.connectTimeoutMillis(), this.client.readTimeoutMillis(), this.client.writeTimeoutMillis(), this.client.retryOnConnectionFailure(), !this.networkRequest.method().equals("GET"));
    }

    private static Response stripBody(Response response) {
        if (response == null || response.body() == null) {
            return response;
        }
        return response.newBuilder().body(null).build();
    }

    public void writingRequestHeaders() {
        if (this.sentRequestMillis != -1) {
            throw new IllegalStateException();
        }
        this.sentRequestMillis = System.currentTimeMillis();
    }

    boolean permitsRequestBody(Request request) {
        return HttpMethod.permitsRequestBody(request.method());
    }

    public Sink getRequestBody() {
        if (this.cacheStrategy != null) {
            return this.requestBodyOut;
        }
        throw new IllegalStateException();
    }

    public BufferedSink getBufferedRequestBody() {
        BufferedSink result = this.bufferedRequestBody;
        if (result != null) {
            return result;
        }
        BufferedSink buffer;
        Sink requestBody = getRequestBody();
        if (requestBody != null) {
            buffer = Okio.buffer(requestBody);
            this.bufferedRequestBody = buffer;
        } else {
            buffer = null;
        }
        return buffer;
    }

    public boolean hasResponse() {
        return this.userResponse != null;
    }

    public Request getRequest() {
        return this.userRequest;
    }

    public Response getResponse() {
        if (this.userResponse != null) {
            return this.userResponse;
        }
        throw new IllegalStateException();
    }

    public Connection getConnection() {
        return this.streamAllocation.connection();
    }

    public HttpEngine recover(IOException e, boolean routeException, Sink requestBodyOut) {
        this.streamAllocation.streamFailed(e);
        if (!this.client.retryOnConnectionFailure()) {
            return null;
        }
        if ((requestBodyOut != null && !(requestBodyOut instanceof RetryableSink)) || !isRecoverable(e, routeException) || !this.streamAllocation.hasMoreRoutes()) {
            return null;
        }
        return new HttpEngine(this.client, this.userRequest, this.bufferRequestBody, this.callerWritesRequestBody, this.forWebSocket, close(), (RetryableSink) requestBodyOut, this.priorResponse);
    }

    public HttpEngine recover(IOException e, boolean routeException) {
        return recover(e, routeException, this.requestBodyOut);
    }

    private boolean isRecoverable(IOException e, boolean routeException) {
        boolean z = true;
        if (e instanceof ProtocolException) {
            return false;
        }
        if (e instanceof InterruptedIOException) {
            if (!((e instanceof SocketTimeoutException) && routeException)) {
                z = false;
            }
            return z;
        } else if (((e instanceof SSLHandshakeException) && (e.getCause() instanceof CertificateException)) || (e instanceof SSLPeerUnverifiedException)) {
            return false;
        } else {
            return true;
        }
    }

    private void maybeCache() throws IOException {
        InternalCache responseCache = Internal.instance.internalCache(this.client);
        if (responseCache != null) {
            if (CacheStrategy.isCacheable(this.userResponse, this.networkRequest)) {
                this.storeRequest = responseCache.put(this.userResponse);
            } else if (HttpMethod.invalidatesCache(this.networkRequest.method())) {
                try {
                    responseCache.remove(this.networkRequest);
                } catch (IOException e) {
                }
            }
        }
    }

    public void releaseStreamAllocation() throws IOException {
        this.streamAllocation.release();
    }

    public void cancel() {
        this.streamAllocation.cancel();
    }

    public StreamAllocation close() {
        if (this.bufferedRequestBody != null) {
            Util.closeQuietly(this.bufferedRequestBody);
        } else if (this.requestBodyOut != null) {
            Util.closeQuietly(this.requestBodyOut);
        }
        if (this.userResponse != null) {
            Util.closeQuietly(this.userResponse.body());
        } else {
            this.streamAllocation.streamFailed(null);
        }
        return this.streamAllocation;
    }

    private Response unzip(Response response) throws IOException {
        if (!this.transparentGzip || !"gzip".equalsIgnoreCase(this.userResponse.header("Content-Encoding")) || response.body() == null) {
            return response;
        }
        Source responseBody = new GzipSource(response.body().source());
        Headers strippedHeaders = response.headers().newBuilder().removeAll("Content-Encoding").removeAll("Content-Length").build();
        return response.newBuilder().headers(strippedHeaders).body(new RealResponseBody(strippedHeaders, Okio.buffer(responseBody))).build();
    }

    public static boolean hasBody(Response response) {
        if (response.request().method().equals("HEAD")) {
            return false;
        }
        int responseCode = response.code();
        if ((responseCode < 100 || responseCode >= 200) && responseCode != 204 && responseCode != 304) {
            return true;
        }
        if (OkHeaders.contentLength(response) != -1 || "chunked".equalsIgnoreCase(response.header("Transfer-Encoding"))) {
            return true;
        }
        return false;
    }

    private Request networkRequest(Request request) throws IOException {
        Builder result = request.newBuilder();
        if (request.header("Host") == null) {
            result.header("Host", Util.hostHeader(request.url(), false));
        }
        if (request.header("Connection") == null) {
            result.header("Connection", "Keep-Alive");
        }
        if (request.header("Accept-Encoding") == null) {
            this.transparentGzip = true;
            result.header("Accept-Encoding", "gzip");
        }
        List<Cookie> cookies = this.client.cookieJar().loadForRequest(request.url());
        if (!cookies.isEmpty()) {
            result.header("Cookie", cookieHeader(cookies));
        }
        if (request.header("User-Agent") == null) {
            result.header("User-Agent", Version.userAgent());
        }
        return result.build();
    }

    private String cookieHeader(List<Cookie> cookies) {
        StringBuilder cookieHeader = new StringBuilder();
        int size = cookies.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                cookieHeader.append("; ");
            }
            Cookie cookie = (Cookie) cookies.get(i);
            cookieHeader.append(cookie.name()).append('=').append(cookie.value());
        }
        return cookieHeader.toString();
    }

    public void readResponse() throws IOException {
        if (this.userResponse == null) {
            if (this.networkRequest == null && this.cacheResponse == null) {
                throw new IllegalStateException("call sendRequest() first!");
            } else if (this.networkRequest != null) {
                Response networkResponse;
                if (this.forWebSocket) {
                    this.httpStream.writeRequestHeaders(this.networkRequest);
                    networkResponse = readNetworkResponse();
                } else if (this.callerWritesRequestBody) {
                    if (this.bufferedRequestBody != null && this.bufferedRequestBody.buffer().size() > 0) {
                        this.bufferedRequestBody.emit();
                    }
                    if (this.sentRequestMillis == -1) {
                        if (OkHeaders.contentLength(this.networkRequest) == -1 && (this.requestBodyOut instanceof RetryableSink)) {
                            this.networkRequest = this.networkRequest.newBuilder().header("Content-Length", Long.toString(((RetryableSink) this.requestBodyOut).contentLength())).build();
                        }
                        this.httpStream.writeRequestHeaders(this.networkRequest);
                    }
                    if (this.requestBodyOut != null) {
                        if (this.bufferedRequestBody != null) {
                            this.bufferedRequestBody.close();
                        } else {
                            this.requestBodyOut.close();
                        }
                        if (this.requestBodyOut instanceof RetryableSink) {
                            this.httpStream.writeRequestBody((RetryableSink) this.requestBodyOut);
                        }
                    }
                    networkResponse = readNetworkResponse();
                } else {
                    networkResponse = new NetworkInterceptorChain(0, this.networkRequest, this.streamAllocation.connection()).proceed(this.networkRequest);
                }
                receiveHeaders(networkResponse.headers());
                if (this.cacheResponse != null) {
                    if (validate(this.cacheResponse, networkResponse)) {
                        this.userResponse = this.cacheResponse.newBuilder().request(this.userRequest).priorResponse(stripBody(this.priorResponse)).headers(combine(this.cacheResponse.headers(), networkResponse.headers())).cacheResponse(stripBody(this.cacheResponse)).networkResponse(stripBody(networkResponse)).build();
                        networkResponse.body().close();
                        releaseStreamAllocation();
                        InternalCache responseCache = Internal.instance.internalCache(this.client);
                        responseCache.trackConditionalCacheHit();
                        responseCache.update(this.cacheResponse, this.userResponse);
                        this.userResponse = unzip(this.userResponse);
                        return;
                    }
                    Util.closeQuietly(this.cacheResponse.body());
                }
                this.userResponse = networkResponse.newBuilder().request(this.userRequest).priorResponse(stripBody(this.priorResponse)).cacheResponse(stripBody(this.cacheResponse)).networkResponse(stripBody(networkResponse)).build();
                if (hasBody(this.userResponse)) {
                    maybeCache();
                    this.userResponse = unzip(cacheWritingResponse(this.storeRequest, this.userResponse));
                }
            }
        }
    }

    private Response readNetworkResponse() throws IOException {
        this.httpStream.finishRequest();
        Response networkResponse = this.httpStream.readResponseHeaders().request(this.networkRequest).handshake(this.streamAllocation.connection().handshake()).sentRequestAtMillis(this.sentRequestMillis).receivedResponseAtMillis(System.currentTimeMillis()).build();
        if (!this.forWebSocket) {
            networkResponse = networkResponse.newBuilder().body(this.httpStream.openResponseBody(networkResponse)).build();
        }
        if ("close".equalsIgnoreCase(networkResponse.request().header("Connection")) || "close".equalsIgnoreCase(networkResponse.header("Connection"))) {
            this.streamAllocation.noNewStreams();
        }
        return networkResponse;
    }

    private Response cacheWritingResponse(final CacheRequest cacheRequest, Response response) throws IOException {
        if (cacheRequest == null) {
            return response;
        }
        Sink cacheBodyUnbuffered = cacheRequest.body();
        if (cacheBodyUnbuffered == null) {
            return response;
        }
        final BufferedSource source = response.body().source();
        final BufferedSink cacheBody = Okio.buffer(cacheBodyUnbuffered);
        return response.newBuilder().body(new RealResponseBody(response.headers(), Okio.buffer(new Source() {
            boolean cacheRequestClosed;

            public long read(Buffer sink, long byteCount) throws IOException {
                try {
                    long bytesRead = source.read(sink, byteCount);
                    if (bytesRead == -1) {
                        if (!this.cacheRequestClosed) {
                            this.cacheRequestClosed = true;
                            cacheBody.close();
                        }
                        return -1;
                    }
                    sink.copyTo(cacheBody.buffer(), sink.size() - bytesRead, bytesRead);
                    cacheBody.emitCompleteSegments();
                    return bytesRead;
                } catch (IOException e) {
                    if (!this.cacheRequestClosed) {
                        this.cacheRequestClosed = true;
                        cacheRequest.abort();
                    }
                    throw e;
                }
            }

            public Timeout timeout() {
                return source.timeout();
            }

            public void close() throws IOException {
                if (!(this.cacheRequestClosed || Util.discard(this, 100, TimeUnit.MILLISECONDS))) {
                    this.cacheRequestClosed = true;
                    cacheRequest.abort();
                }
                source.close();
            }
        }))).build();
    }

    private static boolean validate(Response cached, Response network) {
        if (network.code() == 304) {
            return true;
        }
        Date lastModified = cached.headers().getDate("Last-Modified");
        if (lastModified != null) {
            Date networkLastModified = network.headers().getDate("Last-Modified");
            if (networkLastModified != null && networkLastModified.getTime() < lastModified.getTime()) {
                return true;
            }
        }
        return false;
    }

    private static Headers combine(Headers cachedHeaders, Headers networkHeaders) throws IOException {
        int i;
        Headers.Builder result = new Headers.Builder();
        int size = cachedHeaders.size();
        for (i = 0; i < size; i++) {
            String fieldName = cachedHeaders.name(i);
            String value = cachedHeaders.value(i);
            if (!("Warning".equalsIgnoreCase(fieldName) && value.startsWith("1")) && (!OkHeaders.isEndToEnd(fieldName) || networkHeaders.get(fieldName) == null)) {
                result.add(fieldName, value);
            }
        }
        size = networkHeaders.size();
        for (i = 0; i < size; i++) {
            fieldName = networkHeaders.name(i);
            if (!"Content-Length".equalsIgnoreCase(fieldName) && OkHeaders.isEndToEnd(fieldName)) {
                result.add(fieldName, networkHeaders.value(i));
            }
        }
        return result.build();
    }

    public void receiveHeaders(Headers headers) throws IOException {
        if (this.client.cookieJar() != CookieJar.NO_COOKIES) {
            List<Cookie> cookies = Cookie.parseAll(this.userRequest.url(), headers);
            if (!cookies.isEmpty()) {
                this.client.cookieJar().saveFromResponse(this.userRequest.url(), cookies);
            }
        }
    }

    public Request followUpRequest() throws IOException {
        if (this.userResponse == null) {
            throw new IllegalStateException();
        }
        Route route;
        Connection connection = this.streamAllocation.connection();
        if (connection != null) {
            route = connection.route();
        } else {
            route = null;
        }
        int responseCode = this.userResponse.code();
        String method = this.userRequest.method();
        switch (responseCode) {
            case 300:
            case 301:
            case 302:
            case 303:
                break;
            case StatusLine.HTTP_TEMP_REDIRECT /*307*/:
            case StatusLine.HTTP_PERM_REDIRECT /*308*/:
                if (!(method.equals("GET") || method.equals("HEAD"))) {
                    return null;
                }
            case 401:
                return this.client.authenticator().authenticate(route, this.userResponse);
            case 407:
                Proxy selectedProxy;
                if (route != null) {
                    selectedProxy = route.proxy();
                } else {
                    selectedProxy = this.client.proxy();
                }
                if (selectedProxy.type() == Type.HTTP) {
                    return this.client.proxyAuthenticator().authenticate(route, this.userResponse);
                }
                throw new ProtocolException("Received HTTP_PROXY_AUTH (407) code while not using proxy");
            case 408:
                boolean retryableBody = this.requestBodyOut == null || (this.requestBodyOut instanceof RetryableSink);
                if (!this.callerWritesRequestBody || retryableBody) {
                    return this.userRequest;
                }
                return null;
            default:
                return null;
        }
        if (!this.client.followRedirects()) {
            return null;
        }
        String location = this.userResponse.header("Location");
        if (location == null) {
            return null;
        }
        HttpUrl url = this.userRequest.url().resolve(location);
        if (url == null) {
            return null;
        }
        if (!url.scheme().equals(this.userRequest.url().scheme()) && !this.client.followSslRedirects()) {
            return null;
        }
        Builder requestBuilder = this.userRequest.newBuilder();
        if (HttpMethod.permitsRequestBody(method)) {
            if (HttpMethod.redirectsToGet(method)) {
                requestBuilder.method("GET", null);
            } else {
                requestBuilder.method(method, null);
            }
            requestBuilder.removeHeader("Transfer-Encoding");
            requestBuilder.removeHeader("Content-Length");
            requestBuilder.removeHeader("Content-Type");
        }
        if (!sameConnection(url)) {
            requestBuilder.removeHeader("Authorization");
        }
        return requestBuilder.url(url).build();
    }

    public boolean sameConnection(HttpUrl followUp) {
        HttpUrl url = this.userRequest.url();
        return url.host().equals(followUp.host()) && url.port() == followUp.port() && url.scheme().equals(followUp.scheme());
    }

    private static Address createAddress(OkHttpClient client, Request request) {
        SSLSocketFactory sslSocketFactory = null;
        HostnameVerifier hostnameVerifier = null;
        CertificatePinner certificatePinner = null;
        if (request.isHttps()) {
            sslSocketFactory = client.sslSocketFactory();
            hostnameVerifier = client.hostnameVerifier();
            certificatePinner = client.certificatePinner();
        }
        return new Address(request.url().host(), request.url().port(), client.dns(), client.socketFactory(), sslSocketFactory, hostnameVerifier, certificatePinner, client.proxyAuthenticator(), client.proxy(), client.protocols(), client.connectionSpecs(), client.proxySelector());
    }
}
