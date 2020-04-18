package com.awasik.server;

import com.awasik.server.v2physics.Body;
import com.awasik.server.v2physics.BodyType;
import com.awasik.server.v2physics.World;
import com.awasik.shared.GameStateDto;
import com.awasik.shared.MyJsonMessage;
import com.awasik.shared.PlayerConnectedDto;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.github.czyzby.websocket.serialization.SerializationException;
import com.github.czyzby.websocket.serialization.Serializer;
import com.github.czyzby.websocket.serialization.impl.JsonSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

public class ServerListener implements ApplicationListener {

    private final Vertx vertx = Vertx.vertx();
    private final AtomicInteger idCounter = new AtomicInteger();
    private final Serializer serializer = new JsonSerializer();
    private HttpServer server = null;

    private Map<String, ServerWebSocket> sockets = new ConcurrentHashMap<>();
    private Map<String, Body> bodies = new ConcurrentHashMap<>();
    private World world = new World();
    private List<Body> removedCache = new ArrayList<>();
    private Map<Integer,List<GameStateDto>> gameStates = new ConcurrentHashMap<>();

    private BodyMapper bodyMapper = new BodyMapper();
    private boolean gameStateLocked = false;

    private void handleFrame(final ServerWebSocket webSocket, final WebSocketFrame frame) {
        Object request = null;
        try {
            request = serializer.deserialize(frame.binaryData().getBytes());
        } catch (SerializationException ex) {
            System.out.println("Serialization Exception");
        }
        if(request == null) {
            return;
        }
        if (!(request instanceof MyJsonMessage)) {
            return;
        }
        MyJsonMessage jsonMessage = (MyJsonMessage) request;

        String id = jsonMessage.bodyInfo.id;
        if(id == null || id.isEmpty()) {
            id = String.valueOf(idCounter.getAndIncrement());
            sockets.put(id, webSocket);
            Body body = new Body(id);
            body.setBodyType(BodyType.Dynamic);
            body.dim.set(0.5f,0.5f);
            body.pos.set(0,0);
            body.vel = 5f;
            world.createBody(body);
            bodies.put(id, body);
            final PlayerConnectedDto response = new PlayerConnectedDto();
            response.playerId = id;
            webSocket.writeFinalBinaryFrame(Buffer.buffer(serializer.serialize(response)));
        }
        else {
            /**
             * Przyszedl request z Clienta
             * musze zrobic lock na nowych stanach dla tego socketa dopoki ten request nie zostanie
             * przeprocesowany
             */
            Body body = bodies.get(id);
            if(!body.gameStateLocked) {
                int gameStateId = jsonMessage.gameStateId;
                body.gameStateId = gameStateId;
                System.out.println("\n@@@@@@@@@@@@@@@@@@@@@@@@@@");
                System.out.println("@ NEW CONTROLS RECIEVED  @");
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@");
                System.out.println("Body: " + body.getId());
                System.out.println("Pos: " + body.pos);
                System.out.println("Trans Pos: " + body.getPosition());
                System.out.println("Direction: " + body.toGo);
                System.out.println("Direction from client: " + jsonMessage.bodyInfo.destination);
                System.out.println("State idx: " + body.gameStateId);
                System.out.println("Destination Reached: " + body.reachedDestination);
                System.out.println("Stop At: " + jsonMessage.bodyInfo.stopAt);
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
                if (jsonMessage.bodyInfo.destination.y == -1) {
                    body.goUp(jsonMessage.bodyInfo.stopAt);
                } else if (jsonMessage.bodyInfo.destination.y == 1) {
                    body.goDown(jsonMessage.bodyInfo.stopAt);
                } else if (jsonMessage.bodyInfo.destination.x == -1) {
                    body.goLeft(jsonMessage.bodyInfo.stopAt);
                } else if (jsonMessage.bodyInfo.destination.x == 1) {
                    body.goRight(jsonMessage.bodyInfo.stopAt);
                } else if (jsonMessage.bodyInfo.destination.x == 0
                        && jsonMessage.bodyInfo.destination.y == 0){
                    body.stop(jsonMessage.bodyInfo.stopAt);
                }
                body.gameStateLocked = true;
            }

        }

    }


    @Override
    public void create() {
        launch();
    }

    private void launch() {
        Integer portInteger = Integer.getInteger("http.port");
        int port = 8000;
        if(portInteger != null) {
            port = portInteger;
        }
        String host = System.getProperty("http.address", "0.0.0.0");
        System.out.println("Launching web socket server: " + host + ":" + port);
        server = vertx.createHttpServer();
        server.websocketHandler(webSocket -> {
            webSocket.frameHandler(frame -> handleFrame(webSocket, frame));
        }).listen(port, host);
    }

    @Override
    public void resize(int width, int height) {

    }

    private int framecount = 0;
    private float worldDelta = 0f;
    private float updateDelta = 0f;

    Map<String, Integer> playerIdToGameStateMap = new HashMap<>();
    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        System.out.println("#####################################");
        System.out.println("# Frame Started");
        System.out.println("#####################################");
        for(Map.Entry<String, Body> entry : bodies.entrySet()) {
            Body body = entry.getValue();
            System.out.println("Body: " + body.getId());
            System.out.println("Direction: " + body.toGo);
            System.out.println("Started caluclating state idx: " + body.gameStateId);
            System.out.println("Position: " + body.pos);
            System.out.println("Direction: " + body.toGo);
            System.out.println("NextDirection: " + body.nextToGo);


        }
            /**
             * Register gameState ids which we will calculate in this frame
             */
//        for(Map.Entry<String, Body> entry : bodies.entrySet()) {
//            Body body = entry.getValue();
//            String playerId = entry.getKey();
//            playerIdToGameStateMap.put(playerId,body.gameStateId);
//        }
        world.step(delta);
        worldDelta += delta;
        updateDelta += delta;

        /**
         * Update the world state
         */
        sendGameStates(delta,playerIdToGameStateMap);
        System.out.println("#####################################");
        System.out.println("# Frame Ended");
        System.out.println("#####################################");
    }


    private void sendGameStates(float delta, Map<String, Integer> playerIdToGameStateMap) {
        for(Map.Entry<String, Body> entry : bodies.entrySet()) {
            /**
             * Wez socket gracza
             */
            Body body = entry.getValue();
            String id = entry.getKey();
            ServerWebSocket socket = sockets.get(id);

//            /**
//             * Wez liste stanow gry gracza
//             */
//            List<GameStateDto> playerGameStates = gameStates.get(id);

            /**
             * Zainicjalizuj jesli jeszcze nie istnieje
             */
//            if(playerGameStates == null) {
//                playerGameStates = new ArrayList<>();
//            }


            /**
             * Wyslij nowy stan, ale tylko jesli destination jest reached
             */
            if(body.reachedDestination) {
                /**
                 * Nadaj nowe id stanowi
                 */
                int gameStateId = body.gameStateId;

                /**
                 * Utworz nowy stan
                 */
                final GameStateDto gameState = createGameState(gameStateId, delta, body);

                sendGameState(body, id, socket, gameState);
            }

            System.out.println("Body: " + body.getId());
            System.out.println("Direction: " + body.toGo);
            System.out.println("Finished caluclating state idx: " + body.gameStateId);
            System.out.println("Position: " + body.pos);
            System.out.println("Direction: " + body.toGo);
            System.out.println("NextDirection: " + body.nextToGo);
            body.gameStateLocked = false;
        }

        for(Body body : removedCache) {
            bodies.remove(body.getId());
            gameStates.remove(body.getId());
            world.remove(body);
        }

        removedCache.clear();
    }

    private void sendGameState(Body body, String id, ServerWebSocket socket, GameStateDto gameState) {
        try {
            socket.writeFinalBinaryFrame(Buffer.buffer(serializer.serialize(gameState)));
        }
        catch (IllegalStateException ex) {
            System.out.println("Socket closed for player id: " + id);
            removedCache.add(body);
        }
        catch (SerializationException ex) {
            //System.out.println("");
        }
    }

    private GameStateDto createGameState(int gameStateId, float delta, Body body) {
        final GameStateDto gameStateDto = new GameStateDto();
        gameStateDto.setIndex(gameStateId);
        gameStateDto.delta = delta;
        gameStateDto.playerInfo = bodyMapper.toBodyInfo(body);
        gameStateDto.worldBodies = bodyMapper.toBodyInfoList(bodies.values());
        gameStateDto.playersOnline = bodies.size();
        gameStateDto.serverDelta = delta;
        gameStateDto.setTime(String.valueOf(System.currentTimeMillis()));
        return gameStateDto;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
