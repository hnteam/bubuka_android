package ru.espepe.bubuka.player.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.espepe.bubuka.player.log.Logger;
import ru.espepe.bubuka.player.log.LoggerFactory;

/**
 * Created by wolong on 04/08/14.
 */
public class HttpServer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public static final int SERVER_PORT = 47330;

    private volatile Thread thread;
    private ServerSocket serverSocket;

    private final File filesDir;

    public HttpServer(File filesDir) {
        this.filesDir = filesDir;
    }


    public static void main(String[] args) throws InterruptedException {
        HttpServer server = new HttpServer(new File("./files"));
        server.start();
        server.thread.join();
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        if(thread != null) {
            thread.interrupt();
            try {
                thread.join(5000);
            } catch (InterruptedException e) {
                logger.warn("failed to stop http server", e);
            }

            thread = null;
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT, 0, InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
        } catch (UnknownHostException e) { // impossible
        } catch (IOException e) {
            logger.error("IOException initializing server", e);
            thread = null;
            return;
        }

        while(thread != null) {
            try {
                Socket socket = serverSocket.accept();
                if(socket == null) {
                    continue;
                }

                logger.info("client connected");

                new ClientHandler(socket, filesDir).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private static class ClientHandler extends Thread {
        private static final Pattern rangeQueryPattern = Pattern.compile("^bytes=(\\d*)-(\\d*)$");

        private Socket socket;
        private String path;
        private Map<String, String> headers = new HashMap<String, String>();
        private File fileDir;

        public ClientHandler(Socket socket, File fileDir) {
            super();
            this.socket = socket;
            this.fileDir = fileDir;
        }

        @Override
        public void run() {
            try {
                handleRequest();
            } catch (Exception e) {
                logger.error("failed to handle request", e);
            }
        }

        private void handleRequest() throws Exception {
            try {
                logger.info("client handling started");
                InputStream inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), 8192);
                List<String> headers = new ArrayList<String>();

                String request = reader.readLine();
                if (request == null || request.isEmpty()) {
                    return;
                }

                logger.info("request: {}", request);

                String line = reader.readLine();
                while (line != null && !line.isEmpty()) {
                    logger.info("header line: {}", line);
                    headers.add(line);
                    line = reader.readLine();
                }

                parseRequest(request);
                parseHeaders(headers);

                processHeaders();

            } finally {
                socket.close();
            }
        }

        private void processHeaders() throws Exception {
            if(headers.containsKey("Range")) {
                final String rangeValue = headers.get("Range");
                logger.info("process headers, range value: {}", rangeValue);
                final Matcher matcher = rangeQueryPattern.matcher(rangeValue);
                if(matcher.matches()) {
                    final String rangeFrom = matcher.group(1);
                    final String rangeTo = matcher.group(2);

                    logger.info("range from '{}', range to '{}'", rangeFrom, rangeTo);

                    File file = new File(fileDir, path.substring(1));
                    OutputStream outputStream = socket.getOutputStream();

                    logger.info("search file: {}", file);
                    if(!file.exists() || !file.isFile() || !file.canRead()) {
                        logger.info("file not found, not a file, or not readable");
                        outputStream.write("HTTP/1.1 404 Not Found\r\nConnection: close\r\nContent-Type: text/plain\r\nContent-Length: 3\r\n\r\n404".getBytes("UTF-8"));
                        outputStream.flush();
                        socket.close();
                        return;
                    }

                    int totalSize = (int)file.length();
                    String contentType = contentType(file);

                    int startOffset;
                    int endOffset;
                    int bytesToRead;


                    if(!rangeFrom.isEmpty() && !rangeTo.isEmpty()) { // middle
                        startOffset = Integer.parseInt(rangeFrom);
                        endOffset = Integer.parseInt(rangeTo);
                        int requestedBytes = endOffset - startOffset + 1;
                        int canRead = totalSize - startOffset;
                        bytesToRead = Math.min(canRead, requestedBytes);
                    } else if(!rangeFrom.isEmpty()) { // from x to end of file
                        startOffset = Integer.parseInt(rangeFrom);
                        bytesToRead = totalSize - startOffset;
                        endOffset = totalSize;
                    } else if(!rangeTo.isEmpty()) { // last x bytes
                        bytesToRead = Integer.parseInt(rangeTo);
                        startOffset = totalSize - bytesToRead;
                        endOffset = totalSize;
                    } else {
                        throw new UnsupportedOperationException("invalid range: " + rangeValue);
                    }

                    FileInputStream fileInputStream = null;
                    try {
                        fileInputStream = new FileInputStream(file);
                        response(contentType, fileInputStream, socket.getOutputStream(), startOffset, bytesToRead, endOffset, totalSize);
                    } finally {
                        if(fileInputStream != null) {
                            fileInputStream.close();
                        }
                    }
                } else {
                    throw new UnsupportedOperationException("invalid range: " + rangeValue);
                }
            } else {
                File file = new File(fileDir, path.substring(1));
                OutputStream outputStream = socket.getOutputStream();

                logger.info("try to search file: {}", file);
                if(!file.exists() || !file.isFile() || !file.canRead()) {
                    logger.info("file not found, not a file, or not readable");
                    outputStream.write("HTTP/1.1 404 Not Found\r\nConnection: close\r\nContent-Type: text/plain\r\nContent-Length: 3\r\n\r\n404".getBytes("UTF-8"));
                    outputStream.flush();
                    socket.close();
                    return;
                }

                int totalSize = (int)file.length();
                String contentType = contentType(file);

                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(file);
                    responseFull(contentType, fileInputStream, socket.getOutputStream(), totalSize);
                } finally {
                    if(fileInputStream != null) {
                        fileInputStream.close();
                    }
                }
            }
        }

        private String contentType(File file) {
            return "audio/mpeg";
        }

        private void responseFull(String contentType, FileInputStream inputStream, OutputStream outputStream, int totalSize) throws Exception {
            String responseString = String.format("HTTP/1.1 200 OK\r\nContent-Type: %s\r\nConnection: close\r\nContent-Length: %d\r\n\r\n", contentType, totalSize);
            outputStream.write(responseString.getBytes("UTF-8"));
            byte[] buffer = new byte[1024 * 16];
            while(totalSize > 0) {
                int readSize = Math.min(totalSize, buffer.length);
                int len = inputStream.read(buffer, 0, readSize);

                outputStream.write(buffer, 0, len);
                totalSize -= len;
            }
        }

        private void response(String contentType, FileInputStream inputStream, OutputStream outputStream, int startOffset, int bytesToRead, int endOffset, int totalSize) throws Exception {
            String responseString = String.format("HTTP/1.1 206 Partial Content\r\nContent-Type: %s\r\nConnection: close\r\nContent-Length: %d\r\nContent-Range: %d-%d/%d\r\n\r\n", contentType, bytesToRead, startOffset, endOffset, totalSize);
            outputStream.write(responseString.getBytes("UTF-8"));
            inputStream.skip(startOffset);
            byte[] buffer = new byte[1024 * 16];
            while(bytesToRead > 0) {
                int readSize = Math.min(bytesToRead, buffer.length);
                int len = inputStream.read(buffer, 0, readSize);

                outputStream.write(buffer, 0, len);
                bytesToRead -= len;
            }
        }

        private void parseHeaders(List<String> headers) {
            for(String header : headers) {
                String[] parts = header.split(":\\s*", 2);
                logger.info("HEADER: {} => {}", parts[0], parts[1]);
                this.headers.put(parts[0], parts[1]);
            }
        }

        private void parseRequest(String request) {
            String[] parts = request.split("\\s+");
            String method = parts[0];
            String path = parts[1];
            String httpVersion = parts[2];

            if(method.equals("GET")) {
                logger.info("GET {}", path);
                this.path = path;
            } else {
                throw new UnsupportedOperationException("request: " + request);
            }
        }
    }

}
