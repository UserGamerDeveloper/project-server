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
import java.io.FileWriter;
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

import static com.server.Util.getObjectMapper;

public class Main {
    final static ExecutorService threadPool = Executors.newFixedThreadPool(4);
    //final static ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();;

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
        Session session = Util.getSessionFactory().openSession();

        session.beginTransaction();
        List<Balance> balanceList = session.createNativeQuery(
                "SELECT * FROM balance",
                Balance.class
        ).getResultList();
        FileWriter writer = new FileWriter("balance.json", true);
        writer.write(getObjectMapper().writeValueAsString(balanceList));
        writer.flush();
        List<Item> itemList = session.createNativeQuery(
                "SELECT * FROM items",
                Item.class
        ).getResultList();
        writer = new FileWriter("items.json", true);
        writer.write(getObjectMapper().writeValueAsString(itemList));
        writer.flush();
        List<Mob> mobList = session.createNativeQuery(
                "SELECT * FROM mobs",
                Mob.class
        ).getResultList();
        writer = new FileWriter("mobs.json", true);
        writer.write(getObjectMapper().writeValueAsString(mobList));
        writer.flush();

        DirectExecutor directExecutor = new DirectExecutor();
        HttpServer server = HttpServer.create(new InetSocketAddress(5678), 0);
        server.createContext("/login", new Login());
        server.createContext("/start", new Start());
        server.createContext("/target", new SetTarget());
        server.createContext("/damage", new Damage());
        server.createContext("/continue", new Continue());
        server.createContext("/trade/buy", new TradeBuy());
        server.createContext("/trade/sell", new TradeSell());
        server.createContext("/trade/use/trader", new TradeUseTrader());
        server.createContext("/trade/use/blacksmith", new TradeUseBlacksmith());
        server.createContext("/trade/use/innkeeper", new TradeUseInnkeeper());
        server.createContext("/trade/exit", new TradeExit());
        server.createContext("/use/spell", new UseSpell());
        server.createContext("/use/food", new UseFood());
        server.createContext("/stats/confirm", new StatsConfirm());
        server.createContext("/stats/reset", new StatsReset());
        server.createContext("/test", new Test());
        server.createContext("/dead", new Dead());
        server.createContext("/reset", new Reset());
        server.createContext("/getinfo", new GetInfo());
        server.createContext("/exit", new Exit());
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
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " start Login");
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
                Session session = Util.getSessionFactory().openSession();
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
                        if (player.isLoginCoolDown()){
                            sessionInfo.setKey(request.getKey());
                            session.save(sessionInfo);
                            player.setLoginTime(new Timestamp(new Date().getTime()));
                            session.save(player);
                            response.setData(player.getLoginResponse());
                        }
                        else{
                            player.setLogin(false);
                            session.save(player);
                            response.setError(ResponceErrorCode.LOGIN_COOLDOWN);
                        }
                    }
                    else{
                        if (player.isLoginCoolDown()){
                            SessionInfo sessionInfo = new SessionInfo(player.getID(),request.getKey());
                            session.save(sessionInfo);
                            player.setLogin(true);
                            player.setLoginTime(new Timestamp(new Date().getTime()));
                            session.save(player);
                            response.setData(player.getLoginResponse());
                        }
                        else{
                            response.setError(ResponceErrorCode.LOGIN_COOLDOWN);
                        }
                    }
                    response.setGearScore(player.getGearScore());
                }
                else {
                        if (payload.getEmailVerified()){
                            Player player = new Player(payload.getEmail());
                            session.save(player);
                            session.flush();
                            SessionInfo sessionInfo = new SessionInfo(player.getID(),request.getKey());
                            session.save(sessionInfo);
                            response.setData(player.getLoginResponse());
                        }
                        else {
                            response.setError(ResponceErrorCode.NOT_VERIFIED_EMAIL);
                        }
                }
                commitTransactionAndSendResponse(t, mapper, session, response);
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " stop Login");
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
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " start Start");
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                Byte[] startItemId = mapper.readValue(request.getData(), Byte[].class);
                Session session = Util.getSessionFactory().openSession();
                session.beginTransaction();
                SessionInfo sessionInfo = session.load(SessionInfo.class, request.getKey());
                Player player = session.load(Player.class, sessionInfo.getIdPlayer());
                Response response = new Response();
                if (player.setStartInventory(startItemId)){
                    response.setData(player.getStartResponse());
                    response.setGearScore(player.getGearScore());
                }
                else{
                    response.setError(ResponceErrorCode.CHEAT_OR_BUG);
                }
                commitTransactionAndSendResponse(t, mapper, session, response);
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " stop Start");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class SetTarget implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            try {
                System.out.println(
                        new Date() + " Thread " + Thread.currentThread().getId() + " start SetTarget"
                );
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                byte id = mapper.readValue(request.getData(), byte.class);
                Session session = Util.getSessionFactory().openSession();
                session.beginTransaction();
                SessionInfo sessionInfo = session.load(SessionInfo.class, request.getKey());
                Player player = session.load(Player.class, sessionInfo.getIdPlayer());
                Response response = new Response();
                if (player.setTarget(id)){
                    response.setData(player.getSetTargetResponse());
                    response.setGearScore(player.getGearScore());
                }
                else{
                    response.setError(ResponceErrorCode.CHEAT_OR_BUG);
                }
                commitTransactionAndSendResponse(t, mapper, session, response);
                System.out.println(
                        new Date() +" Thread " + Thread.currentThread().getId() + " stop SetTarget"
                );
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class Damage implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            try {
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " start Damage");
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                CardInventory[] invenroty = mapper.readValue(request.getData(), CardInventory[].class);
                Session session = Util.getSessionFactory().openSession();
                session.beginTransaction();
                SessionInfo sessionInfo = session.load(SessionInfo.class, request.getKey());
                Player player = session.load(Player.class, sessionInfo.getIdPlayer());
                Response response = new Response();
                if (player.damageCheck(invenroty)){
                    response.setData(player.getDamageResponse());
                    response.setGearScore(player.getGearScore());
                }
                else{
                    response.setError(ResponceErrorCode.CHEAT_OR_BUG);
                }
                commitTransactionAndSendResponse(t, mapper, session, response);
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " stop Damage");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class Continue implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            try {
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " start Continue");
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                CardInventory[] inventory = mapper.readValue(request.getData(), CardInventory[].class);
                Session session = Util.getSessionFactory().openSession();
                session.beginTransaction();
                SessionInfo sessionInfo = session.load(SessionInfo.class, request.getKey());
                Player player = session.load(Player.class, sessionInfo.getIdPlayer());
                Response response = new Response();
                if (player.selectLoot(inventory)){
                    response.setData(player.getContinueResponse());
                    response.setGearScore(player.getGearScore());
                }
                else{
                    response.setError(ResponceErrorCode.CHEAT_OR_BUG);
                }
                commitTransactionAndSendResponse(t, mapper, session, response);
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " stop Continue");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class TradeBuy implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            try {
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " start TradeBuy");
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                Session session = Util.getSessionFactory().openSession();
                session.beginTransaction();
                SessionInfo sessionInfo = session.load(SessionInfo.class, request.getKey());
                Player player = session.load(Player.class, sessionInfo.getIdPlayer());
                Response response = new Response();
                CardTrade item = mapper.readValue(request.getData(), CardTrade.class);
                if (!player.buy(item)){
                    response.setError(ResponceErrorCode.CHEAT_OR_BUG);
                }
                response.setGearScore(player.getGearScore());
                commitTransactionAndSendResponse(t, mapper, session, response);
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " stop TradeBuy");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class TradeSell implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            try {
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " start TradeSell");
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                Session session = Util.getSessionFactory().openSession();
                session.beginTransaction();
                SessionInfo sessionInfo = session.load(SessionInfo.class, request.getKey());
                Player player = session.load(Player.class, sessionInfo.getIdPlayer());
                Response response = new Response();
                CardInventory item = mapper.readValue(request.getData(), CardInventory.class);
                if (!player.sell(item)){
                    response.setError(ResponceErrorCode.CHEAT_OR_BUG);
                }
                response.setGearScore(player.getGearScore());
                commitTransactionAndSendResponse(t, mapper, session, response);
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " stop TradeSell");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class TradeUseTrader implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            try {
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " start TradeUseTrader");
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                Session session = Util.getSessionFactory().openSession();
                session.beginTransaction();
                SessionInfo sessionInfo = session.load(SessionInfo.class, request.getKey());
                Player player = session.load(Player.class, sessionInfo.getIdPlayer());
                Response response = new Response();
                if (player.useSkillTrader()){
                    response.setData(player.getUseSkillTraderResponse());
                }
                else{
                    response.setError(ResponceErrorCode.CHEAT_OR_BUG);
                }
                commitTransactionAndSendResponse(t, mapper, session, response);
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " stop TradeUseTrader");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class TradeUseBlacksmith implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            try {
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " start TradeUseBlacksmith");
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                Session session = Util.getSessionFactory().openSession();
                session.beginTransaction();
                SessionInfo sessionInfo = session.load(SessionInfo.class, request.getKey());
                Player player = session.load(Player.class, sessionInfo.getIdPlayer());
                Response response = new Response();
                CardInventory item = mapper.readValue(request.getData(), CardInventory.class);
                if (!player.useSkillBlacksmith(item)){
                    response.setError(ResponceErrorCode.CHEAT_OR_BUG);
                }
                commitTransactionAndSendResponse(t, mapper, session, response);
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " stop TradeUseBlacksmith");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class TradeUseInnkeeper implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            try {
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " start TradeUseInnkeeper");
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                Session session = Util.getSessionFactory().openSession();
                session.beginTransaction();
                SessionInfo sessionInfo = session.load(SessionInfo.class, request.getKey());
                Player player = session.load(Player.class, sessionInfo.getIdPlayer());
                Response response = new Response();
                if (!player.useSkillInnkeeper()){
                    response.setError(ResponceErrorCode.CHEAT_OR_BUG);
                }
                commitTransactionAndSendResponse(t, mapper, session, response);
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " stop TradeUseInnkeeper");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class TradeExit implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            try {
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " start TradeExit");
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                Session session = Util.getSessionFactory().openSession();
                session.beginTransaction();
                SessionInfo sessionInfo = session.load(SessionInfo.class, request.getKey());
                Player player = session.load(Player.class, sessionInfo.getIdPlayer());
                Response response = new Response();
                if (player.exitTrade()){
                    response.setData(player.getContinueResponse());
                }
                else{
                    response.setError(ResponceErrorCode.CHEAT_OR_BUG);
                }
                commitTransactionAndSendResponse(t, mapper, session, response);
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " stop TradeExit");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class Exit implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            try {
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " start Exit");
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                Session session = Util.getSessionFactory().openSession();
                session.beginTransaction();
                SessionInfo sessionInfo = session.load(SessionInfo.class, request.getKey());
                Player player = session.load(Player.class, sessionInfo.getIdPlayer());
                Response response = new Response();
                if (!player.exit()){
                    response.setError(ResponceErrorCode.CHEAT_OR_BUG);
                }
                commitTransactionAndSendResponse(t, mapper, session, response);
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " stop Exit");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class UseSpell implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            try {
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " start UseSpell");
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                UseSpellRequest useSpellRequest = mapper.readValue(request.getData(), UseSpellRequest.class);
                Session session = Util.getSessionFactory().openSession();
                session.beginTransaction();
                SessionInfo sessionInfo = session.load(SessionInfo.class, request.getKey());
                Player player = session.load(Player.class, sessionInfo.getIdPlayer());
                Response response = new Response();
                if (player.useSpell(useSpellRequest)){
                    response.setData(player.getDamageResponse());
                    response.setGearScore(player.getGearScore());
                }
                else{
                    response.setError(ResponceErrorCode.CHEAT_OR_BUG);
                }
                commitTransactionAndSendResponse(t, mapper, session, response);
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " stop UseSpell");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class UseFood implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            try {
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " start UseFood");
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                byte itemID = mapper.readValue(request.getData(), byte.class);
                Session session = Util.getSessionFactory().openSession();
                session.beginTransaction();
                SessionInfo sessionInfo = session.load(SessionInfo.class, request.getKey());
                Player player = session.load(Player.class, sessionInfo.getIdPlayer());
                Response response = new Response();
                if (player.useFood(itemID)){
                    response.setData(player.getUseFoodResponse());
                    response.setGearScore(player.getGearScore());
                }
                else{
                    response.setError(ResponceErrorCode.CHEAT_OR_BUG);
                }
                commitTransactionAndSendResponse(t, mapper, session, response);
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " stop UseFood");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class StatsConfirm implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            try {
                System.out.println(
                        new Date() + " Thread " + Thread.currentThread().getId() + " start stats/confirm"
                );
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                Stats.Request data = mapper.readValue(request.getData(), Stats.Request.class);
                Session session = Util.getSessionFactory().openSession();
                session.beginTransaction();
                SessionInfo sessionInfo = session.load(SessionInfo.class, request.getKey());
                Player player = session.load(Player.class, sessionInfo.getIdPlayer());
                Response response = new Response();
                if (!player.getStats().confirm(data)){
                    response.setError(ResponceErrorCode.CHEAT_OR_BUG);
                }
                commitTransactionAndSendResponse(t, mapper, session, response);
                System.out.println(
                        new Date() +" Thread " + Thread.currentThread().getId() + " stop stats/confirm"
                );
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class StatsReset implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            try {
                System.out.println(
                        new Date() + " Thread " + Thread.currentThread().getId() + " start stats/reset"
                );
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                Session session = Util.getSessionFactory().openSession();
                session.beginTransaction();
                SessionInfo sessionInfo = session.load(SessionInfo.class, request.getKey());
                Player player = session.load(Player.class, sessionInfo.getIdPlayer());
                Response response = new Response();
                if (!player.getStats().reset()){
                    response.setError(ResponceErrorCode.CHEAT_OR_BUG);
                }
                commitTransactionAndSendResponse(t, mapper, session, response);
                System.out.println(
                        new Date() +" Thread " + Thread.currentThread().getId() + " stop stats/reset"
                );
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class Test implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            try {
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " start Test");
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                Session session = Util.getSessionFactory().openSession();
                session.beginTransaction();
                Response response = new Response();
                BalanceRequest balanceRequest = mapper.readValue(request.getData(), BalanceRequest.class);
                String sql = null;
                switch (balanceRequest.getBase()){
                    case "3":
                    case "0":{
                        if (balanceRequest.getParam().equals("VSC")){
                            sql = String.format(
                                    "UPDATE %s SET %s='%f'",
                                    "balance",
                                    balanceRequest.getParam(),
                                    Float.valueOf(balanceRequest.getValue())/100
                            );
                        }
                        else{
                            sql = String.format(
                                    "UPDATE %s SET %s='%s'",
                                    "balance",
                                    balanceRequest.getParam(),
                                    balanceRequest.getValue()
                            );
                        }
                        break;
                    }
                    case "1":{
                        sql = String.format(
                                "UPDATE %s SET %s='%s' WHERE id='%s'",
                                "mobs",
                                balanceRequest.getParam(),
                                balanceRequest.getValue(),
                                balanceRequest.getId()
                        );
                        break;
                    }
                    case "2":{
                        sql = String.format(
                                "UPDATE %s SET %s='%s' WHERE id='%s'",
                                "items",
                                balanceRequest.getParam(),
                                balanceRequest.getValue(),
                                balanceRequest.getId()
                        );
                        break;
                    }
                    default:{
                        response.setError(ResponceErrorCode.CHEAT_OR_BUG);
                        break;
                    }
                }
                session.createNativeQuery(sql).executeUpdate();
                commitTransactionAndSendResponse(t, mapper, session, response);
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " stop Test");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class Dead implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            try {
                System.out.println(
                        new Date() + " Thread " + Thread.currentThread().getId() + " start dead"
                );
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                Session session = Util.getSessionFactory().openSession();
                session.beginTransaction();
                SessionInfo sessionInfo = session.load(SessionInfo.class, request.getKey());
                Player player = session.load(Player.class, sessionInfo.getIdPlayer());
                Response response = new Response();
                player.dead();
                commitTransactionAndSendResponse(t, mapper, session, response);
                System.out.println(
                        new Date() +" Thread " + Thread.currentThread().getId() + " stop dead"
                );
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class Reset implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            try {
                System.out.println(
                        new Date() + " Thread " + Thread.currentThread().getId() + " start reset"
                );
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                Session session = Util.getSessionFactory().openSession();
                session.beginTransaction();
                SessionInfo sessionInfo = session.load(SessionInfo.class, request.getKey());
                Player player = session.load(Player.class, sessionInfo.getIdPlayer());
                Response response = new Response();
                player.resetAccount();
                commitTransactionAndSendResponse(t, mapper, session, response);
                System.out.println(
                        new Date() +" Thread " + Thread.currentThread().getId() + " stop reset"
                );
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class GetInfo implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            try {
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " start GetInfo");
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(getRequestBody(t), Request.class);
                byte idItem = mapper.readValue(request.getData(), byte.class);
                Session session = Util.getSessionFactory().openSession();
                session.beginTransaction();
                Response response = new Response();
                response.setData(Util.getInfoItemResponse(idItem));
                commitTransactionAndSendResponse(t, mapper, session, response);
                System.out.println(new Date() +" Thread " + Thread.currentThread().getId() + " stop GetInfo");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void commitTransactionAndSendResponse(HttpExchange t, ObjectMapper mapper, Session session, Response response) throws IOException {
        session.getTransaction().commit();
        session.close();
        String responseString = mapper.writeValueAsString(response);
        t.sendResponseHeaders(200, responseString.length());
        OutputStream os = t.getResponseBody();
        os.write(responseString.getBytes());
        os.close();
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