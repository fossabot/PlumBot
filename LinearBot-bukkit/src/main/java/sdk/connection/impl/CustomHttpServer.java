package sdk.connection.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import sdk.connection.Connection;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;

/**
 * 基于内置http服务器实现自定义服务
 */
public class CustomHttpServer implements Connection {

    private Integer port;
    private String path;
    private BlockingQueue<String> queue;

    private static Log log = LogFactory.get();

    private HttpServer server;

    public CustomHttpServer(Integer port, String path, BlockingQueue<String> queue) {
        try {
            this.port = port;
            this.path = path;
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext(path, new CustomHttpHandler());
        } catch (IOException e) {
            log.error(e);
        }
        this.queue = queue;
    }

    @Override
    public void create() {
        server.start();
        log.info("HTTP服务器启动，正在监听端口：{}", port);
    }

    class CustomHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            StringBuffer stringBuffer = new StringBuffer();
            String oneLine = "";
            while ((oneLine = reader.readLine()) != null) {
                stringBuffer.append(oneLine);
            }
            queue.add(stringBuffer.toString());
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
        }
    }
}
