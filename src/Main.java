import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main (String[] args) {
        try {
            HttpServer server = makeServer();
            initRoutes (server);

            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }









    private static HttpServer makeServer() throws IOException {

        // укажем на каком сетевом адресе и порте

        // мы будем создавать наш сервер

        String host = "localhost";

        InetSocketAddress address = new InetSocketAddress(host,

                9889);
        String msg = "запускаем сервер по адресу"

                + " http://%s:%s/%n";

        System.out.printf(msg, address.getHostName(), address.getPort());

        // пробуем создать наш сервер по этому адресу

        HttpServer server = HttpServer.create(address, 50);

        System.out.println("  удачно!");   return server;

    }
    private static void initRoutes(HttpServer server) {

        server.createContext("/", Main::handleRequestX);
        server.createContext("/", Main::handleRequestX);
        server.createContext("/apps/",

        Main::handleRequestX); }

    private static void handleRequest(HttpExchange exchange) {
        try {
// укажем, что тип нашего содержимого - это
// простой текст в кодировке utf-8. Без этой строки
// браузер не будет знать как отображать те данные,
// которые он получит.
// text/plain - это MIME описание содержимого
// помогающее получателю понять, что мы
// отправляем просто текст
            exchange.getResponseHeaders()
                    .add("Content-Type", "text/plain; charset=utf-8");
// укажем, что мы удачно обработали запрос,
// отправив ответ с кодом 200. Так же установим
// длину ответа в 0, что означает, что отправляй
// ответ пока мы не закроем поток
            int responseCode = 200;
            int length = 0;
            exchange.sendResponseHeaders(responseCode, length);
// получаем экземпляр класса PrintWriter, который
// умеет записывать в поток текстовые данные
            try (PrintWriter writer = (PrintWriter) getWriterFrom(exchange)) {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String ctxPath = exchange.getHttpContext()
                        .getPath();
                write(writer, "HTTP Метод", method);
                write(writer, "Запрос", uri.toString());
                write(writer, "Обработан через", ctxPath);
                writeHeaders(writer, "Заголовки запроса",
                        exchange.getRequestHeaders());
                writeData (writer, exchange);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRequestX(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders()
                    .add("Content-Type", "text/plain; charset=utf-8");
            int responseCode = 300;
            int length = 0;
            exchange.sendResponseHeaders(responseCode, length);
            try (PrintWriter writer = (PrintWriter) getWriterFrom(exchange)) {
                Headers method = exchange.getRequestHeaders ();
                URI uri = URI.create (exchange.getProtocol ());
                String ctxPath = String.valueOf (exchange.getHttpContext()
                        .getAttributes ());
                write(writer, "HTTP Метод", String.valueOf (method));
                write(writer, "Запросccccc", uri.toString());
                write(writer, "Обработан через", ctxPath);
                writeHeaders(writer, "Заголовки запроса",
                        exchange.getRequestHeaders());
                writeData (writer, exchange);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static Writer getWriterFrom(HttpExchange exchange) {

        // создаём экземпляр класса PrintWriter, который

        // умеет записывать в поток текстовые данные

        // Если вы хотите записывать данные из файла,

        // то вы можете записывать их напрямую в

        // exchange.getResponseBody(); не используя этот метод

        OutputStream output = exchange.getResponseBody();   Charset charset = StandardCharsets.UTF_8;   return new PrintWriter(output, false, charset);

    }

    private static void write(Writer writer,                           String msg,

                              String method) {

        String data = String.format("%s: %s%n%n", msg, method);   try {

            writer.write(data);   } catch (IOException e) {

            e.printStackTrace();

        } }

    private static void writeHeaders(Writer writer, String type, Headers headers) {

        write(writer, type, "");   headers.forEach((k, v) ->

                write(writer, "\t" + k, v.toString())

        );

    }

    private static void writeData (Writer writer, HttpExchange exchange){
        try (BufferedReader reader = getReader (exchange)){
            if (!reader.ready ( )){
                return;
            }
            write(writer, "Body", "");
            reader.lines ().forEach (e-> write(writer,"\t", e));


        }catch (IOException e){
            e.printStackTrace ();
        }
    }
    private static  BufferedReader getReader (HttpExchange exchange){
        InputStream input = exchange.getRequestBody ();
        Charset charset = StandardCharsets.UTF_8;
        InputStreamReader reader = new InputStreamReader (input, charset);
        return new BufferedReader (reader);
    }

}
