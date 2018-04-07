package com.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import org.hibernate.Session;

public class Main {

/*
    final static Hashtable<String, Player> sTokenPlayerHashtable = new Hashtable<>();
    final static Hashtable<String, String> sEmailTokenHashtable = new Hashtable<>();
*/
    final static ExecutorService threadPool = Executors.newFixedThreadPool(4);
/*
    final static ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
*/

    static class DirectExecutor implements Executor {

        public void execute(Runnable r) {
            try {
                threadPool.execute(r);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        HibernateUtil.getSessionFactory().openSession();
        DirectExecutor directExecutor = new DirectExecutor();
        HttpServer server = HttpServer.create(new InetSocketAddress(5678), 0);
        server.createContext("/login", new Login());
        server.createContext("/start", new Start());
        server.setExecutor(directExecutor);
        server.start();
        System.out.println("Start server");
    }

    static class Login implements HttpHandler {
        static final GoogleClientSecrets clientSecrets = getClientSecrets();
        static final String TOKEN_URL = "https://www.googleapis.com/oauth2/v4/token";
        static GoogleClientSecrets getClientSecrets(){
            try {
                return GoogleClientSecrets.load(
                        JacksonFactory.getDefaultInstance(), new FileReader(
                                "C:\\Users\\Skatt\\Desktop\\server\\src\\main\\resources\\client_secret.json"
                        )
                );
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        static final String getPlayerQuery = "SELECT * FROM player WHERE email='%s'";
        static final String getSessionQuery = "SELECT * FROM sessions WHERE idPlayer=%d";

        @Override
        public void handle(HttpExchange t) {
            try {
                System.out.println(System.nanoTime() +" Thread " + Thread.currentThread().getId() + " start Login");
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                Response response = new Response();

                GoogleTokenResponse tokenResponse =
                        new GoogleAuthorizationCodeTokenRequest(
                                new NetHttpTransport(),
                                JacksonFactory.getDefaultInstance(),
                                TOKEN_URL,
                                clientSecrets.getDetails().getClientId(),
                                clientSecrets.getDetails().getClientSecret(),
                                request.getKey(),
                                ""
                        ).execute();

                GoogleIdToken idToken = tokenResponse.parseIdToken();
                GoogleIdToken.Payload payload = idToken.getPayload();
                System.out.println("email: " + payload.getEmail()+" "+payload.getEmailVerified());
                Session session = HibernateUtil.getSessionFactory().openSession();
                List<Player> playerList = session.createNativeQuery(
                        String.format(getPlayerQuery, payload.getEmail()),
                        Player.class
                ).getResultList();
                session.beginTransaction();
                if (!playerList.isEmpty()){
                    Player player = playerList.get(0);
                    if (player.isLogin()){
                        SessionInfo sessionInfo = session.createNativeQuery(
                                String.format(getSessionQuery,player.getID()),
                                SessionInfo.class
                        ).getResultList().get(0);
                        session.delete(sessionInfo);
                        if (player.isLoginCooldown()){
                            sessionInfo.setKey(request.getKey());
                            session.save(sessionInfo);
                            player.setLoginTime(new Timestamp(new Date().getTime()));
                            session.save(player);
                            response.setData(player.getLoginResponce());
                        }
                        else{
                            player.setLogin(false);
                            session.save(player);
                            response.setError(ResponceErrorCode.LOGIN_COOLDOWN);
                        }
                    }
                    else{
                        if (player.isLoginCooldown()){
                            SessionInfo sessionInfo = new SessionInfo(player.getID(),request.getKey());
                            session.save(sessionInfo);
                            player.setLogin(true);
                            player.setLoginTime(new Timestamp(new Date().getTime()));
                            session.save(player);
                            response.setData(player.getLoginResponce());
                        }
                        else{
                            response.setError(ResponceErrorCode.LOGIN_COOLDOWN);
                        }
                    }
                }
                else {
                        if (payload.getEmailVerified()){
                            Player player = new Player(payload.getEmail());
                            session.save(player);
                            session.flush();
                            SessionInfo sessionInfo = new SessionInfo(player.getID(),request.getKey());
                            session.save(sessionInfo);
                            response.setData(player.getLoginResponce());
                        }
                        else {
                            response.setError(ResponceErrorCode.NOT_VERIFIED_EMAIL);
                        }
                }
                session.getTransaction().commit();
                session.close();
                String responseString = mapper.writeValueAsString(response);
                t.sendResponseHeaders(200, responseString.length());
                OutputStream os = t.getResponseBody();
                os.write(responseString.getBytes());
                os.close();
                System.out.println(System.nanoTime() +" Thread " + Thread.currentThread().getId() + " stop");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class Start implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            try {
                System.out.println(System.nanoTime() +" Thread " + Thread.currentThread().getId() + " start Start");
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                Byte[] startItemId = mapper.readValue(request.getData(), Byte[].class);
                Session session = HibernateUtil.getSessionFactory().openSession();
                SessionInfo sessionInfo = session.load(SessionInfo.class, request.getKey());
                Player player = session.load(Player.class, sessionInfo.getIdPlayer());
                Response response = new Response();
                if (player.setStartInventory(startItemId)){
                    response.setData(player.getStartResponce());
                }
                else{
                    response.setError(ResponceErrorCode.CHEAT_OR_BUG);
                }
                String responseString = mapper.writeValueAsString(response);
                t.sendResponseHeaders(200, responseString.length());
                OutputStream os = t.getResponseBody();
                os.write(responseString.getBytes());
                os.close();
                System.out.println(System.nanoTime() +" Thread " + Thread.currentThread().getId() + " stop Start");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static String getRequestBody(HttpExchange t) throws IOException {
        InputStream inputStream = t.getRequestBody();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        String request = result.toString("UTF-8");
        System.out.println("Request: " + request);
        return request;
    }
}
/*
            URL url;
            try {

                url = new URL(
                        "https://api.bitfinex.com/v1/book/BTCUSD/?limit_bids="+ request + "&limit_asks="+ request
                );
                HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

                inputStream = con.getInputStream();
                result = new ByteArrayOutputStream();
                while ((length = inputStream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                request = result.toString("UTF-8");
                System.out.println("HTTPS response: " + request);

            } catch (IOException e) {
                e.printStackTrace();
            }
*/