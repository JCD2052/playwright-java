package org.jcd2052.demo.support;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * A minimal, dependency-free static file server that hosts the bundled demo pages
 * ({@code src/test/resources/demo-site}) used by the framework's offline demo test suite.
 * </p>
 * <p>
 * Built on the JDK's own {@link HttpServer} so no extra Maven dependency is required.
 * </p>
 */
public class DemoServer {
    private static final String RESOURCE_ROOT = "demo-site";
    private static final String DEFAULT_PAGE = "/index.html";

    private final HttpServer server;

    /**
     * Creates and binds the server to an ephemeral local port. Call {@link #start()} to begin
     * accepting requests.
     *
     * @throws IOException if the underlying server socket could not be created
     */
    public DemoServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", this::handle);
        server.setExecutor(null);
    }

    /**
     * Starts accepting requests on the bound port.
     */
    public void start() {
        server.start();
    }

    /**
     * Stops the server immediately. Safe to call even if the server was never started.
     */
    public void stop() {
        server.stop(0);
    }

    /**
     * The base URL of the running server, with no trailing slash (e.g. {@code http://localhost:54231}).
     * Append a demo page path such as {@code "/index.html"} to navigate to a specific page.
     *
     * @return the base URL for this server instance.
     */
    public String getBaseUrl() {
        return "http://localhost:" + server.getAddress().getPort();
    }

    private void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.isEmpty() || path.equals("/")) {
            path = DEFAULT_PAGE;
        }

        String resourcePath = RESOURCE_ROOT + path;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                byte[] notFound = "Not found".getBytes();
                exchange.sendResponseHeaders(404, notFound.length);
                writeAndClose(exchange, notFound);
                return;
            }

            byte[] body = in.readAllBytes();
            exchange.getResponseHeaders().set("Content-Type", contentTypeFor(path));
            exchange.sendResponseHeaders(200, body.length);
            writeAndClose(exchange, body);
        }
    }

    private static void writeAndClose(HttpExchange exchange, byte[] body) throws IOException {
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }

    private static String contentTypeFor(String path) {
        if (path.endsWith(".css")) {
            return "text/css; charset=utf-8";
        }
        if (path.endsWith(".js")) {
            return "application/javascript; charset=utf-8";
        }
        return "text/html; charset=utf-8";
    }
}
